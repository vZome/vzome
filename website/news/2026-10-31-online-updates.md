---
title: Online vZome Updates, October 2026
description: Exports in the custom menu, a loading indicator, scene thumbnails, and fixes for the Parts and Measure tabs
published: true
layout: vzome
---

Here's a round-up of recent improvements to [online vZome](/app/).

## Exports in the Custom Menu

The [custom menu](/news/2025-11-28-online-custom-menu/) can now export your design
or capture an image, just like the "File" menu.
This means you can finally give your favorite export a keyboard shortcut:
```json
[ { "label": "POV-Ray", "action": "export.pov", "key": "R" } ]
```
See the [custom menu article](/news/2025-11-28-online-custom-menu/#exports-and-image-captures)
for the full list of `export.*` and `capture.*` actions.

Custom menu items can also add a keyboard shortcut to built-in commands like "Save",
and an item that leaves out its `action` (using the `label` as the action) now gets
its shortcut too.

## Loading Indicator

When you open a design file, online vZome now shows a "loading" indicator until the design is ready,
so you can tell that something is happening with a large design.

## Scene Thumbnails

The "Scenes" dialog now shows a small picture of each scene next to its title,
so it's much easier to find the scene you want in a long list.
A scene's picture appears once you have viewed that scene in the dialog,
and it updates when you use "Save Camera".
Long scene titles now wrap onto a second line, instead of being cut off.

## Defect Fixes

- The "Parts" and "Measure" tabs now work the first time you click them.
- The "Measure" tab now updates when you change the selection.
- The tabs in the [B. Hall basic models](/app/bhall/basic/) app work again.

As always, you can find me on the [Discord server](https://discord.gg/vhyFsNAFPS)
if you run into problems or have suggestions.
