

export const fetchFileText = selected =>
{
  const temporaryFileReader = new FileReader()

  return new Promise( (resolve, reject) => {
    temporaryFileReader.onerror = () => {
      temporaryFileReader.abort()
      reject( temporaryFileReader.error )
    }

    temporaryFileReader.onload = () => {
      resolve( temporaryFileReader.result )
    }
    temporaryFileReader.readAsText( selected )
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

