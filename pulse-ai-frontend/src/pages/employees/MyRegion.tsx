import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  Box, 
  Container, 
  Typography, 
  Button, 
  Grid, 
  Paper,
  CircularProgress,
  Chip,
  Avatar,
  Divider,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import { 
  Users, 
  Plus, 
  AlertCircle, 
  RefreshCw, 
  Mail, 
  Building2, 
  Briefcase, 
  Edit2 
} from 'lucide-react';
import { toast } from 'react-toastify';
import { getEmployees, createEmployee, updateEmployee, type Employee } from '../../services/employee.service';
import { CreateEmployeeModal } from '../../components/employees/CreateEmployeeModal';
import { UpdateEmployeeModal } from '../../components/employees/UpdateEmployeeModal';
import { useAuthStore } from '../../store/useAuthStore';
import { alpha, useTheme } from '@mui/material/styles';

export const MyRegion = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [employeeToUpdate, setEmployeeToUpdate] = useState<Employee | null>(null);
  const region = useAuthStore(state => state.region);
  const role = useAuthStore(state => state.role);
  const theme = useTheme();
  const [selectedRegion, setSelectedRegion] = useState<string>('ALL');
  
  const queryClient = useQueryClient();

  const { data: employees, isLoading, isError, refetch } = useQuery({
    queryKey: ['employees'],
    queryFn: getEmployees,
  });

  const createMutation = useMutation({
    mutationFn: createEmployee,
    onSuccess: () => {
      toast.success('Employee created successfully');
      setIsCreateModalOpen(false);
      queryClient.invalidateQueries({ queryKey: ['employees'] });
    },
    onError: () => toast.error('Failed to create employee'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number, data: any }) => updateEmployee(id, data),
    onSuccess: () => {
      toast.success('Employee updated successfully');
      setEmployeeToUpdate(null);
      queryClient.invalidateQueries({ queryKey: ['employees'] });
    },
    onError: () => toast.error('Failed to update employee'),
  });

  const filteredEmployees = employees ? employees.filter(emp => {
    const normalizedRole = role?.replace('ROLE_', '');
    if (normalizedRole === 'GLOBAL_HR') {
      if (selectedRegion === 'ALL') return true;
      return emp.region === selectedRegion;
    }
    return true; // Regional HR relies on backend filtering
  }) : [];


  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      {/* Page Header */}
      <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, justifyItems: 'space-between', justifyContent: 'space-between', alignItems: { xs: 'flex-start', md: 'flex-end' }, mb: 4, gap: 2 }}>
        <Box>
          <Typography variant="h4" sx={{ display: 'flex', alignItems: 'center', gap: 1.5, color: 'text.primary' }}>
            <Users size={32} color={theme.palette.primary.main} />
            Employees
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
            Manage {role?.replace('ROLE_', '') === 'GLOBAL_HR' ? 'employees across all regions' : `employees in the ${region} region`}.
          </Typography>
        </Box>
        
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          {role?.replace('ROLE_', '') === 'GLOBAL_HR' && (
            <FormControl size="small" sx={{ minWidth: 150 }}>
              <InputLabel>Filter by Region</InputLabel>
              <Select
                value={selectedRegion}
                label="Filter by Region"
                onChange={(e) => setSelectedRegion(e.target.value)}
              >
                <MenuItem value="ALL">All Regions</MenuItem>
                <MenuItem value="CHENNAI">Chennai</MenuItem>
                <MenuItem value="HYDERABAD">Hyderabad</MenuItem>
                <MenuItem value="PUNE">Pune</MenuItem>
                <MenuItem value="BANGALORE">Bangalore</MenuItem>
                <MenuItem value="NEW_YORK">New York</MenuItem>
                <MenuItem value="LONDON">London</MenuItem>
                <MenuItem value="SINGAPORE">Singapore</MenuItem>
              </Select>
            </FormControl>
          )}
          <Button 
            variant="outlined" 
            color="inherit" 
            onClick={() => refetch()} 
            startIcon={<RefreshCw size={18} className={isLoading ? 'animate-spin' : ''} />}
            sx={{ borderColor: 'divider', color: 'text.secondary' }}
          >
            Refresh
          </Button>
          <Button 
            variant="contained" 
            color="primary" 
            onClick={() => setIsCreateModalOpen(true)}
            startIcon={<Plus size={18} />}
          >
            Add Employee
          </Button>
        </Box>
      </Box>

      {/* Content Area */}
      <Box sx={{ minHeight: 400 }}>
        {isLoading && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, gap: 2 }}>
            <CircularProgress color="primary" />
            <Typography color="text.secondary">Loading employees...</Typography>
          </Box>
        )}

        {isError && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, gap: 2, color: 'error.main' }}>
            <AlertCircle size={48} />
            <Typography variant="h6">Failed to load employees.</Typography>
          </Box>
        )}

        {!isLoading && !isError && (!filteredEmployees || filteredEmployees.length === 0) && (
          <Paper 
            elevation={0}
            sx={{ 
              display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', 
              height: 250, border: '1px dashed', borderColor: 'divider', bgcolor: 'transparent', gap: 2 
            }}
          >
            <Users size={48} color={theme.palette.text.secondary} opacity={0.5} />
            <Typography color="text.secondary" variant="h6">No employees found in your region.</Typography>
          </Paper>
        )}

        {!isLoading && !isError && filteredEmployees && filteredEmployees.length > 0 && (
          <Grid container spacing={3}>
            {filteredEmployees.map(employee => (
              <Grid size={{ xs: 12, md: 6, lg: 4 }} key={employee.id}>
                <Paper 
                  sx={{ 
                    p: 3, 
                    display: 'flex', 
                    flexDirection: 'column', 
                    height: '100%',
                    transition: 'border-color 0.2s',
                    border: '1px solid',
                    borderColor: 'divider',
                    '&:hover': {
                      borderColor: 'primary.light',
                    }
                  }}
                >
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
                    <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                      <Avatar sx={{ 
                        bgcolor: alpha(theme.palette.primary.main, 0.1), 
                        color: 'primary.main', 
                        border: '1px solid',
                        borderColor: alpha(theme.palette.primary.main, 0.2),
                        width: 48,
                        height: 48,
                        fontWeight: 'bold'
                      }}>
                        {employee.firstName.charAt(0)}{employee.lastName.charAt(0)}
                      </Avatar>
                      <Box>
                        <Typography variant="subtitle1" color="text.primary" sx={{ fontWeight: 600, lineHeight: 1.2 }}>
                          {employee.firstName} {employee.lastName}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {employee.employeeCode}
                        </Typography>
                      </Box>
                    </Box>
                    <Button 
                      variant="outlined" 
                      color="inherit"
                      size="small"
                      onClick={() => setEmployeeToUpdate(employee)}
                      sx={{ minWidth: 'auto', p: 1, borderColor: 'divider', color: 'text.secondary' }}
                    >
                      <Edit2 size={16} />
                    </Button>
                  </Box>

                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5, flexGrow: 1, mb: 3 }}>
                    <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <Mail size={16} color={theme.palette.text.secondary} opacity={0.7} />
                      <Box component="span" sx={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{employee.email}</Box>
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <Briefcase size={16} color={theme.palette.text.secondary} opacity={0.7} />
                      {employee.designation}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <Building2 size={16} color={theme.palette.text.secondary} opacity={0.7} />
                      {employee.department} • {employee.region}
                    </Typography>
                  </Box>

                  <Divider sx={{ mx: -3, mb: 2 }} />

                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Chip 
                      label={employee.active ? 'Active' : 'Inactive'} 
                      size="small" 
                      color={employee.active ? 'success' : 'error'}
                      sx={{ 
                        bgcolor: alpha(employee.active ? theme.palette.success.main : theme.palette.error.main, 0.1),
                        fontWeight: 600
                      }}
                    />
                    <Typography variant="caption" color="text.secondary">
                      Joined {new Date(employee.joiningDate).toLocaleDateString()}
                    </Typography>
                  </Box>
                </Paper>
              </Grid>
            ))}
          </Grid>
        )}
      </Box>

      <CreateEmployeeModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSubmit={(data) => createMutation.mutate(data)}
        isSubmitting={createMutation.isPending}
      />

      <UpdateEmployeeModal
        isOpen={employeeToUpdate !== null}
        onClose={() => setEmployeeToUpdate(null)}
        employee={employeeToUpdate}
        onSubmit={(id, data) => updateMutation.mutate({ id, data })}
        isSubmitting={updateMutation.isPending}
      />
    </Container>
  );
};
