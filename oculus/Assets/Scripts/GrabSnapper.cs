using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GrabSnapper : OVRGrabbable
{
    public string vZomeId;
    public VZomeJavaBridge bridge;
 
    public override void GrabBegin(OVRGrabber hand, Collider grabPoint)
    {
        base.GrabBegin(hand, grabPoint);
    }
 
    public override void GrabEnd(Vector3 linearVelocity, Vector3 angularVelocity)
    {
        base.GrabEnd(linearVelocity, angularVelocity);

        bridge .ObjectMoved( vZomeId );
    }
}
