import React from 'react';
import { Grid, Paper, Typography, Box, Card, CardContent } from '@mui/material';
import { useTheme, alpha } from '@mui/material/styles';
import { useQuery } from '@tanstack/react-query';
import { Clock, CheckCircle, PieChart as PieChartIcon } from 'lucide-react';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import { getSurveysByStatus } from '../../../services/survey.service';

export const EmployeeDashboard: React.FC = () => {
  const theme = useTheme();

  const { data: pendingSurveys } = useQuery({
    queryKey: ['surveys', 'PENDING'],
    queryFn: () => getSurveysByStatus('PENDING'),
  });

  const { data: completedSurveys } = useQuery({
    queryKey: ['surveys', 'COMPLETED'],
    queryFn: () => getSurveysByStatus('COMPLETED'),
  });

  const pendingCount = pendingSurveys?.length || 0;
  const completedCount = completedSurveys?.length || 0;
  const totalCount = pendingCount + completedCount;

  const chartData = [
    { name: 'Completed', value: completedCount },
    { name: 'Pending', value: pendingCount }
  ];

  const COLORS = [theme.palette.success.main, theme.palette.warning.main];

  return (
    <Box sx={{ maxWidth: 1200, mx: 'auto' }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" sx={{ fontWeight: 700, color: 'text.primary' }}>
          My Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Overview of your survey participation
        </Typography>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper 
            sx={{ 
              p: 3, 
              display: 'flex',
              alignItems: 'center',
              gap: 3,
              borderRadius: 2,
              border: '1px solid',
              borderColor: 'divider',
              bgcolor: alpha(theme.palette.warning.main, 0.05)
            }}
          >
            <Box sx={{ p: 1.5, borderRadius: '50%', bgcolor: alpha(theme.palette.warning.main, 0.1) }}>
              <Clock size={28} color={theme.palette.warning.main} />
            </Box>
            <Box>
              <Typography variant="h4" sx={{ fontWeight: 'bold', color: theme.palette.warning.main }}>
                {pendingCount}
              </Typography>
              <Typography variant="subtitle1" color="text.secondary" sx={{ fontWeight: 500 }}>Pending Surveys</Typography>
            </Box>
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Paper 
            sx={{ 
              p: 3, 
              display: 'flex',
              alignItems: 'center',
              gap: 3,
              borderRadius: 2,
              border: '1px solid',
              borderColor: 'divider',
              bgcolor: alpha(theme.palette.success.main, 0.05)
            }}
          >
            <Box sx={{ p: 1.5, borderRadius: '50%', bgcolor: alpha(theme.palette.success.main, 0.1) }}>
              <CheckCircle size={28} color={theme.palette.success.main} />
            </Box>
            <Box>
              <Typography variant="h4" sx={{ fontWeight: 'bold', color: theme.palette.success.main }}>
                {completedCount}
              </Typography>
              <Typography variant="subtitle1" color="text.secondary" sx={{ fontWeight: 500 }}>Completed Surveys</Typography>
            </Box>
          </Paper>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Card 
            sx={{ 
              borderRadius: 2,
              border: '1px solid',
              borderColor: 'divider',
              boxShadow: 'none'
            }}
          >
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 3 }}>
                <PieChartIcon size={20} color={theme.palette.primary.main} />
                <Typography variant="h6">Participation Overview</Typography>
              </Box>
              
              {totalCount > 0 ? (
                <Box sx={{ height: 300 }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie
                        data={chartData}
                        cx="50%"
                        cy="50%"
                        innerRadius={60}
                        outerRadius={100}
                        paddingAngle={5}
                        dataKey="value"
                      >
                        {chartData.map((entry, index) => (
                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip />
                      <Legend />
                    </PieChart>
                  </ResponsiveContainer>
                </Box>
              ) : (
                <Box sx={{ height: 300, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Typography color="text.secondary">No survey data available.</Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};
