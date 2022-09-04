
import { initPromise } from './core.js'
import { com } from './core-java.js'

export const newDesign = async fieldName =>
{
  const { documentFactory } = await initPromise;
  const { orbitSource } = documentFactory( fieldName );
  const editableOrbits = new com.vzome.core.math.symmetry.OrbitSet( orbitSource .getSymmetry() );
  return new com.vzome.desktop.controller.OrbitSetController( editableOrbits, orbitSource .getOrbits(), orbitSource );
}
