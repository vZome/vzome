
# vZome Web Experiments

For many years, I have wanted to port vZome to the web.
The primary reason for this is that the web browser is now the truly universal platform,
and an increasingly capable and standardize one, at that.
A secondary reason is the steady erosion of the viability of Java desktop applications,
for various reasons.

This document captures my history of experimentation with web technologies,
always with the goal of porting vZome.  A strong requirement has always been to
continue to support opening existing vZome files, even if the web version of vZome
adopts a very different user experience.

## 3D in the Web

My earliest efforts, starting in 2012, focused on learning about
[WebGL](https://www.khronos.org/webgl/), the browser standard for 3D graphics.
One of my first attempts just uses bare WebGL JavaScript code to
render a Zome ball or any of several other
vZome shapes exported as JSON, such as a short purple strut.
I don't seem to have any source code for this app saved anywhere, except where it is served on `vzome.com`.

<figure style="margin: 5%">
  <iframe id="first-zomeball"
      title="First Web Zome ball"
      width="610"
      height="377"
      src="https://www.vzome.com/testWebGL">
  </iframe>
  <figcaption style="text-align: center; font-style: italic;">
    First Zome ball rendering using WebGL
  </figcaption>
</figure>

<figure style="margin: 5%">
  <iframe id="first-strut"
      title="First Web Zome strut"
      width="610"
      height="377"
      src="https://www.vzome.com/testWebGL/purpleShort.html">
  </iframe>
  <figcaption style="text-align: center; font-style: italic;">
    First purple strut rendering using WebGL
  </figcaption>
</figure>

---

My next attempt, in 2015, used [TDL](https://github.com/greggman/tdl), a wrapper library around WebGL
created by [Gregg Tavares](https://games.greggman.com/game/about/).
The source code for this application is [available in GitHub](https://github.com/vorth/vzome-webview).
Here the data is a collection of JSON files exported from vZome.
This application includes some primitive user interface elements,
buttons to navigate between designs,
and one button that opened a DropBox chooser to load additional designs.

<figure style="margin: 5%">
  <iframe id="tdl-ball"
      title="vZome models rendered using TDL"
      width="610"
      height="377"
      src="https://www.vzome.com/webview/">
  </iframe>
  <figcaption style="text-align: center; font-style: italic;">
    Zome designs rendered using TDL library
  </figcaption>
</figure>

---

As early as 2014, [three.js](https://threejs.org/) had come to my attention,
having already gained a high degree of adoption in the WebGL community.
I was also starting to think about replacing desktop vZome,
looking into [Electron](https://www.electronjs.org/), a framework for building cross-platform desktop apps using web technologies.
In 2017 I created this application, a fairly simple first effort with three.js.

The source code for this application is also [available in GitHub](https://github.com/vorth/vzome-web).
This project started as a fork of [sample code by Jerome Etienne](https://github.com/jeromeetienne/electron-threejs-example).

<figure style="margin: 5%">
  <iframe id="tdl-ball"
      title="Zome ball using three.js"
      width="610"
      height="377"
      src="https://vorth.github.io/vzome-web/">
  </iframe>
  <figcaption style="text-align: center; font-style: italic;">
    Zome ball rendered using three.js
  </figcaption>
</figure>

## Java in the Web
