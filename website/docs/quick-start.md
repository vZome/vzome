---
title: Quick Start Guide
subtitle: The basics you need to get started making geometric designs with vZome
---
This guide is pretty skeletal at present, so I&#39;ll try to continue to improve it incrementally. 

There are slight differences between the desktop and online (web) versions of vZome, largely cosmetic.
The screenshots here were captured using desktop vZome.

If you have questions or feedback, you are encouraged to
[join the Discord server](https://discord.gg/vhyFsNAFPS).
There is a small but very supportive community there, and I'm usually available there for timely help.

## The View Trackball

To use vZome you need to know how to build struts, and to do that you must understand the &quot;trackball&quot; metaphor. For that reason, I&#39;ll start with a brief discussion of the viewing controls in the upper right of the vZome window.

<figure style="margin: auto">
  <img alt="" src="https://www.vzome.com/home/data/uploads/viewcontrols.png" />
</figure>

The &quot;rotation trackball&quot; panel is solely used to initiate mouse dragging actions. As with the classical video game trackballs (like Centipedes), the ball can roll in any direction. When you drag with the mouse, imagine that you are dragging a pane of glass over a real rubber trackball, and the pane of glass can move in any direction but does not rotate at all. Many 3D applications use other trackball control metaphors, but I find them nonintuitive and clumsy.

<p>
	The &quot;distance&quot; control is a logarithmic slider to move the viewpoint closer and farther away. If you have a wheelmouse, the wheel should work to move this slider, when the mouse is in the view control area.</p>
<h2>
	Building Struts</h2>
<p>
	To build struts, vZome uses a dragging metaphor, so you drag from any ball. The drag action is the same trackball action described above for the view rotation, so you&#39;ll see a transparent (in desktop vZome) &quot;preview&quot; strut appear, and roll around the starting ball as you drag. Just let go when you have it where you want it. Note that it is always easiest to build toward your viewpoint, since that&#39;s where the trackball is most natural. You can rotate the viewpoint with the trackball in the upper right corner.</p>

<figure style="margin: auto">
	<img alt="" src="https://www.vzome.com/home/data/uploads/previewstrut.png" />
</figure>
<p>
	What struts you can create is determined by the &quot;strut directions&quot; control panel on the right. You can enable just one color at a time, if you like, or all the colors. You can also limit or expand the available colors from the &quot;Directions...&quot; command in the &quot;System&quot; menu. (vZome supports directions (colors) that don&#39;t exist in real Zome.)</p>

<figure style="margin: auto">
	<img alt="" src="https://www.vzome.com/home/data/uploads/strutdirections.png" />
</figure>
<p>
	The length of the struts you build is determined by the &quot;strut size&quot; control in the lower right, with each color having its own length setting. &nbsp;You can use the buttons on the right to select standard Zome sizes (including half blues and half greens). &nbsp;The slider or the arrows can be used to make the strut size larger or smaller by the usual golden ratio scale factor. &nbsp;(In the &quot;System&quot; menu, you&#39;ll find a &quot;Show Strut Scales&quot; checkbox that will expose controls to define more exotic sizes.)</p>

<figure style="margin: auto">
	<img alt="" src="https://www.vzome.com/home/data/uploads/strutlength.png" />
</figure>
<p>
	Once you&#39;ve created some struts, you can select some struts and balls and use the toolbar buttons, or the Edit or Symmetry menu commands. All commands operate on the current selection, which is glowing white. You modify the selection in the usual way, by clicking on objects. By default, clicking on an unselected item makes it selected, adding to rather than replacing the selection, and clicking on a selected item makes it unselected; if you want the more typical &quot;shift-select to add/remove&quot; behavior, I can show you how to enable that. Click on the background to unselect everything.</p>
<p>
	&quot;Join&quot; will be a command that you use frequently, and &quot;Hide&quot;. 
  The symmetry commands, especially &quot;Icosahedral&quot;, are the most powerful. Some are pretty subtle, so don&#39;t be afraid to ask for help.</p>
<p>
	vZome has &quot;infinite&quot; undo/redo, so you can always recover from mistakes, wild goose chases, box canyons, red herrings, and other bad metaphors.</p>
<p>
	There are various forms of import and export, most of them pretty obvious.</p>

<p>
Below is a more detailed set of instructions for building your first dodecahedron using vZome,
with interactive 3D views.
</p>

  <style>
    section {
      height: 90vh;
      width: 100%;
      margin: auto;
      overflow: hidden;
      resize: both;
      gap: 1em;
      display: grid;
      grid-template-rows: min-content min-content 1fr;
      background: rgba(0, 0, 0, 0.4);
      padding: 1em;
    }

    .flex {
      display: flex;
      align-items: center;
    }

    .vzome-viewer-index-button {
      min-width: 90px;
      font-size: large;
      border-radius: 6px;
      border-style: solid;
      border-color: black;
      background-color: aliceblue;
    }

    vzome-viewer {
      width: 100%;
      height: 100%;
    }

    #title {
      padding-inline-start: 2rem;
      margin-block: 0.5rem;
    }

    #description {
      height: 200px;
    }
  </style>

  <script type="module" >
    import "https://www.vzome.com/modules/vzome-viewer.js"; // registers the custom element

    let scenes;

    const welcomeViewer   = document.getElementById( "welcome" );
    const titleText       = document.getElementById( "title" );
    const descriptionText = document.getElementById( "description" );

    welcomeViewer .addEventListener( "vzome-scenes", ( { detail } ) => {
      scenes = [ ...detail ];
    } );
    welcomeViewer .addEventListener( "vzome-design-rendered", ( { detail: scene } ) => {
      const { index } = scene;
      titleText .innerHTML = scenes[ index ] .title;
      descriptionText .innerHTML = scenes[ index ] .content;
    } );

  </script>

<section>
  <div class="flex">
    <vzome-viewer-previous label="back"    load-camera="true" viewer="welcome" class="hidden">
    </vzome-viewer-previous>

    <vzome-viewer-next     label="forward" load-camera="true" viewer="welcome" class="hidden">
    </vzome-viewer-next>

    <h1 id="title"></h1>
  </div>
  <textarea id="description"></textarea>
  <vzome-viewer id="welcome" indexed="true"
    src="https://raw.githubusercontent.com/vorth/vzome-sharing/main/2024/06/16/11-53-24-welcomeDodec-indexed-scenes/welcomeDodec-indexed-scenes.vZome" >
  </vzome-viewer>
</section>
