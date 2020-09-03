
// import { NewCentroid } from '../jsweet/com/vzome/core/edits/NewCentroid'

// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

export const init = ( window, store ) =>
{
  const field = new window.com.vzome.jsweet.JsAlgebraicField()
  const zero = field.createAlgebraicNumberFromTD( [0,0,1] )
  const two = field.createAlgebraicNumberFromTD( [2,0,1] )

  const origin = new window.com.vzome.core.algebra.AlgebraicVector( [ zero, zero, zero ] )
  const unitX = new window.com.vzome.core.algebra.AlgebraicVector( [ two, zero, zero ] )

  const model = new window.com.vzome.jsweet.JsRealizedModel()
  const selection = new window.com.vzome.core.editor.SelectionImpl()

  const ball1 = new window.com.vzome.jsweet.JsBall( origin )
  selection.select( ball1 )
  const ball2 = new window.com.vzome.jsweet.JsBall( unitX )
  selection.select( ball2 )

  const editClass = window.com.vzome.core.edits[ "NewCentroid" ]
  const edit = new editClass( selection, model )

  edit.perform()

  console.log( "JSweet-compiled Java code COMPLETED" )
}
