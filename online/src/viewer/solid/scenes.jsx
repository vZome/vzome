
import { createMemo, createSignal } from 'solid-js';

import { Select } from "@kobalte/core";
import UnfoldMoreIcon from "@suid/icons-material/UnfoldMore";
import CheckIcon from '@suid/icons-material/Check';

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
              <CheckIcon />
            </Select.ItemIndicator>
          </Select.Item>
        )}
      >
        <Select.Trigger class="select__trigger" aria-label="Scene">
          <Select.Value class="select__value">
            {state => state.selectedOption()}
          </Select.Value>
          <Select.Icon class="select__icon">
            <UnfoldMoreIcon />
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
