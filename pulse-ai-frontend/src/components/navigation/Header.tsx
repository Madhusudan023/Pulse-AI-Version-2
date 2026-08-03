import React from 'react';
import { AppBar, Toolbar, Typography, IconButton, Box, InputBase } from '@mui/material';
import { alpha, useTheme } from '@mui/material/styles';
import { Menu, Bell, Search, Settings, Hexagon } from 'lucide-react';
import { useLocation } from 'react-router-dom';
import { NotificationMenu } from './NotificationMenu';

interface HeaderProps {
  onMenuClick: () => void;
  drawerWidth: number;
}

const getPageTitle = (pathname: string) => {
  if (pathname === '/') return 'Dashboard Overview';
  if (pathname.startsWith('/surveys')) return 'Surveys Management';
  if (pathname.startsWith('/questions')) return 'Question Bank';
  if (pathname.startsWith('/employees')) return 'Employee Directory';
  if (pathname.startsWith('/reports')) return 'Analytics & Reports';
  return 'App Dashboard';
};

export const Header: React.FC<HeaderProps> = ({ onMenuClick, drawerWidth }) => {
  const theme = useTheme();
  const location = useLocation();
  const title = getPageTitle(location.pathname);

  return (
    <AppBar
      position="fixed"
      sx={{
        zIndex: theme.zIndex.drawer + 1,
        bgcolor: alpha(theme.palette.background.paper, 0.8),
        backdropFilter: 'blur(12px)',
        color: theme.palette.text.primary,
        boxShadow: 'none',
        borderBottom: `1px solid ${theme.palette.divider}`,
      }}
    >
      <Toolbar sx={{ minHeight: '70px !important' }}>
        <IconButton
          color="inherit"
          aria-label="open drawer"
          edge="start"
          onClick={onMenuClick}
          sx={{ mr: 2, display: { md: 'none' } }}
        >
          <Menu />
        </IconButton>

        {/* Logo / Brand Area (moved from Sidebar) */}
        <Box sx={{ width: { md: drawerWidth - 24 }, display: 'flex', alignItems: 'center', gap: 1.5, mr: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <img src="/Virtusa-Neural-Hackathon-2025-Software-Developer-Interview-Prep.webp" alt="Pulse AI" style={{ height: 28, width: 'auto', objectFit: 'contain' }} />
          </Box>
          <Typography variant="h6" sx={{ fontWeight: 700, color: 'text.primary', letterSpacing: '-0.5px' }}>
            Pulse<span style={{ color: theme.palette.primary.main }}>AI</span>
          </Typography>
        </Box>
        
        <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
          <Typography variant="h6" noWrap component="div" sx={{ fontWeight: 700, letterSpacing: '-0.5px' }}>
            {title}
          </Typography>
          <Typography variant="caption" color="text.secondary" sx={{ display: { xs: 'none', sm: 'block' } }}>
            Manage your workspace and view performance metrics.
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>

          
          <NotificationMenu />
        </Box>
      </Toolbar>
    </AppBar>
  );
};
