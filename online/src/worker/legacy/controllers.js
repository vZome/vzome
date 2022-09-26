
import { documentFactory } from './core.js'
import { com } from './core-java.js'
import { RenderHistory } from './interpreter.js';

export const newDesign = fieldName =>
{
  const { orbitSource, batchRender, toolsModel, bookmarkFactory } = documentFactory( fieldName );
  
  const controller = new com.vzome.desktop.controller.DefaultController(); // this is the equivalent of DocumentController

  const bookmarkController = new com.vzome.desktop.controller.ToolFactoryController( bookmarkFactory );
  controller .addSubController( 'bookmark', bookmarkController );

  const strutBuilder = new com.vzome.desktop.controller.DefaultController(); // this is the equivalent of StrutBuilderController
  controller .addSubController( 'strutBuilder', strutBuilder );

  const symmController = new com.vzome.desktop.controller.SymmetryController( strutBuilder, orbitSource, null );
  strutBuilder .addSubController( 'symmetry', symmController );

  const toolsController = new com.vzome.desktop.controller.ToolsController( toolsModel );
  toolsController .addTool( toolsModel .get( "bookmark.builtin/ball at origin" ) );
  strutBuilder .addSubController( 'tools', toolsController );

  const design = { batchRender, firstEdit: null };
  const renderHistory = new RenderHistory( design );

  return { controller, renderHistory, orbitSource };
}
