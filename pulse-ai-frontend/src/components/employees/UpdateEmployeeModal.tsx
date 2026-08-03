import React, { useState, useEffect } from 'react';
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
  CircularProgress,
  FormControlLabel,
  Switch
} from '@mui/material';
import { X } from 'lucide-react';
import { alpha, useTheme } from '@mui/material/styles';
import type { Employee, Department, Region, Role } from '../../services/employee.service';

interface UpdateEmployeeModalProps {
  isOpen: boolean;
  onClose: () => void;
  employee: Employee | null;
  onSubmit: (id: number, data: any) => void;
  isSubmitting: boolean;
}

const DEPARTMENTS: Department[] = ['HR', 'ENGINEERING', 'SALES', 'MARKETING', 'FINANCE', 'OPERATIONS', 'IT'];
const REGIONS: Region[] = ['GLOBAL', 'CHENNAI', 'HYDERABAD', 'PUNE', 'BANGALORE', 'NEW_YORK', 'LONDON', 'SINGAPORE'];
const ROLES: Role[] = ['EMPLOYEE', 'REGIONAL_HR', 'GLOBAL_HR', 'VP'];

export const UpdateEmployeeModal = ({ isOpen, onClose, employee, onSubmit, isSubmitting }: UpdateEmployeeModalProps) => {
  const theme = useTheme();
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    designation: '',
    department: 'ENGINEERING',
    businessUnit: '',
    region: 'CHENNAI',
    managerId: '' as string | number,
    role: 'EMPLOYEE',
    active: true,
  });

  useEffect(() => {
    if (employee) {
      setFormData({
        firstName: employee.firstName,
        lastName: employee.lastName,
        designation: employee.designation,
        department: employee.department,
        businessUnit: employee.businessUnit,
        region: employee.region,
        managerId: employee.managerId || '',
        role: employee.role,
        active: employee.active,
      });
    }
  }, [employee]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!employee) return;
    onSubmit(employee.id, {
      ...formData,
      managerId: formData.managerId === '' ? null : Number(formData.managerId)
    });
  };

  const handleChange = (e: any) => {
    const { name, value, checked, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  if (!employee) return null;

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
              Update Employee
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
              Modifying {employee.employeeCode} - {employee.email}
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
                value={formData.managerId}
                onChange={handleChange}
                variant="outlined"
                helperText="Leave blank if no manager"
              />
            </Grid>
            
            <Grid size={{ xs: 12, sm: 6 }} sx={{ display: 'flex', alignItems: 'center' }}>
              <FormControlLabel
                control={
                  <Switch
                    checked={formData.active}
                    onChange={handleChange}
                    name="active"
                    color="success"
                  />
                }
                label={formData.active ? "Account Active" : "Account Deactivated"}
                sx={{ ml: 1, '& .MuiFormControlLabel-label': { fontWeight: 500, color: formData.active ? 'success.main' : 'text.secondary' } }}
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
            {isSubmitting ? 'Saving...' : 'Save Changes'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

