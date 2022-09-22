
import React from 'react';

import { DesignViewer } from '../../ui/viewer/index.jsx'
import { subcontroller } from '../../ui/viewer/store.js';
import { ToolBar, ToolFactoryBar } from './components/toolbars.jsx';
import { useNewDesign } from './controller-hooks.js';
import { OrbitPanel } from './orbit-panel.jsx';

export const ClassicEditor = () =>
{
  useNewDesign();
  const symmController = subcontroller( 'strutBuilder', 'symmetry' );
  // const makeHyperdo = useControllerAction( '', 'Polytope4d' );

  return (
    // <div id='classic' style={{ display: 'grid', gridTemplateRows: 'min-content 1fr' }}>
    //   <div id='menu-bar' class='placeholder' style={{ minHeight: '25px' }}>Menus</div>
      <div id='editor-main' class='editor-main' style={{ backgroundColor: 'whitesmoke' }}>        

        <div id='editor-canvas' style={{ display: 'grid', gridTemplateRows: 'min-content min-content min-content 1fr' }}>
          <div id='article-and-status' style={{ display: 'grid', gridTemplateColumns: 'min-content 1fr' }}>
            <div id='model-article' class='placeholder' style={{ minWidth: '250px' }} >Model | Capture | Article</div>
            <div id='stats-bar' class='placeholder' style={{ minHeight: '30px' }} >Status</div>
          </div>
          <ToolFactoryBar controller={symmController} />
          <ToolBar symmetryController={symmController} toolsController={subcontroller( 'strutBuilder', 'tools' )} />
          <div id='canvas-and-bookmarks' style={{ display: 'grid', gridTemplateColumns: 'min-content 1fr' }}>
            <div id='bookmark-bar' class='placeholder' style={{ minWidth: '40px' }}>Bmks</div>
            <DesignViewer config={ { useSpinner: true } } />
          </div>
        </div>

        <div id='editor-drawer' class='editor-drawer'>
          <div id="camera-control" class='placeholder' style={{ minHeight: '250px' }}>Camera controls</div>
          <div id="build-parts-measure" style={{ height: '100%' }}>
            <div id="build" style={{ display: 'grid', gridTemplateRows: '1fr min-content', height: '100%' }}>
              <OrbitPanel symmController={symmController} orbitSet='buildOrbits' style={{ height: '100%' }} />
              <div id='length' class='placeholder' style={{ minHeight: '250px' }}>Length</div>
            </div>
          </div>
        </div>

      </div>
    // </div>
  )
}
