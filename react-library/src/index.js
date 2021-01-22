
import DesignCanvas from './components/designcanvas'
import BuildPlane from './components/buildplane'
import { ShapedGeometry, MeshGeometry } from './components/geometry'
import UrlViewer from './components/urlviewer'
import Adapter, { createInstance } from './core/adapter'
import * as vZomeJava from './core/legacyjava'
import goldenField from './fields/golden'

// export everything as named exports
export { DesignCanvas, BuildPlane, ShapedGeometry, MeshGeometry, UrlViewer, createInstance, Adapter, vZomeJava, goldenField }

// alternative, more concise syntax for named exports
// export { default as DesignCanvas } from './DesignCanvas'

// you can optionally also set a default export for your module
// export default { DesignCanvas, BuildPlane, ShapedGeometry, UrlViewer }
