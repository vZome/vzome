
/*
  A single flag: whether the app's global menu keyboard shortcuts are currently suspended.

  The classic app binds command keystrokes -- including bare Delete and Backspace -- on
  document.body, and preventDefault()s them without regard to what has focus (see
  registerKeyListener in classic/context/commands.jsx).  Any UI that takes text input has
  to suspend those listeners while it is open, or typing a Backspace in a text field
  deletes the user's selected balls and struts instead of a character.

  This lives in framework/ rather than beside the listeners in classic/ because framework
  components need it too -- the color picker has a text field -- and framework must not
  depend on classic.  It is deliberately just module state with no reactivity: the flag is
  read inside a DOM event handler, never rendered, so making it a signal would buy nothing.
*/

let suspended = false;

export const suspendMenuKeyEvents = () => suspended = true;

export const resumeMenuKeyEvents = () => suspended = false;

export const menuKeyEventsSuspended = () => suspended;
