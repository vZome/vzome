
import goldenField from '../fields/golden'
// import { NewCentroid } from '../jsweet/com/vzome/core/edits/NewCentroid'

// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

const EDIT_TRIGGERED = 'EDIT_TRIGGERED'

export const triggerEdit = ( edit ) =>
{
  return { type: EDIT_TRIGGERED, payload: edit }
}

var vzome = undefined
var model = undefined
var selection = undefined

export const init = ( window, store ) =>
{
  vzome = window.com.vzome

  const field = new vzome.jsweet.JsAlgebraicField( goldenField )
  const zero = field.createAlgebraicNumberFromTD( [0,0,1] )
  const two = field.createAlgebraicNumberFromTD( [2,0,1] )

  const origin = new vzome.core.algebra.AlgebraicVector( [ zero, zero, zero ] )
  const unitX = new vzome.core.algebra.AlgebraicVector( [ two, zero, zero ] )

  model = new vzome.jsweet.JsRealizedModel()
  selection = new vzome.core.editor.SelectionImpl()

  const ball1 = new vzome.jsweet.JsBall( origin )
  selection.select( ball1 )
  const ball2 = new vzome.jsweet.JsBall( unitX )
  selection.select( ball2 )

  console.log( "JSweet-compiled Java init COMPLETED" )
}

export const middleware = store => next => async action => 
{
  if ( action.type === EDIT_TRIGGERED ) {

    const editClass = vzome.core.edits[ action.payload ]
    const edit = new editClass( selection, model )
  
    edit.perform()
    
    console.log( "JSweet-compiled Java edit COMPLETED" )
  }
  
  return next( action )
}
