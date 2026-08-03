import React from 'react';
import { 
  Dialog, DialogTitle, DialogContent, DialogActions,
  Box, Typography, IconButton, Button, Avatar
} from '@mui/material';
import { X, Mail, MapPin, Briefcase } from 'lucide-react';
import { alpha, useTheme } from '@mui/material/styles';
import { type AuthData } from '../../services/auth.service';

interface ProfileModalProps {
  isOpen: boolean;
  onClose: () => void;
  user: AuthData | null;
}

export const ProfileModal: React.FC<ProfileModalProps> = ({ isOpen, onClose, user }) => {
  const theme = useTheme();

  if (!user || !isOpen) return null;

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
      <DialogTitle sx={{ 
        m: 0, p: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
        borderBottom: '1px solid', borderColor: 'divider'
      }}>
        <Box>
          <Typography variant="h5" color="text.primary" sx={{ fontWeight: 700 }}>
            User Profile
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
      
      <DialogContent sx={{ p: 4, display: 'flex', flexDirection: 'column', alignItems: 'center', backgroundColor: alpha(theme.palette.background.default, 0.5) }}>
        <Avatar 
          sx={{ 
            width: 100, 
            height: 100, 
            bgcolor: alpha(theme.palette.primary.main, 0.1),
            color: 'primary.main',
            fontSize: '3rem',
            mb: 3,
            border: `2px solid ${theme.palette.primary.main}`
          }}
        >
          {user.email.charAt(0).toUpperCase()}
        </Avatar>
        
        <Typography variant="h5" sx={{ fontWeight: 600, mb: 1, color: 'text.primary' }}>
          {user.email.split('@')[0].replace('.', ' ').replace(/\b\w/g, l => l.toUpperCase())}
        </Typography>
        
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, width: '100%', mt: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, p: 2, bgcolor: 'background.paper', borderRadius: 2, border: '1px solid', borderColor: 'divider' }}>
            <Mail size={20} color={theme.palette.text.secondary} />
            <Box>
              <Typography variant="caption" color="text.secondary">Email Address</Typography>
              <Typography variant="body1" color="text.primary">{user.email}</Typography>
            </Box>
          </Box>
          
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, p: 2, bgcolor: 'background.paper', borderRadius: 2, border: '1px solid', borderColor: 'divider' }}>
            <Briefcase size={20} color={theme.palette.text.secondary} />
            <Box>
              <Typography variant="caption" color="text.secondary">Job Role</Typography>
              <Typography variant="body1" color="text.primary">{user.role.replace('_', ' ')}</Typography>
            </Box>
          </Box>
          
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, p: 2, bgcolor: 'background.paper', borderRadius: 2, border: '1px solid', borderColor: 'divider' }}>
            <MapPin size={20} color={theme.palette.text.secondary} />
            <Box>
              <Typography variant="caption" color="text.secondary">Location</Typography>
              <Typography variant="body1" color="text.primary">{user.region.replace('_', ' ')}</Typography>
            </Box>
          </Box>
        </Box>
      </DialogContent>
      
      <DialogActions sx={{ p: 3, justifyContent: 'center', bgcolor: 'background.paper', borderTop: '1px solid', borderColor: 'divider' }}>
        <Button 
          variant="contained" 
          color="primary" 
          onClick={onClose}
          fullWidth
          sx={{ py: 1.5 }}
        >
          Close
        </Button>
      </DialogActions>
    </Dialog>
  );
};

