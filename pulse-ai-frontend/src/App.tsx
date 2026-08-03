import { BrowserRouter } from 'react-router-dom';
import { AppRoutes } from './routes/AppRoutes';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from './theme/theme';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <AppRoutes />
        <ToastContainer 
          position="bottom-right"
          theme="dark"
          toastClassName="glass-card !bg-[#0A0A0A]/90 backdrop-blur-md border border-white/10"
        />
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
