import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';

esbuild.buildSync( { ...esbuildConfig, minify: true, sourcemap: false, outdir: 'dist/modules' } )
