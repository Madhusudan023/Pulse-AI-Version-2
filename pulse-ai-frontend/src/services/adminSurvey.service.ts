import { api } from './api';
import type { Survey } from './survey.service';

export const getRegionalSurveys = async (status?: string): Promise<Survey[]> => {
  const url = status ? `/surveys/my-region?status=${status}` : '/surveys/my-region';
  const response = await api.get(url);
  return response.data.data;
};
