import { api } from './api';

export interface ReportTheme {
  id: number;
  theme: string;
  type: 'POSITIVE' | 'NEGATIVE';
}

export interface ReportRecommendation {
  id: number;
  recommendation: string;
}

export interface ReportExport {
  id: number;
  pdfPath: string | null;
  csvPath: string | null;
  generatedAt: string;
}

export interface ReportQuestionAnalysis {
  id: number;
  questionText: string;
  positivePercentage: number;
  neutralPercentage: number;
  negativePercentage: number;
  summary: string;
}

export interface Report {
  id: number;
  surveyId: number;
  region: string;
  month: number;
  year: number;
  overallScore: number;
  positivePercentage: number;
  neutralPercentage: number;
  negativePercentage: number;
  participationRate: number;
  executiveSummary: string;
  generatedAt: string;
  themes: ReportTheme[];
  recommendations: ReportRecommendation[];
  questionAnalysis?: ReportQuestionAnalysis[];
  export: ReportExport | null;
}

export const getRegionReports = async (): Promise<Report[]> => {
  const response = await api.get('/reports/my-region');
  return response.data; // Note: The backend response here returns List<Report> directly, not wrapped in ApiResponse! Let's handle both.
};

export const getRegionalReports = getRegionReports;

// If backend uses direct List, response.data is the array. If it uses ApiResponse, response.data.data is the array.
// From ReportController.java: return ResponseEntity.ok(reportRepository.findByRegion(region));
// So it's direct!

export const downloadReportPdf = async (id: number) => {
  const response = await api.get(`/reports/${id}/pdf`, { responseType: 'blob' });
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `report_${id}.pdf`);
  document.body.appendChild(link);
  link.click();
  link.parentNode?.removeChild(link);
  window.URL.revokeObjectURL(url);
};

export const downloadReportCsv = async (id: number) => {
  const response = await api.get(`/reports/${id}/csv`, { responseType: 'blob' });
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `report_${id}.csv`);
  document.body.appendChild(link);
  link.click();
  link.parentNode?.removeChild(link);
  window.URL.revokeObjectURL(url);
};
