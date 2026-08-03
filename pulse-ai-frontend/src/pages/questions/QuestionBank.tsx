import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Brain, CheckCircle, XCircle, Sparkles, Loader2, AlertCircle, RefreshCw, Plus, Trash2, Edit2 } from 'lucide-react';
import { toast } from 'react-toastify';
import { getQuestions, approveQuestion, rejectQuestion, createQuestion, updateQuestion, deleteQuestion, type Question, type QuestionPayload } from '../../services/question.service';
import { QuestionModal } from '../../components/questions/QuestionModal';
import { 
  Box, Typography, Button, IconButton, Card, CardContent, Chip, 
  Tabs, Tab, Select, MenuItem, useTheme
} from '@mui/material';
import { Grid } from '@mui/material';
import { alpha } from '@mui/material/styles';

type TabStatus = 'DRAFT' | 'APPROVED' | 'REJECTED';

export const QuestionBank = () => {
  const theme = useTheme();
  const [activeTab, setActiveTab] = useState<TabStatus>('DRAFT');
  const [surveyTypeFilter, setSurveyTypeFilter] = useState<string>('ALL');
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState<Question | null>(null);

  const queryClient = useQueryClient();

  const { data: questions, isLoading, isError, refetch } = useQuery({
    queryKey: ['questions', activeTab],
    queryFn: () => getQuestions(activeTab),
  });

  const approveMutation = useMutation({
    mutationFn: approveQuestion,
    onSuccess: () => {
      toast.success('Question approved successfully');
      queryClient.invalidateQueries({ queryKey: ['questions'] });
    },
    onError: () => toast.error('Failed to approve question'),
  });

  const rejectMutation = useMutation({
    mutationFn: rejectQuestion,
    onSuccess: () => {
      toast.success('Question rejected');
      queryClient.invalidateQueries({ queryKey: ['questions'] });
    },
    onError: () => toast.error('Failed to reject question'),
  });

  const createMutation = useMutation({
    mutationFn: createQuestion,
    onSuccess: () => {
      toast.success('Question created successfully');
      setIsModalOpen(false);
      queryClient.invalidateQueries({ queryKey: ['questions'] });
    },
    onError: () => toast.error('Failed to create question'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: QuestionPayload }) => updateQuestion(id, payload),
    onSuccess: () => {
      toast.success('Question updated successfully');
      setIsModalOpen(false);
      setEditingQuestion(null);
      queryClient.invalidateQueries({ queryKey: ['questions'] });
    },
    onError: () => toast.error('Failed to update question'),
  });

  const deleteMutation = useMutation({
    mutationFn: deleteQuestion,
    onSuccess: () => {
      toast.success('Question deleted successfully');
      queryClient.invalidateQueries({ queryKey: ['questions'] });
    },
    onError: () => toast.error('Failed to delete question'),
  });

  const handleCreateOrUpdate = (payload: QuestionPayload) => {
    if (editingQuestion) {
      updateMutation.mutate({ id: editingQuestion.id, payload });
    } else {
      createMutation.mutate(payload);
    }
  };

  const openEditModal = (question: Question) => {
    setEditingQuestion(question);
    setIsModalOpen(true);
  };

  const handleDelete = (id: number) => {
    if (window.confirm('Are you sure you want to delete this question?')) {
      deleteMutation.mutate(id);
    }
  };

  const filteredQuestions = questions?.filter(q => 
    surveyTypeFilter === 'ALL' ? true : q.surveyType === surveyTypeFilter
  ) || [];

  return (
    <Box sx={{ p: { xs: 2, md: 6 }, maxWidth: 'lg', mx: 'auto' }}>
      <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, alignItems: { md: 'flex-end' }, justifyContent: 'space-between', gap: 2, mb: 4 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 'bold', color: 'text.primary', display: 'flex', alignItems: 'center', gap: 1.5 }}>
            Question Bank
          </Typography>
          <Typography sx={{ color: 'text.secondary', mt: 1 }}>
            Manage your AI-generated and manual questions for employee surveys.
          </Typography>
        </Box>
        
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <Button 
            onClick={() => refetch()} 
            variant="outlined"
            sx={{ 
              color: 'text.secondary', 
              borderColor: 'divider',
              bgcolor: 'background.paper',
              '&:hover': { color: 'text.primary', bgcolor: alpha(theme.palette.background.paper, 0.8) }
            }}
            startIcon={<RefreshCw size={16} className={isLoading ? 'animate-spin' : ''} />}
          >
            Refresh
          </Button>
          <Button 
            onClick={() => {
              setEditingQuestion(null);
              setIsModalOpen(true);
            }}
            variant="contained"
            color="primary"
            startIcon={<Plus size={16} />}
            sx={{ boxShadow: `0 4px 14px 0 ${alpha(theme.palette.primary.main, 0.25)}`, whiteSpace: 'nowrap', minWidth: 'max-content' }}
          >
            Create Question
          </Button>
        </Box>
      </Box>

      <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, alignItems: { md: 'center' }, justifyContent: 'space-between', gap: 2, mb: 4 }}>
        <Box sx={{ bgcolor: 'background.paper', p: 0.5, borderRadius: 2, width: 'fit-content', border: 1, borderColor: 'divider' }}>
          <Tabs 
            value={activeTab} 
            onChange={(_, v) => setActiveTab(v)}
            sx={{ minHeight: 'unset', '& .MuiTab-root': { minHeight: 'unset', py: 1, px: 3, zIndex: 1, color: 'text.secondary', '&.Mui-selected': { color: 'primary.main', fontWeight: 'bold' } } }}
          >
            {(['DRAFT', 'APPROVED', 'REJECTED'] as TabStatus[]).map((tab) => (
              <Tab key={tab} value={tab} label={tab} disableRipple />
            ))}
          </Tabs>
        </Box>

        <Select
          value={surveyTypeFilter}
          onChange={(e) => setSurveyTypeFilter(e.target.value as string)}
          size="small"
          sx={{ 
            bgcolor: 'background.paper', 
            color: 'text.primary',
            '& .MuiOutlinedInput-notchedOutline': { borderColor: 'divider' },
            '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: alpha(theme.palette.primary.main, 0.5) },
            '&.Mui-focused .MuiOutlinedInput-notchedOutline': { borderColor: 'primary.main' }
          }}
        >
          <MenuItem value="ALL">All Survey Types</MenuItem>
          <MenuItem value="ONBOARDING">Onboarding</MenuItem>
          <MenuItem value="MONTHLY_PULSE">Monthly Pulse</MenuItem>
        </Select>
      </Box>

      <Box sx={{ minHeight: 400 }}>
        {isLoading && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, gap: 2 }}>
            <Loader2 size={32} className="animate-spin text-primary" style={{ color: theme.palette.primary.main }} />
            <Typography color="text.secondary">Loading questions...</Typography>
          </Box>
        )}

        {isError && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, gap: 2, color: 'error.main' }}>
            <AlertCircle size={48} />
            <Typography variant="h6">Failed to load questions.</Typography>
          </Box>
        )}

        {!isLoading && !isError && filteredQuestions.length === 0 && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, color: 'text.secondary', border: 1, borderStyle: 'dashed', borderColor: 'divider', borderRadius: 4, bgcolor: 'background.paper' }}>
            <Brain size={48} style={{ opacity: 0.5, marginBottom: 16 }} />
            <Typography variant="h6">No questions found in this category.</Typography>
          </Box>
        )}

        <Grid container spacing={3} alignItems="stretch">
          {filteredQuestions.map((question) => (
            <Grid size={{ xs: 12, md: 6 }} key={question.id}>
              <Card sx={{ 
                height: '100%', 
                display: 'flex', 
                flexDirection: 'column',
                bgcolor: 'background.paper',
                border: 1,
                borderColor: 'divider',
                transition: 'border-color 0.3s',
                '&:hover': { borderColor: theme.palette.primary.main, '& .action-buttons': { opacity: 1 } },
                boxShadow: theme.shadows[1],
                position: 'relative'
              }}>
                <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', p: 3 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Chip 
                      icon={question.source === 'AI' ? <Sparkles size={14} color={theme.palette.info.main} /> : undefined}
                      label={question.category} 
                      size="small" 
                      sx={{ 
                        bgcolor: alpha(theme.palette.primary.main, 0.05), 
                        color: 'text.secondary', 
                        border: 1, 
                        borderColor: 'divider',
                        fontWeight: 'medium'
                      }} 
                    />
                    
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Chip 
                        label={question.questionType === 'LIKERT_SCALE' ? 'RATING' : question.questionType.replace('_', ' ')} 
                        size="small" 
                        sx={{ 
                          bgcolor: alpha(theme.palette.background.default, 0.5), 
                          color: 'text.secondary', 
                          border: 1, 
                          borderColor: 'divider'
                        }} 
                      />
                      
                      <Box className="action-buttons" sx={{ 
                        opacity: 0, 
                        transition: 'opacity 0.2s', 
                        display: 'flex', 
                        alignItems: 'center', 
                        gap: 0.5, 
                        bgcolor: alpha(theme.palette.background.paper, 0.9), 
                        borderRadius: 1, 
                        border: 1, 
                        borderColor: 'divider', 
                        p: 0.5, 
                        position: 'absolute', 
                        top: 16, 
                        right: 16, 
                        zIndex: 10, 
                        backdropFilter: 'blur(4px)' 
                      }}>
                        <IconButton 
                          onClick={() => openEditModal(question)}
                          size="small"
                          sx={{ color: 'text.secondary', '&:hover': { color: 'primary.main', bgcolor: alpha(theme.palette.primary.main, 0.1) } }}
                          title="Edit Question"
                        >
                          <Edit2 size={14} />
                        </IconButton>
                        <IconButton 
                          onClick={() => handleDelete(question.id)}
                          size="small"
                          sx={{ color: 'text.secondary', '&:hover': { color: 'error.main', bgcolor: alpha(theme.palette.error.main, 0.1) } }}
                          title="Delete Question"
                        >
                          <Trash2 size={14} />
                        </IconButton>
                      </Box>
                    </Box>
                  </Box>
                  
                  <Typography variant="body1" sx={{ color: 'text.primary', fontWeight: 'medium', mb: 3, flexGrow: 1, lineHeight: 1.6, mt: 1, pr: 4 }}>
                    {question.questionText}
                  </Typography>
                  
                  {activeTab === 'DRAFT' && (
                    <Box sx={{ mt: 'auto', pt: 2, borderTop: 1, borderColor: 'divider', display: 'flex', gap: 1.5 }}>
                      <Button
                        onClick={() => approveMutation.mutate(question.id)}
                        disabled={approveMutation.isPending || rejectMutation.isPending}
                        fullWidth
                        variant="outlined"
                        startIcon={<CheckCircle size={16} />}
                        sx={{ 
                          color: 'success.main',
                          borderColor: alpha(theme.palette.success.main, 0.2),
                          bgcolor: alpha(theme.palette.success.main, 0.05),
                          '&:hover': { bgcolor: alpha(theme.palette.success.main, 0.1) }
                        }}
                      >
                        Approve
                      </Button>
                      <Button
                        onClick={() => rejectMutation.mutate(question.id)}
                        disabled={approveMutation.isPending || rejectMutation.isPending}
                        fullWidth
                        variant="outlined"
                        startIcon={<XCircle size={16} />}
                        sx={{ 
                          color: 'error.main',
                          borderColor: alpha(theme.palette.error.main, 0.2),
                          bgcolor: alpha(theme.palette.error.main, 0.05),
                          '&:hover': { bgcolor: alpha(theme.palette.error.main, 0.1) }
                        }}
                      >
                        Reject
                      </Button>
                    </Box>
                  )}
                  {activeTab === 'APPROVED' && (
                    <Box sx={{ mt: 'auto', pt: 2, borderTop: 1, borderColor: 'divider', display: 'flex', alignItems: 'center', gap: 1, color: 'success.main' }}>
                      <CheckCircle size={16} />
                      <Typography variant="body2" sx={{ fontWeight: "medium" }}>Approved</Typography>
                    </Box>
                  )}
                  {activeTab === 'REJECTED' && (
                    <Box sx={{ mt: 'auto', pt: 2, borderTop: 1, borderColor: 'divider', display: 'flex', alignItems: 'center', gap: 1, color: 'error.main' }}>
                      <XCircle size={16} />
                      <Typography variant="body2" sx={{ fontWeight: "medium" }}>Rejected</Typography>
                    </Box>
                  )}
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>

      <QuestionModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingQuestion(null);
        }}
        onSubmit={handleCreateOrUpdate}
        initialData={editingQuestion}
        isLoading={createMutation.isPending || updateMutation.isPending}
      />
    </Box>
  );
};
