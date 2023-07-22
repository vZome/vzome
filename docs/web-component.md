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
[display multi-scene designs](#multi-scene), read that section.

## Basic Usage

If you are authoring your own web page as HTML, adding the `vzome-viewer` element is very simple.
You can add several viewers to a page, but you must load the Javascript module in a `script` tag somewhere on the page:

```html
<script type="module" src="https://www.vzome.com/modules/vzome-viewer.js"></script>
```

Typically, script tags appear inside the `head` element, but it is not strictly necessary.
Loading the Javascript module like this is what defines the `vzome-viewer` custom element,
making it available to use on the page.

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
resources including a 3D canvas, and browsers may limit the number of canvases allowed on a single page.
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
submenu, and select the "vZome Shapes JSON" item.

If you are sharing vZome designs using [GitHub sharing](./sharing.html), the preview JSON will be generated and uploaded automatically, each time you share a design.

## Fallback HTML

## Usage in Blogs or E-Commerce Systems

## Hosting Designs

## <a id="multi-scene"></a> Displaying Multi-Scene Designs

### Internal Scene Control

You can select different scenes that were defined, illustrating the first steps of using vZome.

Here is source HTML for the `vzome-viewer` element shown below.  Notice the `show-scenes` attribute:
 ```html
<vzome-viewer style="width: 87%; height: 60vh; margin: 5%" show-scenes="true"
       src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/welcomeDodec.vZome" >
  <img src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/watermarked.png" />
</vzome-viewer>
 ```

<figure style="width: 87%; margin: 5%">
  <vzome-viewer style="width: 87%; height: 60vh; margin: 5%" show-scenes="true"
        src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/welcomeDodec.vZome" >
    <img style="width: 100%"
        src="https://vorth.github.io/vzome-sharing/2022/06/19/06-37-55-welcomeDodec/watermarked.png" />
  </vzome-viewer>
  <figcaption style="text-align: center; font-style: italic;">
    example of the vZome Viewer web component
  </figcaption>
</figure>

### External Scene Control

## Hosting the Web Component
