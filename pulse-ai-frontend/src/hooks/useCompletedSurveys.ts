import { useQuery } from '@tanstack/react-query';
import { getSurveysByStatus, type Survey } from '../services/survey.service';

export const useCompletedSurveys = () => {
  return useQuery<Survey[], Error>({
    queryKey: ['completedSurveys'],
    queryFn: () => getSurveysByStatus('COMPLETED'),
  });
};
