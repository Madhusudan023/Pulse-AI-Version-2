import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { useAuthStore } from '../../store/useAuthStore';
import { type CreateSurveyRequest, type Survey } from '../../services/survey.service';
import {
  Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  Select, MenuItem, Button, IconButton, Typography, Box,
  FormControl, InputLabel, useTheme
} from '@mui/material';
import { Grid } from '@mui/material';
import { alpha } from '@mui/material/styles';

interface EditSurveyModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (id: number, payload: CreateSurveyRequest) => void;
  isLoading: boolean;
  survey: Survey | null;
}

export const EditSurveyModal = ({ isOpen, onClose, onSubmit, isLoading, survey }: EditSurveyModalProps) => {
  const theme = useTheme();
  const region = useAuthStore(state => state.region) || 'CHENNAI';

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [surveyType, setSurveyType] = useState('MONTHLY_PULSE');
  const [targetAudience, setTargetAudience] = useState('ALL');
  
  const [month, setMonth] = useState(1);
  const [year, setYear] = useState(2026);
  
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  useEffect(() => {
    if (survey && isOpen) {
      setTitle(survey.title);
      setDescription(survey.description);
      setSurveyType(survey.surveyType || 'MONTHLY_PULSE');
      setTargetAudience(survey.targetAudience || 'ALL');
      setMonth(survey.month || new Date().getMonth() + 1);
      setYear(survey.year || new Date().getFullYear());
      setStartDate(survey.startDate ? new Date(survey.startDate).toISOString().slice(0, 16) : '');
      setEndDate(survey.endDate ? new Date(survey.endDate).toISOString().slice(0, 16) : '');
    }
  }, [survey, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (survey) {
      onSubmit(survey.id, {
        title, description, region, surveyType,
        month: Number(month), year: Number(year),
        targetAudience,
        startDate: startDate ? (startDate.length === 16 ? startDate + ":00" : startDate) : null,
        endDate: endDate ? (endDate.length === 16 ? endDate + ":00" : endDate) : null
      });
    }
  };

  return (
    <Dialog 
      open={isOpen} 
      onClose={onClose}
      fullWidth
      maxWidth="md"
      slotProps={{
        paper: {
          sx: {
            borderRadius: 3,
            backgroundColor: alpha(theme.palette.background.paper, 0.95),
            backdropFilter: 'blur(10px)',
            backgroundImage: 'none',
          }
        }
      }}
    >
      <DialogTitle sx={{ 
        m: 0, p: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
        borderBottom: '1px solid', borderColor: 'divider'
      }}>
        <Box>
          <Typography variant="h5" color="text.primary" sx={{ fontWeight: 700 }}>
            Edit Survey
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

      <form onSubmit={handleSubmit}>
        <DialogContent dividers sx={{ p: 3, backgroundColor: alpha(theme.palette.background.default, 0.5), display: 'flex', flexDirection: 'column', gap: 3 }}>
          <TextField
            label="Title"
            required
            fullWidth
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="e.g. July Engineering Pulse Survey"
            variant="outlined"
          />

          <TextField
            label="Description"
            required
            fullWidth
            multiline
            rows={2}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Describe the purpose of this survey..."
            variant="outlined"
          />

          <Grid container spacing={2}>
            <Grid size={{ xs: 12, md: 3 }}>
              <FormControl fullWidth>
                <InputLabel>Survey Type</InputLabel>
                <Select
                  value={surveyType}
                  onChange={(e) => setSurveyType(e.target.value)}
                  label="Survey Type"
                >
                  <MenuItem value="MONTHLY_PULSE">Monthly Pulse</MenuItem>
                  <MenuItem value="ONBOARDING">Onboarding (Legacy)</MenuItem>
                  <MenuItem value="FIRST_MONTH_SURVEY">1st Month Survey</MenuItem>
                  <MenuItem value="SECOND_MONTH_SURVEY">2nd Month Survey</MenuItem>
                  <MenuItem value="THIRD_MONTH_SURVEY">3rd Month Survey</MenuItem>
                  <MenuItem value="FOURTH_MONTH_SURVEY">4th Month Survey</MenuItem>
                  <MenuItem value="FIFTH_MONTH_SURVEY">5th Month Survey</MenuItem>
                  <MenuItem value="SIXTH_MONTH_SURVEY">6th Month Survey</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid size={{ xs: 12, md: 3 }}>
              <FormControl fullWidth>
                <InputLabel>Target Audience</InputLabel>
                <Select
                  value={targetAudience}
                  onChange={(e) => setTargetAudience(e.target.value)}
                  label="Target Audience"
                >
                  <MenuItem value="ALL">All Employees</MenuItem>
                  <MenuItem value="NEW_JOINERS">New Joiners (&lt; 6 months)</MenuItem>
                  <MenuItem value="TENURED">Tenured (&gt; 6 months)</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid size={{ xs: 12, md: 3 }}>
              <FormControl fullWidth>
                <InputLabel>Month</InputLabel>
                <Select
                  value={month}
                  onChange={(e) => setMonth(Number(e.target.value))}
                  label="Month"
                >
                  {Array.from({ length: 12 }, (_, i) => i + 1).map(m => (
                    <MenuItem key={m} value={m}>
                      {new Date(0, m - 1).toLocaleString('default', { month: 'long' })}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            <Grid size={{ xs: 12, md: 3 }}>
              <TextField
                label="Year"
                type="number"
                required
                fullWidth
                slotProps={{ htmlInput: { min: 2020, max: 2030 } }}
                value={year}
                onChange={(e) => setYear(Number(e.target.value))}
              />
            </Grid>
          </Grid>

          <Grid container spacing={2}>
            <Grid size={{ xs: 12, md: 6 }}>
              <TextField
                label="Start Date"
                type="datetime-local"
                required
                fullWidth
                slotProps={{ inputLabel: { shrink: true } }}
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
              />
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <TextField
                label="End Date"
                type="datetime-local"
                required
                fullWidth
                slotProps={{ inputLabel: { shrink: true } }}
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
              />
            </Grid>
          </Grid>
        </DialogContent>

        <DialogActions sx={{ p: 2, pt: 3 }}>
          <Button onClick={onClose} sx={{ color: 'text.secondary' }}>
            Cancel
          </Button>
          <Button 
            type="submit" 
            variant="contained" 
            color="primary"
            disabled={isLoading}
            sx={{ boxShadow: `0 4px 14px 0 ${alpha(theme.palette.primary.main, 0.25)}` }}
          >
            {isLoading ? 'Saving...' : 'Save Changes'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

