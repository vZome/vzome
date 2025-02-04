---
title: Zomod
description:
  Before vZome there was Zomod, an application created by Will Ackel.
published: true
---

[vZome](https://www.vzome.com) did not exist in any form until 2003.
[Zometool](https://zometool.com) has existed since the early 1990s;
how did they prepare their product packaging and marketing materials, with ray-traced renderings, etc.?
They used Zomod, a program developed by Will Ackel, running on the classic Mac.

<figure style="margin: 5%">
  <img  style="width: 100%" src="https://www.vzome.com/zomod/Zomod-screenshot.png" >
  <figcaption style="text-align: center; font-style: italic;">
    A screenshot from Zomod, running on Mac OS 9.2.2 on a Performa G3
  </figcaption>
</figure>

## Zomod Script

Zomod is pretty simple.  It works by interpreting a very basic script language,
directing the placement of Zome struts and balls.
When the interpretation is complete, the result is rendered to a separate window.
The user can manipulate the camera with the mouse.
There are no interactive commands or tools as in vZome; everything is done with the script.

The rendering was accomplished using 2D graphics, since 3D graphics were not generally
available on home computers at the time.  The various polygons were sorted by distance
from the viewpoint (camera), and rendered back to front.  Will called this the "painter's algorithm"
when I asked about it many years ago, since I needed to do the same thing to generate
PDF exports in vZome.

Here is the Zomod script used in the screenshot above:
```
; This was originally modeled with 60 balls and 110 struts!
; Rewritten by Will Ackel - 4/1/98
; This version has 33 balls and 90 struts. (30 each of Triangular, Rectangular, and Pentagonal.)
; Aspect ratio: 1 x 1
; modelRoot->translate (0.0, 1.8, 0.0);
; modelRoot->rotateY   (-15.0);
; modelRoot->rotateX   (-20.0);
; camera->scale		(1.0, 1.0, 2.0);
; camera->translate	(0, 0, -16.2);

; Large Squashed Icosa with parts removed where the Medium and Small Icosas go.
MP0+2,
B																									; Start at apex.
SP1-3,      B,ST2-3,      MT2+3,SR10+3,       ST0-3,      B,SR11+2,MR11+1,MP5-1,SP5-2,MP0+2,MP0+3	; Back to apex.
SP2-3,      B,ST3-2,MT3-1,MT3+3,SR12+3,       ST1-2,MT1-1, ,MR13+1,SR13+2,SP1-3,      MP0+2,MP0+3	; Back to apex.
SP3-3,      B,ST4-3,      MT4+3,SR14+3,       ST2-3,      B,SR10-3,       SP2-3,      MP0+2,MP0+3	; Back to apex.
SP4-3,      B,ST0-3,      MT0+3,SR11-1,MR11-2,MT3-2,ST3-1,B,SR12-3,       SP3-3,      MP0+2,MP0+3	; Back to apex.
SP5-1,MP5-2, ,MT1-2,ST1-1,MT1+3,MR13-2,SR13-1,ST4-3,      B,SR14-3,       SP4-3,      B,MP0+2,MP0+3	; Back to apex.

MP5-1	; Move to apex of Medium Squashed Icosa.

; Medium Squashed Icosa with one ball removed where it touches the Small Icosa.
B
SP1-2,B,ST2-2,MT2+2,SR10+2,ST0-2,B,SR11+2,SP5-2,MP0+3	; Back to apex.
SP2-2,B,ST3-2,MT3+2,SR12+2,ST1-2, ,SR13+2,SP1-2,MP0+3	; Back to apex.
SP3-2,B,ST4-2,MT4+2,SR14+2,ST2-2,B,SR10-2,SP2-2,MP0+3	; Back to apex.
SP4-2,B,ST0-2,MT0+2,SR11-2,ST3-2,B,SR12-2,SP3-2,MP0+3	; Back to apex.
SP5-2,B,ST1-2,MT1+2,SR13-2,ST4-2,B,SR14-2,SP4-2,B,		; Stay at bottom.

MP5+3	; Move to apex of Small Squashed Icosa.

; Small Squashed Icosa.
B														; Start at apex.
SP1-1,B,ST2-1,MT2+1,SR10+1,ST0-1,B,SR11+1,SP5-1,MP0+2	; Back to apex.
SP2-1,B,ST3-1,MT3+1,SR12+1,ST1-1,B,SR13+1,SP1-1,MP0+2	; Back to apex.
SP3-1,B,ST4-1,MT4+1,SR14+1,ST2-1,B,SR10-1,SP2-1,MP0+2	; Back to apex.
SP4-1,B,ST0-1,MT0+1,SR11-1,ST3-1,B,SR12-1,SP3-1,MP0+2	; Back to apex.
SP5-1,B,ST1-1,MT1+1,SR13-1,ST4-1,B,SR14-1,SP4-1,B,MP0+2	; Back to apex.
```

You can tell that comments start with a semicolon, and run to the end of the line.
Other than that, the commands are pretty cryptic.
Balls must be explicitly created, with the `B` command, and struts are created
with the `S` command.  The different strut shapes (not colors) are indicated
with `P` for pentagon, `T` for triangle, `R` for rectangle, and `D` for diamond
(the green strut cross-section).
If you want the details on the syntax, you can
[download the Zomod manual](/assets/Zomod 1.5.1 Manual.pdf).
It was all Will Ackel's invention, and the strut direction indexing he designed is still used
in the Zomic scripting supported by vZome.

Using Zomod is pretty challenging and painstaking, since you must know how the
direction indices relate to each other in space.
It is effectively impossible for a normal human without a 3D "key" of some sort.
Paul Hildebrandt once showed me a Zome ball he had half-covered with tiny, numbered stickers
for this purpose.
The Zomod manual (linked above) includes a net for a dodecahedron,
marked up with the pentagons, triangles, and rectangles of the Zome ball, and the Zomod
index for each face.
I designed another version of the key, also marked with the indices for green struts,
which are reused for orange and purple struts in vZome.


<figure style="margin: 5%">
  <a href="/assets/vZomeKey.pdf">
    <img  style="width: 100%" src="https://www.vzome.com/zomod/vZomeKey.png" >
  </a>
  <figcaption style="text-align: center; font-style: italic;">
    A Zomod/Zomic key dodecahedron, to cut out and assemble; click the image to download a PDF
  </figcaption>
</figure>


## A Zomod Revival

Zomod won't run on modern Macs, so you can't experience the app unless you have an old Mac that still runs.
However, desktop vZome (but not online vZome) now supports Zomod in the "Scripting" menu,
reviving the Zomod application functionality.
To give you a better view of the design shown in the screenshot above,
here is a vZome design in an interactive viewer, created by executing the same script.

<script type="module" src="https://www.vzome.com/modules/vzome-viewer.js"></script>

<figure style="width: 87%; margin: 5%">
 
 <vzome-viewer style="width: 100%; height: 60dvh" 
       src="https://vorth.github.io/vzome-sharing/2025/02/02/23-01-26-FRACTAL-ICOSA/FRACTAL-ICOSA.vZome" >
   <img  style="width: 100%"
       src="https://vorth.github.io/vzome-sharing/2025/02/02/23-01-26-FRACTAL-ICOSA/FRACTAL-ICOSA.png" >
 </vzome-viewer>

 <figcaption style="text-align: center; font-style: italic;">
    A vZome design created from the Zomod script above
 </figcaption>
</figure>

## Zomod's Impact

Zomod was essential to the early success of Zometool,
being used extensively to produce marketing and packaging materials,
and to produce the Zometool Manual 2.0.
Will also used Zomod to produce the lovely color ray-traced
images that appeared as an insert to the ["Zome Geometry"
book by George Hart and Henri Picciotto](https://www.zometool.com/zome-geometry/).

Zomod was also an inspiration for me, and directly led to my work on vZome.
My first attempt had the same character, having just a script interpreter and
a 3D renderer.
However, I wanted to design a better language, a more powerful and expressive
replacement for the Zomod syntax.  I called the language Zomic.
I also had the benefit of computers with 3D graphics capability,
so the rendering could be much nicer, and much faster.
Zomic scripting survives in vZome to this day, though nobody uses it
given the presence of direct manipulation tools and commands.

Another inspiration came from Walt Venable, who wrote a paper
outlining the possibility of representing Zome coordinates
using integers and the golden ratio.
This intriguing idea was the other major impetus for me to write
vZome, together with the language design challenge and my
interest in 3D graphics.
Walt and Will were friends and neighbors in San Diego,
and Walt introduced Will to Zometool, and to Paul Hildebrandt.
Walt was the designer of the Zomod key dodecahedron found in the manual.
