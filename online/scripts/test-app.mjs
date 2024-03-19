import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';
import { argv } from 'node:process';
import { writeFileSync } from 'node:fs';

const commonConfig = {
  ...esbuildConfig, 
  conditions: ["development", "browser"],
  minify: false,
  sourcemap: true,
  outdir: 'public/modules',
};

const port = 8532;

if ( argv .includes( 'quick' ) ) {
  // Avoid bundling any of the legacy code; the 'vzome-legacy' dynamic bundle will be missing from runtime
  delete commonConfig.entryPoints[ 'vzome-legacy' ];
  commonConfig.external = [ './legacy/*.js' ];

  writeFileSync( 'src/revision.js', 'export const REVISION="QUICKSTART"; export const resourceIndex = []; export const importLegacy = async () => import( "https://www.vzome.com/modules/vzome-legacy.js" ); export const importZomic = async () => import( "https://www.vzome.com/modules/vzome-zomic.js" );' );

  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
  console.log( '' );
  console.log( `       Visit  http://localhost:${port}/59icosahedra` );
  console.log( `       or     http://localhost:${port}/classic` );
  console.log( `       or     http://localhost:${port}/browser` );
  console.log( '       or any other vZome app on the same port.' );
  console.log( '' );
  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
}

let ctx = await esbuild.context( commonConfig );

ctx.serve( { servedir: 'public', port } );
