
import { onMount } from "solid-js";

import { useInteractionTool, grabTool } from "../../../viewer/context/interaction.jsx";
import { useSymmetry } from "../context/symmetry.jsx";


export const SnapCameraTool = props =>
{
  const { snapCamera } = useSymmetry();

  const [ _, setTool ] = useInteractionTool();
  const cursor = 'url(/app/classic/rotate.svg) 9 9, url(/app/classic/rotate.png) 9 9, grab';
  onMount( () => setTool( { ...grabTool, cursor, onTrackballEnd: snapCamera } ) );

  return null;
}
