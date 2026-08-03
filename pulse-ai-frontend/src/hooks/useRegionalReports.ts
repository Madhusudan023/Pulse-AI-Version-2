import { useQuery } from '@tanstack/react-query';
import { getRegionalReports, type Report } from '../services/report.service';

export const useRegionalReports = () => {
  return useQuery<Report[], Error>({
    queryKey: ['regionalReports'],
    queryFn: getRegionalReports,
    staleTime: 5 * 60 * 1000,
  });
};
