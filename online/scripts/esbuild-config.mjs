
export const esbuildConfig = {
  entryPoints: {
    'vzome-online': 'src/app/index.jsx',
    'vzome-buildplane': 'src/app/buildplane/index.jsx',
    'vzome-browser': 'src/app/browser/index.jsx',
    'vzome-classic': 'src/app/classic/index.jsx',
    'vzome-viewer': 'src/wc/index.js',
    'vzome-viewer-dynamic': 'src/ui/viewer/index.jsx',
    'vzome-legacy': 'src/worker/legacy/dynamic.js',
    'vzome-worker-static': 'src/worker/vzome-worker-static.js',
    'fivecell': 'src/app/fivecell/index.jsx',
    'bhall-basic': 'src/app/bhall/basic/index.jsx',
  },
  bundle: true,
  splitting: true,
  loader: { '.vef': 'dataurl' },
  format: 'esm',
};
