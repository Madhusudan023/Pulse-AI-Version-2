const fs = require('fs');

let path = 'src/components/navigation/Sidebar.tsx';
let content = fs.readFileSync(path, 'utf8');

// We need to filter menu items based on role.
content = content.replace(
  /const menuItems = \[([\s\S]*?)\];/,
  `const getAllMenuItems = (role: string | undefined) => {
  const baseItems = [
    { text: 'Dashboard', icon: <Home size={20} />, path: '/' },
  ];
  
  if (role === 'ROLE_EMPLOYEE') {
    return baseItems;
  }
  
  return [
    ...baseItems,
    { text: 'Surveys', icon: <FileText size={20} />, path: '/surveys' },
    { text: 'Questions', icon: <HelpCircle size={20} />, path: '/questions' },
    { text: 'Employees', icon: <Users size={20} />, path: '/employees' },
    { text: 'Reports', icon: <BarChart size={20} />, path: '/reports' },
  ];
};`
);

content = content.replace(
  /\{menuItems\.map\(/,
  `{getAllMenuItems(useAuthStore((state) => state.role)).map(`
);

fs.writeFileSync(path, content, 'utf8');
console.log('Fixed Sidebar.tsx menu items');
