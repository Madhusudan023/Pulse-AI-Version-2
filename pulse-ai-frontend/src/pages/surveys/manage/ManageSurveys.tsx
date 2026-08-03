import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { MessageSquare, Plus, Loader2, AlertCircle, RefreshCw, Send, CheckCircle, PlusCircle, StopCircle, Calendar, Eye, Edit2, PlayCircle, BarChart3, Trash2, UploadCloud } from 'lucide-react';
import { toast } from 'react-toastify';
import { getRegionSurveys, createSurvey, publishSurvey, closeSurvey, addQuestionsToSurvey, deleteSurvey, type Survey } from '../../../services/survey.service';
import { getRegionReports, downloadReportPdf, downloadReportCsv, type Report } from '../../../services/report.service';
import { CreateSurveyModal } from '../../../components/surveys/CreateSurveyModal';
import { AddQuestionsModal } from '../../../components/surveys/AddQuestionsModal';
import { ViewQuestionsModal } from '../../../components/surveys/ViewQuestionsModal';
import { EditSurveyModal } from '../../../components/surveys/EditSurveyModal';
import { PublishSurveyModal } from '../../../components/surveys/PublishSurveyModal';
import { ReportDetailsModal } from '../../../components/reports/ReportDetailsModal';
import { Box, Typography, Button, IconButton, Card, CardContent, Chip, Tabs, Tab, useTheme, Menu, MenuItem } from '@mui/material';
import { Grid } from '@mui/material';
import { alpha } from '@mui/material/styles';
import { useAuthStore } from '../../../store/useAuthStore';
import { EmployeeSurveys } from './EmployeeSurveys';

type TabStatus = 'DRAFT' | 'SCHEDULED' | 'ACTIVE' | 'CLOSED' | 'ARCHIVED';

export const ManageSurveys = () => {
  const role = useAuthStore(state => state.role)?.replace('ROLE_', '');
  if (role === 'EMPLOYEE') {
    return <EmployeeSurveys />;
  }

  const theme = useTheme();
  const [activeTab, setActiveTab] = useState<TabStatus>('DRAFT');
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingSurvey, setEditingSurvey] = useState<Survey | null>(null);
  const [isPublishModalOpen, setIsPublishModalOpen] = useState(false);
  const [publishingSurvey, setPublishingSurvey] = useState<Survey | null>(null);
  const [activeSurveyForQuestions, setActiveSurveyForQuestions] = useState<number | null>(null);
  const [viewQuestionsSurveyId, setViewQuestionsSurveyId] = useState<number | null>(null);
  const [viewQuestionsSurveyTitle, setViewQuestionsSurveyTitle] = useState<string>('');
  const [customAudienceMap, setCustomAudienceMap] = useState<Record<number, string[]>>({});
  
  const [selectedReport, setSelectedReport] = useState<Report | null>(null);
  const [reportMenuAnchor, setReportMenuAnchor] = useState<null | HTMLElement>(null);

  const queryClient = useQueryClient();

  const { data: surveys, isLoading, isError, refetch } = useQuery({
    queryKey: ['surveys', activeTab],
    queryFn: () => getRegionSurveys(activeTab),
  });

  const { data: reports } = useQuery({
    queryKey: ['reports'],
    queryFn: getRegionReports,
  });

  const handleOpenReportMenu = (event: React.MouseEvent<HTMLElement>) => {
    setReportMenuAnchor(event.currentTarget);
  };

  const handleCloseReportMenu = () => {
    setReportMenuAnchor(null);
  };

  const createMutation = useMutation({
    mutationFn: createSurvey,
    onSuccess: () => {
      toast.success('Survey created successfully');
      setIsCreateModalOpen(false);
      queryClient.invalidateQueries({ queryKey: ['surveys'] });
    },
    onError: () => toast.error('Failed to create survey'),
  });

  const publishMutation = useMutation({
    mutationFn: ({ id, customEmails }: { id: number; customEmails?: string[] }) => publishSurvey(id, customEmails),
    onSuccess: () => {
      toast.success('Survey published and sent to employees');
      setIsPublishModalOpen(false);
      setPublishingSurvey(null);
      queryClient.invalidateQueries({ queryKey: ['surveys'] });
    },
    onError: () => toast.error('Failed to publish survey'),
  });

  const closeMutation = useMutation({
    mutationFn: closeSurvey,
    onSuccess: () => {
      toast.success('Survey closed successfully');
      queryClient.invalidateQueries({ queryKey: ['surveys'] });
    },
    onError: () => toast.error('Failed to close survey'),
  });

  const addQuestionsMutation = useMutation({
    mutationFn: ({ id, questionIds }: { id: number; questionIds: number[] }) =>
      addQuestionsToSurvey(id, questionIds),
    onSuccess: () => {
      toast.success('Questions added to survey');
      setActiveSurveyForQuestions(null);
      queryClient.invalidateQueries({ queryKey: ['surveys'] });
    },
    onError: () => toast.error('Failed to add questions'),
  });

  const reactivateMutation = useMutation({
    mutationFn: (id: number) => import('../../../services/survey.service').then(m => m.reactivateSurvey(id)),
    onSuccess: () => {
      toast.success('Survey reactivated successfully');
      queryClient.invalidateQueries({ queryKey: ['surveys'] });
    },
    onError: () => toast.error('Failed to reactivate survey'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: any }) => 
      import('../../../services/survey.service').then(m => m.updateSurvey(id, payload)),
    onSuccess: () => {
      toast.success('Survey updated successfully');
      setIsEditModalOpen(false);
      setEditingSurvey(null);
      queryClient.invalidateQueries({ queryKey: ['surveys'] });
    },
    onError: () => toast.error('Failed to update survey'),
  });

  const deleteMutation = useMutation({
    mutationFn: deleteSurvey,
    onSuccess: () => {
      toast.success('Survey deleted successfully');
      queryClient.invalidateQueries({ queryKey: ['surveys'] });
    },
    onError: () => toast.error('Failed to delete survey'),
  });

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>, surveyId: number) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.name.toLowerCase().endsWith('.xlsx') && !file.name.toLowerCase().endsWith('.csv') && !file.name.toLowerCase().endsWith('.xls')) {
      toast.error('Please upload a valid Excel or CSV file.');
      return;
    }

    import('xlsx').then(XLSX => {
      const reader = new FileReader();
      reader.onload = (event) => {
        try {
          const data = new Uint8Array(event.target?.result as ArrayBuffer);
          const workbook = XLSX.read(data, { type: 'array' });
          
          if (workbook.SheetNames.length === 0) {
            toast.error('The Excel file is empty.');
            return;
          }

          const firstSheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[firstSheetName];
          const json = XLSX.utils.sheet_to_json<any[]>(worksheet, { header: 1 });
          
          const emails: string[] = [];
          
          json.forEach(row => {
            if (Array.isArray(row)) {
              row.forEach(cell => {
                if (typeof cell === 'string' && cell.includes('@') && cell.includes('.')) {
                  emails.push(cell.trim());
                }
              });
            }
          });

          if (emails.length === 0) {
            toast.error('No valid emails found in the file.');
          } else {
            setCustomAudienceMap(prev => ({ ...prev, [surveyId]: emails }));
            toast.success(`Successfully loaded ${emails.length} emails from ${file.name}`);
          }
        } catch (err) {
          toast.error('Failed to parse file.');
        }
      };
      reader.readAsArrayBuffer(file);
    });
  };

  const handlePublish = () => {
    if (publishingSurvey) {
      publishMutation.mutate({ id: publishingSurvey.id, customEmails: customAudienceMap[publishingSurvey.id] });
    }
  };

  const handleClose = (id: number) => {
    if (window.confirm('Close this survey? Employees will no longer be able to submit responses.')) {
      closeMutation.mutate(id);
    }
  };

  const handleDelete = (id: number) => {
    if (window.confirm('Are you sure you want to delete this survey? This action cannot be undone.')) {
      deleteMutation.mutate(id);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'CLOSED':
      case 'ARCHIVED': return 'default';
      case 'SCHEDULED': return 'info';
      default: return 'warning';
    }
  };

  return (
    <Box sx={{ p: { xs: 2, md: 6 }, maxWidth: 'lg', mx: 'auto' }}>
      {/* Header */}
      <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, alignItems: { md: 'flex-end' }, justifyContent: 'space-between', gap: 2, mb: 4 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 'bold', color: 'text.primary' }}>
            Manage Surveys
          </Typography>
          <Typography sx={{ color: 'text.secondary', mt: 1 }}>
            Create, publish, and close employee surveys in your region.
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <Button
            onClick={() => refetch()}
            variant="outlined"
            sx={{ color: 'text.secondary', borderColor: 'divider', bgcolor: 'background.paper' }}
            startIcon={<RefreshCw size={16} className={isLoading ? 'animate-spin' : ''} />}
          >
            Refresh
          </Button>
          <Button
            onClick={() => setIsCreateModalOpen(true)}
            variant="contained"
            color="primary"
            startIcon={<Plus size={16} />}
          >
            Create Survey
          </Button>
        </Box>
      </Box>

      {/* Tabs */}
      <Box sx={{ mb: 4, bgcolor: 'background.paper', p: 0.5, borderRadius: 2, width: 'fit-content', border: 1, borderColor: 'divider' }}>
        <Tabs
          value={activeTab}
          onChange={(_, v) => setActiveTab(v)}
          sx={{ minHeight: 'unset', '& .MuiTab-root': { minHeight: 'unset', py: 1, px: 3, '&.Mui-selected': { color: 'primary.main', fontWeight: 'bold' } } }}
        >
          {(['DRAFT', 'SCHEDULED', 'ACTIVE', 'CLOSED', 'ARCHIVED'] as TabStatus[]).map((tab) => (
            <Tab key={tab} value={tab} label={tab} disableRipple />
          ))}
        </Tabs>
      </Box>

      {/* Content */}
      <Box sx={{ minHeight: 400 }}>
        {isLoading && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, gap: 2 }}>
            <Loader2 size={32} className="animate-spin" style={{ color: theme.palette.primary.main }} />
            <Typography color="text.secondary">Loading surveys...</Typography>
          </Box>
        )}

        {isError && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, gap: 2, color: 'error.main' }}>
            <AlertCircle size={48} />
            <Typography variant="h6">Failed to load surveys.</Typography>
          </Box>
        )}

        {!isLoading && !isError && (!surveys || surveys.length === 0) && (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 250, color: 'text.secondary', border: 1, borderStyle: 'dashed', borderColor: 'divider', borderRadius: 4, bgcolor: 'background.paper' }}>
            <MessageSquare size={48} style={{ opacity: 0.5, marginBottom: 16 }} />
            <Typography variant="h6">No {activeTab.toLowerCase()} surveys found.</Typography>
          </Box>
        )}

        <Grid container spacing={3}>
          {surveys?.map((survey) => (
            <Grid size={{ xs: 12, md: 6 }} key={survey.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', border: 1, borderColor: 'divider', boxShadow: theme.shadows[1], '&:hover': { borderColor: theme.palette.primary.main, boxShadow: `0 4px 20px 0 ${alpha(theme.palette.primary.main, 0.1)}` } }}>
                <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', p: 3 }}>
                  {/* Card Header */}
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Box>
                      <Chip label={survey.surveyType} size="small" sx={{ bgcolor: alpha(theme.palette.secondary.main, 0.1), color: theme.palette.secondary.light, mb: 1 }} />
                      <Typography variant="h6" sx={{ fontWeight: 600 }} color="text.primary">
                        {survey.title}
                      </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <IconButton
                        onClick={() => { setViewQuestionsSurveyId(survey.id); setViewQuestionsSurveyTitle(survey.title); }}
                        size="small"
                        sx={{ color: 'text.secondary', border: 1, borderColor: 'divider', borderRadius: 1, '&:hover': { color: 'primary.main' } }}
                        title="View Questions"
                      >
                        <Eye size={16} />
                      </IconButton>
                      <Chip label={survey.status} size="small" color={getStatusColor(survey.status) as any} variant="outlined" sx={{ fontWeight: 'medium', borderRadius: 1 }} />
                    </Box>
                  </Box>

                  <Typography variant="body2" color="text.secondary" sx={{ mb: 3, flexGrow: 1 }}>
                    {survey.description}
                  </Typography>

                  {/* Dates */}
                  <Grid container spacing={2} sx={{ mb: 3 }}>
                    <Grid size={{ xs: 6 }}>
                      <Box sx={{ bgcolor: alpha(theme.palette.background.default, 0.5), p: 1.5, borderRadius: 2, border: 1, borderColor: 'divider' }}>
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mb: 0.5 }}>
                          <Calendar size={14} /> Start Date
                        </Typography>
                        <Typography variant="body2" color="text.primary" sx={{ fontWeight: 'medium' }}>
                          {new Date(survey.startDate).toLocaleDateString()}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid size={{ xs: 6 }}>
                      <Box sx={{ bgcolor: alpha(theme.palette.background.default, 0.5), p: 1.5, borderRadius: 2, border: 1, borderColor: 'divider' }}>
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mb: 0.5 }}>
                          <Calendar size={14} /> End Date
                        </Typography>
                        <Typography variant="body2" color="text.primary" sx={{ fontWeight: 'medium' }}>
                          {new Date(survey.endDate).toLocaleDateString()}
                        </Typography>
                      </Box>
                    </Grid>
                  </Grid>

                  {/* Actions */}
                  <Box sx={{ mt: 'auto', pt: 2, borderTop: 1, borderColor: 'divider' }}>
                    {activeTab === 'DRAFT' && (
                      <Box sx={{ display: 'flex', gap: 1.5, flexWrap: 'wrap' }}>
                        <Button
                          onClick={() => { setEditingSurvey(survey); setIsEditModalOpen(true); }}
                          fullWidth
                          variant="outlined"
                          startIcon={<Edit2 size={16} />}
                          sx={{ color: 'primary.main', borderColor: alpha(theme.palette.primary.main, 0.5), flex: 1, minWidth: '80px' }}
                        >
                          Edit
                        </Button>
                        <Button
                          onClick={() => handleDelete(survey.id)}
                          disabled={deleteMutation.isPending}
                          fullWidth
                          variant="outlined"
                          startIcon={<Trash2 size={16} />}
                          sx={{ color: 'error.main', borderColor: alpha(theme.palette.error.main, 0.5), flex: 1, minWidth: '80px' }}
                        >
                          Delete
                        </Button>
                        <Button
                          onClick={handleOpenReportMenu}
                          fullWidth
                          variant="outlined"
                          startIcon={<BarChart3 size={16} />}
                          sx={{ 
                            color: 'info.main',
                            borderColor: alpha(theme.palette.info.main, 0.2),
                            bgcolor: alpha(theme.palette.info.main, 0.05),
                            '&:hover': { bgcolor: alpha(theme.palette.info.main, 0.1) }
                          }}
                        >
                          View Reports
                        </Button>
                        <Button
                          onClick={() => setActiveSurveyForQuestions(survey.id)}
                          fullWidth
                          variant="outlined"
                          startIcon={<PlusCircle size={16} />}
                          sx={{ color: 'text.secondary', borderColor: 'divider', flex: 1, minWidth: '100px' }}
                        >
                          Questions
                        </Button>
                        <Button
                          variant="outlined"
                          component="label"
                          startIcon={<UploadCloud size={16} />}
                          sx={{ color: 'secondary.main', borderColor: alpha(theme.palette.secondary.main, 0.5), flex: 1, minWidth: '110px' }}
                        >
                          {customAudienceMap[survey.id] ? `Audience (${customAudienceMap[survey.id].length})` : 'Audience'}
                          <input
                            type="file"
                            hidden
                            accept=".xlsx,.xls,.csv"
                            onChange={(e) => handleFileUpload(e, survey.id)}
                          />
                        </Button>
                        <Button
                          onClick={() => { setPublishingSurvey(survey); setIsPublishModalOpen(true); }}
                          disabled={publishMutation.isPending}
                          fullWidth
                          variant="outlined"
                          color="success"
                          startIcon={<Send size={16} />}
                          sx={{ bgcolor: alpha(theme.palette.success.main, 0.1), flex: 1, minWidth: '100px' }}
                        >
                          Publish
                        </Button>
                      </Box>
                    )}

                    {(activeTab === 'ACTIVE' || activeTab === 'SCHEDULED') && (
                      <Box sx={{ display: 'flex', gap: 1.5 }}>
                        <Box sx={{ flex: 1, bgcolor: alpha(theme.palette.background.default, 0.5), py: 1, px: 2, borderRadius: 2, border: 1, borderColor: 'divider', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                          <Typography variant="caption" color="text.secondary">Responses</Typography>
                          <Typography variant="body2" sx={{ fontWeight: 'medium' }} color="text.primary">{survey.completedParticipants} / {survey.expectedParticipants}</Typography>
                        </Box>
                        <Button
                          onClick={() => {
                            if (window.confirm('Move this survey back to DRAFT?')) {
                              reactivateMutation.mutate(survey.id);
                            }
                          }}
                          disabled={reactivateMutation.isPending}
                          sx={{ flex: 1, bgcolor: alpha(theme.palette.warning.main, 0.1), color: 'warning.main', borderColor: alpha(theme.palette.warning.main, 0.2) }}
                          variant="outlined"
                          startIcon={<Edit2 size={16} />}
                        >
                          Move to Draft
                        </Button>
                        <Button
                          onClick={() => handleClose(survey.id)}
                          disabled={closeMutation.isPending}
                          sx={{ flex: 1, bgcolor: alpha(theme.palette.error.main, 0.1), color: 'error.main', borderColor: alpha(theme.palette.error.main, 0.2) }}
                          variant="outlined"
                          startIcon={<StopCircle size={16} />}
                        >
                          Close Survey
                        </Button>
                      </Box>
                    )}

                    {(activeTab === 'CLOSED' || activeTab === 'ARCHIVED') && (
                      <Box sx={{ display: 'flex', gap: 1.5, alignItems: 'center' }}>
                        <Box sx={{ flex: 1, bgcolor: alpha(theme.palette.background.default, 0.5), py: 1, px: 2, borderRadius: 2, border: 1, borderColor: 'divider', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                          <Typography variant="caption" color="text.secondary">Final</Typography>
                          <Typography variant="body2" sx={{ fontWeight: 'medium' }} color="text.primary">{survey.completedParticipants} / {survey.expectedParticipants}</Typography>
                        </Box>
                        <Button
                          onClick={() => {
                            if (window.confirm('Reactivate this survey? It will become ACTIVE again.')) {
                              reactivateMutation.mutate(survey.id);
                            }
                          }}
                          disabled={reactivateMutation.isPending}
                          sx={{ flex: 1, bgcolor: alpha(theme.palette.info.main, 0.1), color: 'info.main', borderColor: alpha(theme.palette.info.main, 0.2) }}
                          variant="outlined"
                          startIcon={<PlayCircle size={16} />}
                        >
                          Reactivate
                        </Button>
                      </Box>
                    )}
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>

      <Menu
        anchorEl={reportMenuAnchor}
        open={Boolean(reportMenuAnchor)}
        onClose={handleCloseReportMenu}
        slotProps={{
          paper: {
            elevation: 3,
            sx: { mt: 1, minWidth: 200, borderRadius: 2 }
          }
        }}
      >
        {!reports || reports.length === 0 ? (
          <MenuItem disabled>No reports available</MenuItem>
        ) : (
          reports.map(report => (
            <MenuItem 
              key={report.id} 
              onClick={() => {
                setSelectedReport(report);
                handleCloseReportMenu();
              }}
            >
              {new Date(report.year, report.month - 1).toLocaleString('default', { month: 'long', year: 'numeric' })} Report
            </MenuItem>
          ))
        )}
      </Menu>

      <CreateSurveyModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSubmit={(data) => createMutation.mutate(data)}
        isLoading={createMutation.isPending}
      />

      <AddQuestionsModal
        isOpen={activeSurveyForQuestions !== null}
        onClose={() => setActiveSurveyForQuestions(null)}
        onSubmit={(questionIds) => {
          if (activeSurveyForQuestions) {
            addQuestionsMutation.mutate({ id: activeSurveyForQuestions, questionIds });
          }
        }}
        isLoading={addQuestionsMutation.isPending}
      />

      <ViewQuestionsModal
        isOpen={viewQuestionsSurveyId !== null}
        onClose={() => { setViewQuestionsSurveyId(null); setViewQuestionsSurveyTitle(''); }}
        surveyId={viewQuestionsSurveyId}
        surveyTitle={viewQuestionsSurveyTitle}
        isDraft={activeTab === 'DRAFT'}
      />

      <EditSurveyModal
        isOpen={isEditModalOpen}
        onClose={() => { setIsEditModalOpen(false); setEditingSurvey(null); }}
        survey={editingSurvey}
        onSubmit={(id, payload) => updateMutation.mutate({ id, payload })}
        isLoading={updateMutation.isPending}
      />

      <PublishSurveyModal
        isOpen={isPublishModalOpen}
        onClose={() => { setIsPublishModalOpen(false); setPublishingSurvey(null); }}
        surveyTitle={publishingSurvey?.title || ''}
        onPublish={handlePublish}
        isLoading={publishMutation.isPending}
      />
      <ReportDetailsModal
        isOpen={selectedReport !== null}
        onClose={() => setSelectedReport(null)}
        report={selectedReport}
        onDownloadPdf={downloadReportPdf}
        onDownloadCsv={downloadReportCsv}
      />
    </Box>
  );
};
