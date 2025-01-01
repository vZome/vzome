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

const port = 8855;

if ( argv .includes( 'quick' ) ) {
  // Avoid bundling any of the legacy code; the 'vzome-legacy' dynamic bundle will be missing from runtime
  delete commonConfig.entryPoints[ 'vzome-legacy' ];
  commonConfig.external = [ './legacy/*.js' ];

  writeFileSync( 'src/revision.js', 'export const REVISION="QUICKSTART"; export const resourceIndex = []; export const importLegacy = async () => import( "https://www.vzome.com/modules/r114/vzome-legacy.js" );' );

  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
  console.log( '' );
  console.log( `       visit  http://localhost:${port}/?debug=true` );
  console.log( '' );
  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
}

esbuild.serve(
  {
    servedir: 'public',
    port,
  },
  commonConfig
);
