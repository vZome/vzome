import { startProgress, stopProgress } from './progress'
import { showAlert } from './alerts'

export const fileSelected = selected => ( dispatch, getState ) =>
{
  console.log( selected )

  dispatch( startProgress( "Reading file content..." ) )
  
  const reader = new FileReader();
  reader.onload = () =>
  {
    getState().java.parser( selected.name, reader.result, dispatch, getState )
  }
  reader.onerror = () =>
  {
    dispatch( stopProgress() )
    dispatch( showAlert( `Unable to read file: ${selected.name}` ) )
  }
  reader.readAsText( selected )
}

export const fetchModel = ( path ) => ( dispatch, getState ) =>
{
  // TODO: I should really deploy my own copy of this proxy on Heroku
  const fetchWithCORS = url => fetch ( url ).catch ( _ => fetch( 'https://cors-anywhere.herokuapp.com/' + url ) )

  dispatch( startProgress( "Fetching model content..." ) )
  fetchWithCORS( path )
    .then( response =>
    {
      if ( !response.ok ) {
        throw new Error( 'Network response was not ok' );
      }
      return response.text()
    })
    .then( (text) => {
      const name = path.split( '\\' ).pop().split( '/' ).pop()
      getState().java.parser( name, text, dispatch, getState )
    })
    .catch( error =>
    {
      console.error( 'There has been a problem with your fetch operation:', error );
      dispatch( stopProgress() )
      dispatch( showAlert( `Unable to load URL: ${path}` ) )
    });
}

// from https://www.bitdegree.org/learn/javascript-download
export const download = ( filename, bytes ) =>
{
  const blob = new Blob([bytes.subarray(1)]);
  const element = document.createElement( 'a' )
  const blobURI = URL.createObjectURL( blob )
  element.setAttribute( 'href', blobURI )
  element.setAttribute( 'download', filename )

  element.style.display = 'none'
  document.body.appendChild( element )

  element.click()

  document.body.removeChild( element )
}

