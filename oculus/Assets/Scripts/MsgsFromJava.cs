using System;
using UnityEngine;

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