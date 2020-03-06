using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.UI;
using Unity.Jobs;
using Unity.Collections;

public class MyFirstScript : MonoBehaviour
{
    [Serializable]
    public struct Shape
    {
        public string id;
    }

    [Serializable]
    public struct Instance
    {
        public string id;
        public string shape;
        public string color;
        public Vector3 position;
        public Quaternion rotation;
    }

    [Serializable]
    public struct Vector3
    {
        public float x;
        public float y;
        public float z;
    }

    [Serializable]
    public struct Quaternion
    {
        public float x;
        public float y;
        public float z;
        public float w;
    }

    public UnityEngine.UI.Text label;
    public string url;

    public void LoadVZome()
    {
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
        Shape shape = JsonUtility.FromJson<Shape>(json);
        Debug.Log( "%%%%%%%%%%%%%% DefineMesh from Java: " + shape.id );
    }

    void CreateGameObject( string json )
    { 
        Instance instance = JsonUtility.FromJson<Instance>(json);
        Debug.Log( "%%%%%%%%%%%%%% CreateGameObject from Java: " + instance.id );
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
