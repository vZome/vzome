
import { documentFactory } from './core.js'
import { com } from './core-java.js'
import { RenderHistory } from './interpreter.js';

export const newDesign = async fieldName =>
{
  const { orbitSource, batchRender, toolsModel } = documentFactory( fieldName );
  
  const controller = new com.vzome.desktop.controller.DefaultController();
  const strutBuilder = new com.vzome.desktop.controller.DefaultController();
  controller .addSubController( 'strutBuilder', strutBuilder );
  const symmController = new com.vzome.desktop.controller.SymmetryController( strutBuilder, orbitSource, null );
  strutBuilder .addSubController( 'symmetry', symmController );
  const toolsController = new com.vzome.desktop.controller.ToolsController( toolsModel );
  strutBuilder .addSubController( 'tools', toolsController );

  const design = { batchRender, firstEdit: null };
  const renderHistory = new RenderHistory( design );

  return { controller, renderHistory, orbitSource };
}
