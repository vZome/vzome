// Test setup: register custom resolver hooks before tests run.
// Usage: node --import ./test/setup.mjs --test ./test/*.test.mjs

import { register } from 'node:module';
register( './resolve-hooks.mjs', import.meta.url );
