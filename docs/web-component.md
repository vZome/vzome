---
title: vZome Viewer Web Component
description:
  The `vzome-viewer` web component can display your vZome designs on any web page.
  This page explains how to use it.
image: https://docs.vzome.com/assets/vzome-viewer-in-use.png
published: true
---

<script type="module" src="https://www.vzome.com/modules/vzome-viewer.js"></script>

{% comment %}
 - [welcome dodec assets](<https://github.com/vorth/vzome-sharing/tree/main/2022/06/19/06-37-55-welcomeDodec/>)
 
{% endcomment %}


The `vzome-viewer` custom HTML element is an interactive 3D viewer for [vZome](https://vzome.com) designs.
The viewer can be placed on any web page, because it implements
the [web component standard](https://developer.mozilla.org/en-US/docs/Web/API/Web_Components).

If you are using [vZome GitHub sharing](./sharing.html), you can ignore much of this page, since vZome generates
web pages and supporting assets, all pre-configured to work together.  That said, if you want to
adjust your generated pages to
[display multi-scene designs](#multi-scene), read that section below.

  This document is a work-in-progress.  Please [email us](mailto:info@vzome.com) if you have questions,
  or join the [Discord server](https://discord.gg/vhyFsNAFPS).

## Basic Usage

If you are authoring your own web page as HTML, adding the `vzome-viewer` element is very simple.
You can add several viewers to a page, but you must load the Javascript module in a `script` tag somewhere on the page:

```html
<script type="module" src="https://www.vzome.com/modules/vzome-viewer.js"></script>
```

Typically, script tags appear inside the `head` element, but it is not strictly necessary.
Loading the Javascript module like this is what defines the `vzome-viewer` custom element,
making it available to use on the page.
Only one such script tag is necessary, no matter how many `vzome-viewer` elements you have on the page.

The simplest form of the `vzome-viewer` element has just a `src` attribute whose value is a URL that points to a vZome design:
```html
<vzome-viewer src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/welcomeDodec.vZome" ></vzome-viewer>
```
In this example, the URL is *absolute*, containing a scheme ("https") and a domain name ("vorth.github.io").
The URL can also be *relative*, just a path relative to the website or to the current HTML file.  Either way, the URL
must correctly resolve to a vZome file.
```html
<vzome-viewer src="/a/b/c/d.vZome" ></vzome-viewer>
<vzome-viewer src="../f/g/h.vZome" ></vzome-viewer>
```

While there is no prescribed limit to how many viewers you can put on a single page, each one does consume
resources including a 3D canvas and a WebGL context, and browsers may limit the number of contexts allowed on a single page.
Even if they don't explicitly limit them, the performance will degrade if you have too many.
I recommend placing no more than ten `vzome-viewer` elements on a single web page.

## Preview JSON

Parsing and interpreting a vZome file can be complex, since the whole command history must be replayed in order.
In order to provide better performance, the viewer looks for a 3D preview file next to the vZome file,
containing just geometry and no command history, for quick loading.
If the preview file is not found, the viewer will load the vZome file, interpret it, and render the result;
the preview file is completely optional.

The URL of the preview file must be the same URL as the vZome file, but with the "`.vZome`" extension
replaced with "`.shapes.json`".

You can export such a file from the vZome desktop app.  Under the "File" menu, find the "Export 3D Rendering..."
submenu, and select the "vZome Shapes JSON (polygons)" item.

If you are sharing vZome designs using [GitHub sharing](./sharing.html), the preview JSON will be generated and uploaded automatically, each time you share a design.

## Camera Animation

When the view loads the initial camera from a vZome design or preview, it animates the
change from the default camera state.  This "tweening" takes 500ms (1/2 second).
The animation has the nice side-effect of hinting to the user that the viewer is neither
a static image nor a looping animation.

You can control the duration of the animation with the `tween-duration` attribute, with a value in milliseconds:
```html
<vzome-viewer tween-duration="0"
       src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/welcomeDodec.vZome" >
</vzome-viewer>
```
A duration of zero (as shown) disables the animation completely.  Durations longer than 2000ms might not be a good experience,
but ultimately you can judge for yourself.

## Disabling vZome Source Access

If the `.vZome` design source is available, the viewer displays a download button in the lower left.
When the button is clicked, a popup menu appears with options to open the design in
[vZome Online classic](https://www.vzome.com/app/classic) or download the vZome source file.
If you wish to keep your vZome design source private,
you can simply not host the `.vZome` design file at all, just the `.shapes.json` preview file.
The missing `.vZome` source will cause the download button to be hidden.
If you are sharing vZome designs using [GitHub sharing](./sharing.html),
you can delete the `.vZome` file from the assets folder after it is uploaded when you share.
However, *do not* change the `src` URL for the viewer; it should still refer to
the `.vZome` file, even though that resource won't be fetched.

If you're not concerned with the privacy of your vZome designs,
but you simply don't want the extra button on the screen,
you can set the viewer's `download` attribute to false.
```html
<vzome-viewer download="false"
       src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/welcomeDodec.vZome" >
</vzome-viewer>
```
In this case you don't need to delete the uploaded `.vZome` file.

## <a id="multi-scene"></a> Displaying Multi-Scene Designs

If you have captured scenes in your vZome design, you can make those scenes available to the user.
This can be done with an attribute selecting a particular scene,
with an internal drop-down menu, with integrated "next" and "previous" buttons elsewhere
on your web page, or programmatically through Javascript.

The different methods have different strengths.
Is random access appropriate in your use-case, or does the order of scenes matter?
You should design your scenes
and title them (or not) depending on which method you will use.

### Displaying a Specific Scene

Sometimes you want to show the user only one, specific scene, and not give them any
ability to select other scenes.
For that case, use the `scene` attribute.
Its value can be a scene title (which must be unique among your scenes), or scene index
in the form "#0", "#1", and so on.
Scenes are indexed starting at zero, and an untitled scene is addressed based on its position among all scenes,
NOT among all untitled scenes.
A titled scene can still be accessed using its index.
```html
<vzome-viewer scene="#7"
       src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/welcomeDodec.vZome" >
</vzome-viewer>
```
If you use the `scene` attribute, the `show-scenes` attribute (see below) will be ignored.

### Internal Scenes Menu

If you add the `show-scenes` attribute to your `vzome-viewer`, the viewer will display an internal
drop-down menu that allows the user to switch scenes.
There are two meaningful values for the attribute, "all" or "named".  The "all" value means that the
drop-down menu will show all scenes, including the default scene (the main design) and untitled scenes,
which will display in the menu as "#1", "#2", and so on.
The "named" value means that only scenes explicitly given titles will appear in the menu; the default scene is not included.
Any other value for the `show-scenes` attribute means the attribute will be ignored.

Here is source HTML for the `vzome-viewer` element shown below, with the `show-scenes` attribute:
 ```html
<vzome-viewer style="width: 87%; height: 60vh; margin: 5%" show-scenes="named"
       src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/welcomeDodec.vZome" >
  <img src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/watermarked.png" />
</vzome-viewer>
 ```

<figure style="width: 87%; margin: 5%">
  <vzome-viewer style="width: 87%; height: 60vh; margin: 5%" show-scenes="named"
        src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/welcomeDodec.vZome" >
    <img style="width: 100%"
        src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/watermarked.png" />
  </vzome-viewer>
  <figcaption style="text-align: center; font-style: italic;">
    example of the vZome Viewer web component
  </figcaption>
</figure>

#### Camera Animations

When the user changes the scene using the menu, a new camera configuration
will *always* be loaded with the scene.
This camera change will be animated (or not)
based on the value of the `tween-duration` attribute [described earlier](#camera-animation).

### Indexed Scenes

A particularly simple way to display multiple scenes is to let the user visit them
in order using "next" and "previous" buttons.
This is convenient for displaying the steps to construct a physical Zometool model.
You must put the `vzome-viewer` into indexed mode
by adding `indexed="true"`.  In this mode, the `reactive`, `scene`, and `show-scenes` attributes are ignored.

The scene can be controlled using Javascript, using the `nextScene()` and `previousScene()` methods
available on the `vzome-viewer` element.
However, you can also control the viewer without any Javascript code,
by including additional web components, as shown here:
```html
<vzome-viewer-previous viewer="myViewer" label='prev step'></vzome-viewer-previous>
<vzome-viewer-next     viewer="myViewer" label='next step'></vzome-viewer-next>
<vzome-viewer id="myViewer"
       src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/welcomeDodec.vZome" >
</vzome-viewer>
```
The value of the `viewer` attribute should match the `id` on your `vzome-viewer` element.
The `viewer` attribute is optional when you only have one `vzome-viewer` element on your page.

There are also two more components available in the same pattern, `vzome-viewer-start` for the first scene,
and `vzome-viewer-end` for the last scene.
All four of these components render and function as buttons, and can be placed in your HTML, labeled, and styled however you like.

#### Loading the Scene Camera

By default, when the user clicks on any button as configured above,
the viewer will load the new scene but ignore the camera data stored with the scene.
This lets the user control the camera fully (after the first scene load),
and prevents surprising camera jumps.
If you prefer to have each scene load *with* its camera data (possibly animated), use the `load-camera` attribute as below:
```html
<vzome-viewer-previous load-camera='true' label='prev step'></vzome-viewer-previous>
<vzome-viewer-next load-camera='true' label='next step'></vzome-viewer-next>
```

If the camera is loaded, it will be animated (or not)
based on the value of the `tween-duration` attribute [described earlier](#camera-animation).


### Javascript Scene Control

## Controlling the Web Component Version

As described above, the `vzome-viewer.js` module is downloaded each time your web page loads,
except for the usual browser caching mechanisms.  Whenever it is downloaded again, there may be a new revision with updated features
and bug fixes.  This is generally a good thing, but it does introduce risk.
If you are concerned about that risk, you can refer to a specific revision of the module,
and therefore be certain that it won't change except when you want it to:

```html
<script type="module" src="https://www.vzome.com/modules/r224/vzome-viewer.js"></script>
```

When you are ready to capture the revision number ("224" here) to record in your script tag, you can find
it displayed in the browser's debug console whenever the module is loaded:

```
vzome-viewer revision 224
```

Remember to use the "r" prefix before the revision number in your script tag!

## Fallback HTML

## Usage in Blogs or E-Commerce Systems

## Hosting Your Designs

