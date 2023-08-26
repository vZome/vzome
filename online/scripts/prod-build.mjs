import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';
import { writeFileSync } from 'node:fs'

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

const result = await esbuild.build( {
  ...esbuildConfig,
  minify: false,
  metafile: true,
  sourcemap: false,
  outdir: 'dist/modules',
} )

writeFileSync( 'meta.json', JSON.stringify( result.metafile ) );
