import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';

esbuild.serve(
  {
    servedir: 'public',
    port: 8532,
  },
  { ...esbuildConfig, 
    conditions: ["development", "browser"],
    minify: false,
    sourcemap: true,
    outdir: 'public/modules'
  }
);
