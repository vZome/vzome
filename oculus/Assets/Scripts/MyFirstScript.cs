using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using Unity.Jobs;

public class MyFirstScript : MonoBehaviour
{
    public Text label;
    public float width = 1;
    public float height = 1;

    public void Start()
    {
        Debug.Log( "%%%%%%%%%%%%%% new LoadVZomeJob... " );
        LoadVZomeJob job = new LoadVZomeJob();
        JobHandle jh = job .Schedule();
        Debug.Log( "%%%%%%%%%%%%%% job .Scheduled. " );

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

    public void MakeQuad()
    {
        MeshRenderer meshRenderer = gameObject.AddComponent<MeshRenderer>();
        meshRenderer.sharedMaterial = new Material(Shader.Find("Standard"));

        MeshFilter meshFilter = gameObject.AddComponent<MeshFilter>();

        Mesh mesh = new Mesh();

        Vector3[] vertices = new Vector3[4]
        {
            new Vector3(0, 0, 0),
            new Vector3(width*2, 0, 0),
            new Vector3(0, height, 0),
            new Vector3(width, height, 0)
        };
        mesh.vertices = vertices;

        int[] tris = new int[6]
        {
            // lower left triangle
            0, 2, 1,
            // upper right triangle
            2, 3, 1
        };
        mesh.triangles = tris;

        Vector3[] normals = new Vector3[4]
        {
            -Vector3.forward,
            -Vector3.forward,
            -Vector3.forward,
            -Vector3.forward
        };
        mesh.normals = normals;

        Vector2[] uv = new Vector2[4]
        {
            new Vector2(0, 0),
            new Vector2(1, 0),
            new Vector2(0, 1),
            new Vector2(1, 1)
        };
        mesh.uv = uv;

        meshFilter.mesh = mesh;
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
    }
}
