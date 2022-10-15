package com.vzome.desktop.controller;

import com.vzome.core.math.RealVector;

public interface OrbitSnapper
{
    RealVector snapZ( RealVector zIn );
    
    /**
     * Snap the Y-axis.
     * @param zOut already-snapped Z-axis
     * @param yIn
     * @return
     */
    RealVector snapY( RealVector zOut, RealVector yIn );
}