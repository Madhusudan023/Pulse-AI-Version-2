-- Insert ONBOARDING Questions
INSERT IGNORE INTO questions (question_text, question_type, category, source, status, survey_type, version, usage_count, positive_from, positive_to, neutral_from, neutral_to, negative_from, negative_to, created_at, updated_at) VALUES 
('How satisfied were you with the recruitment process?', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Did you feel welcomed by your team on the first day?', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Are the job responsibilities clear to you?', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Did you receive adequate training for your role?', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Is your manager supportive of your initial transition?', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('How satisfied are you with the onboarding tools and resources?', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Were you introduced to the company core values?', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Do you have all the hardware/software needed to do your job?', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('How well did your orientation session cover company policies?', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Have you had a 1-on-1 meeting with your direct manager yet?', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Is the company culture aligned with your expectations?', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Do you feel your ideas and opinions are valued early on?', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Are the benefits explained clearly to you?', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Would you recommend our onboarding process to others?', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Any suggestions to improve the onboarding experience?', 'TEXT', 'CULTURE', 'HR', 'APPROVED', 'ONBOARDING', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW());

-- Insert WORK_LIFE_BALANCE Questions
INSERT IGNORE INTO questions (question_text, question_type, category, source, status, survey_type, version, usage_count, positive_from, positive_to, neutral_from, neutral_to, negative_from, negative_to, created_at, updated_at) VALUES 
('I am able to maintain a healthy balance between my work and personal life.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My workload allows me to take time off when needed.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I am not frequently expected to work beyond normal hours.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The company supports flexible working arrangements.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My manager respects my personal time outside of work hours.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel comfortable disconnecting from work communications after hours.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I have enough time to rest and recharge during the weekends.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The demands of my job do not negatively impact my personal relationships.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I am satisfied with the amount of paid time off provided.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Our culture promotes taking mental health breaks when needed.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel energized rather than drained at the end of the workday.', 'LIKERT_SCALE', 'WORK_LIFE_BALANCE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW());

-- Insert LEADERSHIP Questions
INSERT IGNORE INTO questions (question_text, question_type, category, source, status, survey_type, version, usage_count, positive_from, positive_to, neutral_from, neutral_to, negative_from, negative_to, created_at, updated_at) VALUES 
('The senior leadership team has communicated a clear vision for the company.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I trust the decisions made by the executive leadership.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Leadership keeps employees informed about company performance and changes.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The leaders in this organization demonstrate our core values.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel that senior management is transparent in their communication.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Leaders take action based on feedback from employee surveys.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I have confidence in the future success of the company under current leadership.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Leadership shows genuine care for the well-being of employees.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Our leaders encourage innovation and new ideas from all levels.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel comfortable approaching senior leaders with questions.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Leadership effectively navigates the company through difficult times.', 'LIKERT_SCALE', 'LEADERSHIP', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW());

-- Insert GROWTH Questions
INSERT IGNORE INTO questions (question_text, question_type, category, source, status, survey_type, version, usage_count, positive_from, positive_to, neutral_from, neutral_to, negative_from, negative_to, created_at, updated_at) VALUES 
('I see a clear path for career advancement within the company.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I have opportunities to learn and grow professionally.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My company provides the training I need to advance my career.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I am encouraged to take on new challenges and stretch assignments.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I receive constructive feedback that helps me improve my performance.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My manager actively supports my long-term career goals.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('There are adequate resources for professional development (e.g., courses, books).', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I am recognized when I acquire new skills or certifications.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Internal mobility is supported and encouraged here.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel challenged in a positive way by my current role.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My current role aligns well with my career aspirations.', 'LIKERT_SCALE', 'GROWTH', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW());

-- Insert CULTURE Questions
INSERT IGNORE INTO questions (question_text, question_type, category, source, status, survey_type, version, usage_count, positive_from, positive_to, neutral_from, neutral_to, negative_from, negative_to, created_at, updated_at) VALUES 
('I feel a strong sense of belonging at this company.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My coworkers are committed to doing quality work.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Our team collaborates effectively to solve problems.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Diverse perspectives are valued and respected here.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I am proud to tell others that I work for this company.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('We celebrate team successes and individual milestones.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The company culture is positive and uplifting.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel safe sharing differing opinions without fear of retaliation.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Our daily practices reflect the company stated values.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I enjoy working with the people on my team.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('This company is a great place to work.', 'LIKERT_SCALE', 'CULTURE', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW());

-- Insert BENEFITS Questions
INSERT IGNORE INTO questions (question_text, question_type, category, source, status, survey_type, version, usage_count, positive_from, positive_to, neutral_from, neutral_to, negative_from, negative_to, created_at, updated_at) VALUES 
('I am satisfied with my overall compensation package.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The health and wellness benefits meet my and my family needs.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I understand the full range of benefits available to me.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The retirement or savings plans offered are competitive.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel that I am paid fairly for the work I do.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The company offers useful perks beyond standard benefits.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The process for utilizing benefits (e.g., healthcare claims) is straightforward.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I am satisfied with the parental leave and family support policies.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The company provides adequate support for mental health.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel my salary is competitive compared to similar roles in the industry.', 'LIKERT_SCALE', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('What additional benefits would you like to see offered?', 'TEXT', 'BENEFITS', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW());

-- Insert MANAGER Questions
INSERT IGNORE INTO questions (question_text, question_type, category, source, status, survey_type, version, usage_count, positive_from, positive_to, neutral_from, neutral_to, negative_from, negative_to, created_at, updated_at) VALUES 
('My manager provides clear goals and expectations for my work.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I receive regular, actionable feedback from my manager.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My manager recognizes and appreciates my hard work.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel comfortable discussing personal or work-related challenges with my manager.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My manager treats everyone on the team fairly.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My manager empowers me to make decisions in my work.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I have the autonomy I need to do my job effectively.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My manager cares about me as a person, not just an employee.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My manager effectively resolves conflicts within the team.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I feel supported by my manager when I face roadblocks.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('My manager is a good role model for our company values.', 'LIKERT_SCALE', 'MANAGER', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW());

-- Insert WORKLOAD Questions
INSERT IGNORE INTO questions (question_text, question_type, category, source, status, survey_type, version, usage_count, positive_from, positive_to, neutral_from, neutral_to, negative_from, negative_to, created_at, updated_at) VALUES 
('My workload is reasonable and manageable.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I have the necessary tools and resources to complete my work efficiently.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I rarely feel overwhelmed by the amount of work I have to do.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Deadlines for projects are realistic and achievable.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Processes and procedures at our company allow me to work productively.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I do not spend excessive time on unnecessary meetings.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Expectations for my output are fair and clear.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('I have enough time to produce high-quality work.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('When my workload is heavy, I get the support I need from my team.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('The pace of work in my department is sustainable.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW()),
('Administrative tasks do not get in the way of my core responsibilities.', 'LIKERT_SCALE', 'WORKLOAD', 'HR', 'APPROVED', 'MONTHLY_PULSE', 1, 0, 8, 10, 5, 7, 1, 4, NOW(), NOW());
