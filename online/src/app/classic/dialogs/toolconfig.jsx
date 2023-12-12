import Popover from '@suid/material/Popover';
import DialogTitle from '@suid/material/DialogTitle';
import Button from '@suid/material/Button';
import TextField from '@suid/material/TextField';
import Typography from '@suid/material/Typography';
import IconButton from '@suid/material/IconButton';
import FormGroup from '@suid/material/FormGroup';
import FormControlLabel from '@suid/material/FormControlLabel';
import Checkbox from '@suid/material/Checkbox';
import CloseIcon from '@suid/icons-material/Close';

import { controllerProperty, useEditor } from '../../../viewer/context/editor.jsx';

const ConfigDialogTitle = (props) =>
{
  return (
    <DialogTitle sx={{ m: 0, p: 1, fontSize: 'medium', minWidth: '320px' }} >
      {props.children}
      <IconButton aria-label="close" onClick={props.onClose}
        sx={{ position: 'absolute', padding: 0, right: 6, top: 6, color: 'darkgray' }}
      >
        <CloseIcon fontSize='small' />
      </IconButton>
    </DialogTitle>
  );
}

export const ToolConfig = (props) =>
{
  const { controllerAction } = useEditor();
  const open = () => !!props.anchor;
  const id = () => (open() ? "tool-config-popper" : undefined);

  const selectInputs = () => controllerProperty( props.controller, 'selectInputs' ) === 'true'; // always defined, to control the Checkbox
  const deleteInputs = () => controllerProperty( props.controller, 'deleteInputs' ) === 'true'; // always defined, to control the Checkbox
  const selectOutputs = () => controllerProperty( props.controller, 'selectOutputs' ) === 'true'; // always defined, to control the Checkbox
  const createOutputs = () => controllerProperty( props.controller, 'createOutputs' ) === 'true'; // always defined, to control the Checkbox
  const copyColors = () => controllerProperty( props.controller, 'copyColors' ) === 'true'; // always defined, to control the Checkbox

  const handleToolClick = () =>
  {
    props.onClose();
    props.onClick();
  }
  const handleSelectParams = () =>
  {
    props.onClose();
    controllerAction( props.controller, 'selectParams' );
  }
  const handleRemove = () =>
  {
    props.onClose();
    controllerAction( props.controller, 'hideTool' );
  }
  const handleKeyDown = (e) =>
  {
    // Prevent the global handler from doing things like delete
    e .stopImmediatePropagation();
  }

  return (
    <Popover id={id} anchorEl={props.anchor} placement='top-start'
      open={open()} onClose={props.onClose}
    >
      <ConfigDialogTitle onClose={props.onClose}>{props.bookmark? 'Selection Bookmark' : 'Tool'}</ConfigDialogTitle>

      <div class='tool-config-icon-label' onKeydown={handleKeyDown} >
        <button aria-label={props.label} class='toolbar-button' onClick={handleToolClick} disabled={props.disabled}
            style={{ padding: '0.5em' }}>
          <img src={ `./resources/icons/tools/${props.image}.png`} class='toolbar-image'/>
        </button>
        { !!props.predefined?
          <Typography sx={{ margin: 'auto' }}>{props.label}</Typography>
        :
          <TextField id="tool-name" label="name" variant="outlined" fullWidth value={props.label} sx={{ margin: '5px' }}
            onChange={(event, value) => {
              controllerAction( props.controller, 'setProperty', { name: 'label', value } );
            }}
          />
        }
      </div>

      { ! props.bookmark && <>
        <div class='tool-config-checkboxes'>
          <span class='tool-config-checkbox-pair'>
            <div class='tool-config-param-label'>inputs</div>
            <FormGroup>
              <FormControlLabel control={
                <Checkbox checked={selectInputs()} disabled={props.predefined || deleteInputs()}
                  onChange={(event, checked) => controllerAction( props.controller, 'selectInputs' )}
                  inputProps={{ "aria-label": "controlled" }} />
                } label="select" />
              <FormControlLabel control={
                <Checkbox checked={deleteInputs()} disabled={props.predefined}
                  onChange={(event, checked) => controllerAction( props.controller, 'deleteInputs' )}
                  inputProps={{ "aria-label": "controlled" }} />
                } label="delete" />
            </FormGroup>
          </span>
          <span class='tool-config-checkbox-pair'>
            <div class='tool-config-param-label'>outputs</div>
            <FormGroup>
              <FormControlLabel control={
                <Checkbox checked={selectOutputs()} disabled={!createOutputs()}
                  onChange={(event, checked) => controllerAction( props.controller, 'selectOutputs' )}
                  inputProps={{ "aria-label": "controlled" }} />
                } label="select" />
              <FormControlLabel control={
                <Checkbox checked={createOutputs()}
                  onChange={(event, checked) => controllerAction( props.controller, 'createOutputs' )}
                  inputProps={{ "aria-label": "controlled" }} />
                } label="create" />
            </FormGroup>
          </span>
        </div>
        <div class='tool-config-copy-colors'>
          <FormControlLabel sx={{ margin: 'auto' }} control={
            <Checkbox checked={copyColors()}
              onChange={(event, checked) => controllerAction( props.controller, 'copyColors' )}
              inputProps={{ "aria-label": "controlled" }} />
            } label="copy colors" />
        </div>

        <div class='tool-config-button'>
          <Button size="small" variant="outlined" onClick={handleSelectParams} disabled={!!props.predefined} >
            Select Parameters
          </Button>
        </div>
      </>}

      <div class='tool-config-button'>
        <Button size="small" variant="outlined" onClick={handleRemove} disabled={!!props.predefined} >
          Remove {props.bookmark? 'Bookmark' : 'Tool'}
        </Button>
      </div>
    </Popover>
  );
}