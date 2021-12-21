
import { normalizeRenderedManifestation, parserPromise, realizeShape } from './js2jsweet.js'
import { defaultNew } from './resources/com/vzome/core/parts/index.js'
import { createInstance } from './adapter.js'

export { realizeShape, normalizeRenderedManifestation } from './js2jsweet.js'

export const parse = async text =>
{
  const { parser } = await parserPromise
  return parser( text )
}

const R = 2
const octahedronShape =
{
  id: "octahedron",
  vertices: [
    { x: R, y: 0, z: 0 },
    { x: 0, y: R, z: 0 },
    { x: 0, y: 0, z: R },
    { x: -R, y: 0, z: 0 },
    { x: 0, y: -R, z: 0 },
    { x: 0, y: 0, z: -R },
  ],
  faces: [
    { vertices: [ 0, 1, 2 ] },
    { vertices: [ 0, 5, 1 ] },
    { vertices: [ 0, 4, 5 ] },
    { vertices: [ 0, 2, 4 ] },
    { vertices: [ 3, 2, 1 ] },
    { vertices: [ 3, 4, 2 ] },
    { vertices: [ 3, 5, 4 ] },
    { vertices: [ 3, 1, 5 ] },
  ]
}
const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]

const loadBallShape = ( fieldName, shapesModel ) =>
{
  const { connectorShape, name } = shapesModel
  const { faces } = connectorShape
  const id = `${fieldName}/${name}/connector`
  const vertices = connectorShape.vertices.map( ([x,y,z]) => ({x,y,z}) )
  return { id, vertices, faces }
}

export const getDefaultRenderer = field =>
{
  const defaultShaper = shapes => ( { id, vectors } ) =>
  {
    if ( ! shapes.octahedron ) {
      shapes.octahedron = octahedronShape
    }
    const [ v0 ] = vectors
    const [ x, y, z ] = field.embedv( v0 )
    return { id, position: [ x, y, z ], rotation: IDENTITY_MATRIX, color: "#ffffff", shapeId: "octahedron" }
  }
  const goldenShaper = shapes => ( { id, vectors } ) =>
  {
    if ( ! shapes.goldenBall ) {
      shapes.goldenBall = loadBallShape( "golden", defaultNew )
    }
    const [ v0 ] = vectors
    const [ x, y, z ] = field.embedv( v0 )
    return { id, position: [ x, y, z ], rotation: IDENTITY_MATRIX, color: "#ffffff", shapeId: shapes.goldenBall.id }
  }
  return {
    name: 'default',
    embedding: IDENTITY_MATRIX,
    shaper: ( field.name === "golden" )? goldenShaper : defaultShaper
  }
}

export const cloneMesh = ( { shown, selected, hidden, groups=[] } ) =>
  ({ shown: new Map( shown ), selected: new Map( selected ), hidden: new Map( hidden ), groups: [ ...groups ] })

export const Step = { IN: 0, OVER: 1, OUT: 2, DONE: 3 }

export const interpret = async ( action, mesh, edit, stack=[], recordSnapshot ) =>
{
  // const nextTask = () => {
  //     return new Promise( res => setTimeout( res ) );
  // }
  const step = () =>
  {
    if ( ! edit )
      return Step.DONE;
    if ( edit.isBranch() ) {
      const branchMesh = cloneMesh( mesh );
      stack.push( { branch: edit, mesh } );
      recordSnapshot && recordSnapshot( branchMesh, edit.id(), edit.firstChild(), stack );
      edit = edit.firstChild(); // this assumes there are no empty branches
      mesh = branchMesh;
      return Step.IN;
    } else {
      mesh = cloneMesh( mesh );  // each command builds on the last
      edit.perform( mesh );
      if ( edit.nextSibling() ) {
        recordSnapshot && recordSnapshot( mesh, edit.id(), edit.nextSibling() );
        edit = edit.nextSibling();
        return Step.OVER;
      } else {
        let top;
        do {
          top = stack.pop();
        } while ( top && ! top.branch.nextSibling() )
        if ( top ) {
          mesh = cloneMesh( top.mesh );  // overwrite and discard the prior value
          edit = top.branch.nextSibling();
          recordSnapshot && recordSnapshot( mesh, edit.id(), edit, stack );
          return Step.OUT;
        } else {
          // at the end of the editHistory
          recordSnapshot && recordSnapshot( mesh, edit.id(), null );
          return Step.DONE;
        }
      }
    }
  }

  const conTinue = async () =>
  {
    let stepped;
    do {
      stepped = await stepOut();
      // await nextTask();
    } while ( stepped !== Step.DONE );
  }

  const stepOver = async () =>
  {
    const stepped = step();
    switch ( stepped ) {

      case Step.IN:
        await stepOut();
        return Step.OVER;
    
      default:
        return stepped;
    }
  }

  const stepOut = async () =>
  {
    let stepped;
    do {
      stepped = await stepOver();
      // await nextTask();
    } while ( stepped !== Step.OUT && stepped !== Step.DONE );
    return stepped;
  }

  switch ( action ) {

    case Step.IN:
      await step();
      break;
  
    case Step.OVER:
      await stepOver();
      break;
  
    case Step.OUT:
      await stepOut();
      break;
  
    case Step.DONE:
    default:
      await conTinue();
      break;
  }
}

export const parseAndRender = async ( text ) =>
{
  const { field, targetEdit, firstEdit, renderer, camera, lighting } = await parse( text );
  let latestMesh = { shown: originShown( field ), selected: new Map(), hidden: new Map(), groups: [] };
  let targetMesh = null;
  const record = ( mesh, id ) => {
    if ( !targetMesh && id === targetEdit ) {
      targetMesh = latestMesh; // record the prior state
    }
    latestMesh = mesh; // will record where we failed, if we don't reach targetEdit
  } // yup, overwrite every time
  await interpret( Step.DONE, latestMesh, firstEdit, [], record );

  const { shown, selected } = targetMesh || latestMesh;
  const { shaper, embedding } = renderer;
  const shapes = {};
  const { instances } = shapeMesh( shapes, {}, shown, selected, shaper( shapes ) );

  return {
    camera: { ...camera, fov: 0.75 },
    lighting,
    embedding,
    shapes,
    instances,
  };
}

const originShown = field =>
{
  const originBall = createInstance( [ field.origin( 3 ) ] );
  return new Map().set( originBall.id, originBall )
}

export const shapeMesh = ( shapes, shapedInstances, shown, selected, cachingShaper ) =>
{
  const shapeInstance = ( instance ) =>
  {
    // TODO: handle undefined result from resolve
    let shapedInstance = shapedInstances[ instance.id ];
    if ( shapedInstance && ( instance.color === shapedInstance.color ) ) {
      return shapedInstance;
    }
    shapedInstance = cachingShaper( instance );
    // everything except selected state will go into shapedInstances
    shapedInstance = { ...shapedInstance, vectors: instance.vectors };
    shapedInstances[ instance.id ] = shapedInstance;
    return shapedInstance
  }
  if ( cachingShaper ) {
    try {
      const instances = [];
      const tryToShape = ( instance, selected ) => {
        try {
          instance && instances.push( { ...shapeInstance( instance ), selected } );
        } catch (error) {
          console.log( `Failed to shape instance: ${instance.id}` );
        }
      }
      shown.forEach( instance => tryToShape( instance, false ) );
      selected.forEach( instance => tryToShape( instance, true ) );
      return { shapes, instances };
    } catch (error) {
      console.log( 'Caught an odd error while shaping' );
      return {};
    }
  } else
    console.log( 'no cachingShaper' );
    return {};
}

export const interpretAndRender = ( design, sceneListener ) =>
{
  const shapes = {};
  const renderingListener = ({
    manifestationAdded: rm => {
      const instance = normalizeRenderedManifestation( rm );
      const { shapeId } = instance;
      if ( ! shapes[ shapeId ] ) {
        shapes[ shapeId ] = realizeShape( rm .getShape() );
        shapes[ shapeId ].instances = {};
        // sceneListener .shapeAdded( shapes[ shapeId ] );
      }
      shapes[ shapeId ].instances[ instance.id ] = instance;
      // sceneListener .instanceAdded( instance );
    },
    manifestationRemoved: rm => {
      const instance = normalizeRenderedManifestation( rm );
      const { shapeId } = instance;
      const shape = shapes[ shapeId ];
      delete shape.instances[ instance.id ];
      // sceneListener .instanceRemoved( instance );
    },
    glowChanged: rm => {
      console.log( 'glowChanged' );
    },
    colorChanged: rm => {
      console.log( 'colorChanged' );
    },
  });
  const { targetEdit, firstEdit, renderer, camera, lighting, batchRender } = design;
  const { embedding } = renderer;

  // WORKAROUND
  camera.fov = 0.33915263,

  sceneListener.initialized( { lighting, camera, embedding } );

  // Not attached while we're doing batchRender
  // renderedModel .addListener( renderingListener );

  const record = ( mesh, id ) => {
    if ( id === targetEdit ) {
      console.log( `Hit the target edit: ${targetEdit}` );
    }
  }
  const unusedMesh = {};
  interpret( Step.DONE, unusedMesh, firstEdit, [], record )
    .then( () => {
      batchRender( renderingListener );
      Object.values( shapes ).map( shape => shape.instances = Object.values( shape.instances ) );
      sceneListener.initialized( { shapes } );
    } );
}