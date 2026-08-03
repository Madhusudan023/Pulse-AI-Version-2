import { useAuthStore } from '../../store/useAuthStore';
import { EmployeeDashboard } from './employee/EmployeeDashboard';
import { RegionalDashboard } from './regional/RegionalDashboard';
import { GlobalDashboard } from './global/GlobalDashboard';
import { VPDashboard } from './vp/VPDashboard';
import { Navigate } from 'react-router-dom';

export const DashboardRouter = () => {
  const role = useAuthStore((state) => state.role);

  switch (role?.replace('ROLE_', '')) {
    case 'EMPLOYEE':
      return <EmployeeDashboard />;
    case 'REGIONAL_HR':
      return <RegionalDashboard />;
    case 'GLOBAL_HR':
      return <GlobalDashboard />;
    case 'VP':
      return <VPDashboard />;
    default:
      return <Navigate to="/login" replace />;
  }
};
