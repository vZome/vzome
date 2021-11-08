
export const esbuildConfig = {
  entryPoints: {
    'vzome-online': 'src/app/index.jsx',
    'vzome-viewer': 'src/wc/index.js',
    'vzome-legacy': 'src/wc/legacy/dynamic.js',
  },
  bundle: true,
  splitting: true,
  loader: { '.vef': 'dataurl' },
  format: 'esm',
};
