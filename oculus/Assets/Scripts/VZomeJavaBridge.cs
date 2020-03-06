using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.UI;
using Unity.Jobs;
using Unity.Collections;

public class VZomeJavaBridge : MonoBehaviour
{
    public GameObject group;
    public GameObject template;
    public Text label;
    public string url;

    private IDictionary<string, Mesh> meshes = new Dictionary<string, Mesh>();

    public void LoadVZome()
    {
        AndroidJavaClass jc = new AndroidJavaClass( "com.unity3d.player.UnityPlayer" );
        AndroidJavaObject adapter = new AndroidJavaObject( "com.vzome.unity.Adapter", jc, "JavaCallbacks" );

        UnityEngine.Object[] shapes = Resources.LoadAll( "Shapes", typeof(TextAsset) );
        foreach ( var vef in shapes )
        {
            Debug.Log( "%%%%%%%%%%%%%% loaded shape VEF " + vef.name );
            adapter .Call( "registerShape", vef.name, ((TextAsset) vef).text );
        }

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
        Debug .Log( "%%%%%%%%%%%%%% DefineMesh from Java: " + json );
        Shape shape = JsonUtility.FromJson<Shape>(json);
        Debug.Log( "%%%%%%%%%%%%%% DefineMesh: shape deserialized" );
        Mesh mesh = shape .ToMesh();
        Debug.Log( "%%%%%%%%%%%%%% DefineMesh: mesh created: " + mesh.vertices[0] );
        meshes .Add( shape .id, mesh );
    }

    void CreateGameObject( string json )
    { 
        Instance instance = JsonUtility.FromJson<Instance>(json);
        Debug.Log( "%%%%%%%%%%%%%% CreateGameObject from Java: " + instance.id );
        GameObject copy = Instantiate( template );
        Transform xform = copy .GetComponent<Transform>();
        xform .position = instance .position;
        xform .rotation = instance .rotation;
        MeshRenderer meshRenderer = copy .AddComponent<MeshRenderer>();
        meshRenderer.sharedMaterial = new Material(Shader.Find("Standard"));
        Color color;
        ColorUtility .TryParseHtmlString( instance .color, out color );
        meshRenderer.sharedMaterial .color = color;
        MeshFilter meshFilter = copy .AddComponent<MeshFilter>();
        meshFilter.mesh = meshes[ instance .shape ];
        xform .SetParent( group .transform );
        Debug.Log( "%%%%%%%%%%%%%% CreateGameObject done!" );
    }

    void DeleteGameObject( string json )
    { 
        Debug.Log( "%%%%%%%%%%%%%% DeleteGameObject from Java: " + json );
    }

    [Serializable]
    public class Shape
    {
        public string id;
        public Vector3[] tvertices;
        public Vector3[] normals;
        public int[] triangles;

        public Mesh ToMesh()
        {
            Mesh mesh = new Mesh();
            mesh.vertices = this.tvertices;
            mesh.triangles = this.triangles;
            mesh.normals = this.normals;
            return mesh;
        }
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
