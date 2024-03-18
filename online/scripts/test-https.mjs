import esbuild from 'esbuild';
import { esbuildConfig } from './esbuild-config.mjs';
import http from 'node:http';
import https from 'node:https';
import fs from 'node:fs';

const commonConfig = {
  ...esbuildConfig, 
  conditions: ["development", "browser"],
  minify: false,
  sourcemap: true,
  outdir: 'public/modules',
};
let ctx = await esbuild.context( commonConfig );

const security = {
  key: fs.readFileSync('public/localhost-key.pem'),
  cert: fs.readFileSync('public/localhost.pem')
};

// Start esbuild's server on a random local port
ctx .serve( { servedir: 'public' } )
.then( result => {
  // The result tells us where esbuild's local server is
  const { host, port } = result;

  // Then start a proxy server on port 8532
  https.createServer( security, (req, res) => {
    const options = {
      hostname: host,
      port: port,
      path: req.url,
      method: req.method,
      headers: req.headers,
    }

    // Forward each incoming request to esbuild
    const proxyReq = http.request( options, proxyRes => {
      // If esbuild returns "not found", send a custom 404 page
      if (proxyRes.statusCode === 404) {
        res.writeHead(404, { 'Content-Type': 'text/html' });
        res.end('<h1>A wacky custom 404 page</h1>');
        return;
      }

      // Otherwise, forward the response from esbuild to the client
      res.writeHead( proxyRes.statusCode, proxyRes.headers );
      proxyRes.pipe( res, { end: true } );
    });

    // Forward the body of the request to esbuild
    req.pipe(proxyReq, { end: true });
  }).listen(8532);
});
