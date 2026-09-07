
import { createSignal, createEffect, onCleanup, For, Show } from 'solid-js';
import { createStore, produce, unwrap } from 'solid-js/store';

import { Dialog } from '@kobalte/core/dialog';
import { Tabs } from '@kobalte/core/tabs';
import { ColorArea } from '@kobalte/core/color-area';
import { ColorSlider } from '@kobalte/core/color-slider';
import { ColorSwatch } from '@kobalte/core/color-swatch';
import { parseColor } from '@kobalte/core/colors';

import { readSavedColors, writeSavedColors } from './savedcolors.js';
import { resumeMenuKeyEvents, suspendMenuKeyEvents } from './keyevents.js';

const DEFAULT_COLOR = '#ffffff';

// Kobalte's color components all operate on a Color object in a particular color space.
// We work in HSB throughout: the hue slider drives the "hue" channel, and the area covers
// the remaining two channels ("saturation" x "brightness"), which is the familiar
// saturation/value square found in most color pickers.
const toHsb = hex => parseColor( hex ) .toFormat( 'hsb' );

// Palette entries arrive from the worker as "Name #rrggbb", the one string the controller
// list protocol can carry.  Anything that does not match that shape is skipped rather than
// thrown, so a malformed entry cannot take down the whole dialog.
const parseSwatch = entry =>
{
  const match = /^(.*)\s+(#[0-9a-fA-F]{6})$/ .exec( entry );
  return match && { name: match[1], hex: match[2] };
}

// A trash can, inline so the dialog needs no icon dependency.  currentColor lets the
// button's hover state recolor it.
const TrashIcon = () => (
  <svg class="colorpicker__trash" viewBox="0 0 24 24" aria-hidden="true" focusable="false">
    <path fill="none" stroke="currentColor" stroke-width="2"
        stroke-linecap="round" stroke-linejoin="round"
        d="M4 7h16M10 4h4M9 7v12M15 7v12M6 7l1 13h10l1-13" />
  </svg>
);

/*
  The user's saved colors, held at MODULE level so that every ColorPicker instance shares
  one list.  The app mounts several (the toolbar button, the Edit menu item, the background
  picker); a per-instance store meant each read localStorage once at construction and then
  never saw the others' writes, so the lists silently diverged until a reload.

  This is a STORE, not a signal, and that matters too: <For> keys rows by reference
  identity, so replacing an entry object on every keystroke would tear down and rebuild
  that row's <input>, losing focus and caret after one character.  A store lets us write
  saved[i].name in place, leaving the row's DOM untouched.
*/
const [ saved, setSaved ] = createStore( readSavedColors() );

// Persist whatever the store now holds.  unwrap() strips the proxies so JSON.stringify
// sees plain objects.
const persist = () => writeSavedColors( unwrap( saved ) );

const appendSaved = hex =>
{
  setSaved( saved.length, { hex: hex .toLowerCase(), name: '' } );
  persist();
}

// Index rather than hex identifies the row: duplicate colors are allowed, and two rows
// with the same hex must remain independently nameable and removable.
const renameSaved = ( index, name ) =>
{
  setSaved( index, 'name', name ); // targeted write; does not recreate the row
  persist();
}

const removeSaved = index =>
{
  setSaved( produce( list => { list .splice( index, 1 ); } ) );
  persist();
}

/*
  A modal color picker built from Kobalte primitives.

  props:
    show      — whether the dialog is open
    color     — the initial color, as a CSS hex string like '#33ff99'
    title     — optional dialog title
    close     — called with the chosen hex string on OK, or with undefined on Cancel
    palette   — optional array of "Name #rrggbb" strings.  When present, a "Zometool" tab
                  offers them as fixed swatches.  The caller supplies these (rather than
                  this component reading a controller) so that the picker stays usable
                  outside the editor, where there is no worker.
    onPreview — optional; called with the hex string on every change, for live preview.
                  A caller using this must restore its own prior color when close()
                  reports a cancel, since the preview has already been applied.

  The picker keeps its own working color while open, so dragging never disturbs the
  caller's state; only OK commits.  Cancel discards, which is the whole point of having
  a working copy.
*/
export const ColorPicker = ( props ) =>
{
  const [ value, setValue ] = createSignal( toHsb( DEFAULT_COLOR ) );
  const [ tab, setTab ] = createSignal( 'General' );

  const swatches = () => ( props.palette || [] ) .map( parseSwatch ) .filter( s => !!s );

  const saveCurrentColor = () =>
  {
    appendSaved( value() .toString( 'hex' ) );
    setTab( 'Saved' ); // show the user where the color went, ready to be named
  }

  // Re-seed the working color each time the dialog opens, so a reopened picker starts
  // from the caller's current color rather than wherever the last edit left off.
  // The tab resets too, so the dialog always opens in the same known state.
  //
  // Opening also suspends the app's global menu-key listeners.  Those bind Delete and
  // Backspace on document.body and preventDefault() them regardless of focus (see
  // registerKeyListener in classic/context/commands.jsx), which would otherwise delete the
  // SELECTED MODEL OBJECTS instead of editing the saved-color name field.  Every other
  // dialog with a text field does the same -- see LabelDialog's caller in
  // classic/components/editor.jsx, and sharing.jsx.  The flag lives in framework/ so this
  // component need not reach into classic/.
  createEffect( () => {
    if ( props.show ) {
      setValue( toHsb( props.color || DEFAULT_COLOR ) );
      setTab( 'General' );
      suspendMenuKeyEvents();
    }
    else
      resumeMenuKeyEvents();
  });

  // Also resume if the dialog is torn down while open, so the suspension cannot leak.
  onCleanup( resumeMenuKeyEvents );

  // Both the area and the hue slider report through here, so live preview covers both.
  const changeValue = color =>
  {
    setValue( color );
    props.onPreview ?.( color .toString( 'hex' ) );
  }

  const chooseSwatch = hex => changeValue( toHsb( hex ) );

  // Compared case-insensitively: our own toString() yields uppercase, while the palette
  // hexes come from the worker in lowercase.
  const isSelected = hex => hex .toLowerCase() === value() .toString( 'hex' ) .toLowerCase();

  const handleCancel = () => props.close( undefined );

  const handleOk = () => props.close( value() .toString( 'hex' ) );

  // Kobalte reports open/close from its own affordances (Escape, overlay click); either
  // way a close that did not come through OK is a cancel.
  const handleOpenChange = isOpen => { if ( !isOpen ) handleCancel(); }

  // OK is styled as the default button, so Return should activate it, from anywhere in the
  //  dialog -- including the saved-color name field, which is how a name field behaves in a
  //  normal form.  Kobalte already handles Escape as a dismissal.
  //  Buttons that carry their own Enter meaning are the exception: a focused <button> fires
  //  its own click on Enter, so letting this through as well would both save AND commit.
  //  The exclusion has to be narrow, NOT "any BUTTON": Kobalte's focus scope auto-focuses
  //  the first focusable element when the dialog opens, and that is a tab trigger button,
  //  so excluding every button would disable Enter in the dialog's default state.
  //  .colorpicker__button covers OK, Cancel and Save (which carries both classes).
  const ownsEnter = target =>
    !! target ?.closest?.( '.colorpicker__button, .colorpicker__saved-delete, .colorpicker__saved-chip, .colorpicker__swatch' );

  const handleKeyDown = e =>
  {
    if ( e.key === 'Enter' && ! e.isComposing && ! ownsEnter( e.target ) ) {
      e .preventDefault();
      handleOk();
    }
  }

  const generalPanel = () => (
    <>
      <ColorArea class="ColorAreaRoot"
          value={value()} onChange={changeValue}
          colorSpace="hsb" xChannel="saturation" yChannel="brightness">
        <ColorArea.Background class="ColorAreaBackground">
          <ColorArea.Thumb class="ColorAreaThumb">
            <ColorArea.HiddenInputX />
            <ColorArea.HiddenInputY />
          </ColorArea.Thumb>
        </ColorArea.Background>
      </ColorArea>

      <ColorSlider class="ColorSliderRoot"
          value={value()} onChange={changeValue}
          channel="hue" colorSpace="hsb" orientation="horizontal">
        <ColorSlider.Track class="ColorSliderTrack">
          <ColorSlider.Thumb class="ColorSliderThumb" />
        </ColorSlider.Track>
      </ColorSlider>
    </>
  );

  const palettePanel = () => (
    <div class="colorpicker__swatches">
      <For each={swatches()}>{ swatch =>
        <button type="button" title={swatch.name} aria-label={swatch.name}
            classList={{ 'colorpicker__swatch': true, 'colorpicker__swatch--selected': isSelected( swatch.hex ) }}
            onClick={ () => chooseSwatch( swatch.hex ) }>
          <span class="colorpicker__swatch-chip" style={{ 'background-color': swatch.hex }} />
          <span class="colorpicker__swatch-name">{swatch.name}</span>
        </button>
      }</For>
    </div>
  );

  // Rows are keyed by index (via For's second argument) rather than by content, since
  //  duplicate hexes are permitted and names are edited in place.
  const savedPanel = () => (
    <div class="colorpicker__saved">
      <div class="colorpicker__saved-list">
        <Show when={saved.length > 0}
            fallback={<div class="colorpicker__saved-empty">No saved colors yet.</div>}>
          <For each={saved}>{ ( entry, index ) =>
            <div class="colorpicker__saved-row">
              <button type="button" title={`Use ${entry.name || entry.hex}`}
                  aria-label={`Use ${entry.name || entry.hex}`}
                  classList={{ 'colorpicker__saved-chip': true,
                               'colorpicker__saved-chip--selected': isSelected( entry.hex ) }}
                  style={{ 'background-color': entry.hex }}
                  onClick={ () => chooseSwatch( entry.hex ) } />
              <input class="colorpicker__saved-name" type="text" value={entry.name}
                  placeholder="no name" aria-label="color name"
                  onInput={ e => renameSaved( index(), e.currentTarget.value ) } />
              <button type="button" class="colorpicker__saved-delete"
                  title="Remove this color" aria-label={`Remove ${entry.name || entry.hex}`}
                  onClick={ () => removeSaved( index() ) }>
                <TrashIcon/>
              </button>
            </div>
          }</For>
        </Show>
      </div>
    </div>
  );

  return (
    <Dialog open={props.show} onOpenChange={handleOpenChange} modal={true}>
      <Dialog.Portal>
        <Dialog.Overlay class="colorpicker__overlay" />
        <div class="colorpicker__positioner">
          <Dialog.Content class="colorpicker__content" onKeyDown={handleKeyDown}>

            <Dialog.Title class="colorpicker__title">{props.title || 'Choose a color'}</Dialog.Title>

            <div class="colorpicker__body">

              {/* "Saved" is always offered, so every picker can accumulate the user's own
                  colors; "Zometool" appears only where a palette was supplied. */}
              <Tabs class="colorpicker__tabs" value={tab()} onChange={setTab} aria-label="color source">
                <Tabs.List class="tabs__list">
                  <Tabs.Trigger class="tabs__trigger" value="General">General</Tabs.Trigger>
                  <Show when={swatches().length > 0}>
                    <Tabs.Trigger class="tabs__trigger" value="Zometool">Zometool</Tabs.Trigger>
                  </Show>
                  <Tabs.Trigger class="tabs__trigger" value="Saved">Saved</Tabs.Trigger>
                  <Tabs.Indicator class="tabs__indicator" />
                </Tabs.List>
                <Tabs.Content class="colorpicker__tab-content" value="General">
                  {generalPanel()}
                </Tabs.Content>
                <Show when={swatches().length > 0}>
                  <Tabs.Content class="colorpicker__tab-content" value="Zometool">
                    {palettePanel()}
                  </Tabs.Content>
                </Show>
                <Tabs.Content class="colorpicker__tab-content" value="Saved">
                  {savedPanel()}
                </Tabs.Content>
              </Tabs>

              {/* The current color, shown under every tab.  "Save" lives here rather than
                  in the Saved panel so the color being saved is always the one on view. */}
              <div class="colorpicker__preview">
                <ColorSwatch class="ColorSwatch" value={value()} />
                <div class="colorpicker__hex">{value() .toString( 'hex' )}</div>
                <button type="button" class="colorpicker__button colorpicker__save-button"
                    onClick={saveCurrentColor}>Save</button>
              </div>

            </div>

            <div class="colorpicker__actions">
              <button class="colorpicker__button" onClick={handleCancel}>Cancel</button>
              <button class="colorpicker__button colorpicker__button--primary" onClick={handleOk}>OK</button>
            </div>

          </Dialog.Content>
        </div>
      </Dialog.Portal>
    </Dialog>
  );
}
