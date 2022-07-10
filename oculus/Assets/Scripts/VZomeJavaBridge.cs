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

    public AudioClip snapReleaseSound; 
    public AudioClip modelReadySound; 
    public AudioSource source;

    public Transform canvas;
    public Dropdown dropdown;
    int selectedFile;

    private const string VZOME_PATH = "/mnt/sdcard/Oculus/vZome/";
    private const string VZOME_EXTENSION = ".vzome";
    private List<string> fileNames = new List<string>();
    private List<string> paths = new List<string>();

    private AndroidJavaClass adapterClass;
    private AndroidJavaObject adapter;

    void Start()
    {
        adapterClass = new AndroidJavaClass( "com.vzome.unity.Adapter" );
        adapterClass .CallStatic( "initialize", new AndroidJavaClass( "com.unity3d.player.UnityPlayer" ) );

        UnityEngine.Object[] shapes = Resources.LoadAll( "Shapes", typeof(TextAsset) );
        foreach ( var vef in shapes )
        {
            Debug.Log( "%%%%%%%%%%%%%% loaded shape VEF " + vef.name );
            adapterClass .CallStatic( "registerShape", vef.name, ((TextAsset) vef).text );
        }

        foreach (string path in Directory .GetFiles( VZOME_PATH ) )
        {
            string ext = Path .GetExtension( path ) .ToLower();
            if ( VZOME_EXTENSION .Equals( ext ) ) {
                string filename = Path .GetFileNameWithoutExtension( path );
                fileNames .Add( filename );
                paths .Add( path );
            }
        }
        dropdown .AddOptions( fileNames );
        DropdownIndexChanged( 0 );
    }

    public void DropdownIndexChanged( int index )
    {
        selectedFile = index;
    }

    public void LoadVZome()
    {
        template = this .transform .Find( "vZomeTemplate" ) .gameObject;

        Transform panel = canvas .Find( "Panel" );
        GameObject messages = panel .Find( "JavaMessages" ) .gameObject;

        msgText = messages .GetComponent<Text>();
        msgText .text = "Loading file: " + fileNames[ selectedFile ];
        LoadVZomeJob job = new LoadVZomeJob();

        string filePath = paths[ selectedFile ];
        job .pathN = new NativeArray<byte>( filePath.Length, Allocator.Temp );
        job .pathN .CopyFrom( Encoding.ASCII.GetBytes( filePath ) );

        string anchor = this .name;
        job .objectNameN = new NativeArray<byte>( anchor.Length, Allocator.Temp );
        job .objectNameN .CopyFrom( Encoding.ASCII.GetBytes( anchor ) );

        JobHandle jh = job .Schedule();
        Debug.Log( "%%%%%%%%%%%%%% LoadVZomeJob scheduled. " );
    }

    void AdapterReady( string path )
    { 
        adapter = adapterClass .CallStatic<AndroidJavaObject>( "getAdapter", path );
        Debug.Log( "%%%%%%%%%%%%%% AdapterReady got the adapter: " + adapter .ToString() );
        source .PlayOneShot( modelReadySound, 1f );
    }

    void SetLabelText( string message )
    { 
        Debug.Log( "%%%%%%%%%%%%%% SetLabelText from Java: " + message );
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
        Shape shape = JsonUtility.FromJson<Shape>(json);
        Mesh mesh = shape .ToMesh();
        Debug.Log( "%%%%%%%%%%%%%% DefineMesh: mesh created: " + mesh.vertices[0] );
        meshes .Add( shape .id, mesh );
    }

    void CreateGameObject( string json )
    { 
        Instance instance = JsonUtility.FromJson<Instance>(json);
        GameObject copy = Instantiate( template );
        MeshRenderer meshRenderer = copy .AddComponent<MeshRenderer>();

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

        MeshCollider collider = copy .GetComponent<MeshCollider>();
        collider .sharedMesh = meshFilter .mesh;

        GrabSnapper snapper = copy .GetComponent<GrabSnapper>();
        snapper .vZomeId = instance.id;
        snapper .bridge = this;

        copy .transform .localPosition = instance .position;
        copy .transform .localRotation = instance .rotation;
        copy .transform .SetParent( this .transform, false );

        instances .Add( instance .id, copy );
        Debug.Log( "%%%%%%%%%%%%%% CreateGameObject from Java: " + instance.id );
    }

    void ChangeObjectColor( string json )
    { 
        Instance instance = JsonUtility.FromJson<Instance>(json);
        Debug.Log( "%%%%%%%%%%%%%% ChangeObjectColor from Java: " + instance .id );
        GameObject toChange = instances[ instance .id ];
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
        toChange .GetComponent<MeshRenderer>() .sharedMaterial = material;
    }

    void DeleteGameObject( string json )
    { 
        Deletion deletion = JsonUtility.FromJson<Deletion>(json);
        Debug.Log( "%%%%%%%%%%%%%% DeleteGameObject from Java: " + deletion .id );
        GameObject toDelete = instances[ deletion .id ];
        // Failing to null these out causes a bug; the Destroy() method is too voracious.
        toDelete .GetComponent<MeshCollider>() .sharedMesh = null;
        toDelete .GetComponent<MeshRenderer>() .sharedMaterial = null;
        toDelete .GetComponent<MeshFilter>() .mesh = null;
        instances .Remove( deletion .id );
        Destroy( toDelete );
    }

    // These methods require the adapter

    private void DoModelAction( object obj )
    {
        if ( adapter != null ) {
            string json = JsonUtility .ToJson( obj );
            Debug .Log( "%%%%%%%%%%%%%% Calling doAction: " + json );
            adapter .Call( "doAction", json );
        }
    }

    public void DoSimpleAction( String action )
    {
        if ( adapter != null ) {
            Debug .Log( "%%%%%%%%%%%%%% Calling DoSimpleAction: " + action );
            SimpleAction pa = new SimpleAction();
            pa .action = action;
            DoModelAction( pa );
        }
    }

    public void ObjectMoved( String id, GameObject go )
    {
        // At this point, the local transform is apparently left over
        //  from the OVRGrabber, and does NOT reflect the true parent
        //  transform.  It is still a global transform.
        //  We have to use "true" here to make it truly relative again,
        //  before we send it across to Java.
        go .transform .SetParent( this .transform, true );
        if ( adapter != null ) {
            source .PlayOneShot( snapReleaseSound, 1f );

            MovedInstance mi = new MovedInstance();
            mi .position = go .transform .localPosition;
            mi .rotation = go .transform .localRotation;
            mi .id = id;
            mi .action = "MoveObject";
            DoModelAction( mi );
        }
    }
}

