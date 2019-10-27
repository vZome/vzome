import React from 'react';
import { IonHeader, IonToolbar, IonPage, IonTitle, IonContent } from '@ionic/react';

import { Canvas } from 'react-three-fiber'
import Thing from '../components/thing'


const View3dPage: React.FC = () => {
  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>3D View</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent>
        <Canvas>
          <Thing/>
        </Canvas>
      </IonContent>
    </IonPage>
  );
};

export default View3dPage;
