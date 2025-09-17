# LaTeX Panel Labels for vZome

This implementation adds LaTeX rendering support for panel labels in vZome Online, while keeping the existing billboarding approach for now.

## Features

- **LaTeX Rendering**: Panel labels can now render mathematical expressions using KaTeX
- **Backward Compatibility**: Regular text labels still work as before
- **Panel-Specific Styling**: Enhanced visual styling for panel labels
- **Automatic Detection**: LaTeX content is detected and rendered automatically
- **Fallback Support**: Falls back to plain text if LaTeX rendering fails

## Usage

### Basic Text Labels
```
Hello World
```

### LaTeX Mathematical Expressions
```
$x^2 + y^2 = r^2$
$\int_0^\infty e^{-x} dx = 1$
$\sum_{i=1}^n i = \frac{n(n+1)}{2}$
$$\phi = \frac{1 + \sqrt{5}}{2}$$
```

### Greek Letters and Symbols
```
$\alpha, \beta, \gamma$
$\pi, \sigma, \phi$
$\nabla, \partial, \infty$
```

## Implementation Details

### Files Modified/Created

1. **`/src/viewer/panellabels.jsx`** - New component for panel-specific LaTeX labels
2. **`/src/viewer/panellabels.css`** - Styling for panel labels
3. **`/src/app/classic/dialogs/label.jsx`** - Enhanced dialog with LaTeX support
4. **`/src/viewer/geometry.jsx`** - Updated to use panel labels for panels
5. **`/src/viewer/ltcanvas.jsx`** - Updated to use enhanced labels component

### Key Components

#### `PanelLabel`
- Renders LaTeX content using KaTeX library
- Falls back to plain text if LaTeX rendering fails
- Uses CSS2DObject for billboarding (current approach)
- Enhanced styling specific to panels

#### `EnhancedLabels`
- Replaces the basic Labels component
- Dynamically loads KaTeX library when needed
- Manages the CSS2DRenderer for all labels

### LaTeX Detection Logic

The component automatically detects LaTeX content by checking if the text:
- Contains backslashes (`\`) indicating LaTeX commands
- Starts and ends with `$` (inline math) or `$$` (display math)

### Styling Features

- Semi-transparent background with backdrop blur
- Drop shadow for better visibility
- Hover effects with scaling
- Dark mode support
- High DPI display optimization
- Typography optimized for mathematical content

## Future Enhancements

The current implementation keeps the billboarding approach as requested. Future improvements could include:

1. **Panel Attachment**: Labels that rotate with panel orientation
2. **Label Positioning**: Smart positioning relative to panel geometry
3. **Interactive Labels**: Click/hover interactions
4. **Label Management**: Bulk operations on labels
5. **Export Support**: Include labels in exported models

## Browser Compatibility

- **KaTeX**: Requires modern browsers with ES6 support
- **CSS Features**: Uses backdrop-filter (may require fallbacks for older browsers)
- **Fallback**: Always provides plain text rendering if LaTeX fails

## Performance Notes

- KaTeX library (~150KB) is loaded only when needed
- LaTeX rendering is performed client-side
- CSS2DRenderer handles efficient label positioning
- Minimal impact on 3D rendering performance

## Testing

To test the LaTeX panel labels:

1. Create a panel in vZome
2. Right-click and select "Label"
3. Enter LaTeX content like `$\phi = \frac{1+\sqrt{5}}{2}$`
4. The label should render with proper mathematical formatting

## Notes

- This implementation maintains the current billboarding behavior
- Panel rotation attachment will be implemented in a future iteration
- The LaTeX checkbox in the dialog is informational and doesn't affect functionality
- All LaTeX content is automatically detected and rendered