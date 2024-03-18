
import { createEffect, onMount, useFrame, useThree } from "solid-three";
import { CSS2DObject, CSS2DRenderer } from "three-stdlib";

export const Labels = (props) =>
{
  const scene = useThree(({ scene }) => scene);
  const camera = useThree(({ camera }) => camera);
  const webGL = useThree(({gl}) => gl);

  let labelRenderer;
  onMount( () => {
    labelRenderer = new CSS2DRenderer();
    const labelsElem = labelRenderer.domElement;
    labelsElem.style.isolation = 'isolate';
    labelsElem.style.position = 'absolute';
    labelsElem.style.pointerEvents = 'none';
    labelsElem.style.inset = '0px';
    labelsElem.style.width = '100%';
    labelsElem.style.height = '100%';
    labelsElem.classList .add( 'labels' );

    webGL() .domElement .insertAdjacentElement( "beforebegin", labelsElem );
  });

  createEffect( () => {
    props.size && labelRenderer .setSize( props.size .width, props.size .height );
  } );

  useFrame( () => {
    labelRenderer .render( scene(), camera() );
  })

  return null;
}

export const Label = (props) =>
{
  let label;  
  onMount( () => {
    const elem = document .createElement( 'div' );
    elem.className = 'vzome-label';
    elem.id = `vzome-label-${props.text}`;
    elem.textContent = props.text;
    label = new CSS2DObject( elem );
    props.parent .add( label );
  });

  createEffect( () => {
    const { x, y, z } = props.position;
    label.position.set( x, y, z );
  })

  return label;
}