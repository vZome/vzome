using System.Text;
using UnityEngine;
using Unity.Collections;
using Unity.Jobs;

public struct EditJob : IJob
{
    public NativeArray<byte> pathN;
    public NativeArray<byte> actionN;
    
    public void Execute()
    {
        string path = Encoding.ASCII.GetString( pathN .ToArray() );
        pathN .Dispose();

        string action = Encoding.ASCII.GetString( actionN .ToArray() );
        actionN .Dispose();

        AndroidJNI.AttachCurrentThread();

        AndroidJavaClass adapterClass = new AndroidJavaClass( "com.vzome.unity.Adapter" );
        Debug.Log( "%%%%%%%%%%%%%% DoSimpleAction: getting adapter for: " + path );
        AndroidJavaObject adapter = adapterClass .CallStatic<AndroidJavaObject>( "getAdapter", path );

        SimpleAction pa = new SimpleAction();
        pa .action = action;
        string json = JsonUtility .ToJson( pa );
        Debug .Log( "%%%%%%%%%%%%%% Calling doAction: " + json );
        adapter .Call( "doAction", json );
        Debug.Log( "%%%%%%%%%%%%%% DoSimpleAction complete" );
    }
}