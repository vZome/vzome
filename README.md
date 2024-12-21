# vZome

This is the source repository for [vZome](https://vzome.com/), an application for creating virtual Zome models and exploring other constrained geometric realms.
There are two variants of vZome, [a web application](https://vzome.com/app) and a desktop app.

Development on the desktop variant is almost completely stopped,
since the online version is the future of vZome.
That said, online vZome is not yet completely feature equivalent to desktop vZome.

## Online vZome Development Workflow

See [online/README.md](online/README.md).
If you're interested in exploring the vZome code, this should be your starting point.

## Desktop vZome Development Workflow

See [desktop/README.md](desktop/README.md).
Both the `desktop` and `core` folders contain the Java source code
for desktop vZome.

## Documentation

The `docs` folder contains source for a GitHub Pages site that
provides the [user documentation for vZome](https://docs.vzome.com).

The `developer-docs` folder has a few things captured for developer use,
though they are probably not very current.

## Other Subprojects

There are several other subfolders with sources for efforts that are no longer active,
at least for now.

### `pwa`

This is the predecessor for the `online` application, and explores
how to build a progressive web application.  It is completely
superseded by `online`.

### `oculus`

This folder contains the source code for a virtual reality form
of vZome, using the Unity framework to target the Meta (Oculus) Quest headset.
The frameworks and libraries here are now out-of-date,
and future VR work will focus on WebXR and therefore be built
into the online vZome application.

The code here uses `core` Java code, which can run on Android devices like the Quest.

### `server`

This folder contains source code for a headless server,
originally designed to support conversions (exports) from vZome to other
formats.
It was later adapted to also support a client-server predecessor
to online vZome.
This server also relies on Java code from `core`.
