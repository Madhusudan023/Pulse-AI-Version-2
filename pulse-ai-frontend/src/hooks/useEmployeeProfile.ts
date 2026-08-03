import { useQuery } from '@tanstack/react-query';
import { getEmployeeProfile, type EmployeeProfile } from '../services/employee.service';

export const useEmployeeProfile = () => {
  return useQuery<EmployeeProfile, Error>({
    queryKey: ['employeeProfile'],
    queryFn: getEmployeeProfile,
  });
};
