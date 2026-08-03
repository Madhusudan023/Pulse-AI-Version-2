import React, { useState, useEffect } from 'react';
import { 
  Dialog, DialogTitle, DialogContent, DialogActions,
  Box, Typography, IconButton, Button, CircularProgress,
  TextField, InputAdornment, Checkbox, Chip,
  Select, MenuItem, FormControl, Tooltip
} from '@mui/material';
import { X, Search, Pencil } from 'lucide-react';
import { alpha, useTheme } from '@mui/material/styles';
import { useQuery } from '@tanstack/react-query';
import { getQuestions, type Question } from '../../services/question.service';
import { getLastSurveyQuestionIds } from '../../services/survey.service';
import { EditQuestionInlineModal } from './EditQuestionInlineModal';

interface AddQuestionsModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (questionIds: number[]) => void;
  isLoading: boolean;
}

const ALL_THEMES = [
  'WORK_LIFE_BALANCE', 'LEADERSHIP', 'GROWTH',
  'CULTURE', 'BENEFITS', 'MANAGER', 'WORKLOAD'
];

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

export const AddQuestionsModal = ({ isOpen, onClose, onSubmit, isLoading }: AddQuestionsModalProps) => {
  const theme = useTheme();
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('ALL');
  const [selectedThemes, setSelectedThemes] = useState<Set<string>>(new Set());
  const [editingQuestion, setEditingQuestion] = useState<Question | null>(null);

  const { data: questions, isLoading: isLoadingQuestions } = useQuery({
    queryKey: ['questions', 'APPROVED'],
    queryFn: () => getQuestions('APPROVED'),
    enabled: isOpen,
  });

  const { data: lastMonthlyIds = [] } = useQuery({
    queryKey: ['lastSurveyQuestionIds', 'MONTHLY_PULSE'],
    queryFn: () => getLastSurveyQuestionIds('MONTHLY_PULSE'),
    enabled: isOpen,
  });

  const { data: lastOnboardingIds = [] } = useQuery({
    queryKey: ['lastSurveyQuestionIds', 'ONBOARDING'],
    queryFn: () => getLastSurveyQuestionIds('ONBOARDING'),
    enabled: isOpen,
  });

  useEffect(() => {
    if (isOpen) {
      setSelectedIds(new Set());
      setSearchTerm('');
      setFilterType('ALL');
      setSelectedThemes(new Set());
      setEditingQuestion(null);
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const toggleTheme = (t: string) => {
    const next = new Set(selectedThemes);
    next.has(t) ? next.delete(t) : next.add(t);
    setSelectedThemes(next);
  };

  const filteredQuestions = questions?.filter(q => {
    const matchesSearch = q.questionText.toLowerCase().includes(searchTerm.toLowerCase()) || 
                          q.category.toLowerCase().includes(searchTerm.toLowerCase());
    if (!matchesSearch) return false;
    if (filterType === 'AI') return q.source === 'AI';
    if (filterType === 'LAST_MONTHLY_PULSE') return lastMonthlyIds.includes(q.id);
    if (filterType === 'LAST_ONBOARDING') return lastOnboardingIds.includes(q.id);
    if (filterType !== 'ALL') return q.surveyType === filterType;
    // Theme filter
    if (selectedThemes.size > 0) return selectedThemes.has(q.category);
    return true;
  }) || [];

  const toggleSelection = (id: number) => {
    const newSelection = new Set(selectedIds);
    newSelection.has(id) ? newSelection.delete(id) : newSelection.add(id);
    setSelectedIds(newSelection);
  };

  const selectAll = () => {
    if (selectedIds.size === filteredQuestions.length) {
      setSelectedIds(new Set());
    } else {
      setSelectedIds(new Set(filteredQuestions.map(q => q.id)));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(Array.from(selectedIds));
  };

  return (
    <>
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
              Add Questions to Survey
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
              Select from the approved question bank
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
        
        {/* Filter Controls */}
        <Box sx={{ p: 2, borderBottom: '1px solid', borderColor: 'divider', display: 'flex', gap: 2, alignItems: 'center', backgroundColor: alpha(theme.palette.background.default, 0.5) }}>
          <FormControl size="small" sx={{ minWidth: 160 }}>
            <Select
              value={filterType}
              onChange={(e) => setFilterType(e.target.value)}
              displayEmpty
            >
              <MenuItem value="ALL">All Types</MenuItem>
              <MenuItem value="ONBOARDING">Onboarding</MenuItem>
              <MenuItem value="MONTHLY_PULSE">Monthly Pulse</MenuItem>
              <MenuItem value="LAST_ONBOARDING">Last Onboarding</MenuItem>
              <MenuItem value="LAST_MONTHLY_PULSE">Last Monthly Pulse</MenuItem>
              <MenuItem value="AI">AI Generated</MenuItem>
            </Select>
          </FormControl>
          <TextField
            fullWidth
            size="small"
            placeholder="Search questions by text or category..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Search size={20} />
                </InputAdornment>
              ),
            }}
          />
          <Button 
            variant="outlined" 
            onClick={selectAll}
            sx={{ whiteSpace: 'nowrap' }}
          >
            {selectedIds.size === filteredQuestions.length && filteredQuestions.length > 0 ? 'Deselect All' : 'Select All'}
          </Button>
        </Box>

        {/* Theme Checkboxes */}
        <Box sx={{
          px: 2.5, py: 1.5,
          borderBottom: '1px solid', borderColor: 'divider',
          bgcolor: alpha(theme.palette.background.default, 0.3),
        }}>
          <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600, display: 'block', mb: 1 }}>
            FILTER BY THEME
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {ALL_THEMES.map(t => {
              const active = selectedThemes.has(t);
              const color = THEME_COLORS[t];
              return (
                <Chip
                  key={t}
                  label={formatTheme(t)}
                  size="small"
                  clickable
                  onClick={() => toggleTheme(t)}
                  sx={{
                    borderRadius: 2,
                    border: `1.5px solid ${active ? color : alpha(color, 0.3)}`,
                    bgcolor: active ? alpha(color, 0.12) : 'transparent',
                    color: active ? color : 'text.secondary',
                    fontWeight: active ? 700 : 400,
                    transition: 'all 0.2s',
                    '&:hover': { bgcolor: alpha(color, 0.15), borderColor: color },
                  }}
                  icon={
                    <Box sx={{
                      width: 8, height: 8, borderRadius: '50%',
                      bgcolor: active ? color : alpha(color, 0.4),
                      ml: '6px !important', mr: '-2px !important',
                      transition: 'all 0.2s',
                    }} />
                  }
                />
              );
            })}
            {selectedThemes.size > 0 && (
              <Button
                size="small"
                variant="text"
                onClick={() => setSelectedThemes(new Set())}
                sx={{ color: 'text.secondary', fontSize: 11, py: 0 }}
              >
                Clear themes
              </Button>
            )}
          </Box>
          {selectedThemes.size > 0 && (
            <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5, display: 'block' }}>
              Showing questions from: {Array.from(selectedThemes).map(formatTheme).join(', ')}
            </Typography>
          )}
        </Box>

        <DialogContent sx={{ p: 3, backgroundColor: alpha(theme.palette.background.default, 0.5), minHeight: 300 }}>
          {isLoadingQuestions ? (
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', gap: 2 }}>
              <CircularProgress color="primary" />
              <Typography color="text.secondary">Loading approved questions...</Typography>
            </Box>
          ) : filteredQuestions.length === 0 ? (
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
              <Typography color="text.secondary">No questions found for the selected filters.</Typography>
            </Box>
          ) : (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              {filteredQuestions.map(question => {
                const themeColor = THEME_COLORS[question.category] || theme.palette.primary.main;
                return (
                  <Box 
                    key={question.id}
                    sx={{
                      p: 2,
                      borderRadius: 2,
                      border: '1px solid',
                      borderColor: selectedIds.has(question.id) ? 'primary.main' : 'divider',
                      backgroundColor: selectedIds.has(question.id) ? alpha(theme.palette.primary.main, 0.05) : 'background.paper',
                      cursor: 'pointer',
                      display: 'flex',
                      gap: 2,
                      transition: 'all 0.2s',
                      '&:hover': {
                        borderColor: 'primary.main',
                      }
                    }}
                  >
                    <Checkbox 
                      checked={selectedIds.has(question.id)}
                      onChange={() => toggleSelection(question.id)}
                      onClick={e => e.stopPropagation()}
                      sx={{ p: 0 }}
                    />
                    <Box
                      sx={{ flex: 1 }}
                      onClick={() => toggleSelection(question.id)}
                    >
                      <Box sx={{ display: 'flex', gap: 1, mb: 1, flexWrap: 'wrap' }}>
                        <Chip
                          label={formatTheme(question.category)}
                          size="small"
                          sx={{
                            bgcolor: alpha(themeColor, 0.1),
                            color: themeColor,
                            border: `1px solid ${alpha(themeColor, 0.3)}`,
                            fontWeight: 600,
                          }}
                        />
                        <Chip label={question.questionType === 'LIKERT_SCALE' ? 'RATING' : question.questionType.replace('_', ' ')} size="small" variant="outlined" />
                        <Chip label={question.surveyType} size="small" color="primary" variant="outlined" />
                      </Box>
                      <Typography variant="body2" color="text.primary">
                        {question.questionText}
                      </Typography>
                    </Box>
                    {/* Edit Button */}
                    <Tooltip title="Edit this question">
                      <IconButton
                        size="small"
                        onClick={e => { e.stopPropagation(); setEditingQuestion(question); }}
                        sx={{
                          alignSelf: 'flex-start',
                          color: 'text.secondary',
                          border: '1px solid',
                          borderColor: 'divider',
                          borderRadius: 1,
                          '&:hover': { color: 'primary.main', borderColor: 'primary.main', bgcolor: alpha(theme.palette.primary.main, 0.05) }
                        }}
                      >
                        <Pencil size={14} />
                      </IconButton>
                    </Tooltip>
                  </Box>
                );
              })}
            </Box>
          )}
        </DialogContent>
        
        <DialogActions sx={{ p: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center', bgcolor: 'background.paper' }}>
          <Typography variant="body2" color="text.secondary">
            <strong>{selectedIds.size}</strong> question{selectedIds.size !== 1 ? 's' : ''} selected
          </Typography>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button 
              variant="outlined" 
              color="inherit" 
              onClick={onClose}
              sx={{ borderColor: 'divider' }}
            >
              Cancel
            </Button>
            <Button 
              variant="contained" 
              color="primary" 
              onClick={handleSubmit}
              disabled={isLoading || selectedIds.size === 0}
              startIcon={isLoading ? <CircularProgress size={16} color="inherit" /> : null}
            >
              {isLoading ? 'Adding...' : 'Add Selected Questions'}
            </Button>
          </Box>
        </DialogActions>
      </Dialog>

      {/* Edit Question Modal (layered on top) */}
      <EditQuestionInlineModal
        question={editingQuestion}
        isOpen={editingQuestion !== null}
        onClose={() => setEditingQuestion(null)}
      />
    </>
  );
};
