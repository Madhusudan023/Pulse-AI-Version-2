import React from 'react';
import { Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Box, useTheme, useMediaQuery, Typography, Divider, Toolbar } from '@mui/material';
import { alpha } from '@mui/material/styles';
import { Home, User, HelpCircle, FileText, Users, BarChart, LogOut } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router-dom';
import { authService } from '../../services/auth.service';
import { ProfileModal } from './ProfileModal';
import { useAuthStore } from '../../store/useAuthStore';

interface SidebarProps {
  mobileOpen: boolean;
  onClose: () => void;
  drawerWidth: number;
}

const getAllMenuItems = (role: string | undefined) => {
  const normalizedRole = role?.replace('ROLE_', '');
  const baseItems = [
    { text: 'Dashboard', icon: <Home size={20} />, path: '/' },
    { text: 'Surveys', icon: <FileText size={20} />, path: '/surveys' },
  ];
  
  if (normalizedRole === 'EMPLOYEE') {
    return baseItems;
  }
  
  return [
    ...baseItems,
    { text: 'Questions', icon: <HelpCircle size={20} />, path: '/questions' },
    { text: 'Employees', icon: <Users size={20} />, path: '/employees' },
    { text: 'Survey Responses', icon: <FileText size={20} />, path: '/responses' },
    { text: 'Reports', icon: <BarChart size={20} />, path: '/reports' },
  ];
};

export const Sidebar: React.FC<SidebarProps> = ({ mobileOpen, onClose, drawerWidth }) => {
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up('md'));
  const navigate = useNavigate();
  const location = useLocation();
  const clearAuth = useAuthStore((state) => state.clearAuth);

  const [isProfileModalOpen, setIsProfileModalOpen] = React.useState(false);
  const [currentUser, setCurrentUser] = React.useState<any>(null);

  const handleProfileClick = async () => {
    try {
      const me = await authService.getMe();
      setCurrentUser(me);
      setIsProfileModalOpen(true);
    } catch (error) {
      console.error("Failed to get current user:", error);
    }
  };

  const handleLogout = () => {
    clearAuth();
    navigate('/login');
  };

  const drawerContent = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column', bgcolor: 'background.paper', borderRight: 1, borderColor: 'divider' }}>
      <Toolbar sx={{ minHeight: '70px !important' }} />
      {/* Main Navigation */}
      <List sx={{ px: 2, flexGrow: 1, mt: 2 }}>
        <Typography variant="overline" sx={{ px: 2, color: 'text.secondary', fontWeight: 600, letterSpacing: '1px' }}>
          Overview
        </Typography>
        {getAllMenuItems(useAuthStore((state) => state.role)).map((item) => {
          const isSelected = location.pathname === item.path || (item.path !== '/' && location.pathname.startsWith(item.path));
          return (
            <ListItem key={item.text} disablePadding sx={{ mb: 0.5 }}>
              <ListItemButton 
                selected={isSelected}
                onClick={() => {
                  navigate(item.path);
                  if (!isDesktop) onClose();
                }}
                sx={{
                  borderRadius: 2,
                  py: 1.25,
                  px: 2,
                  transition: 'all 0.2s',
                  '&.Mui-selected': {
                    bgcolor: alpha(theme.palette.primary.main, 0.1),
                    color: 'primary.main',
                    '&:hover': {
                      bgcolor: alpha(theme.palette.primary.main, 0.15),
                    }
                  },
                  '&:not(.Mui-selected)': {
                    color: 'text.secondary',
                    '&:hover': {
                      bgcolor: alpha(theme.palette.action.hover, 0.05),
                      color: 'text.primary'
                    }
                  }
                }}
              >
                <ListItemIcon sx={{ 
                  minWidth: 40,
                  color: 'inherit'
                }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText 
                  primary={item.text} 
                  slotProps={{
                    primary: {
                      sx: { 
                        fontWeight: isSelected ? 600 : 500,
                        fontSize: '0.95rem'
                      }
                    }
                  }} 
                />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>

      {/* Footer Area (Profile / Settings) */}
      <Box sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
        <List disablePadding>
          <ListItem disablePadding sx={{ mb: 0.5 }}>
            <ListItemButton 
              onClick={handleProfileClick}
              sx={{
                borderRadius: 2,
                py: 1,
                px: 2,
                color: 'text.secondary',
                '&:hover': {
                  bgcolor: alpha(theme.palette.action.hover, 0.05),
                  color: 'text.primary'
                }
              }}
            >
              <ListItemIcon sx={{ minWidth: 40, color: 'inherit' }}>
                <User size={20} />
              </ListItemIcon>
              <ListItemText primary="My Profile" slotProps={{ primary: { sx: { fontWeight: 500, fontSize: '0.95rem' } } }} />
            </ListItemButton>
          </ListItem>
          
          <ListItem disablePadding>
            <ListItemButton 
              onClick={handleLogout}
              sx={{
                borderRadius: 2,
                py: 1,
                px: 2,
                color: 'error.main',
                '&:hover': {
                  bgcolor: alpha(theme.palette.error.main, 0.05),
                }
              }}
            >
              <ListItemIcon sx={{ minWidth: 40, color: 'inherit' }}>
                <LogOut size={20} />
              </ListItemIcon>
              <ListItemText primary="Logout" slotProps={{ primary: { sx: { fontWeight: 500, fontSize: '0.95rem' } } }} />
            </ListItemButton>
          </ListItem>
        </List>
      </Box>
    </Box>
  );

  return (
    <>
      <Box component="nav" sx={{ width: { md: drawerWidth }, flexShrink: { md: 0 } }}>
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={onClose}
          ModalProps={{ keepMounted: true }}
          sx={{
            display: { xs: 'block', md: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, borderRight: 'none' },
          }}
        >
          {drawerContent}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', md: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, borderRight: 'none' },
          }}
          open
        >
          {drawerContent}
        </Drawer>
      </Box>
      <ProfileModal isOpen={isProfileModalOpen} onClose={() => setIsProfileModalOpen(false)} user={currentUser} />
    </>
  );
};
