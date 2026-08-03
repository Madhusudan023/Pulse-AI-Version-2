import React, { useState } from 'react';
import { X } from 'lucide-react';
import { useAuthStore } from '../../store/useAuthStore';
import { type CreateSurveyRequest } from '../../services/survey.service';
import {
  Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  Select, MenuItem, Button, IconButton, Typography, Box,
  FormControl, InputLabel, useTheme, Switch, FormControlLabel
} from '@mui/material';
import { Grid } from '@mui/material';
import { alpha } from '@mui/material/styles';

interface CreateSurveyModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (payload: CreateSurveyRequest) => void;
  isLoading: boolean;
}

export const CreateSurveyModal = ({ isOpen, onClose, onSubmit, isLoading }: CreateSurveyModalProps) => {
  const theme = useTheme();
  const region = useAuthStore(state => state.region) || 'CHENNAI';

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [surveyType, setSurveyType] = useState('MONTHLY_PULSE');
  const [targetAudience, setTargetAudience] = useState('ALL');
  const [isAnonymous, setIsAnonymous] = useState(true);
  
  const currentDate = new Date();
  const [month, setMonth] = useState(currentDate.getMonth() + 1);
  const [year, setYear] = useState(currentDate.getFullYear());
  
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      title, description, region, surveyType,
      month: Number(month), year: Number(year),
      targetAudience, isAnonymous,
      startDate: startDate ? (startDate.length === 16 ? startDate + ":00" : startDate) : null,
      endDate: endDate ? (endDate.length === 16 ? endDate + ":00" : endDate) : null
    } as CreateSurveyRequest);
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
            Create New Survey
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
                  <MenuItem value="FIRST_MONTH_SURVEY">1st Month Template</MenuItem>
                  <MenuItem value="SECOND_MONTH_SURVEY">2nd Month Template</MenuItem>
                  <MenuItem value="THIRD_MONTH_SURVEY">3rd Month Template</MenuItem>
                  <MenuItem value="FOURTH_MONTH_SURVEY">4th Month Template</MenuItem>
                  <MenuItem value="FIFTH_MONTH_SURVEY">5th Month Template</MenuItem>
                  <MenuItem value="SIXTH_MONTH_SURVEY">6th Month Template</MenuItem>
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
          
          <Box sx={{ mt: 1 }}>
            <FormControlLabel
              control={<Switch checked={isAnonymous} onChange={(e) => setIsAnonymous(e.target.checked)} color="primary" />}
              label="Anonymous Survey (Employees submit without their email being recorded)"
            />
          </Box>
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
            {isLoading ? 'Creating...' : 'Create Survey'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};
