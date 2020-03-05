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
        Debug.Log( "%%%%%%%%%%%%%% SetLabelText from java: " + message );
        label.text = message;
    }

    void LogInfo( string message )
    { 
        Debug.Log( "%%%%%%%%%%%%%% From Java: " + message );
        label.text = message;
    }

    void LogException( string message )
    { 
        Debug.LogError( "%%%%%%%%%%%%%% From Java: " + message );
        label.text = message;
    }

    void DefineMesh( string json )
    { 
        Debug.Log( "%%%%%%%%%%%%%% DefineMesh from Java: " + json );
    }

    void CreateGameObject( string json )
    { 
        Debug.Log( "%%%%%%%%%%%%%% CreateGameObject from Java: " + json );
    }

    void DeleteGameObject( string json )
    { 
        Debug.Log( "%%%%%%%%%%%%%% DeleteGameObject from Java: " + json );
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

        AndroidJavaClass jc = new AndroidJavaClass( "com.unity3d.player.UnityPlayer" );
        AndroidJavaObject adapter = new AndroidJavaObject( "com.vzome.unity.Adapter", jc, "JavaCallbacks" );
        Debug.Log( "%%%%%%%%%%%%%% adapter created successfully " );
        Debug.Log( "%%%%%%%%%%%%%% attempting to open: " + url );
        adapter .Call<AndroidJavaObject>( "loadUrl", url );
        Debug.Log( "%%%%%%%%%%%%%% loadUrl returned" );
    }
}
