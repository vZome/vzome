---
title: Mouse Tool Improvements for Online vZome
description: Three mouse modes, on-screen mouse hints, box selection, a new Settings dialog, more camera controls, and a working Build Plane app
image: https://vzome.com/assets/marquee-selection.png
published: false
layout: vzome
---

[Online vZome](/app/) has a number of enhancements today: three mouse modes, on-screen mouse hints, marquee (box) selection, a new Settings dialog, and more camera controls.

## Three Mouse Modes

There are now three buttons at the top right of the editor, instead of two:

- **Camera** mode moves your view.
  Drag to rotate, right-drag or shift-drag to pan, and use the mouse wheel (or middle-drag) to zoom.
- **Select** mode changes what is selected.
  Click a part to select or deselect it, click empty space to deselect everything,
  or drag a box to select many parts at once (see below).
- **Build** mode adds to your model.
  Drag from a ball to build a strut, rolling it around to choose its direction,
  and use the mouse wheel while dragging to change its length.

Before, building and selecting shared one mode.
Now, a click in build mode does nothing, so you won't change the selection by accident while building;
switch to select mode to select.
If you'd rather build mode did more, you can change that in the new Settings dialog (see below).
Build mode is active when you open the editor.

You can switch modes from the keyboard: press **C** for camera, **S** for select, or **B** for build.
As before, holding down **Option** or **Alt** switches to camera mode until you let go,
and it now returns you to whichever mode you were in.
The mouse wheel zooms in every mode, not just camera mode.

## Mouse Hints

A small panel in the bottom left corner of the editor tells you what the mouse does in the current mode.
If you don't need it, click the arrow beside the mode's name to shrink it to a single line;
online vZome remembers your choice.
The same summary appears when you hover over a mode button,
and for build mode it includes whatever extra behaviors you have turned on in Settings.

## Box Selection

In select mode, drag anywhere to draw a box, even if you start on top of a part.
When you let go, every part entirely inside the box is added to the selection,
including parts hidden behind other parts.
A box selection only adds: parts that were already selected stay selected.
To replace the selection, click empty space first, then drag your box.

Undo works for box selections too.
It may take two "Undo" steps to undo one box selection completely.

## Settings

There is a new gear button at the right end of the editor's top bar.
It opens a Settings dialog, and online vZome remembers your choices from one visit to the next.
There are two categories so far.

**Build Tool** gives build mode some extra behaviors.
Dragging from a ball always builds a strut, but you can also choose:

- what a **click** does: nothing (the default), or select and deselect parts, as in select mode;
- what a **drag that doesn't start on a ball** does: nothing (the default),
  rotate the view, as in camera mode, or draw a selection box, as in select mode.

To make build mode work like desktop vZome, choose "Select or deselect" for the click
and "Rotate the view" for the drag.

**Rendering** chooses how the camera starts out in a new editor window:
orthographic or perspective, and whether "snap camera" and "show outlines" are on.
The switches in the camera panel still work as before, but they only change the current window.
When you open a design, it opens with its own saved projection, orthographic or perspective.
New designs start with the projection you chose in Settings.

## Other Fixes

- Dragging out a strut now starts right away in every direction.
  Before, a drag that started upward or to the left could be ignored, or treated as a click.
- Right-clicking in camera mode no longer opens the browser's menu,
  since the right mouse button now pans.
- Right-clicking again while a context menu is open no longer brings up the browser's own menu on top of it.
- Designs saved in orthographic view now open in orthographic view.
  Before, the editor always opened them in perspective.

## The Build Plane App

The [vZome Build Plane](/app/buildplane/) app works again:
clicking balls and the empty background had stopped working.
It also shows a strut preview when you move the mouse over a ball,
and you can pan with the right mouse button and zoom with the wheel.

## Known Limitation

On touch screens, one-finger dragging works as before,
but two-finger pinch and pan no longer work in the editor or the Build Plane app.
This will come back in a future update.

As always, you can find me on the [Discord server](https://discord.gg/vhyFsNAFPS)
if you run into problems or have suggestions.
