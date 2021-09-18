
import DesignCanvas from './components/designcanvas.jsx'
import BuildPlane from './components/buildplane.jsx'
import { ShapedGeometry } from './components/geometry.jsx'
import { UrlViewer, MeshGeometry, DesignViewer } from './components/urlviewer.jsx'
import Adapter, { createInstance } from './core/adapter.js'
import { parse, interpret, Step, getDefaultRenderer } from './core/api.js'
import { fetchUrlText, fetchUrlDesign } from './components/hooks.js'
import goldenField from './fields/golden.js'
import root2Field from './fields/root2.js'
import root3Field from './fields/root3.js'
import heptagonField from './fields/heptagon.js'

// export everything as named exports
export {
  DesignCanvas, BuildPlane, ShapedGeometry, MeshGeometry, UrlViewer, DesignViewer,
  createInstance, Adapter, fetchUrlDesign, fetchUrlText,
  parse, interpret, Step, getDefaultRenderer,
  goldenField, root2Field, root3Field, heptagonField
}

// alternative, more concise syntax for named exports
// export { default as DesignCanvas } from './DesignCanvas'

// you can optionally also set a default export for your module
// export default { DesignCanvas, BuildPlane, ShapedGeometry, UrlViewer }
