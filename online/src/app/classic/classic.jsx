
import React from 'react';

import { DesignViewer } from '../../ui/viewer/index.jsx'
import { subcontroller } from '../../ui/viewer/store.js';
import { useNewDesign } from './controller-hooks.js';
import { CameraControls } from './components/camera.jsx';
import { StrutBuildPanel } from './components/orbit-panel.jsx';
import { BookmarkBar, ToolBar, ToolFactoryBar } from './components/toolbars.jsx';

export const ClassicEditor = () =>
{
  useNewDesign();
  const symmController = subcontroller( 'strutBuilder', 'symmetry' );
  const toolsController = subcontroller( 'strutBuilder', 'tools' );
  // const makeHyperdo = useControllerAction( '', 'Polytope4d' );

  return (
    // <div id='classic' style={{ display: 'grid', gridTemplateRows: 'min-content 1fr' }}>
    //   <div id='menu-bar' className='placeholder' style={{ minHeight: '25px' }}>Menus</div>
      <div id='editor-main' className='grid-cols-1-min whitesmoke-bkgd' >

        <div id='editor-canvas' style={{ display: 'grid', gridTemplateRows: 'min-content min-content min-content 1fr' }}>
          <div id='article-and-status' style={{ display: 'grid', gridTemplateColumns: 'min-content 1fr' }}>
            <div id='model-article' className='placeholder' style={{ minWidth: '250px' }} >Model | Capture | Article</div>
            <div id='stats-bar' className='placeholder' style={{ minHeight: '30px' }} >Status</div>
          </div>
          <ToolFactoryBar controller={symmController} />
          <ToolBar symmetryController={symmController} toolsController={toolsController} />
          <div id='canvas-and-bookmarks' style={{ display: 'grid', gridTemplateColumns: 'min-content 1fr' }}>
            <BookmarkBar controller={'bookmark'} toolsController={toolsController} />
            <DesignViewer config={ { useSpinner: true } } />
          </div>
        </div>

        <div id='editor-drawer' className='grid-rows-min-1 editor-drawer'>
          <CameraControls/>
          <div id="build-parts-measure" style={{ height: '100%' }}>
            <StrutBuildPanel symmController={symmController} />
          </div>
        </div>

      </div>
    // </div>
  )
}
