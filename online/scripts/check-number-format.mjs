#!/usr/bin/env node
//
// Asserts that from-java/java/text/NumberFormat.ts reproduces java.text.NumberFormat
// exactly, over the reference corpus in fixtures/number-format-java.txt.
//
//   node scripts/check-number-format.mjs
//
// Why this exists: NumberFormat is a hand-written stand-in for a JDK class the j4ts
// runtime does not provide, and twelve exporters format their geometry through it.
// Its output is therefore part of the contract with the Java exporters -- if the two
// disagree, every exported file disagrees, silently.  The corpus was captured from a
// real JDK; see the header of the fixture.
//
// The subtle failure this guards against: formatting with toFixed() instead of from
// the shortest round-trip decimal.  That passes every case up to about 15 significant
// digits and then diverges -- 500 of these 3200 cases fail that way, all of them at
// the 16-digit precision OffExporter asks for.

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { execFileSync } from 'node:child_process';

const HERE = path.dirname( fileURLToPath( import.meta.url ) );
const FIXTURE = path.join( HERE, 'fixtures', 'number-format-java.txt' );
const SOURCE = path.join( HERE, '../src/worker/legacy/from-java/java/text/NumberFormat.ts' );

//  Bundle the TypeScript through esbuild, then drop the j4ts import: NumberFormat
//  only mentions java.util.Locale as a parameter type, so nothing is needed at runtime.
const bundled = path.join( fs.mkdtempSync( '/tmp/nfcheck-' ), 'NumberFormat.mjs' );
execFileSync( path.join( HERE, '../node_modules/.bin/esbuild' ),
  [ SOURCE, '--bundle', '--format=esm', '--outfile=' + bundled, '--external:*bundle.js', '--log-level=error' ] );
fs.writeFileSync( bundled,
  fs.readFileSync( bundled, 'utf8' ).replace( /import\s*\{[^}]*\}\s*from\s*"[^"]*bundle\.js";?/g, '' ) );

const { NumberFormat } = await import( bundled );

let pass = 0;
const failures = [];
for ( const line of fs.readFileSync( FIXTURE, 'utf8' ).split( '\n' ) ) {
  if ( ! line.trim() || line.startsWith( '#' ) ) continue;
  const [ min, max, value, expected ] = line.split( '|' );
  const format = NumberFormat.getNumberInstance( null );
  if ( +min > 0 ) format .setMinimumFractionDigits( +min );
  format .setMaximumFractionDigits( +max );
  const actual = format .format( Number( value ) );
  if ( actual === expected ) pass ++;
  else failures .push( { min, max, value, expected, actual } );
}

console .log( `NumberFormat: ${ pass } passed, ${ failures.length } failed` );
if ( failures.length ) {
  for ( const f of failures .slice( 0, 12 ) )
    console .error( `  min=${f.min} max=${f.max}  ${f.value}\n     expected ${f.expected}\n     actual   ${f.actual}` );
  if ( failures.length > 12 ) console .error( `  ... and ${ failures.length - 12 } more` );
  process .exit( 1 );
}
