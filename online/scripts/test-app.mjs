import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';

esbuild.serve({
  servedir: 'public',
  port: 8080,
}, { ...esbuildConfig, minify: false, sourcemap: true, outdir: 'public/modules' } );
