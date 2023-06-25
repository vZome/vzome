
import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { makeStyles } from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';

import { selectScene } from '../../workerClient/index.js';

const useStyles = makeStyles((theme) => ({
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
}));

export const SceneMenu = () =>
{
  const scenes = useSelector( state => state.scenes );
  const classes = useStyles();
  const report = useDispatch();
  const [sceneIndex, setSceneIndex] = useState( 0 );
  const handleChange = (event) =>
  {
    const index = event.target.value;
    setSceneIndex( index );
    report( selectScene( index ) );
  }

  return scenes && scenes[1] && (
    <div style={ { position: 'absolute', background: 'lightgray', top: '0px' } }>
      <FormControl variant="outlined" className={classes.formControl}>
        <InputLabel htmlFor="scene-menu-label">Scene</InputLabel>
        <Select
          native
          value={sceneIndex}
          onChange={handleChange}
          label="Scene"
          inputProps={{
            name: 'scene',
            id: 'scene-menu-label',
          }}
        >
          {scenes.map((scene, index) => (
            <option key={index} value={index}>{
              scene.title || (( index === 0 )? "- none -" : `scene ${index}`)
            }</option>)
          )}
        </Select>
      </FormControl>
    </div>
  );
}
