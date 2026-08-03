import React, { useState, useEffect } from 'react';
import { 
  Dialog, DialogTitle, DialogContent, DialogActions,
  Box, Typography, IconButton, Button, Grid, TextField,
  Select, MenuItem, InputLabel, FormControl, CircularProgress
} from '@mui/material';
import { X } from 'lucide-react';
import { alpha, useTheme } from '@mui/material/styles';
import { type QuestionPayload, type Question } from '../../services/question.service';

interface QuestionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (payload: QuestionPayload) => void;
  initialData?: Question | null;
  isLoading?: boolean;
}

export const QuestionModal = ({ isOpen, onClose, onSubmit, initialData, isLoading }: QuestionModalProps) => {
  const theme = useTheme();
  const [questionText, setQuestionText] = useState('');
  const [questionType, setQuestionType] = useState('LIKERT_SCALE');
  const [category, setCategory] = useState('WORK_LIFE_BALANCE');
  const [surveyType, setSurveyType] = useState('ONBOARDING');
  const [options, setOptions] = useState<string[]>(['', '']);

  useEffect(() => {
    if (initialData && isOpen) {
      setQuestionText(initialData.questionText);
      setQuestionType(initialData.questionType);
      setCategory(initialData.category);
      setSurveyType(initialData.surveyType || 'ONBOARDING');
      setOptions(initialData.options || ['', '']);
    } else if (isOpen) {
      setQuestionText('');
      setQuestionType('LIKERT_SCALE');
      setCategory('WORK_LIFE_BALANCE');
      setSurveyType('ONBOARDING');
      setOptions(['', '']);
    }
  }, [initialData, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const payload: QuestionPayload = { questionText, questionType, category, surveyType };
    if (questionType === 'MCQ') {
      payload.options = options.filter(opt => opt.trim() !== '');
    }
    onSubmit(payload);
  };

  const handleOptionChange = (index: number, value: string) => {
    const newOptions = [...options];
    newOptions[index] = value;
    setOptions(newOptions);
  };

  const addOption = () => {
    setOptions([...options, '']);
  };

  const removeOption = (index: number) => {
    const newOptions = options.filter((_, i) => i !== index);
    setOptions(newOptions);
  };

  return (
    <Dialog 
      open={isOpen} 
      onClose={onClose}
      maxWidth="sm"
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
      <form onSubmit={handleSubmit}>
        <DialogTitle sx={{ 
          m: 0, p: 3, display: 'flex', justifyItems: 'space-between', justifyContent: 'space-between', alignItems: 'flex-start',
          borderBottom: '1px solid', borderColor: 'divider'
        }}>
          <Box>
            <Typography variant="h5" color="text.primary" sx={{ fontWeight: 700 }}>
              {initialData ? 'Edit Question' : 'Create Question'}
            </Typography>
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
          <Grid container spacing={3}>
            <Grid size={{ xs: 12 }}>
              <TextField
                required
                fullWidth
                multiline
                rows={3}
                label="Question Text"
                value={questionText}
                onChange={(e) => setQuestionText(e.target.value)}
                variant="outlined"
                placeholder="Enter the question text..."
              />
            </Grid>

            <Grid size={{ xs: 12 }}>
              <FormControl fullWidth>
                <InputLabel>Question Type</InputLabel>
                <Select
                  value={questionType}
                  label="Question Type"
                  onChange={(e) => setQuestionType(e.target.value)}
                >
                  <MenuItem value="LIKERT_SCALE">Rating (1-10)</MenuItem>
                  <MenuItem value="TEXT">Open Ended</MenuItem>
                  <MenuItem value="MCQ">Multiple Choice</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            
            {questionType === 'MCQ' && (
              <Grid size={{ xs: 12 }}>
                <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600 }}>Choices</Typography>
                {options.map((opt, index) => (
                  <Box key={index} sx={{ display: 'flex', gap: 1, mb: 1 }}>
                    <TextField
                      fullWidth
                      size="small"
                      placeholder={`Option ${index + 1}`}
                      value={opt}
                      onChange={(e) => handleOptionChange(index, e.target.value)}
                      required={index < 2} // At least 2 options required
                    />
                    {options.length > 2 && (
                      <IconButton color="error" onClick={() => removeOption(index)}>
                        <X size={20} />
                      </IconButton>
                    )}
                  </Box>
                ))}
                <Button variant="text" size="small" onClick={addOption} sx={{ mt: 1 }}>
                  + Add Option
                </Button>
              </Grid>
            )}

            <Grid size={{ xs: 12, sm: 6 }}>
              <FormControl fullWidth>
                <InputLabel>Category</InputLabel>
                <Select
                  value={category}
                  label="Category"
                  onChange={(e) => setCategory(e.target.value)}
                >
                  <MenuItem value="WORK_LIFE_BALANCE">Work Life Balance</MenuItem>
                  <MenuItem value="LEADERSHIP">Leadership</MenuItem>
                  <MenuItem value="GROWTH">Growth</MenuItem>
                  <MenuItem value="CULTURE">Culture</MenuItem>
                  <MenuItem value="BENEFITS">Benefits</MenuItem>
                  <MenuItem value="MANAGER">Manager</MenuItem>
                  <MenuItem value="WORKLOAD">Workload</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <FormControl fullWidth>
                <InputLabel>Survey Type</InputLabel>
                <Select
                  value={surveyType}
                  label="Survey Type"
                  onChange={(e) => setSurveyType(e.target.value)}
                >
                  <MenuItem value="ONBOARDING">Onboarding</MenuItem>
                  <MenuItem value="MONTHLY_PULSE">Monthly Pulse</MenuItem>
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        
        <DialogActions sx={{ p: 3, display: 'flex', justifyContent: 'flex-end', gap: 2, bgcolor: 'background.paper' }}>
          <Button 
            variant="outlined" 
            color="inherit" 
            onClick={onClose}
            disabled={isLoading}
            sx={{ borderColor: 'divider' }}
          >
            Cancel
          </Button>
          <Button 
            type="submit"
            variant="contained" 
            color="primary" 
            disabled={isLoading}
            startIcon={isLoading ? <CircularProgress size={16} color="inherit" /> : null}
          >
            {isLoading ? 'Saving...' : initialData ? 'Save Changes' : 'Create Question'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

