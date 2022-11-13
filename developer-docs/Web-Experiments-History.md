
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

<iframe id="first-zomeball"
    title="First Web Zome ball"
    width="600"
    height="400"
    src="https://www.vzome.com/testWebGL">
</iframe>

<iframe id="first-strut"
    title="First Web Zome strut"
    width="600"
    height="400"
    src="https://www.vzome.com/testWebGL/purpleShort.html">
</iframe>

<iframe id="three-ball"
    title="First Three.js"
    width="600"
    height="400"
    src="https://vorth.github.io/vzome-web/">
</iframe>


My next attempt, in 2015, used [TDL](https://github.com/greggman/tdl), a wrapper library around WebGL
created by [Gregg Tavares](https://games.greggman.com/game/about/).

<iframe id="first-strut"
    title="First Web Zome strut"
    width="600"
    height="400"
    src="https://www.vzome.com/testWebGL/purpleShort.html">
</iframe>
