import shape from '@material-ui/core/styles/shape';
import { useMemo, useState, useEffect, useRef } from 'react'
import { Matrix4 } from 'three'

console.log( `import.meta.url = ${import.meta.url}` );

export const fetchUrlText = async ( url ) =>
{
  let response
  try {
    response = await fetch( url )
  } catch ( error ) {
    console.log( `Fetching ${url} failed with "${error}"; trying cors-anywhere` )
    // TODO: I should really deploy my own copy of this proxy on Heroku
    response = await fetch( 'https://cors-anywhere.herokuapp.com/' + url )
  }
  if ( !response.ok ) {
    throw new Error( `Failed to fetch "${url}": ${response.statusText}` )
  }
  return response.text()
}

export const useVZomeUrl = ( url, defaultScene, worker ) =>
{
  const [ scene, setScene ] = useState( defaultScene )
  const [ text, setText ] = useState( null )
  worker.onmessage = function(e) {
    console.log( `Message received from worker: ${e.data.type}` );
    switch ( e.data.type ) {

      case "text":
        setText( e.data.text )
        break;
    
      case "scene":
        setScene( { ...scene, ...e.data.scene } );
        break;
    
      case "shape": {
        const shape = e.data.shape;
        const shapes = { ...scene.shapes, [shape.id]: { ...shape, instances: [] } };
        setScene( { ...scene, shapes } );
        break;
      }

      case "instance": {
        const { shapeId } = e.data.instance;
        const shape = scene.shapes[ shapeId ]
        const updatedShape = { ...shape, instances: [ ...shape.instances, e.data.instance ] };
        const shapes = { ...scene.shapes, [shapeId]: updatedShape };  // make a copy
        setScene( { ...scene, shapes } );
        break;
      }
    
      default:
        console.log( `Unknown message type received from worker: ${JSON.stringify(e.data, null, 2)}` );
        break;
    }
  }
  useEffect( () => {
    if ( ! url.endsWith( ".vZome" ) ) {
      // This is the only case in which we don't resolve the promise with text,
      //  since there is no point in allowing download of non-vZome text.
      alert( `Unrecognized file name: ${url}` );
      return;
    }
    worker.postMessage( { type: "fetchShapesAndText", url } );
    console.log( 'Posted the text to the worker!' );
  }, [ url ] )
  // We have text if we could find the vZome file,
  //  and a scene, either because there was a 3D preview JSON next to it,
  //  or because we parsed, interpreted, and rendered the vZome file.
  return { text, scene }
}

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
