import React, { useRef } from 'react'
import { IonHeader, IonToolbar, IonPage, IonTitle, IonContent } from '@ionic/react'

import Ball from '../components/ball'
import Plane from '../components/plane'

import { Canvas, useThree, useRender, useResource, extend } from 'react-three-fiber';
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'

extend({ TrackballControls })

function Controls( props ) {
  const { gl, camera } = useThree()
  const ref = useRef()
  useRender(() => ref.current.update())
  return <trackballControls ref={ref} args={[camera, gl.domElement]} {...props} />
}

const View3dPage = () => {
  const [geometryRef, geometry] = useResource()
  const [materialRef, material] = useResource()
  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>3D View</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent>
        <Canvas camera={{ fov: 30 }}>
          <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
          <Plane/>
          <dodecahedronBufferGeometry ref={geometryRef} args={[0.4, 0]} />
          <meshNormalMaterial ref={materialRef} />

          {geometry && (
            <Ball geom={geometry} material={material} position={[0,0,-0.2]} />)}
            <Ball geom={geometry} material={material} position={[0,1,-0.2]} />)}
            <Ball geom={geometry} material={material} position={[1,0,-0.2]} />)}
            <Ball geom={geometry} material={material} position={[1,2,-0.2]} />)}
            <Ball geom={geometry} material={material} position={[-1,-1,-0.2]} />)}
        </Canvas>
      </IonContent>
    </IonPage>
  );
};

export default View3dPage;
