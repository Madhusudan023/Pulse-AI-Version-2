import React, { useEffect, useState } from 'react';
import { 
  Box, 
  Typography, 
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Button
} from '@mui/material';
import { getRegionSurveys, getAdminSurveyResponses, getAdminSurveyQuestions } from '../../services/survey.service';
import type { Survey, FullSurveyResponseDTO } from '../../services/survey.service';
import { EmployeeAnswersModal } from '../../components/surveys/EmployeeAnswersModal';
import { Eye, Clock } from 'lucide-react';
import { alpha, useTheme } from '@mui/material/styles';

export const SurveyResponsesPage = () => {
  const theme = useTheme();
  const [surveys, setSurveys] = useState<Survey[]>([]);
  const [selectedSurveyId, setSelectedSurveyId] = useState<number | ''>('');
  const [responses, setResponses] = useState<FullSurveyResponseDTO[]>([]);
  const [questions, setQuestions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedResponse, setSelectedResponse] = useState<FullSurveyResponseDTO | null>(null);

  useEffect(() => {
    loadSurveys();
  }, []);

  const loadSurveys = async () => {
    try {
      const data = await getRegionSurveys('ALL');
      setSurveys(data);
    } catch (err) {
      console.error('Failed to load surveys', err);
    }
  };

  useEffect(() => {
    if (selectedSurveyId !== '') {
      loadResponses(selectedSurveyId);
    }
  }, [selectedSurveyId]);

  const loadResponses = async (surveyId: number) => {
    setLoading(true);
    try {
      const [resData, qData] = await Promise.all([
        getAdminSurveyResponses(surveyId),
        getAdminSurveyQuestions(surveyId)
      ]);
      setResponses(resData || []);
      setQuestions(qData || []);
    } catch (err) {
      console.error('Failed to load responses', err);
      setResponses([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 3, maxWidth: 1200, mx: 'auto' }}>
      <Typography variant="h4" sx={{ mb: 4, fontWeight: 700 }}>Survey Responses</Typography>
      
      <Paper elevation={0} sx={{ p: 3, mb: 4, border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
        <Typography variant="subtitle1" sx={{ mb: 2, fontWeight: 600 }}>Select a Survey</Typography>
        <FormControl fullWidth sx={{ maxWidth: 400 }}>
          <InputLabel>Survey</InputLabel>
          <Select
            value={selectedSurveyId}
            label="Survey"
            onChange={(e) => setSelectedSurveyId(e.target.value as number)}
          >
            <MenuItem value=""><em>Select Survey</em></MenuItem>
            {surveys.map(s => (
              <MenuItem key={s.id} value={s.id}>
                {s.title} ({s.status}) {s.isAnonymous ? ' - Anonymous' : ''}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Paper>

      {selectedSurveyId !== '' && (
        <TableContainer component={Paper} elevation={0} sx={{ border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: alpha(theme.palette.primary.main, 0.05) }}>
                <TableCell sx={{ fontWeight: 600 }}>Employee Email</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Submitted At</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Duration</TableCell>
                <TableCell align="right" sx={{ fontWeight: 600 }}>Action</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={4} align="center" sx={{ py: 4 }}>Loading responses...</TableCell>
                </TableRow>
              ) : responses.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                    No non-anonymous responses found for this survey.
                  </TableCell>
                </TableRow>
              ) : (
                responses.map((dto) => (
                  <TableRow key={dto.response.id} hover>
                    <TableCell sx={{ fontWeight: 500 }}>{dto.response.employeeEmail}</TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'text.secondary' }}>
                        <Clock size={14} />
                        {new Date(dto.response.submittedAt).toLocaleString()}
                      </Box>
                    </TableCell>
                    <TableCell>{dto.response.responseDuration}</TableCell>
                    <TableCell align="right">
                      <Button
                        variant="outlined"
                        size="small"
                        startIcon={<Eye size={16} />}
                        onClick={() => setSelectedResponse(dto)}
                      >
                        View Answers
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      <EmployeeAnswersModal
        isOpen={!!selectedResponse}
        onClose={() => setSelectedResponse(null)}
        responseDto={selectedResponse}
        questions={questions}
      />
    </Box>
  );
};
