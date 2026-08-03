import React from 'react';
import { X } from 'lucide-react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions, Button, IconButton, Typography, Box, useTheme
} from '@mui/material';
import { alpha } from '@mui/material/styles';

interface PublishSurveyModalProps {
  isOpen: boolean;
  onClose: () => void;
  onPublish: () => void;
  isLoading: boolean;
  surveyTitle: string;
}

export const PublishSurveyModal = ({ isOpen, onClose, onPublish, isLoading, surveyTitle }: PublishSurveyModalProps) => {
  const theme = useTheme();

  if (!isOpen) return null;

  return (
    <Dialog 
      open={isOpen} 
      onClose={onClose}
      fullWidth
      maxWidth="sm"
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
            Publish Survey
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            {surveyTitle}
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

      <DialogContent sx={{ p: 3, display: 'flex', flexDirection: 'column', gap: 3 }}>
        <Typography variant="body1">
          This survey will be sent to the target audience defined in the survey settings.
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Are you sure you want to publish this survey now?
        </Typography>
      </DialogContent>

      <DialogActions sx={{ p: 2, pt: 3, borderTop: '1px solid', borderColor: 'divider' }}>
        <Button onClick={onClose} sx={{ color: 'text.secondary' }}>
          Cancel
        </Button>
        <Button 
          onClick={() => onPublish()}
          variant="contained" 
          color="primary"
          disabled={isLoading}
          sx={{ boxShadow: `0 4px 14px 0 ${alpha(theme.palette.primary.main, 0.25)}` }}
        >
          {isLoading ? 'Publishing...' : 'Publish Survey'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

