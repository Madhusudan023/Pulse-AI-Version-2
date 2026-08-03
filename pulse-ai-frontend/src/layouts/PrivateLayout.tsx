import React, { useState } from 'react';
import { Box, Toolbar, CssBaseline } from '@mui/material';
import { Sidebar } from '../components/navigation/Sidebar';
import { Header } from '../components/navigation/Header';
import { Outlet } from 'react-router-dom';

const drawerWidth = 280;

export const PrivateLayout = () => {
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      <CssBaseline />
      
      <Header onMenuClick={handleDrawerToggle} drawerWidth={drawerWidth} />
      
      <Sidebar mobileOpen={mobileOpen} onClose={handleDrawerToggle} drawerWidth={drawerWidth} />
      
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: { xs: 2, md: 4 },
          width: { md: `calc(100% - ${drawerWidth}px)` },
        }}
      >
        <Toolbar /> {/* Spacer matching AppBar height */}
        <Outlet />
      </Box>
    </Box>
  );
};
