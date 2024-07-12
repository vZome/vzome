

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

  const normalizeShape = shape =>
  {
    const { name, orbit, orbitC, length, id, vertices, faces } = shape;
    if ( orbitC ) // latest format 2024-07
      return { id, orbit: orbitC, length, vertices, faces };
    if ( name === 'ball' )
      return { id, name, vertices, faces };

    // earlier 2024 strut format, with orbit and name
    if ( name && orbit ) {
      const orbits = {
        'red'   : '[[-1,1,1],[0,0,1]]',
        'yellow': '[[0,0,1],[2,-1,1]]',
        'blue'  : '[[0,0,1],[0,0,1]]',
        'green' : '[[2,-1,1],[5,-3,1]]',
      }
      // TODO: handle more orbits, and odd lengths
      if ( orbits[ orbit ] ) {
        let length;
        switch ( name ) {

          case ' :1/2':
            length = '[1,0,2]';
            break;

          case ' :1/2*phi':
            length = '[0,1,2]';
            break;
  
          case ' :-1/2 +1/2*phi':
            length = '[-1,1,2]';
            break;
          
          case ' :2 -phi':
            length = '[2,-1,1]';
            break;

          case 'b0':
          case 'y0':
          case 'r0':
          case 'g0':
            length = '[-1,1,1]';
            break;
        
          case 'b1':
          case 'y1':
          case 'r1':
          case 'g1':
            length = '[1,0,1]';
            break;
            
          case 'b2':
          case 'y2':
          case 'r2':
          case 'g2':
            length = '[0,1,1]';
            break;
        
          default:
            break;
        }

        if ( length )
          return { id, orbit: orbits[ orbit ], length, vertices, faces };
      }
    }

    // These three fields are the minumum required for rendering.
    //  Any other fields (above) support bill-of-materials features.
    return { id, vertices, faces };
  }

  // Convert the preview.shapes array to the final shapes object
  const shapes = {}
  preview.shapes.map( shape => {
    shapes[ shape.id ] = normalizeShape( shape );
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

