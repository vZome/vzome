
export const esbuildConfig = {
  entryPoints: {
    'vzome-online': 'src/app/index.jsx',
    'vzome-viewer': 'src/wc/index.js',
    'vzome-viewer-dynamic': 'src/ui/viewer/index.jsx',
    'vzome-legacy': 'src/worker/legacy/dynamic.js',
  },
  bundle: true,
  splitting: true,
  loader: { '.vef': 'dataurl' },
  format: 'esm',
};
