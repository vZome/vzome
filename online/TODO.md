#TODO

 - fix recursion bug for https://raw.githubusercontent.com/vorth/vzome-sharing/main/2021/07/08/10-19-38-600cell-vanOss-A/600cell-vanOss-A.vZome

 - remove the mesh everywhere, and the renderer too

 - move the Redux store to the worker, and dump react-redux... use hooks exclusively for React state

 - do I even want Redux in the worker?  undo-redo is pretty clean, but can I expose the "document" state there?

 - minimize React reconciliation even for the editor... batch up the scene changes per command before setScene

 - consider refactoring Java controller classes to isolate those that are JSweet-ready, avoiding DocumentController

 - try module workers
 