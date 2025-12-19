import { saveTextFile } from "../../viewer/util/files";

const fileTree = document .querySelector( 'file-tree' );
const viewer = document .querySelector( '#viewer' );
let vZomeFiles = [];
let directoryHandle = null;

fileTree.addEventListener('file-selected', async ({ detail }) =>
  {
    if ( detail.path .endsWith( '.vZome' ) )
    {
      const { file, path } = detail;
      const { name, contents } = file;
      console.log( 'loading', path );
      await viewer .loadFromText( name, contents );
    }
  }
);

async function collectVZomeFiles( dirHandle, basePath = '' ) {
  for await (const entry of dirHandle.values()) {
    const childPath = basePath ? `${basePath}/${entry.name}` : entry.name;
    
    if (entry.kind === 'file' && entry.name.endsWith('.vZome')) {
      console.log( childPath );
      const fileHandle = entry;
      const file = await fileHandle.getFile();
      
      vZomeFiles.push({
        name: entry.name,
        path: childPath,
        file: file,
        folder: dirHandle,
      });
    } else if (entry.kind === 'directory') {
      await collectVZomeFiles(entry, childPath);
    }
  }
}

function updateBatchExportButton() {
  const batchExportBtn = document.getElementById('batchExportBtn');
  batchExportBtn.disabled = vZomeFiles.length === 0;
  if (vZomeFiles.length > 0) {
    batchExportBtn.textContent = `Batch Export (${vZomeFiles.length} files)`;
  } else {
    batchExportBtn.textContent = 'Batch Export';
  }
}

document.getElementById('openFolderButton') .addEventListener('click', () => {
  fileTree .openDirectory() .then( async handle => {
    vZomeFiles = [];
    await collectVZomeFiles(handle);
    updateBatchExportButton();
    directoryHandle = handle;
  } ) .catch( ( error ) => {
    directoryHandle = null;
  } );
});

// Modal functionality
const modal = document.getElementById('batchExportModal');
const batchExportBtn = document.getElementById('batchExportBtn');
const cancelBtn = document.getElementById('cancelBtn');
const exportBtn = document.getElementById('exportBtn');
const progressContainer = document.getElementById('progressContainer');
const progressBarFill = document.getElementById('progressBarFill');
const progressText = document.getElementById('progressText');

batchExportBtn.addEventListener('click', () => {
  modal.classList.add('show');
});

cancelBtn.addEventListener('click', () => {
  modal.classList.remove('show');
  progressContainer.classList.remove('show');
});

// Close modal when clicking outside
modal.addEventListener('click', (e) => {
  if (e.target === modal) {
    modal.classList.remove('show');
    progressContainer.classList.remove('show');
  }
});

// Get format extension mapping
const formatExtensions = {
  'pov': 'pov',
  'shapes': 'shapes.json',
  'vrml': 'vrml',
  'stl': 'stl',
  'off': 'off',
  'ply': 'ply',
  'step': 'step',
  'mesh': 'mesh.json',
  'cmesh': 'cmesh.json',
  'dxf': 'dxf',
  'scad': 'scad',
  'build123d': 'py',
  'svg': 'svg',
  'pdf': 'pdf',
  'ps': 'ps',
};

// Export functionality
exportBtn.addEventListener('click', async () => {
  const checkboxes = document.querySelectorAll('.format-option input[type="checkbox"]:checked');
  const selectedFormats = Array.from(checkboxes).map(cb => cb.value);

  if (selectedFormats.length === 0) {
    alert('Please select at least one format to export.');
    return;
  }

  if (vZomeFiles.length === 0) {
    alert('No vZome files found in the selected directory.');
    return;
  }

  // Request write permission on the DIRECTORY again, just in case
  const permission = await directoryHandle.requestPermission({ mode: 'readwrite' });
  if (permission !== 'granted') {
    throw new Error('Write permission denied');
  }

  // Disable buttons during export
  exportBtn.disabled = true;
  cancelBtn.disabled = true;
  progressContainer.classList.add('show');

  const totalOperations = vZomeFiles.length * selectedFormats.length;
  let completedOperations = 0;

  try {
    for (const vZomeFile of vZomeFiles) {
      const baseName = vZomeFile.name.replace('.vZome', '');
      
      // Load the file into the viewer
      await loadFileInViewer(vZomeFile);
      
      // Wait a bit for the file to be fully loaded
      await sleep(500);

      for (const format of selectedFormats) {
        try {
          progressText.textContent = `Exporting ${baseName} as ${format}...`;
          
          await exportFormat( baseName, format, vZomeFile.folder );
          
          completedOperations++;
          const progress = (completedOperations / totalOperations) * 100;
          progressBarFill.style.width = `${progress}%`;
          
          // Small delay between exports
          await sleep(300);
        } catch (error) {
          console.error(`Failed to export ${baseName} as ${format}:`, error);
          // Continue with other exports even if one fails
        }
      }
    }

    progressText.textContent = `Completed: ${completedOperations} exports`;
    await sleep(1500);
    
    // Reset and close
    modal.classList.remove('show');
    progressContainer.classList.remove('show');
    progressBarFill.style.width = '0%';
  } catch (error) {
    console.error('Batch export error:', error);
    alert(`Batch export failed: ${error.message}`);
  } finally {
    exportBtn.disabled = false;
    cancelBtn.disabled = false;
  }
});

async function loadFileInViewer(vZomeFile)
{
  const { file } = vZomeFile;
  const contents = await file.text();
  return viewer.loadFromText(vZomeFile.name, contents);
}

async function exportFormat(baseName, format, dirHandle)
{
  const extension = formatExtensions[format];
  const fileName = `${baseName}.${extension}`;
  const fileHandle = await dirHandle .getFileHandle( fileName, { create: true } );
 
  return viewer.exportText( format ) .then( async ( exportedText ) => {
    return saveTextFile( fileHandle, exportedText, 'text/plain' ); // TODO: set correct MIME type based on format
  } );
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}
