import { EXPORT_FORMATS } from "../../viewer/context/viewer";
import { saveFile, saveTextFile } from "../../viewer/util/files";

const regression = new URLSearchParams(window.location.search).has('regression');

// Generate labels for all checkboxes based on EXPORT_FORMATS
document.querySelectorAll('.format-option input[type="checkbox"]').forEach(checkbox => {
  const format = checkbox.id.replace('format-', '');
  const formatInfo = EXPORT_FORMATS[format];
  if (formatInfo) {
    const label = document.createElement('label');
    label.setAttribute('for', checkbox.id);
    label.textContent = formatInfo.label;
    checkbox.parentElement.appendChild(label);
    
    // Gray out label when checkbox is disabled
    if (checkbox.disabled) {
      label.style.color = '#999';
    }
  }
});

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
    if (regression) {
      batchExportBtn.textContent = `Run Regression (${vZomeFiles.length} files)`;
    } else {
      batchExportBtn.textContent = `Batch Export (${vZomeFiles.length} files)`;
    }
  } else {
    batchExportBtn.textContent = regression ? 'Run Regression' : 'Batch Export';
  }
}

document.getElementById('openFolderButton') .addEventListener('click', () => {
  fileTree .openDirectory() .then( async handle => {
    if ( !handle ) return;
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

batchExportBtn.addEventListener('click', async () => {
  if (regression) {
    await runRegression();
  } else {
    modal.classList.add('show');
  }
});

async function runRegression() {
  if (vZomeFiles.length === 0) {
    console.log('No vZome files found in the selected directory.');
    return;
  }

  console.log(`Starting regression test with ${vZomeFiles.length} files`);
  const successes = [];
  const failures = [];
  
  for (const vZomeFile of vZomeFiles) {
    try {
      console.log(`Loading ${vZomeFile.path}...`);
      await loadFileInViewer(vZomeFile);
      await sleep(100); // Brief pause for loading
      successes.push(vZomeFile.path);
    } catch (error) {
      failures.push(vZomeFile.path);
      console.error(`✗ ${vZomeFile.path}: ${error.message}`, error);
    }
  }

  console.log(`Regression complete: ${successes.length} succeeded`);
  console.dir({ successes, failures });
  
  // Show results dialog
  showRegressionResults(successes, failures);
}

function showRegressionResults(successes, failures) {
  const resultsModal = document.getElementById('regressionResultsModal');
  const resultsDiv = document.getElementById('regressionResults');
  const closeBtn = document.getElementById('resultsCloseBtn');
  
  let html = `<div class="success">✓ ${successes.length} succeeded</div>`;
  
  if (failures.length > 0) {
    html += `<div class="failure">✗ ${failures.length} failed:\n\n`;
    html += failures.map(path => `  ${path}`).join('\n');
    html += '</div>';
  }
  
  resultsDiv.innerHTML = html;
  resultsModal.classList.add('show');
  
  closeBtn.onclick = () => {
    resultsModal.classList.remove('show');
  };
  
  resultsModal.onclick = (e) => {
    if (e.target === resultsModal) {
      resultsModal.classList.remove('show');
    }
  };
}

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
          progressText.textContent = `Exporting ${baseName} as ${EXPORT_FORMATS[format].label}...`;
          
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
  const { ext, mime, image } = EXPORT_FORMATS[format];
  // Some formats don't specify an extension
  const fileName = `${baseName}.${ext ? ext : format}`; 
  const fileHandle = await dirHandle .getFileHandle( fileName, { create: true } );
 
  if ( image ) {
    return viewer.captureImage( format ) .then( async ( blob ) => {
      return saveFile( fileHandle, blob, mime );
    } );
  } else {
    return viewer.exportText( format ) .then( async ( exportedText ) => {
      return saveTextFile( fileHandle, exportedText, mime );
    } );
  }
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}
