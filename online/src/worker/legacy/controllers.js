
import { initPromise } from './core.js'
import { com } from './core-java.js'

export const newDesign = async fieldName =>
{
  const { documentFactory } = await initPromise;
  const { orbitSource } = documentFactory( fieldName );
  
  const mainController = new com.vzome.desktop.controller.DefaultController();
  const strutBuilder = new com.vzome.desktop.controller.DefaultController();
  mainController .addSubController( 'strutBuilder', strutBuilder );
  const symmController = new com.vzome.desktop.controller.SymmetryController( strutBuilder, orbitSource, null );
  strutBuilder .addSubController( 'symmetry', symmController );

  return mainController;
}
