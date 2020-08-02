
export const FILE_SELECTED = 'FILE_SELECTED'
export const FILE_LOADED = 'FILE_LOADED'

export const fileSelected = (selected) => dispatch =>
{
  console.log( selected )

  // This is not used currently
  dispatch( {
    type: FILE_SELECTED,
    payload: selected
  } )
  
  // read the file
  const reader = new FileReader();
  // file reading finished successfully
  reader.onload = () => dispatch( {
    type: FILE_LOADED,
    payload: { name: selected.name, text: reader.result }
  } )
  // file reading failed
  reader.onerror = () => alert('Error : Failed to read file')
  // read as text file
  reader.readAsText( selected )
}
