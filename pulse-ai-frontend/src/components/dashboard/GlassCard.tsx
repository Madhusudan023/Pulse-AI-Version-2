import { ReactNode } from 'react';
import { motion } from 'framer-motion';
import { useTheme } from '@mui/material/styles';

interface GlassCardProps {
  title?: string;
  children: ReactNode;
  className?: string;
  delay?: number;
}

export const GlassCard = ({ title, children, className = '', delay = 0 }: GlassCardProps) => {
  const theme = useTheme();

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay, duration: 0.4 }}
      className={`p-6 rounded-2xl border ${className}`}
      style={{
        backgroundColor: theme.palette.background.paper,
        borderColor: theme.palette.divider,
        boxShadow: theme.shadows[1]
      }}
    >
      {title && (
        <h3 className="text-lg font-semibold mb-4 flex items-center" style={{ color: theme.palette.text.primary }}>
          {title}
        </h3>
      )}
      {children}
    </motion.div>
  );
};
