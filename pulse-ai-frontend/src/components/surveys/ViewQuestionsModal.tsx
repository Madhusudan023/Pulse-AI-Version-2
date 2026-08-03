import React, { useState } from 'react';
import { X, Loader2, AlertCircle, Trash2 } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getSurveyQuestions, removeQuestionsFromSurvey } from '../../services/survey.service';
import { getQuestions } from '../../services/question.service';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Button, IconButton, Typography, Box, useTheme, Chip,
  CircularProgress, Tooltip
} from '@mui/material';
import { alpha } from '@mui/material/styles';
import { toast } from 'react-toastify';

interface ViewQuestionsModalProps {
  surveyId: number | null;
  isOpen: boolean;
  onClose: () => void;
  surveyTitle?: string;
  isDraft?: boolean;
}

const THEME_COLORS: Record<string, string> = {
  WORK_LIFE_BALANCE: '#7c4dff',
  LEADERSHIP:        '#0288d1',
  GROWTH:            '#2e7d32',
  CULTURE:           '#f57c00',
  BENEFITS:          '#c62828',
  MANAGER:           '#00838f',
  WORKLOAD:          '#6d4c41',
};

const formatTheme = (t: string) =>
  t.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());

export const ViewQuestionsModal = ({ surveyId, isOpen, onClose, surveyTitle, isDraft = false }: ViewQuestionsModalProps) => {
  const theme = useTheme();
  const queryClient = useQueryClient();
  const [pendingRemoval, setPendingRemoval] = useState<Set<number>>(new Set());

  const { data: mappings, isLoading: isLoadingMappings, isError: isErrorMappings } = useQuery({
    queryKey: ['survey-questions', surveyId],
    queryFn: () => getSurveyQuestions(surveyId!),
    enabled: isOpen && surveyId !== null,
  });

  const { data: allQuestions, isLoading: isLoadingQuestions } = useQuery({
    queryKey: ['questions', 'ALL'],
    queryFn: () => getQuestions(),
    enabled: isOpen,
  });

  const removeMutation = useMutation({
    mutationFn: () => removeQuestionsFromSurvey(surveyId!, Array.from(pendingRemoval)),
    onSuccess: () => {
      toast.success(`${pendingRemoval.size} question(s) removed from survey`);
      setPendingRemoval(new Set());
      queryClient.invalidateQueries({ queryKey: ['survey-questions', surveyId] });
    },
    onError: () => toast.error('Failed to remove questions'),
  });

  const isLoading = isLoadingMappings || isLoadingQuestions;
  const isError = isErrorMappings;

  let displayQuestions: any[] = [];
  
  if (mappings && allQuestions) {
    displayQuestions = mappings
      .sort((a, b) => a.displayOrder - b.displayOrder)
      .map(mapping => {
        const fullQuestion = allQuestions.find(q => q.id === mapping.questionId);
        return { ...mapping, fullQuestion };
      })
      .filter(item => item.fullQuestion !== undefined);
  }

  const toggleRemoval = (questionId: number) => {
    const next = new Set(pendingRemoval);
    next.has(questionId) ? next.delete(questionId) : next.add(questionId);
    setPendingRemoval(next);
  };

  const handleClose = () => {
    setPendingRemoval(new Set());
    onClose();
  };

  return (
    <Dialog 
      open={isOpen} 
      onClose={handleClose}
      fullWidth
      maxWidth="md"
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
            Survey Questions
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
            {surveyTitle || 'Loading...'}
          </Typography>
          {isDraft && (
            <Typography variant="caption" color="warning.main" sx={{ display: 'block', mt: 0.5, fontWeight: 600 }}>
              DRAFT — Click ✕ on a question to mark it for removal
            </Typography>
          )}
        </Box>
        <IconButton
          aria-label="close"
          onClick={handleClose}
          sx={{ color: 'text.secondary' }}
        >
          <X />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers sx={{ p: 3, backgroundColor: alpha(theme.palette.background.default, 0.5), minHeight: 300 }}>
        {isLoading ? (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', gap: 2 }}>
            <CircularProgress color="primary" />
            <Typography color="text.secondary">Loading survey questions...</Typography>
          </Box>
        ) : isError ? (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', gap: 2, color: 'error.main' }}>
            <AlertCircle size={48} />
            <Typography variant="h6">Failed to load questions.</Typography>
          </Box>
        ) : displayQuestions.length === 0 ? (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', color: 'text.secondary', border: 1, borderStyle: 'dashed', borderColor: 'divider', borderRadius: 2 }}>
            <Typography>No questions have been added to this survey yet.</Typography>
          </Box>
        ) : (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            {displayQuestions.map((item, index) => {
              const cat = item.fullQuestion!.category || '';
              const themeColor = THEME_COLORS[cat] || theme.palette.primary.main;
              const markedForRemoval = pendingRemoval.has(item.questionId);

              return (
                <Box 
                  key={item.id}
                  sx={{
                    p: 2,
                    borderRadius: 2,
                    bgcolor: markedForRemoval
                      ? alpha(theme.palette.error.main, 0.05)
                      : 'background.paper',
                    border: 1,
                    borderColor: markedForRemoval ? 'error.main' : 'divider',
                    display: 'flex',
                    gap: 2,
                    transition: 'all 0.2s',
                  }}
                >
                  {/* Number bubble */}
                  <Box sx={{ 
                    flexShrink: 0, 
                    width: 32, 
                    height: 32, 
                    borderRadius: '50%', 
                    bgcolor: markedForRemoval
                      ? alpha(theme.palette.error.main, 0.15)
                      : alpha(theme.palette.primary.main, 0.1), 
                    color: markedForRemoval ? 'error.main' : 'primary.main', 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center', 
                    fontWeight: 'bold', 
                    fontSize: 13,
                  }}>
                    {index + 1}
                  </Box>

                  <Box sx={{ flex: 1 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1, flexWrap: 'wrap' }}>
                      <Chip 
                        label={formatTheme(cat)} 
                        size="small" 
                        sx={{
                          bgcolor: alpha(themeColor, 0.1),
                          color: themeColor,
                          border: `1px solid ${alpha(themeColor, 0.3)}`,
                          fontWeight: 600,
                        }}
                      />
                      <Chip 
                        label={item.fullQuestion!.questionType === 'LIKERT_SCALE' ? 'RATING' : item.fullQuestion!.questionType.replace('_', ' ')} 
                        size="small" 
                        variant="outlined"
                      />
                      {/* Rating scale badge */}
                      {item.fullQuestion!.questionType === 'LIKERT_SCALE' && (
                        <Chip
                          label={`🔴${item.fullQuestion!.negativeFrom ?? 1}–${item.fullQuestion!.negativeTo ?? 4} 🟡${item.fullQuestion!.neutralFrom ?? 5}–${item.fullQuestion!.neutralTo ?? 7} 🟢${item.fullQuestion!.positiveFrom ?? 8}–${item.fullQuestion!.positiveTo ?? 10}`}
                          size="small"
                          variant="outlined"
                          sx={{ fontSize: 10, color: 'text.secondary' }}
                        />
                      )}
                      {markedForRemoval && (
                        <Chip label="Marked for removal" size="small" color="error" variant="filled" />
                      )}
                    </Box>
                    <Typography variant="body1" color="text.primary">
                      {item.fullQuestion!.questionText}
                    </Typography>
                  </Box>

                  {/* Remove toggle button — only in Draft mode */}
                  {isDraft && (
                    <Tooltip title={markedForRemoval ? 'Undo removal' : 'Remove from survey'}>
                      <IconButton
                        size="small"
                        onClick={() => toggleRemoval(item.questionId)}
                        sx={{
                          alignSelf: 'flex-start',
                          color: markedForRemoval ? 'error.main' : 'text.secondary',
                          border: '1px solid',
                          borderColor: markedForRemoval ? 'error.main' : 'divider',
                          borderRadius: 1,
                          bgcolor: markedForRemoval ? alpha(theme.palette.error.main, 0.05) : 'transparent',
                          '&:hover': {
                            color: 'error.main',
                            borderColor: 'error.main',
                            bgcolor: alpha(theme.palette.error.main, 0.08),
                          },
                        }}
                      >
                        <X size={14} />
                      </IconButton>
                    </Tooltip>
                  )}
                </Box>
              );
            })}
          </Box>
        )}
      </DialogContent>
      
      <DialogActions sx={{ p: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center', bgcolor: 'background.paper' }}>
        <Box>
          {isDraft && pendingRemoval.size > 0 && (
            <Typography variant="body2" color="error.main" sx={{ fontWeight: 600 }}>
              {pendingRemoval.size} question(s) marked for removal
            </Typography>
          )}
          {!isDraft && displayQuestions.length > 0 && (
            <Typography variant="body2" color="text.secondary">
              {displayQuestions.length} question{displayQuestions.length !== 1 ? 's' : ''} in this survey
            </Typography>
          )}
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          {isDraft && pendingRemoval.size > 0 && (
            <Button
              variant="contained"
              color="error"
              startIcon={removeMutation.isPending ? <CircularProgress size={16} color="inherit" /> : <Trash2 size={16} />}
              disabled={removeMutation.isPending}
              onClick={() => removeMutation.mutate()}
            >
              {removeMutation.isPending ? 'Removing...' : `Remove Selected (${pendingRemoval.size})`}
            </Button>
          )}
          <Button
            onClick={handleClose}
            variant="contained"
            color="primary"
          >
            Close
          </Button>
        </Box>
      </DialogActions>
    </Dialog>
  );
};
