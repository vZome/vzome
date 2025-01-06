# Architecture of Online vZome

Online vZome is a fairly complex piece of software, comprising several different
web applications and web components, all built and deployed simultaneously,
from a single set of source files.
This document should elucidate some of the structure of the code and
its runtime architecture.

## Module Reuse

In order to load efficiently as several different web applications and web components,
the code is bundled into a collection of [ES modules](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Modules),
with [esbuild](https://esbuild.github.io/api/#overview) as the build and bundling tool.
The rationale for what code goes into what module is explained where it is
implemented, as comments in [../scripts/esbuild-config.mjs](../scripts/esbuild-config.mjs).

## Viewing vs. Editing

The `vzome-viewer` web component and most of the web apps support *viewing* vZome designs in two formats.
The URL provided for a design is always for the `.vZome` legacy file format,
but if a similar URL with a `.shapes.json` suffix is found (and there is no need to edit the design),
that JSON file format will be used, since it can load *much* faster,
because it contains just a description of the final geometry.
If the `.shapes.json` preview URL fails, the `.vZome` design file will be
loaded, parsed, and interpreted, redoing each of the edits in the history automatically.

If the web app in use is meant to *edit* a vZome design, then the `.vZome` URL
is used directly, and the preview JSON is ignored.

## Web Worker

Whether loading a full vZome design or just a preview JSON, the loading is performed by a web worker, to keep the UI responsive.
Furthermore, the worker only loads the legacy vZome code (a large module) when necessary,
if the preview JSON is missing or if editing is required.

Any given edit command may do significant computation, such as computing a convex hull or a 4D polytope.
That cost is compounded by the new fields of high order, and the arbitrary precision arithmetic.
The bottom line is that each command really should be performed in a Web Worker;
there is no other way to guarantee that rendering is not impacted,
except to break up the processing into small chunks using setTimeout(0),
and that would be invasive in the legacy Java code.

Web Workers do not share any memory with the main Javascript thread.
Any necessary data flows as serialized copies in the messages passed back and forth.
This means that we must take one of two approaches: either the Web Worker is essentially stateless,
or it maintains the mesh state for every design.  For the stateless approach, the entire mesh would
have to flow in and out in the messages.  The stateful approach would be more similar to the client-server
approach I used for the Unity implementation, where only rendering state is stored with the main thread,
and "render events" would flow back from the Web Worker.

A third approach would be to use offline canvas, essentially moving all the processing *and* rendering
to the worker.  This sounds pretty attractive, but presents two obstacles at the moment.
First, offline canvas is only supported in Chromium browsers today.  Second, and more critically,
I'm not sure I'd be able to use `solid-three` in the worker, though I don't know why not.

## SolidJS

More content to follow.

### Context

More content to follow.
