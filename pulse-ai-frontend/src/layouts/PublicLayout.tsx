import { Outlet } from 'react-router-dom';
import { motion } from 'framer-motion';

export const PublicLayout = () => {
  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-[#f9fafb]">
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="z-10 w-full max-w-md"
      >
        <Outlet />
      </motion.div>
    </div>
  );
};
