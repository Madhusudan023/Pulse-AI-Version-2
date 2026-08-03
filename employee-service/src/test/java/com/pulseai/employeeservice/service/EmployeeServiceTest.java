package com.pulseai.employeeservice.service;

import com.pulseai.employeeservice.client.AuthFeignClient;
import com.pulseai.employeeservice.dto.*;
import com.pulseai.employeeservice.entity.Employee;
import com.pulseai.employeeservice.enums.Department;
import com.pulseai.employeeservice.enums.Region;
import com.pulseai.employeeservice.enums.Role;
import com.pulseai.employeeservice.exception.BusinessException;
import com.pulseai.employeeservice.exception.ResourceNotFoundException;
import com.pulseai.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuthFeignClient authFeignClient;

    @InjectMocks
    private EmployeeService employeeService;

    private CreateEmployeeRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateEmployeeRequest();
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setEmail("john.doe@company.com");
        validRequest.setPassword("securePass123");
        validRequest.setDesignation("Software Engineer");
        validRequest.setDepartment(Department.ENGINEERING);
        validRequest.setBusinessUnit("Core Tech");
        validRequest.setRegion(Region.HYDERABAD);
        validRequest.setRole(Role.EMPLOYEE);
        validRequest.setJoiningDate(LocalDate.now());
        validRequest.setManagerId(99L);
    }

    // --- 1. createEmployee Tests ---

    @Test
    void testCreateEmployee_Success() {
        when(employeeRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(employeeRepository.count()).thenReturn(10L);
        
        Employee savedEmp = new Employee();
        savedEmp.setId(100L);
        savedEmp.setEmail(validRequest.getEmail());
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmp);

        assertDoesNotThrow(() -> employeeService.createEmployee(validRequest));

        verify(employeeRepository, times(1)).save(argThat(emp -> {
            assertEquals("EMP0011", emp.getEmployeeCode());
            assertEquals("John", emp.getFirstName());
            return true;
        }));
        verify(authFeignClient, times(1)).createCredential(argThat(cred -> {
            assertEquals(100L, cred.getEmployeeId());
            assertEquals("john.doe@company.com", cred.getEmail());
            return true;
        }));
    }

    @Test
    void testCreateEmployee_EmailAlreadyExists() {
        when(employeeRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> 
            employeeService.createEmployee(validRequest)
        );
        assertTrue(ex.getMessage().contains("already exists"));
        verify(employeeRepository, never()).save(any());
        verifyNoInteractions(authFeignClient);
    }

    @Test
    void testCreateEmployee_AuthClientThrowsException_RollbackCompensatingTransaction() {
        when(employeeRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        
        Employee savedEmp = new Employee();
        savedEmp.setId(100L);
        savedEmp.setEmail(validRequest.getEmail());
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmp);

        doThrow(new RuntimeException("Auth service offline")).when(authFeignClient).createCredential(any());

        BusinessException ex = assertThrows(BusinessException.class, () -> 
            employeeService.createEmployee(validRequest)
        );
        assertTrue(ex.getMessage().contains("Failed to create employee credentials"));
    }

    // --- 2. updateEmployee Tests ---

    @Test
    void testUpdateEmployee_Success_GlobalCaller() {
        Employee existingEmp = new Employee();
        existingEmp.setId(1L);
        existingEmp.setRegion(Region.HYDERABAD);

        UpdateEmployeeRequest updateReq = new UpdateEmployeeRequest();
        updateReq.setFirstName("Alex");
        updateReq.setRegion(Region.BENGALURU);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmp));

        assertDoesNotThrow(() -> employeeService.updateEmployee(1L, updateReq, "GLOBAL"));

        assertEquals("Alex", existingEmp.getFirstName());
        assertEquals(Region.BENGALURU, existingEmp.getRegion());
        verify(employeeRepository, times(1)).save(existingEmp);
    }

    @Test
    void testUpdateEmployee_Success_RegionalHrSameRegion() {
        Employee existingEmp = new Employee();
        existingEmp.setId(1L);
        existingEmp.setRegion(Region.HYDERABAD);

        UpdateEmployeeRequest updateReq = new UpdateEmployeeRequest();
        updateReq.setFirstName("Bob");
        updateReq.setRegion(Region.HYDERABAD);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmp));

        assertDoesNotThrow(() -> employeeService.updateEmployee(1L, updateReq, "HYDERABAD"));

        verify(employeeRepository, times(1)).save(existingEmp);
    }

    @Test
    void testUpdateEmployee_Failure_RegionalHrMismatchRegion() {
        Employee existingEmp = new Employee();
        existingEmp.setId(1L);
        existingEmp.setRegion(Region.HYDERABAD);

        UpdateEmployeeRequest updateReq = new UpdateEmployeeRequest();
        updateReq.setRegion(Region.BENGALURU);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmp));

        BusinessException ex = assertThrows(BusinessException.class, () -> 
            employeeService.updateEmployee(1L, updateReq, "BENGALURU") // BENGALURU HR updating HYDERABAD employee
        );
        assertTrue(ex.getMessage().contains("not authorized"));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            employeeService.updateEmployee(1L, new UpdateEmployeeRequest(), "GLOBAL")
        );
    }

    // --- 3. getEmployeesByRegion Tests ---

    @Test
    void testGetEmployeesByRegion_Global() {
        Employee e1 = new Employee(); e1.setId(1L);
        Employee e2 = new Employee(); e2.setId(2L);
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<EmployeeResponse> list = employeeService.getEmployeesByRegion("GLOBAL");
        assertEquals(2, list.size());
        verify(employeeRepository, times(1)).findAll();
        verify(employeeRepository, never()).findByRegion(any());
    }

    @ParameterizedTest
    @EnumSource(value = Region.class, names = {"GLOBAL"}, mode = EnumSource.Mode.EXCLUDE)
    void testGetEmployeesByRegion_SpecificRegions(Region region) {
        Employee e1 = new Employee(); e1.setId(1L); e1.setRegion(region);
        when(employeeRepository.findByRegion(region)).thenReturn(Collections.singletonList(e1));

        List<EmployeeResponse> list = employeeService.getEmployeesByRegion(region.name());
        assertEquals(1, list.size());
        assertEquals(region, list.get(0).getRegion());
        verify(employeeRepository, times(1)).findByRegion(region);
    }

    // --- 4. getEmployeeById & getEmployeeInternal Tests ---

    @Test
    void testGetEmployeeById_Success() {
        Employee emp = new Employee();
        emp.setId(5L);
        emp.setEmail("e@test.com");
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(emp));

        EmployeeResponse res = employeeService.getEmployeeById(5L);
        assertEquals(5L, res.getId());
        assertEquals("e@test.com", res.getEmail());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(5L));
    }

    @Test
    void testGetEmployeeInternal_Success() {
        Employee emp = new Employee();
        emp.setId(7L);
        emp.setEmail("internal@test.com");
        emp.setRegion(Region.CHENNAI);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(emp));

        EmployeeInternalResponse res = employeeService.getEmployeeInternal(7L);
        assertEquals(7L, res.getEmployeeId());
        assertEquals("internal@test.com", res.getEmail());
        assertEquals(Region.CHENNAI, res.getRegion());
    }

    // --- 5. Joining date filters and Manager flows ---

    @Test
    void testGetNewJoinersByRegion() {
        Employee e = new Employee(); e.setId(10L); e.setRegion(Region.CHENNAI);
        when(employeeRepository.findByRegionAndJoiningDateGreaterThan(eq(Region.CHENNAI), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(e));

        List<EmployeeInternalResponse> res = employeeService.getNewJoinersByRegion("CHENNAI");
        assertEquals(1, res.size());
        assertEquals(10L, res.get(0).getEmployeeId());
    }

    @Test
    void testGetTenuredEmployeesByRegion() {
        Employee e = new Employee(); e.setId(20L); e.setRegion(Region.CHENNAI);
        when(employeeRepository.findByRegionAndJoiningDateLessThanEqual(eq(Region.CHENNAI), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(e));

        List<EmployeeInternalResponse> res = employeeService.getTenuredEmployeesByRegion("CHENNAI");
        assertEquals(1, res.size());
        assertEquals(20L, res.get(0).getEmployeeId());
    }

    @Test
    void testGetInternalEmployeesByManager() {
        Employee e = new Employee(); e.setId(1L); e.setManagerId(99L);
        when(employeeRepository.findByManagerId(99L)).thenReturn(Collections.singletonList(e));

        List<EmployeeInternalResponse> res = employeeService.getInternalEmployeesByManager(99L);
        assertEquals(1, res.size());
        assertEquals(1L, res.get(0).getEmployeeId());
    }

    @ParameterizedTest
    @CsvSource({
        "GLOBAL, 100",
        "HYDERABAD, 50",
        "CHENNAI, 0"
    })
    void testGetEmployeeCountByRegion(String regionStr, long expectedCount) {
        if ("GLOBAL".equals(regionStr)) {
            when(employeeRepository.count()).thenReturn(expectedCount);
        } else {
            when(employeeRepository.countByRegion(Region.valueOf(regionStr))).thenReturn(expectedCount);
        }

        long count = employeeService.getEmployeeCountByRegion(regionStr);
        assertEquals(expectedCount, count);
    }
}
