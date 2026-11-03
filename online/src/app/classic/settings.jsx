
import { For } from 'solid-js'

import Typography from '@suid/material/Typography'
import Paper from '@suid/material/Paper'
import FormControl from '@suid/material/FormControl'
import FormControlLabel from '@suid/material/FormControlLabel'
import FormLabel from '@suid/material/FormLabel'
import Radio from '@suid/material/Radio'
import RadioGroup from '@suid/material/RadioGroup'

import { BUILD_CLICKS, BUILD_DRAGS, buildClick, buildDrag } from './tools/build.jsx'

// One setting as a panel of radio buttons, one per value.
//   title     the panel's heading
//   values    { value: { label } }, in display order
//   setting   from createSetting (framework/settings.js)
const ChoicePanel = props =>
{
  const id = `setting-${props.name}`;
  return (
    <Paper variant="outlined" sx={{ padding: '12px 16px' }}>
      <FormControl>
        <FormLabel id={id}>{props.title}</FormLabel>
        <RadioGroup aria-labelledby={id} name={props.name} value={ props.setting .value() }
            onChange={ ( e, value ) => props.setting .set( value ) }>
          <For each={ Object .entries( props.values ) }>{ ( [ value, { label } ] ) =>
            <FormControlLabel value={value} control={<Radio/>} label={label} />
          }</For>
        </RadioGroup>
      </FormControl>
    </Paper>
  );
}

const BuildToolSettings = () => (
  <div style={{ display: 'flex', 'flex-direction': 'column', gap: '16px' }}>
    <div>
      <Typography variant="h6">Additional Mouse Behaviors</Typography>
      <Typography variant="body2" color="text.secondary">
        Build mode always builds a strut when you drag from a ball.
        These settings give its other gestures something to do.
        For the behavior of desktop vZome, choose "Select or deselect" and "Rotate the view".
      </Typography>
    </div>
    <ChoicePanel name="buildClick" title="Click" values={BUILD_CLICKS} setting={buildClick} />
    <ChoicePanel name="buildDrag" title="Drag that doesn't start on a ball" values={BUILD_DRAGS} setting={buildDrag} />
  </div>
);

// The classic app's categories for the Settings dialog (framework/settingsdialog.jsx).
export const classicSettings = [
  { id: 'buildTool', label: 'Build Tool', Panel: BuildToolSettings },
];
