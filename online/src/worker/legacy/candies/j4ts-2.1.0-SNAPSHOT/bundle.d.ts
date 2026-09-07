// Type declarations for the j4ts runtime bundle (bundle.js).
//
// bundle.js was patched (by the since-removed cicd/jsweet-legacy-code.bash) to
// `export var java` / `export var javaemul`, so it is an ES module at runtime.  j4ts.d.ts, however,
// declares those as *ambient global namespaces*.  This file bridges the two:
// it pulls in the ambient declarations and re-exports them as module bindings,
// so `import { java } from ".../bundle.js"` is properly typed.
/// <reference path="./j4ts.d.ts" />

declare const _java: typeof java;
declare const _javaemul: typeof javaemul;

export { _java as java, _javaemul as javaemul };
