using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.UI;
using Unity.Jobs;
using Unity.Collections;

public class MyFirstScript : MonoBehaviour
{
    public Text label;
    public string url;

    public void LoadVZome()
    {
        // string url = "https://vzome.com/models/2007/04-Apr/5cell/A4_9.vZome";
        label.text = "Loading url: " + url;
        Debug.Log( "%%%%%%%%%%%%%% new LoadVZomeJob... " );
        LoadVZomeJob job = new LoadVZomeJob();
        job.urlBytes = new NativeArray<byte>( url.Length, Allocator.Temp );
        job.urlBytes .CopyFrom( Encoding.ASCII.GetBytes( url ) );
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
    public NativeArray<byte> urlBytes;
    
    public void Execute()
    {
        Debug.Log( "%%%%%%%%%%%%%% AndroidJNI.AttachCurrentThread... " );
        AndroidJNI.AttachCurrentThread();

        string url = Encoding.ASCII.GetString( urlBytes.ToArray() );
        urlBytes .Dispose();

        AndroidJavaObject application = new AndroidJavaObject( "com.vzome.api.Application" );
        Debug.Log( "%%%%%%%%%%%%%% app created successfully " );
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
