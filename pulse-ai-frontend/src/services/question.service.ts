import { api } from './api';

export interface Question {
  id: number;
  questionText: string;
  questionType: string;
  category: string;
  surveyType: string;
  source: string;
  status: string;
  region?: string;
  createdAt?: string;
  // Flexible Rating Scale
  positiveFrom?: number;
  positiveTo?: number;
  neutralFrom?: number;
  neutralTo?: number;
  negativeFrom?: number;
  negativeTo?: number;
  options?: string[];
}

export const getQuestions = async (status?: string): Promise<Question[]> => {
  const url = status ? `/questions?status=${status}` : '/questions';
  const response = await api.get(url);
  return response.data.data;
};

export const approveQuestion = async (id: number): Promise<void> => {
  await api.put(`/questions/${id}/approve`);
};

export const rejectQuestion = async (id: number): Promise<void> => {
  await api.put(`/questions/${id}/reject`);
};

export interface QuestionPayload {
  questionText: string;
  questionType: string;
  category: string;
  surveyType: string;
  // Flexible Rating Scale (optional — defaults applied on backend)
  positiveFrom?: number;
  positiveTo?: number;
  neutralFrom?: number;
  neutralTo?: number;
  negativeFrom?: number;
  negativeTo?: number;
  options?: string[];
}

export const createQuestion = async (payload: QuestionPayload): Promise<Question> => {
  const response = await api.post('/questions', payload);
  return response.data.data;
};

export const updateQuestion = async (id: number, payload: Partial<QuestionPayload>): Promise<Question> => {
  const response = await api.put(`/questions/${id}`, payload);
  return response.data.data;
};


export const deleteQuestion = async (id: number): Promise<void> => {
  await api.delete(`/questions/${id}`);
};

export const getOnboardingQuestions = async (): Promise<Question[]> => {
  const response = await api.get('/questions/onboarding');
  return response.data.data;
};

export const getMonthlyPulseQuestions = async (): Promise<Question[]> => {
  const response = await api.get('/questions/monthly-pulse');
  return response.data.data;
};
