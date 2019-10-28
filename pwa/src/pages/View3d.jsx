import React, { useRef } from 'react'
import { IonHeader, IonToolbar, IonPage, IonTitle, IonContent } from '@ionic/react'

import Thing from '../components/thing'
import Plane from '../components/plane'

import { Canvas, useThree, useRender, extend } from 'react-three-fiber';
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'

extend({ TrackballControls })
const Controls = props => {
  const { gl, camera } = useThree()
  const ref = useRef()
  useRender(() => ref.current.update())
  return <trackballControls ref={ref} args={[camera, gl.domElement]} {...props} />
}


const View3dPage = () => {
  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>3D View</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent>
        <Canvas>
          <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
          <Plane/>
          <Thing/>
        </Canvas>
      </IonContent>
    </IonPage>
  );
};

export default View3dPage;
