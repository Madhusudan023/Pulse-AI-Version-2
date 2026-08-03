import { useState } from 'react';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { useNavigate } from 'react-router-dom';
import { useTheme, alpha } from '@mui/material/styles';
import { GlassCard } from '../../../components/dashboard/GlassCard';
import { StatsCard } from '../../../components/dashboard/StatsCard';
import { Users, FileCheck, HelpCircle, BarChart3, Globe, Sparkles } from 'lucide-react';
import { useRegionalEmployees } from '../../../hooks/useRegionalEmployees';
import { useRegionalSurveys } from '../../../hooks/useRegionalSurveys';
import { useDraftQuestions } from '../../../hooks/useDraftQuestions';
import { useRegionalReports } from '../../../hooks/useRegionalReports';

export const RegionalDashboard = () => {
  const navigate = useNavigate();
  const theme = useTheme();
  const { data: employees, isLoading: isEmployeesLoading, isError: isEmployeesError, refetch: refetchEmployees } = useRegionalEmployees();
  const { data: surveys, isLoading: isSurveysLoading, isError: isSurveysError } = useRegionalSurveys();
  const { data: questions, isLoading: isQuestionsLoading, isError: isQuestionsError } = useDraftQuestions();
  const { data: reports, isLoading: isReportsLoading, isError: isReportsError } = useRegionalReports();

  const activeSurveys = surveys?.filter((s) => s.status === 'ACTIVE').length || 0;
  const draftQuestions = questions?.filter((q) => q.status === 'DRAFT').length || 0;
  const aiGeneratedQuestions = questions?.filter((q) => q.status === 'DRAFT' && q.source === 'AI').length || 0;

  const avgParticipation = reports?.length
    ? Math.round(reports.reduce((acc, r) => acc + r.participationRate, 0) / reports.length)
    : 0;

  return (
    <div className="space-y-6 pb-12 relative">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900 tracking-tight">Regional Dashboard</h2>
          <p className="text-gray-500 mt-1">Survey and employee metrics for your region.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {isEmployeesLoading ? (
          <div className="glass-panel p-6 h-[140px] animate-pulse bg-white border border-gray-100 rounded-xl"></div>
        ) : isEmployeesError ? (
          <div className="glass-panel p-6 h-[140px] flex items-center justify-center border-rose-500/30">
            <span className="text-rose-400">Failed to load employees</span>
          </div>
        ) : (
          <StatsCard
            title="Regional Employees"
            value={employees?.length || 0}
            icon={Users}
            trend="Active in your region"
            trendUp={true}
            delay={0.1}
            onClick={() => navigate('/employees')}
          />
        )}

        {isSurveysLoading ? (
          <div className="glass-panel p-6 h-[140px] animate-pulse bg-white border border-gray-100 rounded-xl"></div>
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
          <div className="glass-panel p-6 h-[140px] animate-pulse bg-white border border-gray-100 rounded-xl"></div>
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
          <div className="glass-panel p-6 h-[140px] animate-pulse bg-white border border-gray-100 rounded-xl"></div>
        ) : isReportsError ? (
          <div className="glass-panel p-6 h-[140px] flex items-center justify-center border-rose-500/30">
            <span className="text-rose-400">Failed to load reports</span>
          </div>
        ) : (
          <StatsCard
            title="Avg Participation"
            value={avgParticipation + '%'}
            icon={BarChart3}
            trend="Regional average"
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
    </div>
  );
};
