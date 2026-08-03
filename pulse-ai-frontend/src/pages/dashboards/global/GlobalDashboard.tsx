import { useState } from 'react';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { useTheme, alpha } from '@mui/material/styles';
import { GlassCard } from '../../../components/dashboard/GlassCard';
import { StatsCard } from '../../../components/dashboard/StatsCard';
import { Users, FileCheck, HelpCircle, BarChart3, Globe, Sparkles, Plus, X } from 'lucide-react';
import { useRegionalEmployees } from '../../../hooks/useRegionalEmployees';
import { useRegionalSurveys } from '../../../hooks/useRegionalSurveys';
import { useDraftQuestions } from '../../../hooks/useDraftQuestions';
import { useRegionalReports } from '../../../hooks/useRegionalReports';
import { createRegionalHr } from '../../../services/employee.service';

export const GlobalDashboard = () => {
  const theme = useTheme();
  const { data: employees, isLoading: isEmployeesLoading, isError: isEmployeesError, refetch: refetchEmployees } = useRegionalEmployees();
  const { data: surveys, isLoading: isSurveysLoading, isError: isSurveysError } = useRegionalSurveys();
  const { data: questions, isLoading: isQuestionsLoading, isError: isQuestionsError } = useDraftQuestions();
  const { data: reports, isLoading: isReportsLoading, isError: isReportsError } = useRegionalReports();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    department: 'HR',
    designation: 'Regional HR Manager',
    region: 'BENGALURU'
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const activeSurveys = surveys?.filter((s) => s.status === 'ACTIVE').length || 0;
  const draftQuestions = questions?.filter((q) => q.status === 'DRAFT').length || 0;
  const aiGeneratedQuestions = questions?.filter((q) => q.status === 'DRAFT' && q.source === 'AI').length || 0;

  const avgParticipation = reports?.length
    ? Math.round(reports.reduce((acc, r) => acc + r.participationRate, 0) / reports.length)
    : 0;

  // Group employees by region for comparison
  const regionCounts = employees?.reduce((acc, emp) => {
    const r = emp.region || 'Unknown';
    acc[r] = (acc[r] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);

  const handleAddHr = async (formData) => {
  try {
    // Construct the full payload required by the backend
    const fullPayload = {
      ...formData,
      // Hardcode the role since this specific function is for creating Regional HRs
      role: 'REGIONAL_HR',
      // Generate a temporary employee code if your form doesn't ask for one
      employeeCode: formData.employeeCode || `EMP-${Math.floor(Math.random() * 10000)}`,
      // Add default business unit if your form doesn't ask for one
      businessUnit: formData.businessUnit || 'HR',
      // Add today's date if joining date isn't provided by the form
      joiningDate: formData.joiningDate || new Date().toISOString().split('T')[0], 
    };

    console.log("Sending payload:", fullPayload); // Verify this logs the complete object
    
    await createRegionalHr(fullPayload);
    
    // Handle success (close modal, show toast, refresh table)
  } catch (error) {
    console.error("Failed to create Regional HR", error);
  }
};

  return (
    <div className="space-y-6 pb-12 relative">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900 tracking-tight">Global HR Dashboard</h2>
          <p className="text-gray-500 mt-1">Enterprise-wide survey and employee metrics across all regions.</p>
        </div>
        <button 
          onClick={() => setIsModalOpen(true)}
          className="px-4 py-2 bg-primary/90 hover:bg-primary text-white rounded-xl font-medium transition-colors flex items-center space-x-2"
        >
          <Plus className="w-4 h-4" />
          <span>Add Regional HR</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {isEmployeesLoading ? (
          <div className="glass-panel p-6 h-[140px] animate-pulse bg-white/5"></div>
        ) : isEmployeesError ? (
          <div className="glass-panel p-6 h-[140px] flex items-center justify-center border-rose-500/30">
            <span className="text-rose-400">Failed to load employees</span>
          </div>
        ) : (
          <StatsCard
            title="Global Employees"
            value={employees?.length || 0}
            icon={Users}
            trend={Object.keys(regionCounts || {}).length + ' active regions'}
            trendUp={true}
            delay={0.1}
            onClick={() => navigate('/employees')}
          />
        )}

        {isSurveysLoading ? (
          <div className="glass-panel p-6 h-[140px] animate-pulse bg-white/5"></div>
        ) : isSurveysError ? (
          <div className="glass-panel p-6 h-[140px] flex items-center justify-center border-rose-500/30">
            <span className="text-rose-400">Failed to load surveys</span>
          </div>
        ) : (
          <StatsCard
            title="Active Surveys"
            value={activeSurveys}
            icon={Globe}
            trend={(surveys?.length || 0) + ' total created'}
            trendUp={true}
            delay={0.2}
            onClick={() => navigate('/surveys')}
          />
        )}

        {isQuestionsLoading ? (
          <div className="glass-panel p-6 h-[140px] animate-pulse bg-white/5"></div>
        ) : isQuestionsError ? (
          <div className="glass-panel p-6 h-[140px] flex items-center justify-center border-rose-500/30">
            <span className="text-rose-400">Failed to load questions</span>
          </div>
        ) : (
          <StatsCard
            title="Pending Approvals"
            value={draftQuestions}
            icon={HelpCircle}
            trend={aiGeneratedQuestions + ' AI-generated'}
            trendUp={aiGeneratedQuestions > 0}
            delay={0.3}
            onClick={() => navigate('/questions')}
          />
        )}

        {isReportsLoading ? (
          <div className="glass-panel p-6 h-[140px] animate-pulse bg-white/5"></div>
        ) : isReportsError ? (
          <div className="glass-panel p-6 h-[140px] flex items-center justify-center border-rose-500/30">
            <span className="text-rose-400">Failed to load reports</span>
          </div>
        ) : (
          <StatsCard
            title="Avg Participation"
            value={avgParticipation + '%'}
            icon={BarChart3}
            trend="Enterprise-wide"
            trendUp={avgParticipation > 75}
            delay={0.4}
          />
        )}
      </div>

      
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <GlassCard title="Employee Participation" delay={0.5} className="h-[400px]">
          {isReportsLoading ? (
            <div className="flex items-center justify-center h-[300px] text-gray-400 animate-pulse">Loading...</div>
          ) : isReportsError ? (
            <div className="flex items-center justify-center h-[300px] text-rose-400">Error loading data.</div>
          ) : (
            <div className="h-[300px] w-full mt-4">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={[
                      { name: 'Participated', value: avgParticipation, color: theme.palette.primary.main },
                      { name: 'Not Participated', value: 100 - avgParticipation, color: alpha(theme.palette.primary.main, 0.2) }
                    ]}
                    cx="50%"
                    cy="50%"
                    innerRadius={70}
                    outerRadius={100}
                    paddingAngle={5}
                    dataKey="value"
                    stroke="none"
                  >
                    { [0, 1].map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={index === 0 ? theme.palette.primary.main : alpha(theme.palette.primary.main, 0.2)} />
                    ))}
                  </Pie>
                  <Tooltip 
                    contentStyle={{ backgroundColor: alpha(theme.palette.background.paper, 0.9), border: '1px solid '+theme.palette.divider, borderRadius: '8px', color: theme.palette.text.primary }}
                    itemStyle={{ color: theme.palette.text.primary }}
                    formatter={(value) => `${value}%`}
                  />
                  <Legend verticalAlign="bottom" height={36} iconType="circle" />
                </PieChart>
              </ResponsiveContainer>
            </div>
          )}
        </GlassCard>

        <GlassCard title="Sentiment Analysis" delay={0.6} className="h-[400px]">
          {isReportsLoading ? (
            <div className="flex items-center justify-center h-[300px] text-gray-400 animate-pulse">Loading...</div>
          ) : isReportsError ? (
            <div className="flex items-center justify-center h-[300px] text-rose-400">Error loading data.</div>
          ) : (
            <div className="h-[300px] w-full mt-4">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={[
                      { name: 'Positive', value: reports?.length ? Math.round(reports.reduce((acc, r) => acc + r.positivePercentage, 0) / reports.length) : 60 },
                      { name: 'Neutral', value: reports?.length ? Math.round(reports.reduce((acc, r) => acc + r.neutralPercentage, 0) / reports.length) : 25 },
                      { name: 'Negative', value: reports?.length ? Math.round(reports.reduce((acc, r) => acc + r.negativePercentage, 0) / reports.length) : 15 }
                    ]}
                    cx="50%"
                    cy="50%"
                    innerRadius={70}
                    outerRadius={100}
                    paddingAngle={5}
                    dataKey="value"
                    stroke="none"
                  >
                    <Cell fill={theme.palette.success.main} />
                    <Cell fill={theme.palette.warning.main} />
                    <Cell fill={theme.palette.error.main} />
                  </Pie>
                  <Tooltip 
                    contentStyle={{ backgroundColor: alpha(theme.palette.background.paper, 0.9), border: '1px solid '+theme.palette.divider, borderRadius: '8px', color: theme.palette.text.primary }}
                    itemStyle={{ color: theme.palette.text.primary }}
                    formatter={(value) => `${value}%`}
                  />
                  <Legend verticalAlign="bottom" height={36} iconType="circle" />
                </PieChart>
              </ResponsiveContainer>
            </div>
          )}
        </GlassCard>
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
          <div className="bg-[#1a1b26] border border-white/10 rounded-2xl w-full max-w-md p-6 relative shadow-2xl">
            <button 
              onClick={() => setIsModalOpen(false)}
              className="absolute top-4 right-4 text-gray-400 hover:text-white transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
            <h3 className="text-xl font-semibold text-white mb-6">Add Regional HR</h3>
            
            <form onSubmit={handleAddHr} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-1">First Name</label>
                  <input
                    type="text"
                    required
                    value={formData.firstName}
                    onChange={(e) => setFormData({...formData, firstName: e.target.value})}
                    className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-2 text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-primary/50"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-1">Last Name</label>
                  <input
                    type="text"
                    required
                    value={formData.lastName}
                    onChange={(e) => setFormData({...formData, lastName: e.target.value})}
                    className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-2 text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-primary/50"
                  />
                </div>
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-1">Email</label>
                <input
                  type="email"
                  required
                  value={formData.email}
                  onChange={(e) => setFormData({...formData, email: e.target.value})}
                  className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-2 text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-primary/50"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-1">Temporary Password</label>
                <input
                  type="password"
                  required
                  value={formData.password}
                  onChange={(e) => setFormData({...formData, password: e.target.value})}
                  className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-2 text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="Minimum 6 characters"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-1">Region</label>
                                <select
                  value={formData.region}
                  onChange={(e) => setFormData({...formData, region: e.target.value})}
                  className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-primary/50 [&>option]:bg-[#1a1b26]"
                >
                  <option value="BENGALURU">Bengaluru</option>
                  <option value="CHENNAI">Chennai</option>
                  <option value="HYDERABAD">Hyderabad</option>
                  <option value="PUNE">Pune</option>
                </select>
              </div>
              
              <div className="pt-4 flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-gray-300 hover:text-white hover:bg-white/5 rounded-xl transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="px-6 py-2 bg-primary hover:bg-primary/90 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isSubmitting ? 'Creating...' : 'Create HR'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};


