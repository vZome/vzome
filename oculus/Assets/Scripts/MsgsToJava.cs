using System;
using UnityEngine;


[Serializable]
public struct SimpleAction
{
    public string action;
}

[Serializable]
public struct MovedInstance
{
    public string action;
    public string id;
    public Vector3 position;
    public Quaternion rotation;
}