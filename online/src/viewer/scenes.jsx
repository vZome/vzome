
import { useSceneTitles } from './context/scene.jsx';

export const SceneMenu = (props) =>
{
  const { showTitledScene, sceneTitle, setSceneTitle, sceneTitles, } = useSceneTitles();

  const handleChange = (e) =>
  {
    const title = e.target.value;
    setSceneTitle( title );
    showTitledScene( title );
  }

  return (
    <Show when={ sceneTitles() .length > 1 }>
    <div style={ { position: 'absolute', top: '1em', left: '1em' } }>
      <select class="scene__select" aria-label="Scene" onChange={handleChange}>
        <For each={sceneTitles()}>{ title =>
          <option value={title} selected={title === sceneTitle()}>{title}</option>
        }</For>
      </select>
    </div>
    </Show>
  );
}
