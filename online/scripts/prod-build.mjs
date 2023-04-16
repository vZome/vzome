import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';

esbuild.build( { ...esbuildConfig, minify: false, sourcemap: false, outdir: 'dist/modules' } )

esbuild.buildSync( {
  entryPoints: {
    'service-worker': 'src/service-worker.js',
  },
  bundle: true,
  splitting: false,
  minify: true,
  sourcemap: false,
  outdir: 'dist/app'
} );
