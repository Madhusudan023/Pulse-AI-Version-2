import { Routes, Route, Navigate } from 'react-router-dom';
import { PublicLayout } from '../layouts/PublicLayout';
import { PrivateLayout } from '../layouts/PrivateLayout';
import { PublicRoute } from './PublicRoute';
import { ProtectedRoute } from './ProtectedRoute';
import { Login } from '../pages/Login';

import { DashboardRouter } from '../pages/dashboards/DashboardRouter';
import { RegionalHrList } from '../pages/dashboards/global/RegionalHrList';
import { TakeSurvey } from '../pages/surveys/TakeSurvey';
import { QuestionBank } from '../pages/questions/QuestionBank';
import { ManageSurveys } from '../pages/surveys/manage/ManageSurveys';
import { MyRegion } from '../pages/employees/MyRegion';
import { Reports } from '../pages/reports/Reports';
import { SurveyResponsesPage } from '../pages/admin/SurveyResponsesPage';

export const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route element={<PublicRoute />}>
        <Route element={<PublicLayout />}>
          <Route path="/login" element={<Login />} />
        </Route>
      </Route>

      {/* Protected Routes */}
      <Route element={<ProtectedRoute />}>
        <Route element={<PrivateLayout />}>
          <Route path="/" element={<DashboardRouter />} />
          <Route path="/regional-hrs" element={<RegionalHrList />} />
          <Route path="/surveys/:id/take" element={<TakeSurvey />} />
          <Route path="/questions" element={<QuestionBank />} />
          <Route path="/surveys" element={<ManageSurveys />} />
          <Route path="/employees" element={<MyRegion />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="/responses" element={<SurveyResponsesPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Route>
    </Routes>
  );
};
