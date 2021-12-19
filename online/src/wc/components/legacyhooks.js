import { useMemo, useState } from 'react'
import * as legacy from '../legacy/api'

// TODO: can we refactor this away?  Only used on online designs.
export const fetchUrlDesign = async ( textPromise, url ) =>
{
  const text = await textPromise
  if ( ! url.endsWith( ".vZome" ) ) {
    // This is the only case in which we don't resolve the promise with text,
    //  since there is no point in allowing download of non-vZome text.
    throw new Error( `Unrecognized file name: ${url}` )
  }
  try {
    const scene = {}; //await fetchPreview( url )
    return { text, scene }
  } catch (error) {
    console.log( `Preview load for "${url}" failed due to error: ${error}` )
    try {
      const parsed = await legacy.parse( text ) // TODO run this in a worker!
      if ( ! parsed.firstEdit ) {
        return { text, error: `Unable to parse XML from ${url}` }
      }
      if ( parsed.field.unknown ) {
        return { text, error: `Field ${parsed.field.name} is not implemented.` }
      }
      return { text, parsed }
    } catch (error) {
      return { text, error: error.message }
    }
  }
}

export const useInstanceShaper = ( shown, selected, shaper ) =>
{
  const [ shapes ] = useState( {} )
  const [ shapedInstances ] = useState( {} )
  const cachingShaper = shaper && shaper( shapes )
  const shapeInstances = () => legacy.shapeMesh( shapes, shapedInstances, shown, selected, cachingShaper );
  return useMemo( shapeInstances, [ shown, selected, shaper ] )
}
