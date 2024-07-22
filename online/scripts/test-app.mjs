import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';
import { argv } from 'node:process';
import { writeFileSync } from 'node:fs';

const commonConfig = {
  ...esbuildConfig, 
  conditions: ["development", "browser"],
  minify: false,
  sourcemap: true,
  outdir: 'serve/modules',
};

const port = 8532;

if ( argv .includes( 'nolegacy' ) ) {
  // Avoid bundling any of the legacy code; the 'vzome-legacy' dynamic bundle will be missing from runtime
  delete commonConfig.entryPoints[ 'vzome-legacy' ];
  commonConfig.external = [ './legacy/*.js' ];
}

console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
console.log( '' );
console.log( `       Visit  http://localhost:${port}/app/test/` );
console.log( '' );
console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );

let ctx = await esbuild.context( commonConfig );

ctx.serve( { servedir: 'serve', port } );
