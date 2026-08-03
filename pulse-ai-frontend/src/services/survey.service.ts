import { api } from './api';
import axios from 'axios';
import { useAuthStore } from '../store/useAuthStore';

export interface Survey {
  id: number;
  title: string;
  description: string;
  region: string;
  surveyType: string;
  month: number;
  year: number;
  status: string;
  startDate: string;
  endDate: string;
  publishedAt?: string;
  closedAt?: string;
  expectedParticipants: number;
  completedParticipants: number;
  aiProcessed: boolean;
  isAnonymous: boolean;
}

export const getSurveysByStatus = async (status: 'PENDING' | 'COMPLETED'): Promise<Survey[]> => {
  const response = await api.get(`/employee/surveys?status=${status}`);
  return response.data.data;
};

export const getSurveyDetails = async (id: string) => {
  const response = await api.get(`/employee/surveys/${id}`);
  return response.data.data;
};

export interface SubmitSurveyRequest {
  responseDuration: string;
  answers: {
    questionId: number;
    ratingAnswer?: number;
    textAnswer?: string;
    optionAnswer?: string;
  }[];
}

export const submitSurvey = async (id: string, payload: SubmitSurveyRequest) => {
  const response = await api.post(`/employee/surveys/${id}/submit`, payload);
  return response.data;
};

export interface CreateSurveyRequest {
  title: string;
  description: string;
  region: string;
  surveyType: string;
  month: number;
  year: number;
  startDate: string; // ISO String
  endDate: string; // ISO String
  targetAudience: string;
  isAnonymous: boolean;
}

export const createSurvey = async (payload: CreateSurveyRequest): Promise<Survey> => {
  const response = await api.post('/surveys', payload);
  return response.data.data;
};

export const getRegionSurveys = async (status?: string): Promise<Survey[]> => {
  const url = status && status !== 'ALL' ? `/surveys/my-region?status=${status}` : '/surveys/my-region';
  const response = await api.get(url);
  return response.data.data;
};

export const addQuestionsToSurvey = async (id: number, questionIds: number[]): Promise<void> => {
  await api.post(`/surveys/${id}/questions/bulk`, { questionIds });
};

export const publishSurvey = async (id: number, customEmails?: string[]): Promise<void> => {
  await api.post(`/surveys/${id}/publish`, { customEmails });
};

export const closeSurvey = async (id: number): Promise<void> => {
  await api.post(`/surveys/${id}/close`);
};

export const reactivateSurvey = async (id: number): Promise<void> => {
  await api.post(`/surveys/${id}/reactivate`);
};

export const updateSurvey = async (id: number, payload: CreateSurveyRequest): Promise<Survey> => {
  const response = await api.put(`/surveys/${id}`, payload);
  return response.data.data;
};

export const deleteSurvey = async (id: number): Promise<void> => {
  await api.delete(`/surveys/${id}`);
};

export const removeQuestionsFromSurvey = async (surveyId: number, questionIds: number[]): Promise<void> => {
  await api.delete(`/surveys/${surveyId}/questions/bulk`, { data: questionIds });
};



export interface SurveyQuestionMapping {
  id: number;
  surveyId: number;
  questionId: number;
  displayOrder: number;
}

export const getSurveyQuestions = async (id: number): Promise<SurveyQuestionMapping[]> => {
  const token = useAuthStore.getState().token;
  const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';
  const response = await axios.get(`${API_BASE}/survey-service/api/v1/internal/surveys/${id}/questions`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.data;
};

export const getLastSurveyQuestionIds = async (surveyType: string): Promise<number[]> => {
  const response = await api.get(`/surveys/last/${surveyType}/question-ids`);
  return response.data.data;
};

export interface AnswerDTO {
  id: number;
  responseId: number;
  questionId: number;
  ratingAnswer?: number;
  textAnswer?: string;
  optionAnswer?: string;
}

export interface SurveyResponseDTO {
  id: number;
  surveyId: number;
  employeeId?: number;
  employeeEmail?: string;
  submittedAt: string;
  responseDuration: string;
}

export interface FullSurveyResponseDTO {
  response: SurveyResponseDTO;
  answers: AnswerDTO[];
}

export const getAdminSurveyResponses = async (surveyId: number): Promise<FullSurveyResponseDTO[]> => {
  const response = await api.get(`/surveys/${surveyId}/responses`);
  return response.data.data;
};

export const getAdminSurveyQuestions = async (surveyId: number): Promise<any[]> => {
  const response = await api.get(`/surveys/${surveyId}/questions`);
  return response.data.data;
};

