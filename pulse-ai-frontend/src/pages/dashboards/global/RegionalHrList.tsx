import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Users, Filter } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { getEmployees } from '../../../services/employee.service';

export const RegionalHrList = () => {
  const [selectedRegion, setSelectedRegion] = useState<string>('ALL');

  const { data: employees, isLoading, isError } = useQuery({
    queryKey: ['regionalEmployees'],
    queryFn: getEmployees,
    staleTime: 5 * 60 * 1000,
  });

  const regions = ['ALL', 'GLOBAL', 'CHENNAI', 'BENGALURU', 'PUNE', 'HYDERABAD'];

  // Filter for both selected region and the role (REGIONAL_HR or GLOBAL_HR)
  const filteredEmployees = employees?.filter(e => {
    const isHrRole = e.role === 'REGIONAL_HR' || e.role === 'GLOBAL_HR';
    const matchesRegion = selectedRegion === 'ALL' || e.region === selectedRegion;
    return isHrRole && matchesRegion;
  });

  return (
    <div className="p-8 w-full max-w-7xl mx-auto space-y-8">
      {/* Header and Filter */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-white tracking-tight flex items-center">
            <Users className="w-8 h-8 mr-3 text-primary" />
            Regional HRs
          </h1>
          <p className="text-gray-400 mt-2">Manage and view all Regional HR personnel across the organization.</p>
        </div>
        
        <div className="relative">
          <Filter className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
          <select
            value={selectedRegion}
            onChange={(e) => setSelectedRegion(e.target.value)}
            className="pl-10 pr-8 py-2.5 bg-white/5 border border-white/10 rounded-xl text-white appearance-none focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all font-medium min-w-[200px]"
          >
            {regions.map(r => (
              <option key={r} value={r} className="bg-[#1a1b26] text-white">
                {r === 'ALL' ? 'All Regions' : r}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Main Content Area */}
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="glass-panel p-6"
      >
        {isLoading ? (
          <div className="flex flex-col space-y-4">
            <div className="h-16 w-full bg-white/5 animate-pulse rounded-xl"></div>
            <div className="h-16 w-full bg-white/5 animate-pulse rounded-xl"></div>
            <div className="h-16 w-full bg-white/5 animate-pulse rounded-xl"></div>
          </div>
        ) : isError ? (
          <div className="flex items-center justify-center h-64 text-rose-400">
            Error loading HR personnel.
          </div>
        ) : !filteredEmployees?.length ? (
          <div className="flex flex-col items-center justify-center h-64 text-gray-500">
            <Users className="w-12 h-12 mb-4 opacity-50" />
            <p className="text-lg">No HR personnel found for this region.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-white/10 text-gray-400 text-sm">
                  <th className="py-4 px-6 font-medium">Employee Name</th>
                  <th className="py-4 px-6 font-medium">Email Address</th>
                  <th className="py-4 px-6 font-medium">Region</th>
                  <th className="py-4 px-6 font-medium">Role</th>
                </tr>
              </thead>
              <tbody>
                {filteredEmployees.map((hr, index) => (
                  <motion.tr 
                    initial={{ opacity: 0, x: -10 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: index * 0.05 }}
                    key={hr.id} 
                    className="border-b border-white/5 hover:bg-white/5 transition-colors group"
                  >
                    <td className="py-4 px-6">
                      <div className="flex items-center space-x-3">
                        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary to-purple-600 flex items-center justify-center text-white font-bold border border-white/20 shadow-sm shrink-0">
                          {hr.firstName?.charAt(0).toUpperCase() || 'H'}
                        </div>
                        <div>
                          <div className="text-white font-medium group-hover:text-primary transition-colors">
                            {hr.firstName} {hr.lastName}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="py-4 px-6 text-gray-300">{hr.email}</td>
                    <td className="py-4 px-6">
                      <span className="text-[11px] font-semibold px-2.5 py-1 rounded-full bg-white/10 text-gray-200 border border-white/20">
                        {hr.region}
                      </span>
                    </td>
                    <td className="py-4 px-6">
                      <span className="text-[11px] font-semibold px-2.5 py-1 rounded-full bg-primary/20 text-primary border border-primary/30">
                        {hr.role.replace('_', ' ')}
                      </span>
                    </td>
                  </motion.tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </motion.div>
    </div>
  );
};
