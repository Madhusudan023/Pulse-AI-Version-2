-- Insert HRs
INSERT IGNORE INTO employees (employee_code, first_name, last_name, email, designation, department, business_unit, region, role, joining_date, active, created_at, updated_at) VALUES 
('EMP-HR-01', 'Shivani', 'Vanga', 'shivanivanga01@gmail.com', 'Regional HR', 'HR', 'Core', 'PUNE', 'REGIONAL_HR', '2020-01-15', true, NOW(), NOW()),
('EMP-HR-02', 'Aditi', 'Sharma', 'hr.bangalore@pulseai.com', 'Regional HR', 'HR', 'Core', 'BANGALORE', 'REGIONAL_HR', '2020-02-10', true, NOW(), NOW()),
('EMP-HR-03', 'Rohan', 'Mehta', 'hr.mumbai@pulseai.com', 'Regional HR', 'HR', 'Core', 'MUMBAI', 'REGIONAL_HR', '2021-03-20', true, NOW(), NOW()),
('EMP-HR-04', 'Kiran', 'Rao', 'hr.hyderabad@pulseai.com', 'Regional HR', 'HR', 'Core', 'HYDERABAD', 'REGIONAL_HR', '2021-05-12', true, NOW(), NOW()),
('EMP-HR-05', 'Nitya', 'Kumar', 'hr.chennai@pulseai.com', 'Regional HR', 'HR', 'Core', 'CHENNAI', 'REGIONAL_HR', '2022-07-25', true, NOW(), NOW()),
('EMP-HR-06', 'Vikram', 'Singh', 'hr.thane@pulseai.com', 'Regional HR', 'HR', 'Core', 'THANE', 'REGIONAL_HR', '2023-09-30', true, NOW(), NOW());

-- Insert Employees under PUNE HR (> 6 months and < 6 months experience)
INSERT IGNORE INTO employees (employee_code, first_name, last_name, email, designation, department, business_unit, region, role, joining_date, active, created_at, updated_at) VALUES 
('EMP-PN-01', 'Manikanta', 'Alamanda', 'manikantaalamanda5@gmail.com', 'Software Engineer', 'ENGINEERING', 'Digital', 'PUNE', 'EMPLOYEE', '2025-01-01', true, NOW(), NOW()), -- > 6 months
('EMP-PN-02', 'Sneha', 'Patil', 'sneha.patil@pulseai.com', 'QA Analyst', 'ENGINEERING', 'Digital', 'PUNE', 'EMPLOYEE', '2024-05-15', true, NOW(), NOW()), -- > 6 months
('EMP-PN-03', 'Madhusudan', 'Badgujar', 'madhusudanbadgujar260@gmail.com', 'Full Stack Developer', 'ENGINEERING', 'Digital', 'PUNE', 'EMPLOYEE', '2026-06-01', true, NOW(), NOW()), -- < 6 months
('EMP-PN-04', 'Pooja', 'Pentela', 'poojapentela13@gmail.com', 'UI Designer', 'DESIGN', 'Digital', 'PUNE', 'EMPLOYEE', '2026-07-10', true, NOW(), NOW()), -- < 6 months
('EMP-PN-05', 'Hindu', 'V', 'hindu0528@gmail.com', 'DevOps Engineer', 'ENGINEERING', 'Digital', 'PUNE', 'EMPLOYEE', '2026-05-15', true, NOW(), NOW()); -- < 6 months

-- Insert Employees under BANGALORE HR
INSERT IGNORE INTO employees (employee_code, first_name, last_name, email, designation, department, business_unit, region, role, joining_date, active, created_at, updated_at) VALUES 
('EMP-BL-01', 'Anand', 'Iyer', 'anand.iyer@pulseai.com', 'Senior Engineer', 'ENGINEERING', 'Data', 'BANGALORE', 'EMPLOYEE', '2023-06-15', true, NOW(), NOW()),
('EMP-BL-02', 'Divya', 'Reddy', 'divya.reddy@pulseai.com', 'Data Scientist', 'DATA', 'Data', 'BANGALORE', 'EMPLOYEE', '2024-02-10', true, NOW(), NOW()),
('EMP-BL-03', 'Arjun', 'Menon', 'arjun.menon@pulseai.com', 'QA Engineer', 'ENGINEERING', 'Data', 'BANGALORE', 'EMPLOYEE', '2025-08-20', true, NOW(), NOW()),
('EMP-BL-04', 'Preeti', 'Nair', 'preeti.nair@pulseai.com', 'Frontend Developer', 'ENGINEERING', 'Data', 'BANGALORE', 'EMPLOYEE', '2026-03-05', true, NOW(), NOW()),
('EMP-BL-05', 'Karthik', 'Gowda', 'karthik.gowda@pulseai.com', 'Backend Developer', 'ENGINEERING', 'Data', 'BANGALORE', 'EMPLOYEE', '2026-06-15', true, NOW(), NOW());

-- Insert Employees under MUMBAI HR
INSERT IGNORE INTO employees (employee_code, first_name, last_name, email, designation, department, business_unit, region, role, joining_date, active, created_at, updated_at) VALUES 
('EMP-MB-01', 'Ravi', 'Shah', 'ravi.shah@pulseai.com', 'Finance Analyst', 'FINANCE', 'Corporate', 'MUMBAI', 'EMPLOYEE', '2021-04-10', true, NOW(), NOW()),
('EMP-MB-02', 'Meera', 'Kapoor', 'meera.kapoor@pulseai.com', 'Marketing Lead', 'MARKETING', 'Corporate', 'MUMBAI', 'EMPLOYEE', '2022-09-25', true, NOW(), NOW()),
('EMP-MB-03', 'Sanjay', 'Bhatia', 'sanjay.bhatia@pulseai.com', 'Sales Manager', 'SALES', 'Corporate', 'MUMBAI', 'EMPLOYEE', '2023-12-05', true, NOW(), NOW()),
('EMP-MB-04', 'Neha', 'Gupta', 'neha.gupta@pulseai.com', 'HR Associate', 'HR', 'Corporate', 'MUMBAI', 'EMPLOYEE', '2026-02-18', true, NOW(), NOW()),
('EMP-MB-05', 'Tarun', 'Mishra', 'tarun.mishra@pulseai.com', 'Legal Counsel', 'LEGAL', 'Corporate', 'MUMBAI', 'EMPLOYEE', '2026-07-01', true, NOW(), NOW());

-- Insert Employees under HYDERABAD HR
INSERT IGNORE INTO employees (employee_code, first_name, last_name, email, designation, department, business_unit, region, role, joining_date, active, created_at, updated_at) VALUES 
('EMP-HY-01', 'Suresh', 'Babu', 'suresh.babu@pulseai.com', 'Support Engineer', 'SUPPORT', 'Services', 'HYDERABAD', 'EMPLOYEE', '2023-01-15', true, NOW(), NOW()),
('EMP-HY-02', 'Kavita', 'Rani', 'kavita.rani@pulseai.com', 'Technical Writer', 'ENGINEERING', 'Services', 'HYDERABAD', 'EMPLOYEE', '2024-06-20', true, NOW(), NOW()),
('EMP-HY-03', 'Gopi', 'Chand', 'gopi.chand@pulseai.com', 'System Admin', 'IT', 'Services', 'HYDERABAD', 'EMPLOYEE', '2025-10-10', true, NOW(), NOW()),
('EMP-HY-04', 'Bhavya', 'Sri', 'bhavya.sri@pulseai.com', 'Network Engineer', 'IT', 'Services', 'HYDERABAD', 'EMPLOYEE', '2026-04-22', true, NOW(), NOW()),
('EMP-HY-05', 'Mohan', 'Krishna', 'mohan.krishna@pulseai.com', 'Support Analyst', 'SUPPORT', 'Services', 'HYDERABAD', 'EMPLOYEE', '2026-07-10', true, NOW(), NOW());

-- Insert Employees under CHENNAI HR
INSERT IGNORE INTO employees (employee_code, first_name, last_name, email, designation, department, business_unit, region, role, joining_date, active, created_at, updated_at) VALUES 
('EMP-CH-01', 'Venkatesh', 'Prasad', 'venkatesh.prasad@pulseai.com', 'DBA', 'IT', 'Infrastructure', 'CHENNAI', 'EMPLOYEE', '2022-03-10', true, NOW(), NOW()),
('EMP-CH-02', 'Lakshmi', 'Narayan', 'lakshmi.narayan@pulseai.com', 'Cloud Architect', 'IT', 'Infrastructure', 'CHENNAI', 'EMPLOYEE', '2023-08-05', true, NOW(), NOW()),
('EMP-CH-03', 'Ramesh', 'Kumar', 'ramesh.kumar@pulseai.com', 'Security Analyst', 'IT', 'Infrastructure', 'CHENNAI', 'EMPLOYEE', '2024-11-15', true, NOW(), NOW()),
('EMP-CH-04', 'Geetha', 'Rajan', 'geetha.rajan@pulseai.com', 'DevOps Junior', 'IT', 'Infrastructure', 'CHENNAI', 'EMPLOYEE', '2026-05-12', true, NOW(), NOW()),
('EMP-CH-05', 'Saravanan', 'M', 'saravanan.m@pulseai.com', 'Cloud Engineer', 'IT', 'Infrastructure', 'CHENNAI', 'EMPLOYEE', '2026-07-20', true, NOW(), NOW());

-- Insert Employees under THANE HR
INSERT IGNORE INTO employees (employee_code, first_name, last_name, email, designation, department, business_unit, region, role, joining_date, active, created_at, updated_at) VALUES 
('EMP-TH-01', 'Ashok', 'Jadhav', 'ashok.jadhav@pulseai.com', 'Operations Manager', 'OPERATIONS', 'Admin', 'THANE', 'EMPLOYEE', '2020-10-10', true, NOW(), NOW()),
('EMP-TH-02', 'Rutuja', 'Shinde', 'rutuja.shinde@pulseai.com', 'Admin Executive', 'ADMIN', 'Admin', 'THANE', 'EMPLOYEE', '2021-05-20', true, NOW(), NOW()),
('EMP-TH-03', 'Vishal', 'More', 'vishal.more@pulseai.com', 'Facility Executive', 'ADMIN', 'Admin', 'THANE', 'EMPLOYEE', '2022-12-15', true, NOW(), NOW()),
('EMP-TH-04', 'Priyanka', 'Sawant', 'priyanka.sawant@pulseai.com', 'Receptionist', 'ADMIN', 'Admin', 'THANE', 'EMPLOYEE', '2026-01-10', true, NOW(), NOW()),
('EMP-TH-05', 'Siddharth', 'Kamble', 'siddharth.kamble@pulseai.com', 'Operations Trainee', 'OPERATIONS', 'Admin', 'THANE', 'EMPLOYEE', '2026-06-25', true, NOW(), NOW());
