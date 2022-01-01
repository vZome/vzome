
import { useMemo, useEffect, useRef, useLayoutEffect } from 'react';
import { BufferGeometry, Vector3, Float32BufferAttribute, Matrix4 } from 'three';

export const useEmbedding = embedding =>
{
  const ref = useRef()
  useEffect( () => {
    if ( embedding && ref.current && ref.current.matrix ) {
      const m = new Matrix4()
      m.set( ...embedding )
      m.transpose()
      ref.current.matrix.identity()  // Required, or applyMatrix4() changes will accumulate
      // This imperative approach is required because I was unable to decompose the
      //   embedding matrix (a shear) into a scale and rotation.
      ref.current.applyMatrix4( m )
    }
  }, [embedding] )
  return ref
}

export const useRotation = rotation =>
{
  const ref = useRef();
  useLayoutEffect( () => {
    const m = new Matrix4()
    m.set( ...rotation )
    ref.current.matrix = m;
  }, [rotation] );
  return ref;
}

export const useGeometry = shape =>
{
  const geometry = useMemo( () => {
    const { vertices, faces } = shape;
    const computeNormal = ( [ v0, v1, v2 ] ) => {
      const e1 = new Vector3().subVectors( v1, v0 )
      const e2 = new Vector3().subVectors( v2, v0 )
      return new Vector3().crossVectors( e1, e2 ).normalize()
    }
    let positions = [];
    let normals = [];
    faces.forEach( face => {
      const corners = face.vertices.map( i => vertices[ i ] )
      const { x:nx, y:ny, z:nz } = computeNormal( corners )
      corners.forEach( ( { x, y, z } ) => {
        positions.push( x, y, z )
        normals.push( nx, ny, nz )
      } )
    } );
    const geometry = new BufferGeometry();
    geometry.setAttribute( 'position', new Float32BufferAttribute( positions, 3 ) );
    geometry.setAttribute( 'normal', new Float32BufferAttribute( normals, 3 ) );
    geometry.computeBoundingSphere();
    return geometry;
  }, [ shape ] );
  return geometry;
}