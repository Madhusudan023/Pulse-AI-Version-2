const fs = require('fs');
const path = 'src/pages/surveys/manage/ManageSurveys.tsx';

let content = fs.readFileSync(path, 'utf8');

// Fix card background and border
content = content.replace(
  /bgcolor: alpha\(theme\.palette\.common\.white, 0\.05\),\s*backdropFilter: 'blur\(10px\)',\s*border: 1,\s*borderColor: alpha\(theme\.palette\.common\.white, 0\.1\),\s*transition: 'border-color 0\.3s',\s*'&:hover': \{ borderColor: alpha\(theme\.palette\.primary\.main, 0\.3\) \},\s*boxShadow: 'none'/g,
  `bgcolor: 'background.paper',
                border: 1,
                borderColor: 'divider',
                transition: 'border-color 0.3s, box-shadow 0.3s',
                '&:hover': { 
                  borderColor: theme.palette.primary.main,
                  boxShadow: \`0 4px 20px 0 \${alpha(theme.palette.primary.main, 0.1)}\`
                },
                boxShadow: theme.shadows[1]`
);

// Fix the "View Questions" button
content = content.replace(
  /bgcolor: alpha\(theme\.palette\.common\.white, 0\.05\),\s*border: 1,\s*borderColor: alpha\(theme\.palette\.common\.white, 0\.05\),\s*borderRadius: 1,\s*'&:hover': \{ color: 'common\.white', bgcolor: alpha\(theme\.palette\.common\.white, 0\.1\) \}/g,
  `bgcolor: alpha(theme.palette.background.default, 0.5),
                          border: 1,
                          borderColor: 'divider',
                          borderRadius: 1,
                          '&:hover': { color: 'primary.main', bgcolor: alpha(theme.palette.primary.main, 0.1) }`
);

// Fix the footer top border
content = content.replace(
  /borderTop: 1, borderColor: alpha\(theme\.palette\.common\.white, 0\.1\)/g,
  `borderTop: 1, borderColor: 'divider'`
);

// Fix Add Questions button
content = content.replace(
  /borderColor: alpha\(theme\.palette\.common\.white, 0\.1\),\s*bgcolor: alpha\(theme\.palette\.common\.white, 0\.05\),\s*'&:hover': \{ bgcolor: alpha\(theme\.palette\.common\.white, 0\.1\) \}/g,
  `borderColor: 'divider',
                            bgcolor: alpha(theme.palette.background.default, 0.5),
                            '&:hover': { bgcolor: alpha(theme.palette.action.hover, 0.05), color: 'primary.main', borderColor: 'primary.main' }`
);

// Fix Refresh button on top
content = content.replace(
  /bgcolor: alpha\(theme\.palette\.common\.white, 0\.05\),\s*'&:hover': \{ color: 'common\.white', bgcolor: alpha\(theme\.palette\.common\.white, 0\.1\) \}/g,
  `bgcolor: 'background.paper',
              '&:hover': { color: 'text.primary', bgcolor: alpha(theme.palette.background.paper, 0.8) }`
);

fs.writeFileSync(path, content, 'utf8');
console.log('Fixed ManageSurveys styling');
