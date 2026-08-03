import React from 'react';
import { Grid, Paper, Typography, Box, Button, IconButton, Card, CardContent } from '@mui/material';
import { useTheme, alpha } from '@mui/material/styles';
import { Briefcase, TrendingUp, PieChart as PieChartIcon, MoreVertical } from 'lucide-react';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';

const vpData = [
  { name: 'Q1', target: 80, actual: 85 },
  { name: 'Q2', target: 90, actual: 88 },
  { name: 'Q3', target: 100, actual: 105 },
];

const pieData = [
  { name: 'Product A', value: 400 },
  { name: 'Product B', value: 300 },
  { name: 'Product C', value: 300 },
];
const COLORS = ['#0088FE', '#00C49F', '#FFBB28'];

export const VPDashboard: React.FC = () => {
  const theme = useTheme();
  
  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1" sx={{ fontWeight: "bold" }}>
          VP Overview
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button variant="contained" color="secondary" startIcon={<TrendingUp size={18} />}>
            Generate Report
          </Button>
        </Box>
      </Box>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper 
            sx={{ 
              p: 3, 
              bgcolor: alpha(theme.palette.background.paper, 0.8),
              backdropFilter: 'blur(10px)',
              borderRadius: 2
            }}
          >
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h6">Quarterly Targets</Typography>
              <IconButton size="small"><Briefcase size={18} /></IconButton>
            </Box>
            <Box sx={{ height: 300 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={vpData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="target" fill={alpha(theme.palette.primary.main, 0.5)} radius={[4, 4, 0, 0]} />
                  <Bar dataKey="actual" fill={theme.palette.primary.main} radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </Box>
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Card 
            sx={{ 
              bgcolor: alpha(theme.palette.background.paper, 0.8),
              backdropFilter: 'blur(10px)',
              borderRadius: 2
            }}
          >
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h6">Market Share</Typography>
                <IconButton size="small"><PieChartIcon size={18} /></IconButton>
              </Box>
              <Box sx={{ height: 300 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={pieData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={100}
                      fill="#8884d8"
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {pieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};
