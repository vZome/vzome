// Custom Node.js module resolution hooks for testing.
// Resolves extensionless relative imports (e.g., '../fields/common') to '.js' files,
// matching the behavior of the esbuild bundler used in production.

import { fileURLToPath } from 'node:url';
import { existsSync } from 'node:fs';

export async function resolve( specifier, context, next ) {
  // Only handle relative imports without file extensions
  if ( specifier.startsWith('.') && !specifier.match( /\.\w+$/ ) ) {
    const withJs = specifier + '.js';
    try {
      return await next( withJs, context );
    } catch {
      // fall through to default resolution
    }
  }
  return next( specifier, context );
}
