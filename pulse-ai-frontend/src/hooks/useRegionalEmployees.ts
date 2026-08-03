import { useQuery } from '@tanstack/react-query';
import { getEmployees, type EmployeeProfile } from '../services/employee.service';

export const useRegionalEmployees = () => {
  return useQuery<EmployeeProfile[], Error>({
    queryKey: ['regionalEmployees'],
    queryFn: getEmployees,
    staleTime: 5 * 60 * 1000,
  });
};
