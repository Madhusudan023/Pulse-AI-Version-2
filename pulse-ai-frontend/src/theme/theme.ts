import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'light',
    background: {
      default: '#F8FAFC',
      paper: '#FFFFFF',
    },
    primary: {
      main: '#4338CA',
      light: '#6366F1',
      dark: '#312E81',
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: '#0D9488',
      light: '#14B8A6',
      dark: '#0F766E',
    },
    success: {
      main: '#10B981',
      light: '#D1FAE5',
    },
    warning: {
      main: '#F59E0B',
      light: '#FEF3C7',
    },
    error: {
      main: '#E11D48',
      light: '#FFE4E6',
    },
    text: {
      primary: '#0F172A',
      secondary: '#64748B',
    },
    divider: '#E2E8F0',
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h4: { fontWeight: 700, letterSpacing: '-0.02em' },
    h5: { fontWeight: 600, letterSpacing: '-0.01em' },
    h6: { fontWeight: 600 },
    button: { textTransform: 'none', fontWeight: 600 },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '8px 16px',
          boxShadow: 'none',
          '&:hover': {
            boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
          boxShadow: '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)',
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          fontWeight: 600,
          color: '#64748B',
          backgroundColor: '#F8FAFC',
          borderBottom: '2px solid #E2E8F0',
        },
      },
    },
  },
});

export default theme;
