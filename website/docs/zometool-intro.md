---
title: Introduction to Zometool
subtitle: The basic geometry of Zometool balls and struts
---
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
    src="https://vorth.github.io/vzome-sharing/2025/07/06/09-34-12-Zometool-intro/Zometool-intro.vZome" >
  </vzome-viewer>
</section>
