
import { createContext, createSignal, useContext } from "solid-js";


const GltfExportContext = createContext( { setExporter: ()=>{}, exporter: ()=>{} } );

export const GltfExportProvider = (props) =>
{
  const [ exporter, setExporter ] = createSignal( {} );

  return (
    <GltfExportContext.Provider value={ { exporter, setExporter } }>
      {props.children}
    </GltfExportContext.Provider>
  );
}

export const useGltfExporter = () => { return useContext( GltfExportContext ); };


const ImageCaptureContext = createContext( { setCapturer: ()=>{}, capturer: ()=>{} } );

export const ImageCaptureProvider = (props) =>
{
  const [ capturer, setCapturer ] = createSignal( {} );

  const captureImage = ( format, params ) => { // params are ignored!
    return new Promise( ( resolve, reject ) => {
      try {
        capturer() .capture( format, resolve )
      } catch (error) {
        reject( error );
      }
    } );
  }

  return (
    <ImageCaptureContext.Provider value={ { setCapturer, captureImage } }>
      {props.children}
    </ImageCaptureContext.Provider>
  );
}

export const useImageCapture = () => { return useContext( ImageCaptureContext ); };
