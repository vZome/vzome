import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';

esbuild.buildSync( { ...esbuildConfig, /* minify: true, sourcemap: false,*/ outdir: 'dist/modules' } )

esbuild.buildSync( {
  entryPoints: {
    'service-worker': 'src/app/service-worker.js',
  },
  bundle: true,
  splitting: false,
  minify: true,
  sourcemap: false,
  outdir: 'dist/app'
} );

esbuild.buildSync( {
  entryPoints: {
    'vzome-worker-static': 'src/worker/vzome-worker-static.js',
  },
  bundle: true,
  splitting: false,
  // minify: true,
  sourcemap: true,
  loader: { '.vef': 'dataurl' },
  outdir: 'dist/modules'
} );
