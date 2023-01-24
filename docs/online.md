---
title: vZome Online Web Applications
image: https://user-images.githubusercontent.com/1584024/211131345-202c81ed-3972-4daf-a8f3-3a789a670e2d.png
description:
  There are several variants of vZome Online, a port of desktop vZome technology to the web.
published: true
---

<img width="1011" alt="Screen Shot 2023-01-06 at 19 05 18" src="https://user-images.githubusercontent.com/1584024/211131345-202c81ed-3972-4daf-a8f3-3a789a670e2d.png">

## Introduction

For years I have wanted to port [vZome](https://vzome.com/) to the web, for several reasons.
The most important reason is the ubiquity of the web as a platform, of course, which means that
web applications are available to everyone on the Internet, with no installation step required.

Now, the goal is in sight.  I have a "classic" variant of vZome Online that is starting to approach
feature parity with desktop vZome.  Along the way, I've implemented a number of web applications
using the same core software.  This page is an introduction to the whole menagerie;
most of the headings below are links to the applications being described.

## [Classic vZome Online](https://vzome.com/app/classic)

This application is intended to be a faithful port of desktop vZome to the web,
preserving almost all aspects of the desktop user interface for the sake of
familiarity.
It still has a long way to go, but check back often, as it is continually improving.
It can already open and save vZome files, and so is fully interoperable with desktop vZome.

## [vZome Online Build Plane](https://vzome.com/app/buildplane)

This is a proof-of-concept -- an experiment in providing a more intuitive experience
for building struts in 3D space.  It accomplishes this by restricting you to a 2D
plane, with gestures to change the plane.
I intend for this to be the centerpiece of a new vZome Online, one that has
a modern UI more in the style of [Desmos](https://www.desmos.com/geometry) or [Geogebra](https://www.geogebra.org/geometry).
For now, it is fun to play with.
Even so, it already supports saving designs that you can open with desktop vZome 7.1.14 or later.

## [vZome Online History Inspector](https://vzome.com/app)

This URL will be the eventual home of the main vZome Online application.
For now, it hosts an application that allows you to inspect the edit history of a vZome design,
viewing the changes with every edit.

## The vZome Online Web Component

In many ways, this is the most important application, and it is not a single application in any sense.
I have wrapped the core vZome Online technology as a [web component](https://developer.mozilla.org/en-US/docs/Web/Web_Components),
which makes it nearly trivial to embed interactive 3D vZome designs into any web page,
blog, e-commerce site, or whatever.

<script type="module" src="https://www.vzome.com/modules/vzome-viewer.js"></script>
<figure style="margin: 5%">
  <vzome-viewer style="width: 100%; height: 60vh"
      src="https://vorth.github.io/vzome-sharing/2021/09/11/10-10-11-rose-olive-bombshell-solid/rose-olive-bombshell-solid.vZome" >
    <img  style="width: 100%"
      src="https://vorth.github.io/vzome-sharing/2021/09/11/10-10-11-rose-olive-bombshell-solid/rose-olive-bombshell-solid.png" >
  </vzome-viewer>
  <figcaption style="text-align: center; font-style: italic;">
    A sample design exported from vZome, displayed using the vzome-viewer custom HTML element
  </figcaption>
</figure>

Here is the essential HTML for the figure above:
```
<script type="module" src="https://www.vzome.com/modules/vzome-viewer.js"></script>

<vzome-viewer style="width: 100%; height: 60vh"
    src="https://vorth.github.io/vzome-sharing/2021/09/11/10-10-11-rose-olive-bombshell-solid/rose-olive-bombshell-solid.vZome" >
  <img  style="width: 100%"
    src="https://vorth.github.io/vzome-sharing/2021/09/11/10-10-11-rose-olive-bombshell-solid/rose-olive-bombshell-solid.png" >
</vzome-viewer>
```
The `<script/>` tag loads the Javascript code that defines the `<vzome-viewer/>` custom tag, which does all the
heavy lifting of loading the vZome model and rendering it in a 3D viewer.  Only one such script tag is needed per page,
no matter how many viewer elements appear on the page.

The web component plays a huge role in vZome desktop's mechanism for
[sharing designs via GitHub](./sharing.html).
You can see the power of this sharing feature when you look at [my geometry blog](https://vorth.github.io/vzome-sharing/),
and even more when you browse [John Kostick's](https://John-Kostick.github.io/vzome-sharing/).
A number of other vZome users have set up their own sharing website;
follow the [simple instructions](./sharing.html), if you'd like to do the same.

## [vZome Online GitHub Browser](https://vzome.com/app/browser)

This a utility that comes in handy when [sharing vZome designs via GitHub](./sharing.html) as described above,
or more specifically, when composing web pages that show multiple designs.
A common pattern is to export a web page when sharing one design,
then enhance the page with more designs already shared.
This browser helps you find those existing designs and grab the HTML snippet to include them
in any web page.  I used it when writing this web page!

By default, the app will display my own GitHub repository.  To display another GitHub user's designs,
simply add their GitHub username as a query parameter:
```
https://vzome.com/app/browser?user=John-Kostick
```


