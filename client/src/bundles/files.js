import { startProgress, stopProgress } from './progress'
import { FILE_EXPORTED } from './jre'

export const FILE_FAILED = 'FILE_FAILED'
export const FILE_LOADED = 'FILE_LOADED'

export const fileSelected = selected => dispatch =>
{
  console.log( selected )

  dispatch( startProgress( "Reading file content..." ) )
  
  const reader = new FileReader();
  reader.onload = () =>
  {
    dispatch( {
      type: FILE_LOADED,
      payload: { name: selected.name, text: reader.result }
    } )
  }
  reader.onerror = () =>
  {
    dispatch( stopProgress() )
    dispatch( { type: FILE_FAILED, payload: selected.name } )
  }
  reader.readAsText( selected )
}

export const fetchModel = path => dispatch =>
{
  dispatch( startProgress( "Fetching model content..." ) )
  fetch( path )
    .then( response =>
    {
      if ( !response.ok ) {
        throw new Error( 'Network response was not ok' );
      }
      return response.text()
    })
    .then( (text) => {
      dispatch( {
        type: FILE_LOADED,
        payload: {
          name: path.split( '\\' ).pop().split( '/' ).pop(),
          text
        }
      } )
    })
    .catch( error =>
    {
      console.error( 'There has been a problem with your fetch operation:', error );
      dispatch( stopProgress() )
      dispatch( { type: FILE_FAILED, payload: path } )
    });
}

// from https://www.bitdegree.org/learn/javascript-download
export const download = (filename, blob) =>
{
  const element = document.createElement( 'a' )
  const blobURI = URL.createObjectURL( blob )
  element.setAttribute( 'href', blobURI )
  element.setAttribute( 'download', filename )

  element.style.display = 'none'
  document.body.appendChild( element )

  element.click()

  document.body.removeChild( element )
}


export const middleware = store => next => async action => 
{
  if ( action.type === FILE_EXPORTED ) {
    const { name, bytes } = action.payload
    const blob = new Blob([bytes.subarray(1)]);
    download( name, blob )
  }
  
  return next( action )
}
