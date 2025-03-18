
export const openFile = async ( types ) => {
  // Feature detection. The API needs to be supported
  // and the app not run in an iframe.
  const supportsFileSystemAccess =
    "showOpenFilePicker" in window &&
    (() => {
      try {
        return window.self === window.top;
      } catch {
        return false;
      }
    })();
  // If the File System Access API is supported…
  if (supportsFileSystemAccess) {
    let file = undefined;
    try {
      // Show the file picker, optionally allowing multiple files.
      const handles = await showOpenFilePicker({ multiple: false, types });
      // Only one file is requested.
      // Add the `FileSystemFileHandle` as `.handle`.
      file = await handles[0].getFile();
      file.handle = handles[0];
    } catch (err) {
      // Fail silently if the user has simply canceled the dialog.
      if (err.name !== 'AbortError') {
        console.error(err.name, err.message);
      }
    }
    return file;
  }
  // Fallback if the File System Access API is not supported.
  return new Promise((resolve) => {
    // Append a new `<input type="file" />` and hide it.
    const input = document.createElement('input');
    input.style.display = 'none';
    input.type = 'file';
    document.body.append(input);
    // The `change` event fires when the user interacts with the dialog.
    input.addEventListener('change', () => {
      // Remove the `<input type="file" />` again from the DOM.
      input.remove();
      // If no files were selected, return.
      if (!input.files) {
        return;
      }
      // Return just one file.
      resolve( input.files[0] );
    });
    // Show the picker.
    if ('showPicker' in HTMLInputElement.prototype) {
      input.showPicker();
    } else {
      input.click();
    }
  });
};


export const saveTextFile = async ( handle, text, type ) =>
{
  const blob = new Blob( [ text ], { type } );
  return saveFile( handle, blob );
};


export const saveFile = async ( handle, blob ) =>
{
  // can't get here unless a handle was returned earlier, so no need for feature detection
  try {
    const writable = await handle.createWritable();
    await writable.write(blob);
    await writable.close();
  } catch (err) {
    // Fail silently if the user has simply canceled the dialog.
    if (err.name !== 'AbortError') {
      console.error(err.name, err.message);
    }
    return { success: false };
  }
  return { success: true };
}

export const saveTextFileAs = async ( suggestedName, text, type ) =>
{
  const blob = new Blob( [ text ], { type } );
  return saveFileAs( suggestedName, blob );
}

export const saveFileAs = async ( suggestedName, blob ) =>
{
  // Feature detection. The API needs to be supported
  // and the app not run in an iframe.
  const supportsFileSystemAccess =
    'showSaveFilePicker' in window &&
    (() => {
      try {
        return window.self === window.top;
      } catch {
        return false;
      }
    })();
  // If the File System Access API is supported…
  if (supportsFileSystemAccess) {
    try {
      // Show the file save dialog.
      const handle = await showSaveFilePicker( { suggestedName } );
      // Write the blob to the file.
      const writable = await handle.createWritable();
      await writable.write(blob);
      await writable.close();
      return { handle, success: true };
    } catch (err) {
      // Fail silently if the user has simply canceled the dialog.
      if (err.name !== 'AbortError') {
        console.error(err.name, err.message);
      }
      return { success: false };
    }
  }
  // Fallback if the File System Access API is not supported…
  // Create the blob URL.
  const blobURL = URL.createObjectURL(blob);
  // Create the `<a download>` element and append it invisibly.
  const a = document.createElement('a');
  a.href = blobURL;
  a.download = suggestedName;
  a.style.display = 'none';
  document.body.append(a);
  // Click the element.
  a.click();
  // Revoke the blob URL and remove the element.
  setTimeout(() => {
    URL.revokeObjectURL(blobURL);
    a.remove();
  }, 1000);
  return { success: false }; // we really can't tell, but we know we don't want to set the name in this case
};
