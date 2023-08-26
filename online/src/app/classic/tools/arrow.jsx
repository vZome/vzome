
import { mergeProps } from 'solid-js';
import { LineBasicMaterial, Vector3, BufferGeometry, SphereGeometry, MeshLambertMaterial } from 'three';

const defaultProps = {
  vector: [ 0, 0, 0 ],
  color: 0x202020,
  scale: 13,
  width: 16
};

const VectorArrow = props =>
{
  const merged = mergeProps( defaultProps, props );

  const lineMaterial = new LineBasicMaterial( {
    color: merged.color,
    linewidth: merged.width
  } );
  const meshMaterial = new MeshLambertMaterial( { color: merged.color } );
  const sphereGeom = new SphereGeometry( 0.2, 9, 9 );

  const tip = () => {
    const [ x, y, z ] = merged.vector;
    return new Vector3( x, y, z ) .multiplyScalar( merged.scale );
  }

  const lineGeom = () => {
    const points = [];
    points.push( new Vector3( 0, 0, 0 ) );
    points.push( tip() );
    return new BufferGeometry().setFromPoints( points );
  }

  return (
    <group>
      <lineSegments material={lineMaterial} geometry={lineGeom()} />
      <mesh position={tip()} material={meshMaterial} geometry={sphereGeom} />
    </group>
  );
}

export { VectorArrow };