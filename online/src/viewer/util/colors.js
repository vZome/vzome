
// The neutral background shown by the web components before a design's own background is known.
//   (The editor app keeps DEFAULT_BACKGROUND_COLOR from camera.jsx.)
export const PLACEHOLDER_BACKGROUND_COLOR = '#CCCCCC';

let context2d = null;

// Normalize any CSS color string to '#rrggbb', or return null if it is not a valid CSS color.
//   three.js Color.setStyle only understands a subset of CSS colors, and it warns on anything else;
//   since the scene background is re-applied every frame, an unnormalized color could warn every frame.
//   Rendering one pixel through a 2D canvas lets the browser do the parsing, for any color syntax it supports.
//   Alpha is ignored, and wide-gamut colors are clamped to sRGB.
export const normalizeCssColor = value =>
{
  if ( typeof value !== 'string' || value.trim() === '' )
    return null;
  context2d = context2d || document.createElement( 'canvas' ) .getContext( '2d', { willReadFrequently: true } );
  if ( ! context2d )
    return null;

  // An invalid fillStyle assignment is silently ignored, so try it against two different sentinels.
  context2d.fillStyle = '#000000';
  context2d.fillStyle = value;
  const first = context2d.fillStyle;
  context2d.fillStyle = '#ffffff';
  context2d.fillStyle = value;
  if ( context2d.fillStyle !== first )
    return null;

  context2d.clearRect( 0, 0, 1, 1 );
  context2d.fillRect( 0, 0, 1, 1 );
  const [ r, g, b ] = context2d.getImageData( 0, 0, 1, 1 ).data;
  return '#' + [ r, g, b ] .map( c => c.toString( 16 ) .padStart( 2, '0' ) ) .join( '' );
}
