

const normalizePreviewCamera = camera =>
{
  const { lookAtPoint, upDirection, lookDirection, viewDistance, width, nearClipDistance, farClipDistance, perspective } = camera
  const lookAt = [ ...Object.values( lookAtPoint ) ]
  const up = [ ...Object.values( upDirection ) ]
  const lookDir = [ ...Object.values( lookDirection ) ];
  const near = nearClipDistance;
  const far = farClipDistance;
  const distance = viewDistance;
  return {
    near, far, width, distance,
    up, lookAt, lookDir,
    perspective
  }
}

// Normalize a legacy .shapes.json (produced by desktop)
//
export const normalizePreview = ( preview ) =>
{
  const { polygons, lights, embedding, orientations, instances } = preview;
  
  const dlights = lights.directionalLights.map( ({ direction, color }) => {
    const { x, y, z } = direction
    return { direction: [ x, y, z ], color }
  })
  const lighting = { ...lights, directionalLights: dlights };

  const normalizeInstances = ( instances, idPrefix ) =>
  {
    let i = 0;
    return instances.map( ({ position, orientation, color, shape, label, glow }) => {
      const id = idPrefix + i++;
      const { x, y, z } = position;
      const selected = !!glow && glow > 0.001;
      const instance = { id, position: [ x, y, z ], orientation, color, selected, shapeId: shape, type: 'irrelevant', label };
      return instance;
    });
  }

  const snapshots = preview.snapshots? [ ...preview.snapshots ] .map( (snapshot,i) => {
    return normalizeInstances( snapshot, `snap${i}_` );
  }) : [];
  snapshots .push( normalizeInstances( instances, "main_" ) );
  const defaultSnapshot = snapshots.length-1;

  // Convert the preview.shapes array to the final shapes object
  const shapes = {}
  preview.shapes.map( shape => {
    shapes[ shape.id ] = shape;
  } );

  const normalizePreviewScene = ( { title, snapshot, view } ) =>
  {
    const camera = normalizePreviewCamera( view );
    return { title, snapshot, camera };
  }
    
  const camera = normalizePreviewCamera( preview.camera );
  const defaultScene = { title: 'default scene', camera, snapshot: defaultSnapshot };
  let normalizedScenes = [ defaultScene ];
  if ( preview.scenes ) {
    const scenes = preview.scenes .map( normalizePreviewScene );
    normalizedScenes = [ defaultScene, ...scenes ];
  }

  return { lighting, embedding, orientations, polygons, shapes, snapshots, scenes: normalizedScenes };
}

