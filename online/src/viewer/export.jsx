
import { DropdownMenu, Link } from "@kobalte/core";

import { useViewer } from "./context/viewer.jsx";
import { saveFileAs } from "../viewer/util/files.js";
import { useGltfExporter } from "./geometry.jsx";

const editUrlBase = 'https://vzome.com/app/classic/index.html?design=';

export const ExportMenu = (props) =>
{
  const { source } = useViewer();
  const { exporter } = useGltfExporter();

  const downloadGltf = () =>
  {
    const { name } = source;
    const vName = name || 'untitled.vZome';
    const fileName = vName .substring( 0, vName.length-6 ) .concat( ".gltf" );
    const { exportGltf } = exporter();
    exportGltf( gltf => saveFileAs( fileName, JSON.stringify( gltf, null, 2 ), 'model/gltf+json' ) );
  }

  const downloadVZome = () =>
  {
    const { name, text, changedText } = source;
    const fileName = name || 'untitled.vZome';
    // if ( changedText ) {
    //   const { camera, liveCamera, lighting } = scene;
    //   const fullText = serializeVZomeXml( changedText, lighting, liveCamera, camera );
    //   saveFileAs( fileName, fullText, 'application/xml' );
    // }
    // else
      saveFileAs( fileName, text, 'application/xml' );
  }

  return (
    <Show when={ source?.text || source?.changedText }>
    <DropdownMenu.Root modal={true}>
      <DropdownMenu.Trigger class="exports__trigger corner__icon__button">
        <svg focusable="false" viewBox="0 0 24 24" aria-hidden="true">
          <path d="M16.59 9H15V4c0-.55-.45-1-1-1h-4c-.55 0-1 .45-1 1v5H7.41c-.89 0-1.34 1.08-.71 1.71l4.59 4.59c.39.39 1.02.39 1.41 0l4.59-4.59c.63-.63.19-1.71-.7-1.71zM5 19c0 .55.45 1 1 1h12c.55 0 1-.45 1-1s-.45-1-1-1H6c-.55 0-1 .45-1 1z"></path>
        </svg>
      </DropdownMenu.Trigger>
      <DropdownMenu.Portal mount={props.root}>
        <DropdownMenu.Content class="exports__content">
          <DropdownMenu.Item closeOnSelect={true} class="exports__item">
            <Link.Root class="link" href={ editUrlBase + source.url } target="_blank" rel="noopener">
              Open in vZome Online
            </Link.Root>
          </DropdownMenu.Item>
          <DropdownMenu.Item onSelect={downloadVZome} closeOnSelect={true} class="exports__item">
            .vZome source
          </DropdownMenu.Item>
          <DropdownMenu.Item closeOnSelect={true} class="exports__item" onSelect={downloadGltf} >
            glTF scene
          </DropdownMenu.Item>
          {/* <Show when={canDownloadScene}>
            <DropdownMenu.Item class="exports__item">
              Scene JSON
            </DropdownMenu.Item>
          </Show> */}
          <DropdownMenu.Arrow />
        </DropdownMenu.Content>
      </DropdownMenu.Portal>
    </DropdownMenu.Root>
    </Show>
  );
}