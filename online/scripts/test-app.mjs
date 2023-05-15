import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';
import { argv } from 'node:process';

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
  
  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
  console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
  console.log( '' );
  console.log( `       visit  http://localhost:${port}/browser` );
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
