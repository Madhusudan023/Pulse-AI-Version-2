import { motion } from 'framer-motion';
import type { LucideIcon } from 'lucide-react';
import { useTheme, alpha } from '@mui/material/styles';

interface StatsCardProps {
  title: string;
  value: string | number;
  icon: LucideIcon;
  trend?: string;
  trendUp?: boolean;
  delay?: number;
  onClick?: () => void;
}

export const StatsCard = ({ title, value, icon: Icon, trend, trendUp, delay = 0, onClick }: StatsCardProps) => {
  const theme = useTheme();

  return (
    <motion.div
      onClick={onClick}
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ delay, duration: 0.4 }}
      className={`p-6 flex flex-col relative overflow-hidden group rounded-2xl border ${onClick ? 'cursor-pointer hover:border-primary/50 transition-colors' : ''}`}
      style={{
        backgroundColor: theme.palette.background.paper,
        borderColor: onClick ? undefined : theme.palette.divider,
        boxShadow: theme.shadows[1]
      }}
    >
      <div 
        className="absolute -right-4 -top-4 w-24 h-24 rounded-full blur-2xl transition-all duration-500 group-hover:scale-150"
        style={{ backgroundColor: alpha(theme.palette.primary.main, 0.1) }}
      ></div>
      
      <div className="flex items-start justify-between mb-4 relative z-10 gap-3">
        <div 
          className="p-3 rounded-xl border shrink-0"
          style={{ 
            backgroundColor: alpha(theme.palette.primary.main, 0.05),
            borderColor: alpha(theme.palette.primary.main, 0.1)
          }}
        >
          <Icon className="w-6 h-6" style={{ color: theme.palette.primary.main }} />
        </div>
        {trend && (
          <span 
            className="text-[11px] leading-tight font-semibold px-2.5 py-1 rounded-full border text-center"
            style={{
              backgroundColor: trendUp ? alpha(theme.palette.success.main, 0.1) : alpha(theme.palette.error.main, 0.1),
              color: trendUp ? theme.palette.success.main : theme.palette.error.main,
              borderColor: trendUp ? alpha(theme.palette.success.main, 0.2) : alpha(theme.palette.error.main, 0.2),
            }}
          >
            {trend}
          </span>
        )}
      </div>
      
      <div className="relative z-10 mt-auto pt-2">
        <h4 className="text-sm font-medium mb-1" style={{ color: theme.palette.text.secondary }}>{title}</h4>
        <div className="text-3xl font-bold tracking-tight" style={{ color: theme.palette.text.primary }}>{value}</div>
      </div>
    </motion.div>
  );
};
