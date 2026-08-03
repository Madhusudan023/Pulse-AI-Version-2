import React, { useState } from 'react';
import { 
  Dialog, 
  DialogTitle, 
  DialogContent, 
  DialogActions,
  Box,
  Typography,
  IconButton,
  Button,
  Grid,
  TextField,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  CircularProgress
} from '@mui/material';
import { X } from 'lucide-react';
import { alpha, useTheme } from '@mui/material/styles';
import type { CreateEmployeeRequest, Department, Region, Role } from '../../services/employee.service';

interface CreateEmployeeModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateEmployeeRequest) => void;
  isSubmitting: boolean;
}

const DEPARTMENTS: Department[] = ['HR', 'ENGINEERING', 'SALES', 'MARKETING', 'FINANCE', 'OPERATIONS', 'IT'];
const REGIONS: Region[] = ['GLOBAL', 'CHENNAI', 'HYDERABAD', 'PUNE', 'BANGALORE', 'NEW_YORK', 'LONDON', 'SINGAPORE'];
const ROLES: Role[] = ['EMPLOYEE', 'REGIONAL_HR', 'GLOBAL_HR', 'VP'];

export const CreateEmployeeModal = ({ isOpen, onClose, onSubmit, isSubmitting }: CreateEmployeeModalProps) => {
  const theme = useTheme();
  
  const [formData, setFormData] = useState<CreateEmployeeRequest>({
    employeeCode: '',
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    designation: '',
    department: 'ENGINEERING',
    businessUnit: '',
    region: 'CHENNAI',
    managerId: null,
    role: 'EMPLOYEE',
    joiningDate: new Date().toISOString().split('T')[0],
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  const handleChange = (e: any) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'managerId' ? (value ? parseInt(value) : null) : value
    }));
  };

  return (
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
      <form onSubmit={handleSubmit}>
        <DialogTitle sx={{ 
          m: 0, p: 3, display: 'flex', justifyItems: 'space-between', justifyContent: 'space-between', alignItems: 'flex-start',
          borderBottom: '1px solid', borderColor: 'divider'
        }}>
          <Box>
            <Typography variant="h5" color="text.primary" sx={{ fontWeight: 700 }}>
              Add New Employee
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
              Create a new employee profile in the system
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
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                required
                fullWidth
                label="First Name"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                variant="outlined"
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                required
                fullWidth
                label="Last Name"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                variant="outlined"
              />
            </Grid>
            
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                required
                fullWidth
                label="Email"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                variant="outlined"
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                required
                fullWidth
                label="Initial Password"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                variant="outlined"
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                required
                fullWidth
                label="Employee Code"
                name="employeeCode"
                value={formData.employeeCode}
                onChange={handleChange}
                variant="outlined"
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                required
                fullWidth
                label="Joining Date"
                name="joiningDate"
                type="date"
                InputLabelProps={{ shrink: true }}
                value={formData.joiningDate}
                onChange={handleChange}
                variant="outlined"
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                required
                fullWidth
                label="Designation"
                name="designation"
                value={formData.designation}
                onChange={handleChange}
                variant="outlined"
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                required
                fullWidth
                label="Business Unit"
                name="businessUnit"
                value={formData.businessUnit}
                onChange={handleChange}
                variant="outlined"
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 4 }}>
              <FormControl fullWidth>
                <InputLabel>Role</InputLabel>
                <Select
                  name="role"
                  value={formData.role}
                  label="Role"
                  onChange={handleChange}
                >
                  {ROLES.map(r => <MenuItem key={r} value={r}>{r.replace('_', ' ')}</MenuItem>)}
                </Select>
              </FormControl>
            </Grid>
            <Grid size={{ xs: 12, sm: 4 }}>
              <FormControl fullWidth>
                <InputLabel>Department</InputLabel>
                <Select
                  name="department"
                  value={formData.department}
                  label="Department"
                  onChange={handleChange}
                >
                  {DEPARTMENTS.map(d => <MenuItem key={d} value={d}>{d}</MenuItem>)}
                </Select>
              </FormControl>
            </Grid>
            <Grid size={{ xs: 12, sm: 4 }}>
              <FormControl fullWidth>
                <InputLabel>Region</InputLabel>
                <Select
                  name="region"
                  value={formData.region}
                  label="Region"
                  onChange={handleChange}
                >
                  {REGIONS.map(r => <MenuItem key={r} value={r}>{r.replace('_', ' ')}</MenuItem>)}
                </Select>
              </FormControl>
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                label="Manager ID (Optional)"
                name="managerId"
                type="number"
                value={formData.managerId || ''}
                onChange={handleChange}
                variant="outlined"
                helperText="Leave blank if no manager"
              />
            </Grid>
          </Grid>
        </DialogContent>
        
        <DialogActions sx={{ p: 3, display: 'flex', justifyContent: 'flex-end', gap: 2, bgcolor: 'background.paper' }}>
          <Button 
            variant="outlined" 
            color="inherit" 
            onClick={onClose}
            disabled={isSubmitting}
            sx={{ borderColor: 'divider' }}
          >
            Cancel
          </Button>
          <Button 
            type="submit"
            variant="contained" 
            color="primary" 
            disabled={isSubmitting}
            startIcon={isSubmitting ? <CircularProgress size={16} color="inherit" /> : null}
          >
            {isSubmitting ? 'Creating...' : 'Create Employee'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

