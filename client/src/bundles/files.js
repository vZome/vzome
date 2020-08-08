import { PROGRESS_STARTED, PROGRESS_STOPPED } from './progress'

export const FILE_FAILED = 'FILE_FAILED'
export const FILE_LOADED = 'FILE_LOADED'

export const fileSelected = selected => dispatch =>
{
  console.log( selected )

  dispatch( { type: PROGRESS_STARTED } )
  
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
    dispatch( { type: PROGRESS_STOPPED } )
    dispatch( { type: FILE_FAILED, payload: selected.name } )
  }
  reader.readAsText( selected )
}

export const fetchModel = path => dispatch =>
{
  dispatch( { type: PROGRESS_STARTED } )
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
      dispatch( { type: PROGRESS_STOPPED } )
      dispatch( { type: FILE_FAILED, payload: path } )
    });
}

 