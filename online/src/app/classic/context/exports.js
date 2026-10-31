
import { useEditor } from '../../framework/context/editor.jsx';
import { saveFileAs, saveTextFileAs, } from "../../../viewer/util/files.js";
import { useImageCapture, useGltfExporter } from "../../../viewer/context/export.jsx";
import { useViewer, EXPORT_FORMATS } from "../../../viewer/context/viewer.jsx";

export const IMAGE_FORMATS = {
  png:  { label: 'PNG',  mime: 'image/png' },
  jpg:  { label: 'JPEG', mime: 'image/jpeg' },
  webp: { label: 'WebP', mime: 'image/webp' },
  bmp:  { label: 'BMP',  mime: 'image/bmp' },
};

// Command action names, used by CommandsProvider, the File menu, and custom menu items
export const exportAction = format => `export.${format}`;
export const captureAction = ext => `capture.${ext}`;

// Must be called in a component (or provider) scope, below GltfExportProvider and ImageCaptureProvider
export const useFileExports = () =>
{
  const { state } = useEditor();
  const { setProblem, exportAs } = useViewer();
  const { exporter } = useGltfExporter();
  const { captureImage } = useImageCapture();

  const fileName = extension => (state.designName || 'untitled') .concat( "." + extension );

  const exportFile = ( format, params={} ) =>
  {
    if (format === 'gltf') {
      const { exportGltf } = exporter();
      exportGltf( glb => saveFileAs( fileName( 'glb' ), glb, "model/gltf+binary" ) );
    } else {
      const { ext=format, mime } = EXPORT_FORMATS[ format ];
      exportAs( format, params )
        .then( text => saveTextFileAs( fileName( ext ), text, mime ) );
    }
  }

  const captureImageFile = ext =>
  {
    const { mime } = IMAGE_FORMATS[ ext ];
    captureImage( mime ) .then( blob => {
      if ( blob.size === 0 ) {
        setProblem( 'Captured image is empty; please report this as a defect' );
        return;
      }
      saveFileAs( fileName( ext ), blob, mime );
    });
  }

  return { exportFile, captureImageFile };
}
