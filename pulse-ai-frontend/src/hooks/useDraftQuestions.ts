import { useQuery } from '@tanstack/react-query';
import { getQuestions, type Question } from '../services/question.service';

export const useDraftQuestions = () => {
  return useQuery<Question[], Error>({
    queryKey: ['draftQuestions'],
    queryFn: () => getQuestions('DRAFT'),
    staleTime: 5 * 60 * 1000,
  });
};
