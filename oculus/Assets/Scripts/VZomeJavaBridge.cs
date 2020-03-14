using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using UnityEngine;
using UnityEngine.UI;
using Unity.Jobs;
using Unity.Collections;

public class VZomeJavaBridge : MonoBehaviour
{
    private IDictionary<string, Mesh> meshes = new Dictionary<string, Mesh>();
    private IDictionary<string, Material> materials = new Dictionary<string, Material>();
    private IDictionary<string, GameObject> instances = new Dictionary<string, GameObject>();

    private Text msgText;
    private GameObject template;

    public Transform canvas;
    public Dropdown dropdown;
    string selectedFile;

    private const string VZOME_PATH = "/mnt/sdcard/Oculus/vZome/";
    private List<string> fileNames = new List<string>();

    void Start()
    {
        foreach (string path in Directory .GetFiles( VZOME_PATH ) )
        {
            string filename = Path .GetFileName( path );
            fileNames .Add( filename );
        }
        dropdown .AddOptions( fileNames );
    }

    public void DropdownIndexChanged( int index )
    {
        selectedFile = fileNames[ index ];
    }

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

        template = this .transform .Find( "vZomeTemplate" ) .gameObject;

        GameObject messages = canvas .Find( "JavaMessages" ) .gameObject;

        msgText = messages .GetComponent<Text>();
        msgText .text = "Loading file: " + selectedFile;
        Debug.Log( "%%%%%%%%%%%%%% new LoadVZomeJob... " );
        LoadVZomeJob job = new LoadVZomeJob();

        string filePath = VZOME_PATH + selectedFile;
        job.urlBytes = new NativeArray<byte>( filePath.Length, Allocator.Temp );
        job.urlBytes .CopyFrom( Encoding.ASCII.GetBytes( filePath ) );

        string anchor = this .name;
        job.anchorBytes = new NativeArray<byte>( anchor.Length, Allocator.Temp );
        job.anchorBytes .CopyFrom( Encoding.ASCII.GetBytes( anchor ) );

        JobHandle jh = job .Schedule();
        Debug.Log( "%%%%%%%%%%%%%% job .Scheduled. " );
    }

    void SetLabelText( string message )
    { 
        Debug.Log( "%%%%%%%%%%%%%% SetLabelText from java: " + message );
        msgText .text = message;
    }

    void LogInfo( string message )
    { 
        Debug.Log( "%%%%%%%%%%%%%% From Java: " + message );
        msgText .text = message;
    }

    void LogException( string message )
    { 
        Debug.LogError( "%%%%%%%%%%%%%% From Java: " + message );
        msgText .text = message;
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
        MeshRenderer meshRenderer = copy .AddComponent<MeshRenderer>();

        Debug.Log( "&&&&& instance .color: " + instance.color );
        Material material;
        if ( materials .ContainsKey( instance .color ) ) {
            material = materials[ instance .color ];
        } else {
            material = new Material( Shader.Find("Standard") );
            Debug.Log( "&&&&& material created for " + instance.color );
            Color color;
            ColorUtility .TryParseHtmlString( instance .color, out color );
            material .color = color;
            materials .Add( instance .color, material );
        }
        meshRenderer.sharedMaterial = material;

        MeshFilter meshFilter = copy .AddComponent<MeshFilter>();
        meshFilter.mesh = meshes[ instance .shape ];

        copy .transform .localPosition = instance .position;
        copy .transform .localRotation = instance .rotation;
        copy .transform .SetParent( this .transform, false );

        instances .Add( instance .id, copy );
        Debug.Log( "%%%%%%%%%%%%%% CreateGameObject done!" );
    }

    void DeleteGameObject( string json )
    { 
        Deletion deletion = JsonUtility.FromJson<Deletion>(json);
        Debug.Log( "%%%%%%%%%%%%%% DeleteGameObject from Java: " + deletion .id );
        Destroy( instances[ deletion .id ] );
        instances .Remove( deletion .id );
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
    public struct Deletion
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
}

public struct LoadVZomeJob : IJob
{
    public NativeArray<byte> urlBytes;
    public NativeArray<byte> anchorBytes;
    
    public void Execute()
    {
        Debug.Log( "%%%%%%%%%%%%%% AndroidJNI.AttachCurrentThread... " );
        AndroidJNI.AttachCurrentThread();

        string url = Encoding.ASCII.GetString( urlBytes.ToArray() );
        urlBytes .Dispose();

        string anchor = Encoding.ASCII.GetString( anchorBytes.ToArray() );
        anchorBytes .Dispose();

        AndroidJavaClass jc = new AndroidJavaClass( "com.unity3d.player.UnityPlayer" );
        AndroidJavaObject adapter = new AndroidJavaObject( "com.vzome.unity.Adapter", jc, anchor );
        Debug.Log( "%%%%%%%%%%%%%% adapter created successfully " );
        Debug.Log( "%%%%%%%%%%%%%% attempting to open: " + url );
        adapter .Call<AndroidJavaObject>( "loadFile", url );
        Debug.Log( "%%%%%%%%%%%%%% loadFile returned" );
    }
}
