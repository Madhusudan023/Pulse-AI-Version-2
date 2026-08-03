import { api } from './api';

export type Department = 'HR' | 'ENGINEERING' | 'SALES' | 'MARKETING' | 'FINANCE' | 'OPERATIONS' | 'IT';
export type Region = 'GLOBAL' | 'CHENNAI' | 'HYDERABAD' | 'PUNE' | 'BANGALORE' | 'NEW_YORK' | 'LONDON' | 'SINGAPORE';
export type Role = 'EMPLOYEE' | 'REGIONAL_HR' | 'GLOBAL_HR' | 'VP';

export interface Employee {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  designation: string;
  department: Department;
  businessUnit: string;
  region: Region;
  managerId: number | null;
  role: Role;
  joiningDate: string;
  active: boolean;
  createdAt: string;
}

export interface CreateEmployeeRequest {
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
  designation: string;
  department: Department;
  businessUnit: string;
  region: Region;
  managerId: number | null;
  role: Role;
  joiningDate: string;
}

export interface UpdateEmployeeRequest {
  firstName: string;
  lastName: string;
  designation: string;
  department: Department;
  businessUnit: string;
  region: Region;
  managerId: number | null;
  role: Role;
  active: boolean;
}

export const getEmployees = async (): Promise<Employee[]> => {
  const response = await api.get('/employees');
  return response.data.data;
};

export type EmployeeProfile = Employee;

export const getEmployeeProfile = async (): Promise<EmployeeProfile> => {
  const response = await api.get('/employees/me');
  return response.data.data;
};

export const createEmployee = async (data: CreateEmployeeRequest): Promise<void> => {
  await api.post('/employees', data);
};

export const createRegionalHr = async (data: CreateEmployeeRequest): Promise<void> => {
  await api.post('/employees/regional-hrs', data);
};

export const updateEmployee = async (id: number, data: UpdateEmployeeRequest): Promise<void> => {
  await api.put(`/employees/${id}`, data);
};