#TODO

 - fix recursion bug for https://raw.githubusercontent.com/vorth/vzome-sharing/main/2021/07/08/10-19-38-600cell-vanOss-A/600cell-vanOss-A.vZome


 - add spinner while waiting for worker
 - fix 5cell Article and BHall usage of SceneViewer (no worker!)
 - restore undo/redo
 - restore debugger

 - debug "vzome-worker-static.js:91 Uncaught (in promise) TypeError: module.parse is not a function"

 - debug extra strut in orangePurpleChiral.vZome in /app

 - fix timing violations in UI... React suspense?



 - do I even want Redux in the worker?  undo-redo is pretty clean, but can I expose the "document" state there?

 - minimize React reconciliation even for the editor... batch up the scene changes per command before setScene

 - consider refactoring Java controller classes to isolate those that are JSweet-ready, avoiding DocumentController

 - try module workers
 

 
 - implement glowChanged
 - figure out hybrid state model for editor
 - add "edit in vZome Online" gesture to viewer
 - shape instances as object not array? (optimization)

 - implement fog

