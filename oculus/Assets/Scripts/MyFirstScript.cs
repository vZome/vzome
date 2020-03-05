using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using Unity.Jobs;

public class MyFirstScript : MonoBehaviour
{
    public Text label;

    public void Start()
    {
        // AndroidJavaClass clazz = new AndroidJavaClass( "org.vorthmann.zome.app.impl.ApplicationController" );
        // AndroidJavaObject application = clazz .CallStatic<AndroidJavaObject>( "createHeadless" );
        // Debug.Log( "%%%%%%%%%%%%%% app created successfully " );
        // string url = "https://vzome.com/models/2007/04-Apr/5cell/A4_9.vZome";
        // Debug.Log( "%%%%%%%%%%%%%% attempting to open: " + url );
        // application .Call( "doAction", "openURL-" + url );
        // Debug.Log( "%%%%%%%%%%%%%% opened successfully: " + url );
        // AndroidJavaObject document = application .Call<AndroidJavaObject>( "getSubController", url );
        // Debug.Log( "%%%%%%%%%%%%%% retrieved successfully: " + url );
    }

    public void LoadVZome()
    {
        label.text = "Loading vZome file...";
        Debug.Log( "%%%%%%%%%%%%%% new LoadVZomeJob... " );
        LoadVZomeJob job = new LoadVZomeJob();
        JobHandle jh = job .Schedule();
        Debug.Log( "%%%%%%%%%%%%%% job .Scheduled. " );
    }

    void SetLabelText( string message )
    { 
        Debug.Log( "message from java: " + message );
        label.text = message;
    }
}


public struct LoadVZomeJob : IJob
{
    public void Execute()
    {
        Debug.Log( "%%%%%%%%%%%%%% AndroidJNI.AttachCurrentThread... " );
        AndroidJNI.AttachCurrentThread();

        AndroidJavaObject jo = new AndroidJavaObject("java.lang.String", "some_string"); 
        int hash = jo.Call<int>("hashCode"); 
        Debug.Log( "%%%%%%%%%%%%%% hashCode: " + hash ); 

        AndroidJavaObject application = new AndroidJavaObject( "com.vzome.api.Application" );
        Debug.Log( "%%%%%%%%%%%%%% app created successfully " );
        string url = "https://vzome.com/models/2007/04-Apr/5cell/A4_9.vZome";
        Debug.Log( "%%%%%%%%%%%%%% attempting to open: " + url );
        AndroidJavaObject document = application .Call<AndroidJavaObject>( "loadUrl", url );
        Debug.Log( "%%%%%%%%%%%%%% opened successfully: " + url );
        document = document .Call<AndroidJavaObject>( "getDocumentModel" );
        Debug.Log( "%%%%%%%%%%%%%% got the DocumentModel" );
        AndroidJavaObject field = document .Call<AndroidJavaObject>( "getField" );
        Debug.Log( "%%%%%%%%%%%%%% got the AlgebraicField" );
        string fieldName = field .Call<string>( "getName" );
        Debug.Log( "%%%%%%%%%%%%%% got the field name" );

        using ( AndroidJavaClass jc = new AndroidJavaClass( "com.unity3d.player.UnityPlayer" ) ) { 
            jc .CallStatic( "UnitySendMessage", "JavaCallbacks", "SetLabelText", "field name: " + fieldName );
        } 
    }
}
