
// import { NewCentroid } from '../jsweet/com/vzome/core/edits/NewCentroid'

// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

export const init = ( window, store ) =>
{
  const num = [ 1, 2, 3 ]
  const field = new window.com.vzome.jsweet.JsAlgebraicField()
  const anum = field.createAlgebraicNumberFromTD( num )
  const vector = new window.com.vzome.core.algebra.AlgebraicVector( [ anum, anum, anum ] )
  console.log( "AlgebraicVector value: " + vector.toRealVector() )
}
