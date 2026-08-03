import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { 
  Box, 
  Container, 
  Typography, 
  Button, 
  Grid, 
  Paper,
  CircularProgress,
  Chip,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import { 
  PieChart, 
  RefreshCw, 
  AlertCircle, 
  FileText, 
  Download, 
  Target
} from 'lucide-react';
import { getRegionReports, downloadReportPdf, downloadReportCsv, type Report } from '../../services/report.service';
import { ReportDetailsModal } from '../../components/reports/ReportDetailsModal';
import { alpha } from '@mui/material/styles';
import { useAuthStore } from '../../store/useAuthStore';

export const Reports = () => {
  const [selectedReport, setSelectedReport] = useState<Report | null>(null);
  const role = useAuthStore(state => state.role)?.replace('ROLE_', '');
  const [selectedRegion, setSelectedRegion] = useState('ALL');
  const [selectedMonth, setSelectedMonth] = useState('ALL');
  const [selectedYear, setSelectedYear] = useState('ALL');

  const { data: reports, isLoading, isError, refetch } = useQuery({
    queryKey: ['reports'],
    queryFn: getRegionReports,
  });

  const filteredReports = reports ? reports.filter(report => {
    let regionMatch = true;
    if (role === 'GLOBAL_HR' && selectedRegion !== 'ALL') {
      regionMatch = report.region === selectedRegion;
    }
    let monthMatch = true;
    if (selectedMonth !== 'ALL') {
      monthMatch = report.month.toString() === selectedMonth;
    }
    let yearMatch = true;
    if (selectedYear !== 'ALL') {
      yearMatch = report.year.toString() === selectedYear;
    }
    return regionMatch && monthMatch && yearMatch;
  }) : [];

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      {/* Page Header */}
      <Box sx={{ display: 'flex', flexDirection: 'column', mb: 4, gap: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ display: 'flex', alignItems: 'center', gap: 1.5, color: 'text.primary' }}>
            <PieChart size={32} color="#4338CA" />
            Reports & Sentiment Analysis
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
            Review AI-generated sentiment insights and trends from your region's completed surveys.
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', flexWrap: 'wrap' }}>
          {role === 'GLOBAL_HR' && (
            <FormControl size="small" sx={{ minWidth: 120 }}>
              <InputLabel>Region</InputLabel>
              <Select value={selectedRegion} label="Region" onChange={(e) => setSelectedRegion(e.target.value)}>
                <MenuItem value="ALL">All Regions</MenuItem>
                <MenuItem value="CHENNAI">Chennai</MenuItem>
                <MenuItem value="HYDERABAD">Hyderabad</MenuItem>
                <MenuItem value="PUNE">Pune</MenuItem>
                <MenuItem value="BANGALORE">Bangalore</MenuItem>
                <MenuItem value="NEW_YORK">New York</MenuItem>
                <MenuItem value="LONDON">London</MenuItem>
                <MenuItem value="SINGAPORE">Singapore</MenuItem>
              </Select>
            </FormControl>
          )}
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Month</InputLabel>
            <Select value={selectedMonth} label="Month" onChange={(e) => setSelectedMonth(e.target.value)}>
              <MenuItem value="ALL">All Months</MenuItem>
              <MenuItem value="1">January</MenuItem>
              <MenuItem value="2">February</MenuItem>
              <MenuItem value="3">March</MenuItem>
              <MenuItem value="4">April</MenuItem>
              <MenuItem value="5">May</MenuItem>
              <MenuItem value="6">June</MenuItem>
              <MenuItem value="7">July</MenuItem>
              <MenuItem value="8">August</MenuItem>
              <MenuItem value="9">September</MenuItem>
              <MenuItem value="10">October</MenuItem>
              <MenuItem value="11">November</MenuItem>
              <MenuItem value="12">December</MenuItem>
            </Select>
          </FormControl>
          <FormControl size="small" sx={{ minWidth: 100 }}>
            <InputLabel>Year</InputLabel>
            <Select value={selectedYear} label="Year" onChange={(e) => setSelectedYear(e.target.value)}>
              <MenuItem value="ALL">All Years</MenuItem>
              <MenuItem value="2026">2026</MenuItem>
              <MenuItem value="2025">2025</MenuItem>
              <MenuItem value="2024">2024</MenuItem>
            </Select>
          </FormControl>
          <Button 
            variant="outlined" 
            color="inherit" 
            onClick={() => refetch()} 
            startIcon={<RefreshCw size={18} className={isLoading ? 'animate-spin' : ''} />}
            sx={{ borderColor: 'divider', color: 'text.secondary', height: 40 }}
          >
            Refresh
          </Button>
        </Box>
      </Box>

      {/* Content Area */}
      <Box sx={{ minHeight: 400 }}>
        {isLoading && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, gap: 2 }}>
            <CircularProgress color="primary" />
            <Typography color="text.secondary">Analyzing survey reports...</Typography>
          </Box>
        )}

        {isError && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, gap: 2, color: 'error.main' }}>
            <AlertCircle size={48} />
            <Typography variant="h6">Failed to load reports.</Typography>
          </Box>
        )}

        {!isLoading && !isError && (!filteredReports || filteredReports.length === 0) && (
          <Paper 
            elevation={0}
            sx={{ 
              display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', 
              height: 250, border: '1px dashed', borderColor: 'divider', bgcolor: 'transparent', gap: 2 
            }}
          >
            <PieChart size={48} color="#64748B" opacity={0.5} />
            <Typography color="text.secondary" variant="h6">No AI reports have been generated yet for your region.</Typography>
          </Paper>
        )}

        {!isLoading && !isError && filteredReports && filteredReports.length > 0 && (
          <Grid container spacing={3}>
            {filteredReports.map(report => (
              <Grid size={{ xs: 12, md: 6 }} key={report.id}>
                <Paper 
                  sx={{ 
                    p: 3, 
                    display: 'flex', 
                    flexDirection: 'column', 
                    height: '100%',
                    transition: 'border-color 0.2s',
                    border: '1px solid',
                    borderColor: 'divider',
                    '&:hover': {
                      borderColor: 'primary.light',
                    }
                  }}
                >
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
                    <Box>
                      <Box sx={{ display: 'flex', gap: 1, mb: 1, flexWrap: 'wrap' }}>
                        <Chip label={report.region} size="small" color="primary" sx={{ bgcolor: alpha('#4338CA', 0.1), color: 'primary.main', fontWeight: 600 }} />
                        <Chip 
                          label={new Date(report.year, report.month - 1).toLocaleString('default', { month: 'long', year: 'numeric' })} 
                          size="small" 
                          variant="outlined" 
                        />
                      </Box>
                      <Typography variant="h6" color="text.primary" sx={{ lineHeight: 1.2 }}>
                        Sentiment Analysis Report
                      </Typography>
                      <Typography variant="caption" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
                        <Target size={14} /> Survey ID: {report.surveyId}
                      </Typography>
                    </Box>
                    <Box sx={{ textAlign: 'right' }}>
                      <Typography variant="h4" color="text.primary">{report.overallScore}</Typography>
                      <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>Overall Score</Typography>
                    </Box>
                  </Box>

                  {/* Sentiment Bar */}
                  <Box sx={{ display: 'flex', height: 12, borderRadius: 6, overflow: 'hidden', mb: 2, bgcolor: 'divider' }}>
                    <Box sx={{ bgcolor: 'success.main', width: `${report.positivePercentage}%` }} title={`Positive: ${report.positivePercentage}%`} />
                    <Box sx={{ bgcolor: 'warning.main', width: `${report.neutralPercentage}%` }} title={`Neutral: ${report.neutralPercentage}%`} />
                    <Box sx={{ bgcolor: 'error.main', width: `${report.negativePercentage}%` }} title={`Negative: ${report.negativePercentage}%`} />
                  </Box>

                  {/* Sentiment Metrics */}
                  <Grid container spacing={2} sx={{ textAlign: 'center', mb: 3 }}>
                    <Grid size={{ xs: 4 }}>
                      <Typography variant="h6" color="success.main" sx={{ fontWeight: 700 }}>{report.positivePercentage}%</Typography>
                      <Typography variant="caption" color="text.secondary" sx={{ textTransform: 'uppercase', letterSpacing: 1, fontSize: '0.65rem' }}>Positive</Typography>
                    </Grid>
                    <Grid size={{ xs: 4 }}>
                      <Typography variant="h6" color="warning.main" sx={{ fontWeight: 700 }}>{report.neutralPercentage}%</Typography>
                      <Typography variant="caption" color="text.secondary" sx={{ textTransform: 'uppercase', letterSpacing: 1, fontSize: '0.65rem' }}>Neutral</Typography>
                    </Grid>
                    <Grid size={{ xs: 4 }}>
                      <Typography variant="h6" color="error.main" sx={{ fontWeight: 700 }}>{report.negativePercentage}%</Typography>
                      <Typography variant="caption" color="text.secondary" sx={{ textTransform: 'uppercase', letterSpacing: 1, fontSize: '0.65rem' }}>Negative</Typography>
                    </Grid>
                  </Grid>

                  {/* Actions */}
                  <Box sx={{ mt: 'auto', pt: 2, borderTop: '1px solid', borderColor: 'divider', display: 'flex', gap: 1.5, flexWrap: 'wrap' }}>
                    <Button 
                      variant="contained" 
                      color="primary" 
                      fullWidth 
                      startIcon={<FileText size={18} />}
                      onClick={() => setSelectedReport(report)}
                      sx={{ flex: 1, minWidth: 120 }}
                    >
                      View Details
                    </Button>
                    <Button 
                      variant="outlined" 
                      color="inherit"
                      onClick={() => downloadReportPdf(report.id)}
                      startIcon={<Download size={18} />}
                      sx={{ borderColor: 'divider' }}
                      title="Download PDF"
                    >
                      PDF
                    </Button>
                    <Button 
                      variant="outlined" 
                      color="inherit"
                      onClick={() => downloadReportCsv(report.id)}
                      startIcon={<Download size={18} />}
                      sx={{ borderColor: 'divider' }}
                      title="Download CSV"
                    >
                      CSV
                    </Button>
                  </Box>
                </Paper>
              </Grid>
            ))}
          </Grid>
        )}
      </Box>

      <ReportDetailsModal
        isOpen={selectedReport !== null}
        onClose={() => setSelectedReport(null)}
        report={selectedReport}
        onDownloadPdf={downloadReportPdf}
        onDownloadCsv={downloadReportCsv}
      />
    </Container>
  );
};
