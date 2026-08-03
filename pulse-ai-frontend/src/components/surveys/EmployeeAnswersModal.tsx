import React from 'react';
import { 
  Dialog, 
  DialogTitle, 
  DialogContent, 
  Box,
  Typography,
  IconButton,
  Paper,
  Divider,
  Rating
} from '@mui/material';
import { X, MessageSquare, Star, Mail, Clock } from 'lucide-react';
import { alpha, useTheme } from '@mui/material/styles';
import type { FullSurveyResponseDTO } from '../../services/survey.service';

interface EmployeeAnswersModalProps {
  isOpen: boolean;
  onClose: () => void;
  responseDto: FullSurveyResponseDTO | null;
  questions: any[]; // we will pass the full questions array here
}

export const EmployeeAnswersModal = ({ isOpen, onClose, responseDto, questions }: EmployeeAnswersModalProps) => {
  const theme = useTheme();
  
  if (!responseDto) return null;

  const { response, answers } = responseDto;

  // Helper to find the actual question text
  const getQuestionText = (questionId: number) => {
    const q = questions.find(q => q.questionId === questionId || q.id === questionId);
    return q?.text || q?.questionText || `Question ${questionId}`;
  };

  return (
    <Dialog 
      open={isOpen} 
      onClose={onClose}
      maxWidth="md"
      fullWidth
      slotProps={{ paper: {
        sx: {
          borderRadius: 3,
          backgroundColor: alpha(theme.palette.background.paper, 0.95),
          backdropFilter: 'blur(10px)',
          backgroundImage: 'none',
        }
      } }}
    >
      <DialogTitle sx={{ 
        m: 0, p: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
        borderBottom: '1px solid', borderColor: 'divider'
      }}>
        <Box>
          <Typography variant="h5" color="text.primary" sx={{ fontWeight: 700 }}>
            Employee Answers
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 3, mt: 1 }}>
            <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Mail size={16} /> {response.employeeEmail}
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Clock size={16} /> {new Date(response.submittedAt).toLocaleString()}
            </Typography>
          </Box>
        </Box>
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{ color: 'text.secondary' }}
        >
          <X />
        </IconButton>
      </DialogTitle>
      
      <DialogContent dividers sx={{ p: 3, backgroundColor: alpha(theme.palette.background.default, 0.5) }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          {answers.map((answer, idx) => (
            <Paper elevation={0} key={answer.id || idx} sx={{ p: 2.5, border: '1px solid', borderColor: 'divider', bgcolor: 'background.paper', borderRadius: 2 }}>
              <Typography variant="subtitle1" color="text.primary" sx={{ fontWeight: 600, mb: 2, display: 'flex', gap: 1.5 }}>
                <Box component="span" sx={{ color: 'text.secondary' }}>Q{idx + 1}.</Box>
                {getQuestionText(answer.questionId)}
              </Typography>
              
              {answer.ratingAnswer !== null && answer.ratingAnswer !== undefined && (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: answer.textAnswer ? 2 : 0 }}>
                  <Typography variant="body2" color="text.secondary" sx={{ minWidth: 60 }}>Rating:</Typography>
                  <Rating value={answer.ratingAnswer} readOnly size="large" sx={{ color: 'primary.main' }} />
                </Box>
              )}
              
              {answer.optionAnswer && (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: answer.textAnswer ? 2 : 0 }}>
                  <Typography variant="body2" color="text.secondary" sx={{ minWidth: 60 }}>Option:</Typography>
                  <Typography variant="body1" color="text.primary" sx={{ fontWeight: 500 }}>{answer.optionAnswer}</Typography>
                </Box>
              )}

              {answer.textAnswer && (
                <Box sx={{ mt: 1 }}>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>Written Response:</Typography>
                  <Box sx={{ p: 2, bgcolor: alpha(theme.palette.primary.main, 0.03), borderRadius: 1, border: '1px solid', borderColor: alpha(theme.palette.primary.main, 0.1) }}>
                    <Typography variant="body2" color="text.primary" sx={{ lineHeight: 1.7 }}>
                      {answer.textAnswer}
                    </Typography>
                  </Box>
                </Box>
              )}
            </Paper>
          ))}
          {answers.length === 0 && (
            <Typography variant="body1" color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
              No answers recorded for this submission.
            </Typography>
          )}
        </Box>
      </DialogContent>
    </Dialog>
  );
};
