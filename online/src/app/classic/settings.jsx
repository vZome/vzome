
import { For } from 'solid-js'

import Typography from '@suid/material/Typography'
import Paper from '@suid/material/Paper'
import FormControl from '@suid/material/FormControl'
import FormControlLabel from '@suid/material/FormControlLabel'
import FormGroup from '@suid/material/FormGroup'
import FormLabel from '@suid/material/FormLabel'
import Checkbox from '@suid/material/Checkbox'
import Radio from '@suid/material/Radio'
import RadioGroup from '@suid/material/RadioGroup'

import { BUILD_CLICKS, BUILD_DRAGS, buildClick, buildDrag } from './tools/build.jsx'
import { PROJECTIONS, projection, showOutlines, snapCamera } from './context/rendering.jsx'

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

// On/off settings as a panel of checkboxes.
//   title     the panel's heading
//   items     [ { label, setting } ], each setting from createBooleanSetting (framework/settings.js)
const CheckboxPanel = props => (
  <Paper variant="outlined" sx={{ padding: '12px 16px' }}>
    <FormControl component="fieldset">
      <FormLabel component="legend">{props.title}</FormLabel>
      <FormGroup>
        <For each={props.items}>{ item =>
          <FormControlLabel label={item.label}
            control={ <Checkbox checked={ item.setting .value() } onChange={ ( e, checked ) => item.setting .set( checked ) } /> } />
        }</For>
      </FormGroup>
    </FormControl>
  </Paper>
);

// A category's heading and description, above its panels.
const CategoryHeader = props => (
  <div>
    <Typography variant="h6">{props.title}</Typography>
    <Typography variant="body2" color="text.secondary">{props.children}</Typography>
  </div>
);

const CategoryLayout = props => (
  <div style={{ display: 'flex', 'flex-direction': 'column', gap: '16px' }}>
    {props.children}
  </div>
);

const RenderingSettings = () => (
  <CategoryLayout>
    <CategoryHeader title="Rendering">
      How each new window starts.  The switches in the camera panel change only the current
      window, and an opened design shows its own saved projection.
      New designs start with the projection chosen here.
    </CategoryHeader>
    <ChoicePanel name="projection" title="Camera" values={PROJECTIONS} setting={projection} />
    <CheckboxPanel title="Options" items={ [
      { label: 'Snap camera', setting: snapCamera },
      { label: 'Show outlines', setting: showOutlines },
    ] } />
  </CategoryLayout>
);

const BuildToolSettings = () => (
  <CategoryLayout>
    <CategoryHeader title="Additional Mouse Behaviors">
      Build mode always builds a strut when you drag from a ball.
      These settings give its other gestures something to do.
      For the behavior of desktop vZome, choose "Select or deselect" and "Rotate the view".
    </CategoryHeader>
    <ChoicePanel name="buildClick" title="Click" values={BUILD_CLICKS} setting={buildClick} />
    <ChoicePanel name="buildDrag" title="Drag that doesn't start on a ball" values={BUILD_DRAGS} setting={buildDrag} />
  </CategoryLayout>
);

// The classic app's categories for the Settings dialog (framework/settingsdialog.jsx).
export const classicSettings = [
  { id: 'buildTool', label: 'Build Tool', Panel: BuildToolSettings },
  { id: 'rendering', label: 'Rendering', Panel: RenderingSettings },
];
