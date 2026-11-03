
import { createSignal, For, Show } from 'solid-js'
import { Dynamic } from 'solid-js/web'

import Dialog from '@suid/material/Dialog'
import DialogActions from '@suid/material/DialogActions'
import DialogTitle from '@suid/material/DialogTitle'
import DialogContent from '@suid/material/DialogContent'
import IconButton from '@suid/material/IconButton'
import Button from '@suid/material/Button'
import List from '@suid/material/List'
import ListItemButton from '@suid/material/ListItemButton'
import ListItemText from '@suid/material/ListItemText'
import SettingsRoundedIcon from '@suid/icons-material/SettingsRounded'

import { Tooltip } from './tooltip.jsx'
import { resumeMenuKeyEvents, suspendMenuKeyEvents } from './keyevents.js'

/*
  A gear button that opens the app's Settings dialog.  Each app contributes its own settings
  as categories, so the dialog itself knows nothing about any of them:

    categories   [ { id, label, Panel } ], in list order.  Panel is a component that shows
                 and changes the category's settings; settings take effect as they change
                 (see settings.js), so the dialog has no Apply or Cancel.

  The left column lists the categories, and the right column shows the selected one's Panel.
*/
export const SettingsDialog = props =>
{
  const [ open, setOpen ] = createSignal( false );
  const [ selectedId, setSelectedId ] = createSignal( null );
  const selected = () => props.categories .find( c => c.id === selectedId() ) || props.categories[ 0 ];

  // The editor's single-key shortcuts must not fire behind the dialog.
  const handleOpen = () => {
    suspendMenuKeyEvents();
    setOpen( true );
  };
  const handleClose = () => {
    setOpen( false );
    resumeMenuKeyEvents();
  };

  return (
    <>
      <Tooltip title="Settings" aria-label="settings">
        <IconButton color="inherit" aria-label="settings" onClick={handleOpen}>
          <SettingsRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
      <Dialog onClose={handleClose} aria-labelledby="settings-dialog-title" open={open()} maxWidth="md" fullWidth>
        <DialogTitle id="settings-dialog-title">Settings</DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', gap: '24px', padding: 0, minHeight: '360px' }}>
          <List sx={{ width: '200px', flexShrink: 0, borderRight: '1px solid rgba(0,0,0,0.12)' }} aria-label="settings categories">
            <For each={props.categories}>{ category =>
              <ListItemButton selected={ selected() ?.id === category.id } onClick={ () => setSelectedId( category.id ) }>
                <ListItemText primary={category.label} />
              </ListItemButton>
            }</For>
          </List>
          <div style={{ flex: '1 1 auto', padding: '16px 24px 16px 0', 'min-width': 0 }}>
            <Show when={selected()}>
              <Dynamic component={ selected() .Panel } />
            </Show>
          </div>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Close</Button>
        </DialogActions>
      </Dialog>
    </>
  )
}
