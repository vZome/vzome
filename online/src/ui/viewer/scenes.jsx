
import React from 'react';

import { makeStyles } from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';

import { selectSnapshot } from './store.js';

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
  const snapshots = useSelector( state => state.snapshots );
  const classes = useStyles();
  const report = useDispatch();
  const [snapshotIndex, setSnapshotIndex] = useState( 0 );
  const handleChange = (event) =>
  {
    const index = event.target.value;
    setSnapshotIndex( index );
    report( selectSnapshot( index ) );
  }

  return snapshots && snapshots[1] && (
    <div style={ { position: 'absolute', background: 'lightgray', top: '0px' } }>
      <FormControl variant="outlined" className={classes.formControl}>
        <InputLabel htmlFor="scene-menu-label">Scene</InputLabel>
        <Select
          native
          value={snapshotIndex}
          onChange={handleChange}
          label="Scene"
          inputProps={{
            name: 'scene',
            id: 'scene-menu-label',
          }}
        >
          {snapshots.map((snapshot, index) => (
            <option key={index} value={index}>{
              snapshot.title || (( index === 0 )? "- none -" : `scene ${index}`)
            }</option>)
          )}
        </Select>
      </FormControl>
    </div>
  );
}
