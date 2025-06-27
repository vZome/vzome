# Online vZome Rendering State

## Worker In-memory State

In the worker, loading or creating a vZome design passes through `initializeDesign`, which returns, among other things:
- `instances` - the instances in the default snapshot
- `camera` - the camera for the default scene
- `snapshots` - all explicitly-created snapshots
- `scenes` - all explicitly-created scenes
- `lighting`
The default snapshot and scene are **not** represented in `snapshots` and `scenes`.

Once loaded, we want to send `scenes`, `camera`, and `lighting` to the client context, which will manage that state, and remove them from the state on the worker side.

## Canonical "online" `.shapes.json`

To export this format, we must add back some data from the client:
- `camera` - redundant with the default `scenes[0]`; should we remove it?
- `scenes`
- `lighting`

