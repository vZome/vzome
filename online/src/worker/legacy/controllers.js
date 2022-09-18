
import { documentFactory } from './core.js'
import { com } from './core-java.js'
import { RenderHistory } from './interpreter.js';

export const newDesign = async fieldName =>
{
  const { orbitSource, batchRender } = documentFactory( fieldName );
  
  const controller = new com.vzome.desktop.controller.DefaultController();
  const strutBuilder = new com.vzome.desktop.controller.DefaultController();
  controller .addSubController( 'strutBuilder', strutBuilder );
  const symmController = new com.vzome.desktop.controller.SymmetryController( strutBuilder, orbitSource, null );
  strutBuilder .addSubController( 'symmetry', symmController );

  const design = { batchRender, firstEdit: null };
  const renderHistory = new RenderHistory( design );

  return { controller, renderHistory, orbitSource };
}
