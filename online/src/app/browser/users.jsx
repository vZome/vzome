
import { createSignal, For, Show } from "solid-js";

import { Select } from "@kobalte/core/select";

import Dialog from '@suid/material/Dialog';
import DialogTitle from '@suid/material/DialogTitle';
import DialogContent from '@suid/material/DialogContent';
import DialogActions from '@suid/material/DialogActions';
import List from '@suid/material/List';
import ListItem from '@suid/material/ListItem';
import ListItemText from '@suid/material/ListItemText';
import IconButton from '@suid/material/IconButton';
import TextField from '@suid/material/TextField';
import Button from '@suid/material/Button';
import CircularProgress from '@suid/material/CircularProgress';
import EditIcon from '@suid/icons-material/Edit';
import DeleteIcon from '@suid/icons-material/Delete';
import CheckIcon from '@suid/icons-material/Check';

import { verifyGithubUser } from './github.js';

const UsersEditDialog = (props) =>
{
  const [ newUser, setNewUser ] = createSignal( '' );
  const [ checking, setChecking ] = createSignal( false );
  const [ error, setError ] = createSignal( '' );

  const handleClose = () =>
  {
    setNewUser( '' );
    setError( '' );
    props.onClose();
  }

  const addNewUser = async () =>
  {
    const user = newUser() .trim() .toLowerCase();
    if ( ! user )
      return;
    if ( props.users .some( u => u .toLowerCase() === user ) ) {
      setError( 'Already in the list' );
      return;
    }
    setChecking( true );
    setError( '' );
    const { valid, rateLimited, resetAt } = await verifyGithubUser( user );
    setChecking( false );
    if ( rateLimited ) {
      setError( `GitHub API rate limit reached.${ resetAt ? ` Try again after ${ resetAt.toLocaleTimeString() }.` : '' }` );
      return;
    }
    if ( ! valid ) {
      setError( `No public vzome-sharing repository found for "${user}"` );
      return;
    }
    setNewUser( '' );
    props.onAdd( user );
  }

  return (
    <Dialog onClose={handleClose} aria-labelledby="users-edit-dialog-title" open={props.open}>
      <DialogTitle id="users-edit-dialog-title">
        Edit GitHub Users
      </DialogTitle>
      <DialogContent>
        <List dense>
          <For each={ props.users }>{ user =>
            <ListItem
              secondaryAction={
                <IconButton edge="end" aria-label={`remove ${user}`} onClick={ () => props.onRemove( user ) }>
                  <DeleteIcon />
                </IconButton>
              }
            >
              <ListItemText primary={ user } />
            </ListItem>
          }</For>
        </List>
        <div style={{ display: 'flex', gap: '0.5rem', 'align-items': 'flex-start', 'margin-top': '12px' }}>
          <TextField
            size="small"
            placeholder="GitHub username"
            value={ newUser() }
            onChange={ e => setNewUser( e.target.value ) }
            onKeyDown={ e => { if ( e.key === 'Enter' ) addNewUser(); } }
            error={ !! error() }
            helperText={ error() }
            style={{ flex: '1 1 auto' }}
          />
          <IconButton aria-label="verify and add user" onClick={ addNewUser } disabled={ checking() || ! newUser() .trim() }>
            <Show when={ ! checking() } fallback={ <CircularProgress size={20} /> }>
              <CheckIcon />
            </Show>
          </IconButton>
        </div>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Done</Button>
      </DialogActions>
    </Dialog>
  );
}

export const UsersMenu = (props) =>
{
  const [ editOpen, setEditOpen ] = createSignal( false );

  const changeUser = user => {
    // user should never be null, now that we lowercase the query parameter first, but still...
    if ( !! user )
      props.setUser( user );
  }
  return (
    <div style={ { background: 'lightgray', display: 'flex', 'align-items': 'center' } }>
      <Select
        value={props.currentUser}
        onChange={changeUser}
        options={props.users}
        placeholder="Select a GitHub user…"
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
        <Select.Portal>
          <Select.Content class="select__content">
            <Select.Listbox class="select__listbox" />
          </Select.Content>
        </Select.Portal>
      </Select>
      <IconButton aria-label="edit github users" size="small" onClick={ () => setEditOpen( true ) }>
        <EditIcon fontSize="small" />
      </IconButton>
      <UsersEditDialog
        open={ editOpen() }
        users={ props.users }
        onAdd={ props.onAddUser }
        onRemove={ props.onRemoveUser }
        onClose={ () => setEditOpen( false ) }
      />
    </div>
  );
}
