package com.vzome.desktop.controller;

import javax.vecmath.Vector3f;

public interface OrbitSnapper
{
    void snapDirections( Vector3f lookDir, Vector3f upDir );
}