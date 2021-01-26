

export const fetchFileText = selected =>
{
  const temporaryFileReader = new FileReader()

  return new Promise( (resolve, reject) => {
    temporaryFileReader.onerror = () => {
      temporaryFileReader.abort()
      reject(new DOMException("Problem parsing input file."))
    }

    temporaryFileReader.onload = () => {
      resolve( temporaryFileReader.result )
    }
    temporaryFileReader.readAsText( selected )
  })
}


export const fetchUrlText = ( path ) =>
{
  // TODO: I should really deploy my own copy of this proxy on Heroku
  const fetchWithCORS = url => fetch ( url ).catch ( _ => fetch( 'https://cors-anywhere.herokuapp.com/' + url ) )

  return fetchWithCORS( path )
  .then( response =>
  {
    if ( !response.ok ) {
      throw new Error( 'Network response was not ok' );
    }
    return response.text()
  })
  .catch( error =>
  {
    console.error( 'There has been a problem with your fetch operation:', error );
    return null
  })
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

