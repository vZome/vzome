

export const realizeShape = ( shape ) =>
{
  const vertices = shape.getVertexList().toArray().map( av => {
    const { x, y, z } = av.toRealVector();  // this is too early to do embedding, which is done later, globally
    return { x, y, z };
  })
  const faces = shape.getTriangleFaces().toArray().map( ({ vertices }) => ({ vertices }) );  // not a no-op, converts to POJS
  const id = 's' + shape.getGuid().toString();
  return { id, vertices, faces, instances: [] };
}

export const normalizeRenderedManifestation = rm =>
{
  const id = rm.getGuid().toString();
  const shapeId = 's' + rm.getShapeId().toString();
  const positionAV = rm.getLocationAV();
  const { x, y, z } = ( positionAV && positionAV.toRealVector() ) || { x:0, y:0, z:0 };
  const rotation = rm .getOrientation() .getRowMajorRealElements();
  const selected = rm .getGlow() > 0.001;
  const componentToHex = c => {
    let hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
  }
  let color = "#ffffff";
  const rmc = rm.getColor();
  if ( rmc )
    color = "#" + componentToHex(rmc.getRed()) + componentToHex(rmc.getGreen()) + componentToHex(rmc.getBlue());

  return { id, position: [ x, y, z ], rotation, color, selected, shapeId };
}

export const renderedModelTransducer = ( shapeCache, sceneReporter ) =>
{
  const manifestationAdded = rm =>
  {
    const shapeId = 's' + rm.getShapeId().toString();
    let shape = shapeCache[ shapeId ];
    if ( ! shape ) {
      shape = realizeShape( rm .getShape() );
      shapeCache[ shapeId ] = shape;
      sceneReporter .shapeDefined( shape );
    }
    let instance = normalizeRenderedManifestation( rm );
    // Record this instance for the current edit
    sceneReporter .instanceAdded( instance );
  }

  const glowChanged = rm =>
  {
    const shapeId = 's' + rm.getShapeId().toString();
    const id = rm.getGuid().toString();
    const selected = rm .getGlow() > 0.001;
    sceneReporter .selectionToggled( shapeId, id, selected );
  }

  return { manifestationAdded, glowChanged }
}