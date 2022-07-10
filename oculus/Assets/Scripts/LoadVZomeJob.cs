using System.Text;
using UnityEngine;
using Unity.Collections;
using Unity.Jobs;

public struct LoadVZomeJob : IJob
{
    public NativeArray<byte> pathN;
    public NativeArray<byte> objectNameN;
    
    public void Execute()
    {
        string path = Encoding.ASCII.GetString( pathN .ToArray() );
        pathN .Dispose();

        string objectName = Encoding.ASCII.GetString( objectNameN .ToArray() );
        objectNameN .Dispose();

        AndroidJNI.AttachCurrentThread();

        AndroidJavaClass adapterClass = new AndroidJavaClass( "com.vzome.unity.Adapter" );
        Debug.Log( "%%%%%%%%%%%%%% LoadVZomeJob attempting to open: " + path );
        adapterClass .CallStatic( "loadFile", path, objectName );
        // adapterClass .CallStatic( "loadUrl", "https://raw.githubusercontent.com/vorth/vzome-sharing/main/2022/07/04/16-28-03-test-demo-johnK/test-demo-johnK.vZome", objectName );
        Debug.Log( "%%%%%%%%%%%%%% LoadVZomeJob: loadFile returned" );
    }
}