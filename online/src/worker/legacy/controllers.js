
import { initPromise } from './core.js'
import { com } from './core-java.js'

export const newDesign = async fieldName =>
{
  const { documentFactory } = await initPromise;
  const { orbitSource } = documentFactory( fieldName );
  const symmetry = orbitSource .getSymmetry();
  const editableOrbits = new com.vzome.core.math.symmetry.OrbitSet( symmetry );

  for (let iter = symmetry.getOrbitSet().getDirections().iterator(); iter.hasNext();) {
    const orbit = iter.next();
    editableOrbits .add( orbit );
  }
  
  const controller = new com.vzome.desktop.controller.OrbitSetController( editableOrbits, orbitSource .getOrbits(), orbitSource );

  return controller;
}
