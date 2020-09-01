
// import { NewCentroid } from '../jsweet/com/vzome/core/edits/NewCentroid'

// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

export const init = ( window, store ) =>
{
  console.log( "Created a NewCentroid: " + new window.com.vzome.core.edits.NewCentroid(null,null) )
}