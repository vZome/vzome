import React from 'react';
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar } from '@ionic/react';
import View3d from '../components/view3d';

const Tab3: React.FC = () => {
  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>3D View</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent>
        <View3d/>
      </IonContent>
    </IonPage>
  );
};

export default Tab3;