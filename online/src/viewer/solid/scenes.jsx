
import { createMemo, createSignal } from 'solid-js';

import { Select } from "@kobalte/core";

import { selectScene, useWorkerClient } from '../../workerClient/index.js';

const styles = {
  margin: '1em',
  minWidth: 120,
};

export const SceneMenu = (props) =>
{
  const { state, postMessage } = useWorkerClient();
  const sceneTitles = createMemo( () => state.scenes .map( (scene,index) =>
    scene.title || (( index === 0 )? "default scene" : `scene ${index}`) ) );
  const [ sceneTitle, setSceneTitle ] = createSignal( sceneTitles()[0] );

  const handleChange = (sceneTitle) =>
  {
    setSceneTitle( sceneTitle );
    postMessage( selectScene( sceneTitles() .indexOf( sceneTitle ) ) );
  }

  return (
    <div style={ { position: 'absolute', background: 'lightgray', top: '1em', left: '1em' } }>
      <Select.Root
        value={sceneTitle()}
        onChange={handleChange}
        options={sceneTitles()}
        placeholder="Select a scene…"
        itemComponent={props => (
          <Select.Item item={props.item} class="select__item">
            <Select.ItemLabel>{props.item.rawValue}</Select.ItemLabel>
            <Select.ItemIndicator class="select__item-indicator">
              <svg aria-hidden="true" viewBox="0 0 24 24" focusable="false" data-testid="CheckIcon">
                <path d="M0 0h24v24H0z" fill="none"></path>
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"></path>
              </svg>
            </Select.ItemIndicator>
          </Select.Item>
        )}
      >
        <Select.Trigger class="select__trigger" aria-label="Scene">
          <Select.Value class="select__value">
            {state => state.selectedOption()}
          </Select.Value>
          <Select.Icon class="select__icon">
            <svg aria-hidden="true" viewBox="0 0 24 24" focusable="false" data-testid="UnfoldMoreIcon">
              <path d="M0 0h24v24H0z" fill="none"></path>
              <path d="M12 5.83L15.17 9l1.41-1.41L12 3 7.41 7.59 8.83 9 12 5.83zm0 12.34L8.83 15l-1.41 1.41L12 21l4.59-4.59L15.17 15 12 18.17z"></path>
            </svg>
          </Select.Icon>
        </Select.Trigger>
        <Select.Portal mount={props.root}>
          <Select.Content class="select__content">
            <Select.Listbox class="select__listbox" />
          </Select.Content>
        </Select.Portal>
      </Select.Root>
    </div>
  );
}
