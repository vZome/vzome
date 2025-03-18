
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

  return (
    <ImageCaptureContext.Provider value={ { capturer, setCapturer } }>
      {props.children}
    </ImageCaptureContext.Provider>
  );
}

export const useImageCapture = () => { return useContext( ImageCaptureContext ); };
