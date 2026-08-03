import React, { useState, useEffect } from 'react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Box, Typography, IconButton, Button, CircularProgress,
  TextField, Select, MenuItem, FormControl, InputLabel,
  Divider, Tooltip
} from '@mui/material';
import { X, Sliders } from 'lucide-react';
import { alpha, useTheme } from '@mui/material/styles';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'react-toastify';
import { updateQuestion, type Question } from '../../services/question.service';

interface EditQuestionInlineModalProps {
  question: Question | null;
  isOpen: boolean;
  onClose: () => void;
}

const CATEGORIES = [
  'WORK_LIFE_BALANCE', 'LEADERSHIP', 'GROWTH', 'CULTURE',
  'BENEFITS', 'MANAGER', 'WORKLOAD'
];

const QUESTION_TYPES = ['LIKERT_SCALE', 'OPEN_ENDED', 'MULTIPLE_CHOICE'];

const formatCategory = (cat: string) =>
  cat.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());

export const EditQuestionInlineModal = ({ question, isOpen, onClose }: EditQuestionInlineModalProps) => {
  const theme = useTheme();
  const queryClient = useQueryClient();

  const [questionText, setQuestionText] = useState('');
  const [questionType, setQuestionType] = useState('LIKERT_SCALE');
  const [category, setCategory] = useState('LEADERSHIP');
  const [positiveFrom, setPositiveFrom] = useState(8);
  const [positiveTo, setPositiveTo]     = useState(10);
  const [neutralFrom, setNeutralFrom]   = useState(5);
  const [neutralTo, setNeutralTo]       = useState(7);
  const [negativeFrom, setNegativeFrom] = useState(1);
  const [negativeTo, setNegativeTo]     = useState(4);

  useEffect(() => {
    if (question) {
      setQuestionText(question.questionText);
      setQuestionType(question.questionType || 'LIKERT_SCALE');
      setCategory(question.category || 'LEADERSHIP');
      setPositiveFrom(question.positiveFrom ?? 8);
      setPositiveTo(question.positiveTo   ?? 10);
      setNeutralFrom(question.neutralFrom  ?? 5);
      setNeutralTo(question.neutralTo    ?? 7);
      setNegativeFrom(question.negativeFrom ?? 1);
      setNegativeTo(question.negativeTo   ?? 4);
    }
  }, [question]);

  const mutation = useMutation({
    mutationFn: () => updateQuestion(question!.id, {
      questionText,
      questionType,
      category,
      surveyType: question!.surveyType,
      positiveFrom,
      positiveTo,
      neutralFrom,
      neutralTo,
      negativeFrom,
      negativeTo,
    }),
    onSuccess: () => {
      toast.success('Question updated successfully');
      queryClient.invalidateQueries({ queryKey: ['questions'] });
      onClose();
    },
    onError: () => toast.error('Failed to update question'),
  });

  // Compute visual scale bar segments (total scale assumed 1–10)
  const total = 10;
  const negPct   = ((negativeTo - negativeFrom + 1) / total) * 100;
  const neuPct   = ((neutralTo - neutralFrom + 1)   / total) * 100;
  const posPct   = ((positiveTo - positiveFrom + 1) / total) * 100;

  if (!isOpen || !question) return null;

  const isLikert = questionType === 'LIKERT_SCALE';

  const NumInput = ({
    label, value, onChange, min = 1, max = 10, color
  }: {
    label: string; value: number; onChange: (v: number) => void;
    min?: number; max?: number; color: string;
  }) => (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 0.5 }}>
      <Typography variant="caption" color="text.secondary" sx={{ fontSize: 11 }}>{label}</Typography>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
        <IconButton size="small" onClick={() => onChange(Math.max(min, value - 1))}
          sx={{ bgcolor: alpha(color, 0.1), color, width: 24, height: 24, fontSize: 14, borderRadius: 1 }}>−</IconButton>
        <Box sx={{
          width: 36, height: 36, display: 'flex', alignItems: 'center', justifyContent: 'center',
          border: `2px solid ${color}`, borderRadius: 1, fontWeight: 700, fontSize: 16, color
        }}>
          {value}
        </Box>
        <IconButton size="small" onClick={() => onChange(Math.min(max, value + 1))}
          sx={{ bgcolor: alpha(color, 0.1), color, width: 24, height: 24, fontSize: 14, borderRadius: 1 }}>+</IconButton>
      </Box>
    </Box>
  );

  return (
    <Dialog
      open={isOpen}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
      slotProps={{
        paper: {
          sx: {
            borderRadius: 3,
            backgroundColor: alpha(theme.palette.background.paper, 0.97),
            backdropFilter: 'blur(12px)',
            backgroundImage: 'none',
          }
        }
      }}
    >
      <DialogTitle sx={{
        m: 0, p: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
        borderBottom: '1px solid', borderColor: 'divider',
        background: `linear-gradient(135deg, ${alpha(theme.palette.primary.main, 0.08)}, transparent)`,
      }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Box sx={{
            width: 36, height: 36, borderRadius: 2,
            bgcolor: alpha(theme.palette.primary.main, 0.12),
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'primary.main'
          }}>
            <Sliders size={18} />
          </Box>
          <Box>
            <Typography variant="h6" color="text.primary" sx={{ fontWeight: 700 }}>Edit Question</Typography>
            <Typography variant="caption" color="text.secondary">Modify text, category & rating scale</Typography>
          </Box>
        </Box>
        <IconButton onClick={onClose} size="small" sx={{ color: 'text.secondary' }}>
          <X size={18} />
        </IconButton>
      </DialogTitle>

      <DialogContent sx={{ p: 3, display: 'flex', flexDirection: 'column', gap: 3 }}>
        {/* Question Text */}
        <TextField
          label="Question Text"
          value={questionText}
          onChange={e => setQuestionText(e.target.value)}
          multiline
          rows={3}
          fullWidth
          variant="outlined"
        />

        {/* Type & Category */}
        <Box sx={{ display: 'flex', gap: 2 }}>
          <FormControl fullWidth size="small">
            <InputLabel>Question Type</InputLabel>
            <Select value={questionType} onChange={e => setQuestionType(e.target.value)} label="Question Type">
              {QUESTION_TYPES.map(t => (
                <MenuItem key={t} value={t}>
                  {t === 'LIKERT_SCALE' ? 'Rating (Likert)' : t.replace(/_/g, ' ')}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <FormControl fullWidth size="small">
            <InputLabel>Category / Theme</InputLabel>
            <Select value={category} onChange={e => setCategory(e.target.value)} label="Category / Theme">
              {CATEGORIES.map(c => (
                <MenuItem key={c} value={c}>{formatCategory(c)}</MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>

        {/* Flexible Rating Scale — only for Likert */}
        {isLikert && (
          <Box sx={{
            border: '1px solid', borderColor: 'divider', borderRadius: 2, p: 2.5,
            bgcolor: alpha(theme.palette.background.default, 0.6),
          }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
              <Sliders size={16} color={theme.palette.primary.main} />
              <Typography variant="subtitle2" color="primary" sx={{ fontWeight: 700 }}>
                Dynamic Flexible Rating Scale
              </Typography>
            </Box>
            <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2.5 }}>
              Define how scores are interpreted. Defaults: Positive 8–10 · Neutral 5–7 · Negative 1–4
            </Typography>

            {/* Negative Zone */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2, p: 1.5, borderRadius: 1.5, bgcolor: alpha('#f44336', 0.05), border: `1px solid ${alpha('#f44336', 0.2)}` }}>
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: '#f44336', flexShrink: 0 }} />
              <Typography variant="body2" sx={{ fontWeight: 600, color: '#f44336', minWidth: 80 }}>Negative</Typography>
              <Box sx={{ display: 'flex', gap: 2, ml: 'auto' }}>
                <NumInput label="From" value={negativeFrom} onChange={setNegativeFrom} color="#f44336" />
                <NumInput label="To"   value={negativeTo}   onChange={setNegativeTo}   color="#f44336" />
              </Box>
            </Box>

            {/* Neutral Zone */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2, p: 1.5, borderRadius: 1.5, bgcolor: alpha('#ff9800', 0.05), border: `1px solid ${alpha('#ff9800', 0.2)}` }}>
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: '#ff9800', flexShrink: 0 }} />
              <Typography variant="body2" sx={{ fontWeight: 600, color: '#ff9800', minWidth: 80 }}>Neutral</Typography>
              <Box sx={{ display: 'flex', gap: 2, ml: 'auto' }}>
                <NumInput label="From" value={neutralFrom} onChange={setNeutralFrom} color="#ff9800" />
                <NumInput label="To"   value={neutralTo}   onChange={setNeutralTo}   color="#ff9800" />
              </Box>
            </Box>

            {/* Positive Zone */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3, p: 1.5, borderRadius: 1.5, bgcolor: alpha('#4caf50', 0.05), border: `1px solid ${alpha('#4caf50', 0.2)}` }}>
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: '#4caf50', flexShrink: 0 }} />
              <Typography variant="body2" sx={{ fontWeight: 600, color: '#4caf50', minWidth: 80 }}>Positive</Typography>
              <Box sx={{ display: 'flex', gap: 2, ml: 'auto' }}>
                <NumInput label="From" value={positiveFrom} onChange={setPositiveFrom} color="#4caf50" />
                <NumInput label="To"   value={positiveTo}   onChange={setPositiveTo}   color="#4caf50" />
              </Box>
            </Box>

            {/* Visual Preview Bar */}
            <Box>
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>Scale Preview</Typography>
              <Box sx={{ display: 'flex', height: 16, borderRadius: 2, overflow: 'hidden', border: '1px solid', borderColor: 'divider' }}>
                <Tooltip title={`Negative: ${negativeFrom}–${negativeTo}`}>
                  <Box sx={{ width: `${negPct}%`, bgcolor: '#f44336', transition: 'width 0.3s', cursor: 'default' }} />
                </Tooltip>
                <Tooltip title={`Neutral: ${neutralFrom}–${neutralTo}`}>
                  <Box sx={{ width: `${neuPct}%`, bgcolor: '#ff9800', transition: 'width 0.3s', cursor: 'default' }} />
                </Tooltip>
                <Tooltip title={`Positive: ${positiveFrom}–${positiveTo}`}>
                  <Box sx={{ width: `${posPct}%`, bgcolor: '#4caf50', transition: 'width 0.3s', cursor: 'default' }} />
                </Tooltip>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 0.5 }}>
                <Typography variant="caption" color="text.secondary">1</Typography>
                <Typography variant="caption" color="text.secondary">5</Typography>
                <Typography variant="caption" color="text.secondary">10</Typography>
              </Box>
            </Box>
          </Box>
        )}
      </DialogContent>

      <DialogActions sx={{ p: 3, borderTop: '1px solid', borderColor: 'divider', bgcolor: 'background.paper', gap: 1 }}>
        <Button variant="outlined" color="inherit" onClick={onClose} sx={{ borderColor: 'divider' }}>
          Cancel
        </Button>
        <Button
          variant="contained"
          color="primary"
          onClick={() => mutation.mutate()}
          disabled={mutation.isPending || !questionText.trim()}
          startIcon={mutation.isPending ? <CircularProgress size={16} color="inherit" /> : null}
        >
          {mutation.isPending ? 'Saving...' : 'Save Changes'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
