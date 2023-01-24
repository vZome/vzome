# vZome Server

This project contains code for the server side of a client-server web application emulating desktop vZome functionality.
That client-server application has been abandoned in favor of a pure client-side implementation,
the code for which can be found in the `online` subproject of this repository;
see [the README](/online/README.md).

The original client code can be found in the repository commit history, as a `client` subproject,
which morphed into the current `online` subproject over time, even before it was renamed as such.

To the best of my recollection, I never got very far in terms of feature parity with desktop vZome
before abandoning the client-server approach; at most, there might have been the ability to open
and render existing documents, with perhaps some simple commands like `select-all` or `create-strut`.

The WebSocket server was [available on Heroku](https://vzome-websocket.herokuapp.com/), but is now
be offline since Heroku is no longer free.  The URL was meant to be used by a client app; accessed directly,
it rendered a kind of console for testing the websocket.

## Server Functionality

When a WebSocket is opened, a new `DocumentController` is created, just as desktop vZome would do.
A `JsonClientRendering` object is connected as the renderer, sending JSON render events back to the client.
Finally, the client sends actions to perform as JSON, which are directed to the appropriate subcontroller.

The implementation can be found in [ControllerWebSocket.java](./src/main/java/com/vzome/server/ControllerWebSocket.java).

## Exporter Servlet

As part of this server implementation, I embedded the `ExporterServlet`.  This had been deployed as a
separate [servlet on Heroku](https://vzome-converter.herokuapp.com/), which is now offline since Heroku
has eliminated their free tier.
