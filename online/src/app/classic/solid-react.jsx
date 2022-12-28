
// reusable wrapper to inject React inside Solid

import { createEffect, untrack } from "solid-js";
import { createRoot } from 'react-dom/client';
import { createElement } from "react";

export const solidify = Component =>
{
  return (props) => {
    let el;
    const result = <div ref={el} style={props.style} />;
    const root = createRoot( el );
    createEffect(() => {
      Object.values( props );
      untrack( () => {
        root.render( createElement( Component, props ) );
        // render( createElement( Component, props ), el );
      } );
    });
    return result;
  };
}
