#TODO

 - fix recursion bug for https://raw.githubusercontent.com/vorth/vzome-sharing/main/2021/07/08/10-19-38-600cell-vanOss-A/600cell-vanOss-A.vZome


 - debug local file load in app

 - debug URL load in app (when preview is available)


 - do I even want Redux in the worker?  undo-redo is pretty clean, but can I expose the "document" state there?

 - minimize React reconciliation even for the editor... batch up the scene changes per command before setScene

 - consider refactoring Java controller classes to isolate those that are JSweet-ready, avoiding DocumentController

 - try module workers
 

 
 - implement glowChanged
 - figure out hybrid state model for editor
 - fix 5cell Article and BHall usage of SceneViewer (no worker!)
 - add "edit in vZome Online" gesture to viewer
 - remove view capability from Online?  must remain backward-compatible
 - scene instances as object not array? (optimization)


