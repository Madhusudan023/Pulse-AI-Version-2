package com.pulseai.employeeservice.repository;

import com.pulseai.employeeservice.entity.Employee;
import com.pulseai.employeeservice.enums.Region;
import com.pulseai.employeeservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByEmployeeCode(String employeeCode);
    List<Employee> findByRegion(Region region);
    List<Employee> findByRole(Role role);
    List<Employee> findByManagerId(Long managerId);
    List<Employee> findByRegionAndRoleAndActiveTrue(Region region, Role role);
    List<Employee> findByRoleAndActiveTrue(Role role);
    boolean existsByEmail(String email);
    long countByRegion(Region region);
    List<Employee> findByRegionAndJoiningDateGreaterThan(Region region, java.time.LocalDate date);
    List<Employee> findByRegionAndJoiningDateLessThanEqual(Region region, java.time.LocalDate date);
}
