import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';

const commonConfig = {
  ...esbuildConfig, 
  conditions: ["development", "browser"],
  minify: false,
  sourcemap: true,
  outdir: 'serve/modules',
};

const port = 8532;

console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
console.log( '' );
console.log( `       Visit  http://localhost:${port}/app/test/` );
console.log( '' );
console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );
console.log( '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%' );

let ctx = await esbuild.context( commonConfig );

ctx.serve( { servedir: 'serve', port } );
