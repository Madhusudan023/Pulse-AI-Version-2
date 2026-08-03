import { useQuery } from '@tanstack/react-query';
import { getSurveysByStatus, type Survey } from '../services/survey.service';

export const usePendingSurveys = () => {
  return useQuery<Survey[], Error>({
    queryKey: ['pendingSurveys'],
    queryFn: () => getSurveysByStatus('PENDING'),
  });
};
