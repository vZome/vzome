import { startProgress, stopProgress } from './progress'

export const FILE_FAILED = 'FILE_FAILED'
export const FILE_LOADED = 'FILE_LOADED'

export const fileSelected = selected => dispatch =>
{
  console.log( selected )

  dispatch( startProgress() )
  
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
  dispatch( startProgress() )
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

 