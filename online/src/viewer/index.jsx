
import { REVISION } from '../revision.js'

console.log( `vzome-viewer revision ${REVISION}` );

import { urlViewerCSS } from "./urlviewer.css";

import { createEffect, createSignal, getOwner, Match, mergeProps, onCleanup, onMount, Show, Switch } from 'solid-js';
import { render } from 'solid-js/web';

import { useWorkerClient, WorkerProvider } from './context/worker.jsx';
import { ViewerProvider, useViewer } from './context/viewer.jsx';
import { SceneIndexingProvider, SceneProvider, SceneTitlesProvider, useScene, useSceneIndexing, useSceneTitles } from './context/scene.jsx';
import { InteractionToolProvider } from './context/interaction.jsx';
import { CameraProvider, useCamera } from './context/camera.jsx';
import { GltfExportProvider } from './context/export.jsx';

import { SceneCanvas } from "./scenecanvas.jsx";
import { Spinner } from './spinner.jsx';
import { ErrorAlert } from './alert.jsx';
import { SceneMenu } from './scenes.jsx';
import { FullscreenButton } from './fullscreen.jsx';
import { ExportMenu } from './export.jsx';
import { IconButton } from './iconbutton.jsx';
import { Settings } from './settings.jsx';

let stylesAdded = false; // for the onMount in DesignViewer

const normalStyle = {
  display: 'flex',       // flex is for the light dom content, usually an image
  height: '100%',
  width: '100%',
  position: 'relative',
  overflow: 'hidden',    // curiously, this forces Canvas to recompute its size when changing back
};

const SceneViewer = ( props ) =>
{
  const dynConfig = () => mergeProps( { allowFullViewport: false, showSettings: true, showOutlines: true }, props.config );

  const { scene } = useScene();

  const [ showSettings, setShowSettings ] = createSignal( false );

  let rootRef;
  
  const [ fullScreen, setFullScreen ] = createSignal( false );
  const toggleFullScreen = () =>
  {
    if ( document.fullscreenElement ) {
      document .exitFullscreen();
    }
    else if ( document.fullscreenEnabled && rootRef.requestFullscreen) {
      rootRef.requestFullscreen();
    }
  }
  const fullscreenListener = () => setFullScreen( !! document.fullscreenElement );
  document .addEventListener( "fullscreenchange", fullscreenListener );
  getOwner() &&
    onCleanup(() => {
      document .removeEventListener( "fullscreenchange", fullscreenListener );
      if ( document.fullscreenElement ) {
        document .exitFullscreen();
      }
    });

  return (
    <div id='scene-viewer' ref={rootRef} style={normalStyle}>
      <InteractionToolProvider>
        <GltfExportProvider>

          {/* This renders the light DOM if the scene couldn't load.
            TODO: refactor more for use in VRML and glTF, where there is no scene context.
          */}
          <Show when={scene} fallback={props.children}>
            <SceneCanvas id='scene-canvas' height={props.height} width={props.width} >
              {props.children3d}
            </SceneCanvas>
          </Show>

          <Show when={dynConfig().showSettings} >
            <IconButton class='settings-button' tooltip='Show settings' onClick={() => setShowSettings(true)} root={rootRef}>
              <svg aria-hidden="true" viewBox="-1 -1 25 25" focusable="false" data-testid="SettingsIcon">
                <g>
                  <path d="M0,0h24v24H0V0z" fill="none"></path>
                  <path d="M19.14,12.94c0.04-0.3,0.06-0.61,0.06-0.94c0-0.32-0.02-0.64-0.07-0.94l2.03-1.58c0.18-0.14,0.23-0.41,0.12-0.61 l-1.92-3.32c-0.12-0.22-0.37-0.29-0.59-0.22l-2.39,0.96c-0.5-0.38-1.03-0.7-1.62-0.94L14.4,2.81c-0.04-0.24-0.24-0.41-0.48-0.41 h-3.84c-0.24,0-0.43,0.17-0.47,0.41L9.25,5.35C8.66,5.59,8.12,5.92,7.63,6.29L5.24,5.33c-0.22-0.08-0.47,0-0.59,0.22L2.74,8.87 C2.62,9.08,2.66,9.34,2.86,9.48l2.03,1.58C4.84,11.36,4.8,11.69,4.8,12s0.02,0.64,0.07,0.94l-2.03,1.58 c-0.18,0.14-0.23,0.41-0.12,0.61l1.92,3.32c0.12,0.22,0.37,0.29,0.59,0.22l2.39-0.96c0.5,0.38,1.03,0.7,1.62,0.94l0.36,2.54 c0.05,0.24,0.24,0.41,0.48,0.41h3.84c0.24,0,0.44-0.17,0.47-0.41l0.36-2.54c0.59-0.24,1.13-0.56,1.62-0.94l2.39,0.96 c0.22,0.08,0.47,0,0.59-0.22l1.92-3.32c0.12-0.22,0.07-0.47-0.12-0.61L19.14,12.94z M12,15.6c-1.98,0-3.6-1.62-3.6-3.6 s1.62-3.6,3.6-3.6s3.6,1.62,3.6,3.6S13.98,15.6,12,15.6z"></path>
                </g>
              </svg>
            </IconButton>
          </Show>

          <Show when={showSettings()} >
            <Settings root={rootRef} showOutlines={scene?.polygons && dynConfig().showOutlines} close={() => setShowSettings(false)} />
          </Show>

          <Show when={dynConfig().allowFullViewport}>
            <FullscreenButton fullScreen={fullScreen()} root={rootRef} toggle={toggleFullScreen} />
          </Show>

          {props.children}
        </GltfExportProvider>
      </InteractionToolProvider>
    </div>
  );
}

const InitializeScene = ( props ) =>
{
  const { showIndexedScene } = useSceneIndexing();
  const { showTitledScene, sceneTitle } = useSceneTitles();

  createEffect( () => {
    if ( props.showScenes === 'indexed') {
      showIndexedScene( 1 );
    } else if ( props.showScenes === 'none' ) {
      showIndexedScene( 0 );
    } else if ( props.showScenes !== 'managed' ) {
      showTitledScene( sceneTitle() );
    }
  } );

  return null;
}

const DesignViewer = ( props ) =>
{
  const dynConfig = () => mergeProps( { showScenes: 'none', useSpinner: false, download: true }, props.config );

  const { waiting } = useViewer();

  const showSpinner = () => {
    return props.config?.useSpinner && waiting();
  }

  onMount( () => {
    if ( ! props.componentRoot && ! stylesAdded ) {
      // Not in a web component context, or props.componentRoot would be set
      document.body .appendChild( document.createElement("style") ).textContent = urlViewerCSS;
      // We don't want to add multiple identical style elements, one for each DesignViewer on the page.
      stylesAdded = true;
    }
  });

  let rootRef;
  return (
    <SceneTitlesProvider show={dynConfig().showScenes} title={dynConfig().sceneTitle} >
      <SceneViewer children3d={props.children3d} height={props.height} width={props.width} config={dynConfig()} >

        <InitializeScene showScenes={dynConfig().showScenes} />

        <div ref={rootRef}>

          <Show when={dynConfig().download} >
            <ExportMenu root={rootRef} />
          </Show>

          <Show when={showSpinner()}>
            <Spinner root={rootRef} />
          </Show>

          <ErrorAlert root={rootRef} />

          {props.children}

          <Show when={ dynConfig().showScenes === 'all' || dynConfig().showScenes === 'titled' }>
            <SceneMenu root={rootRef} />
          </Show>

        </div>
      </SceneViewer>
    </SceneTitlesProvider>
  );
}

const UrlViewer = (props) =>
{
  return (
    <CameraProvider tweening={props.config?.tweening}>
      <WorkerProvider>
        <ViewerProvider
            config={{
              url: props.url,
              preview: true,
              debug: false,
              labels: props.config?.labels,
              source: props.config?.download
            }}>
          <SceneProvider index={ props.config.snapshot } config={ props.config }>
            <SceneIndexingProvider>
              {/* Is there a good reason that both of those providers are not built into DesignViewer? */}
              <DesignViewer config={ { ...props.config, allowFullViewport: true } }
                  componentRoot={props.componentRoot}
                  height="100%" width="100%" >
                {props.children}
              </DesignViewer>
            </SceneIndexingProvider>
          </SceneProvider>
        </ViewerProvider>
      </WorkerProvider>
    </CameraProvider>
  );
}

const WebComponentBindings = (props) =>
{
  const { showIndexedScene, lastSceneIndex, setLastSceneIndex } = useSceneIndexing();
  const { setTweenDuration } = useCamera();
  const { requestDesign, resetScenes, openText, exportAs, } = useViewer();
  const { showTitledScene } = useSceneTitles();
  const apiObject = { showIndexedScene, showTitledScene, requestDesign, setTweenDuration, resetScenes, openText, exportAs, };
  const { subscribeFor } = useWorkerClient();
  const { setApi, onAlert, onSceneRendered, onScenesDiscovered } = props.callbacks;
  setApi( apiObject );
  subscribeFor( 'ALERT_RAISED', onAlert ); 
  subscribeFor( 'SCENES_DISCOVERED', ( { scenes } ) => { setLastSceneIndex(null); onScenesDiscovered( scenes ); } );

  createEffect( () => {
    // The setLastSceneIndex(null) call above ensures that this effect runs after the first scene is shown,
    //   even if the first scene index is 0, unchanged from the last scene of the last design.
    //   This supports reuse of a viewer component for multiple designs, as in our regression test.
    if ( lastSceneIndex() !== null ) {
      onSceneRendered( lastSceneIndex() );
    }
  });

  return null;
}

const renderViewer = ( container, config, callbacks ) =>
{
  const bindComponent = () =>
  {
    return (
      <UrlViewer config={config} componentRoot={container} >
        {/* Make a slot for the light DOM, somehow? */}
        <WebComponentBindings callbacks={callbacks} />
      </UrlViewer>
    );
  }

  container .appendChild( document.createElement("style") ).textContent = urlViewerCSS;
  // Apply external override styles to the shadow dom
  const linkElem = document.createElement("link");
  linkElem .setAttribute("rel", "stylesheet");
  linkElem .setAttribute("href", "./vzome-viewer-styles.css");
  container .appendChild( linkElem );

  render( bindComponent, container );
}


export { CameraProvider, DesignViewer, UrlViewer, SceneViewer, SceneCanvas, renderViewer, InitializeScene };