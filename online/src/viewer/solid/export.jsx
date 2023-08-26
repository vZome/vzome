
import { DropdownMenu } from "@kobalte/core";

import { download, serializeVZomeXml, useWorkerClient } from "../../workerClient";

export const ExportMenu = (props) =>
{
  const { state } = useWorkerClient();
  const downloadVZome = () =>
  {
    const { name, text, changedText } = state.source;
    const fileName = name || 'untitled.vZome';
    if ( changedText ) {
      const { camera, liveCamera, lighting } = scene;
      const fullText = serializeVZomeXml( changedText, lighting, liveCamera, camera );
      download( fileName, fullText, 'application/xml' );
    }
    else
      download( fileName, text, 'application/xml' );
  }

  return (
    <DropdownMenu.Root modal={false}>
      <DropdownMenu.Trigger class="exports__trigger corner__icon__button">
        <svg focusable="false" viewBox="0 0 24 24" aria-hidden="true">
          <path d="M16.59 9H15V4c0-.55-.45-1-1-1h-4c-.55 0-1 .45-1 1v5H7.41c-.89 0-1.34 1.08-.71 1.71l4.59 4.59c.39.39 1.02.39 1.41 0l4.59-4.59c.63-.63.19-1.71-.7-1.71zM5 19c0 .55.45 1 1 1h12c.55 0 1-.45 1-1s-.45-1-1-1H6c-.55 0-1 .45-1 1z"></path>
        </svg>
      </DropdownMenu.Trigger>
      <DropdownMenu.Portal mount={props.root}>
        <DropdownMenu.Content class="exports__content">
          <DropdownMenu.Item onSelect={downloadVZome} closeOnSelect={true} class="exports__item">
            .vZome source
          </DropdownMenu.Item>
          <DropdownMenu.Item closeOnSelect={true} class="exports__item" disabled>
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
  );
}