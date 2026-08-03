import React from 'react';
import { Grid, Paper, Typography, Box, Button, Chip, CircularProgress, Divider } from '@mui/material';
import { useTheme, alpha } from '@mui/material/styles';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { FileText, CheckCircle, Clock, Calendar, AlertCircle, PlayCircle } from 'lucide-react';
import { getSurveysByStatus } from '../../../services/survey.service';

export const EmployeeSurveys: React.FC = () => {
  const theme = useTheme();
  const navigate = useNavigate();

  const { data: pendingSurveys, isLoading: isPendingLoading, isError: isPendingError } = useQuery({
    queryKey: ['surveys', 'PENDING'],
    queryFn: () => getSurveysByStatus('PENDING'),
  });

  const { data: completedSurveys, isLoading: isCompletedLoading, isError: isCompletedError } = useQuery({
    queryKey: ['surveys', 'COMPLETED'],
    queryFn: () => getSurveysByStatus('COMPLETED'),
  });

  const renderSurveyCard = (survey: any, isCompleted: boolean) => (
    <Grid item xs={12} md={6} lg={4} key={survey.id}>
      <Paper 
        sx={{ 
          p: 3, 
          height: '100%', 
          display: 'flex', 
          flexDirection: 'column',
          transition: 'all 0.2s ease-in-out',
          border: '1px solid',
          borderColor: 'divider',
          '&:hover': {
            borderColor: isCompleted ? 'success.light' : 'primary.light',
            transform: 'translateY(-2px)',
            boxShadow: theme.shadows[4]
          }
        }}
      >
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
          <Chip 
            label={survey.surveyType} 
            size="small" 
            sx={{ 
              bgcolor: alpha(isCompleted ? theme.palette.success.main : theme.palette.primary.main, 0.1),
              color: isCompleted ? 'success.main' : 'primary.main',
              fontWeight: 600,
              fontSize: '0.7rem'
            }} 
          />
          <Chip 
            label={isCompleted ? 'Completed' : 'Pending'} 
            size="small" 
            color={isCompleted ? 'success' : 'warning'}
            variant={isCompleted ? 'filled' : 'outlined'}
            icon={isCompleted ? <CheckCircle size={14} /> : <Clock size={14} />}
            sx={{ fontWeight: 500 }}
          />
        </Box>

        <Typography variant="h6" sx={{ mb: 1, fontWeight: 'bold', lineHeight: 1.2 }}>
          {survey.title}
        </Typography>
        
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3, flexGrow: 1, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
          {survey.description}
        </Typography>

        <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'text.secondary' }}>
            <Calendar size={14} />
            <Typography variant="caption">Ends: {new Date(survey.endDate).toLocaleDateString()}</Typography>
          </Box>
        </Box>

        <Divider sx={{ mb: 2, mx: -3 }} />

        <Box sx={{ mt: 'auto', display: 'flex', justifyContent: 'flex-end' }}>
          {!isCompleted ? (
            <Button 
              variant="contained" 
              color="primary" 
              fullWidth
              startIcon={<PlayCircle size={18} />}
              onClick={() => navigate(`/surveys/${survey.id}/take`)}
            >
              Take Survey
            </Button>
          ) : (
            <Button 
              variant="outlined" 
              color="inherit" 
              fullWidth
              disabled
              startIcon={<CheckCircle size={18} />}
              sx={{ borderColor: 'divider' }}
            >
              Response Submitted
            </Button>
          )}
        </Box>
      </Paper>
    </Grid>
  );

  return (
    <Box sx={{ p: { xs: 2, md: 4 }, maxWidth: 1200, mx: 'auto' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <FileText size={32} color={theme.palette.primary.main} />
        <Box>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 700, color: 'text.primary' }}>
            My Surveys
          </Typography>
          <Typography variant="body1" color="text.secondary">
            View and complete your assigned pulse surveys
          </Typography>
        </Box>
      </Box>

      {/* Pending Surveys Section */}
      <Box sx={{ mb: 6 }}>
        <Typography variant="h6" sx={{ mb: 3, display: 'flex', alignItems: 'center', gap: 1, fontWeight: 600 }}>
          <Clock size={20} color={theme.palette.warning.main} /> Pending Action
        </Typography>

        {isPendingLoading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
            <CircularProgress />
          </Box>
        )}

        {isPendingError && (
          <Paper sx={{ p: 4, display: 'flex', alignItems: 'center', gap: 2, color: 'error.main', bgcolor: alpha(theme.palette.error.main, 0.05) }}>
            <AlertCircle />
            <Typography>Failed to load pending surveys.</Typography>
          </Paper>
        )}

        {!isPendingLoading && !isPendingError && (!pendingSurveys || pendingSurveys.length === 0) && (
          <Paper sx={{ p: 6, textAlign: 'center', border: '1px dashed', borderColor: 'divider', bgcolor: 'transparent' }}>
            <CheckCircle size={48} color={theme.palette.success.main} style={{ margin: '0 auto', opacity: 0.5 }} />
            <Typography variant="h6" sx={{ mt: 2, color: 'text.secondary' }}>You're all caught up!</Typography>
            <Typography variant="body2" color="text.secondary">No pending surveys require your attention at this time.</Typography>
          </Paper>
        )}

        {pendingSurveys && pendingSurveys.length > 0 && (
          <Grid container spacing={3}>
            {pendingSurveys.map(survey => renderSurveyCard(survey, false))}
          </Grid>
        )}
      </Box>

      {/* Completed Surveys Section */}
      <Box>
        <Typography variant="h6" sx={{ mb: 3, display: 'flex', alignItems: 'center', gap: 1, fontWeight: 600 }}>
          <CheckCircle size={20} color={theme.palette.success.main} /> Completed Surveys
        </Typography>

        {isCompletedLoading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
            <CircularProgress />
          </Box>
        )}

        {isCompletedError && (
          <Paper sx={{ p: 4, display: 'flex', alignItems: 'center', gap: 2, color: 'error.main', bgcolor: alpha(theme.palette.error.main, 0.05) }}>
            <AlertCircle />
            <Typography>Failed to load completed surveys.</Typography>
          </Paper>
        )}

        {!isCompletedLoading && !isCompletedError && (!completedSurveys || completedSurveys.length === 0) && (
          <Paper sx={{ p: 6, textAlign: 'center', border: '1px dashed', borderColor: 'divider', bgcolor: 'transparent' }}>
            <FileText size={48} color={theme.palette.text.secondary} style={{ margin: '0 auto', opacity: 0.3 }} />
            <Typography variant="h6" sx={{ mt: 2, color: 'text.secondary' }}>No completed surveys yet</Typography>
            <Typography variant="body2" color="text.secondary">Your completed surveys will appear here.</Typography>
          </Paper>
        )}

        {completedSurveys && completedSurveys.length > 0 && (
          <Grid container spacing={3}>
            {completedSurveys.map(survey => renderSurveyCard(survey, true))}
          </Grid>
        )}
      </Box>
    </Box>
  );
};
