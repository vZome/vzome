

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

export const toWebColor = color =>
{
  const componentToHex = c => {
    let hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
  }
  let result = "#ffffff";
  if ( color )
    result = "#" + componentToHex(color.getRed()) + componentToHex(color.getGreen()) + componentToHex(color.getBlue());
  return result;
}

export const normalizeRenderedManifestation = rm =>
{
  const id = rm.getGuid().toString();
  const type = rm .getShape() .name;
  const shapeId = 's' + rm.getShapeId().toString();
  const positionAV = rm.getLocationAV();
  const { x, y, z } = ( positionAV && positionAV.toRealVector() ) || { x:0, y:0, z:0 };
  const rotation = rm .getOrientation() .getRowMajorRealElements();
  const selected = rm .getGlow() > 0.001;
  const componentToHex = c => {
    let hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
  }
  let color = toWebColor( rm.getColor() );

  return { id, position: [ x, y, z ], rotation, color, selected, shapeId, type };
}

export const renderedModelTransducer = ( shapeCache, clientEvents ) =>
{
  const manifestationAdded = rm =>
  {
    const shapeId = 's' + rm.getShapeId().toString();
    let shape = shapeCache[ shapeId ];
    if ( ! shape ) {
      shape = realizeShape( rm .getShape() );
      const orbit = rm .getStrutOrbit();
      shape.zone = orbit? `${orbit.toString()} ${rm.getStrutZone()}` : 'Ball';
      shapeCache[ shapeId ] = shape;
      clientEvents .shapeDefined( shape );
    }
    let instance = normalizeRenderedManifestation( rm );
    // Record this instance for the current edit
    clientEvents .instanceAdded( instance );
  }

  const manifestationRemoved = rm =>
  {
    const shapeId = 's' + rm.getShapeId().toString();
    const id = rm.getGuid().toString();
    clientEvents .instanceRemoved( shapeId, id );
  }

  const glowChanged = rm =>
  {
    const shapeId = 's' + rm.getShapeId().toString();
    const id = rm.getGuid().toString();
    const selected = rm .getGlow() > 0.001;
    clientEvents .selectionToggled( shapeId, id, selected );
  }

  return { manifestationAdded, manifestationRemoved, glowChanged };
}

export const resolveBuildPlanes = buildPlanes =>
{
  const resolveAV = av => {
    const { x, y, z } = av.toRealVector(); // does this need to be embedded?  I think not.
    return [ x, y, z ];
  }
  const planes = {};
  for ( const [ name, plane ] of Object.entries( buildPlanes ) ) {
    const zones = [];
    for ( const zone of plane.zones ) {
      const vectors = zone .vectors .map( ({ point }) => resolveAV( point ) );
      zones .push( { name: zone.name, color: zone.color, vectors, orientation: zone.orientation } );
    }
    const { x, y, z } = plane.normal.toRealVector() .normalize(); // does this need to be embedded?  I think not.
    planes[ name ] = { color: plane.color, normal: [ x, y, z ], zones, orientation: plane.orientation };
  }
  return planes;
}
