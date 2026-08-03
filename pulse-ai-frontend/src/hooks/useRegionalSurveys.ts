import { useQuery } from '@tanstack/react-query';
import { getRegionalSurveys } from '../services/adminSurvey.service';
import type { Survey } from '../services/survey.service';

export const useRegionalSurveys = () => {
  return useQuery<Survey[], Error>({
    queryKey: ['regionalSurveys'],
    queryFn: () => getRegionalSurveys(),
  });
};
