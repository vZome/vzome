
import { initPromise } from './core.js'
import { com } from './core-java.js'

export const newDesign = async fieldName =>
{
  const { documentFactory } = await initPromise;
  const { orbitSource } = documentFactory( fieldName );
  
  const parent = new com.vzome.desktop.controller.DefaultController();
  const controller = new com.vzome.desktop.controller.SymmetryController( parent, orbitSource, null );

  return controller .getSubController( 'buildOrbits' );
}
