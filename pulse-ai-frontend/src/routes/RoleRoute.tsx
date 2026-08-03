import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';

interface RoleRouteProps {
  roles: string[];
}

export const RoleRoute = ({ roles }: RoleRouteProps) => {
  const userRole = useAuthStore((state) => state.role);

  if (!userRole || !roles.includes(userRole)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
};
