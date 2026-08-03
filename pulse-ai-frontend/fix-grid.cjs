const fs = require('fs');
const glob = require('glob');

const files = glob.sync('src/**/*.tsx');

files.forEach(file => {
  let content = fs.readFileSync(file, 'utf8');
  let changed = false;

  // Replace <Grid item xs={X} sm={Y} md={Z}> with <Grid size={{ xs: X, sm: Y, md: Z }}>
  const regex = /<Grid\s+item\s+xs={(\d+)}(?:\s+sm={(\d+)})?(?:\s+md={(\d+)})?(?:\s+lg={(\d+)})?/g;
  content = content.replace(regex, (match, xs, sm, md, lg) => {
    changed = true;
    let sizeStr = `xs: ${xs}`;
    if (sm) sizeStr += `, sm: ${sm}`;
    if (md) sizeStr += `, md: ${md}`;
    if (lg) sizeStr += `, lg: ${lg}`;
    return `<Grid size={{ ${sizeStr} }}`;
  });

  // Also catch cases where item is just <Grid item> without size
  if (content.includes('<Grid item>')) {
    content = content.replace(/<Grid item>/g, '<Grid>');
    changed = true;
  }

  // Catch <Grid item xs={12}> with other stuff
  const regex2 = /<Grid\s+item\s+xs={(\d+)}\s*>/g;
  content = content.replace(regex2, (match, xs) => {
    changed = true;
    return `<Grid size={{ xs: ${xs} }}>`;
  });

  if (changed) {
    fs.writeFileSync(file, content);
    console.log(`Updated ${file}`);
  }
});
