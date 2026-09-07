/**
 * Bundles and runs scripts/batch-pov.mjs.
 *
 * The legacy (transpiled Java) sources cannot be imported directly by Node: they use
 * extensionless imports and import ".vef" files as modules, both of which need a bundler.
 * The same esbuild loaders the app uses handle it (see scripts/esbuild-config.mjs).
 *
 *   node scripts/run-batch-pov.mjs <inputRoot> <outputRoot> [--compare <goldenRoot>]
 */

import { build } from 'esbuild';
import { spawn } from 'child_process';
import path from 'path';
import os from 'os';
import fs from 'fs';

const here = import.meta.dirname ?? path.dirname( new URL( import.meta.url ).pathname );
const outfile = path .join( fs .mkdtempSync( path .join( os .tmpdir(), 'vzome-pov-' ) ), 'batch-pov.cjs' );

await build( {
  entryPoints: [ path .join( here, 'batch-pov.mjs' ) ],
  bundle: true,
  platform: 'node',
  // CommonJS, so that `require` exists: j4ts tests for it to decide it is running on Node,
  // and falls back to a shell stub (with no console.log) when it is missing.
  format: 'cjs',
  outfile,
  loader: { '.vef': 'dataurl', '.svg': 'text' },
  banner: { js: 'globalThis.require=require;globalThis.process=process;' },
  logLevel: 'warning',
} );

const child = spawn( process.execPath, [ '--max-old-space-size=4096', outfile, ...process.argv.slice( 2 ) ],
                     { stdio: 'inherit',
                       env: { ...process.env, VZOME_ONLINE_ROOT: path .resolve( here, '..' ) } } );
child .on( 'exit', code => process .exit( code ?? 1 ) );
