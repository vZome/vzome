
// reusable wrapper to inject React inside Solid

import { createEffect, untrack } from "solid-js";
import { render } from "react-dom";
import { createElement } from "react";

export const solidify = Component =>
{
  return (props) => {
    let el;
    const result = <div ref={el} />;
    createEffect(() => {
      Object.values(props);
      untrack( () => render( createElement( Component, props ), el ) );
    });
    return result;
  };
}
