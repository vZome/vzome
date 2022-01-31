package com.vzome.core.render;

public interface RealZomeScaling
{
    double VZOME_BLUE_DIAMETER = 2.0d;
    
    //  nominal ball diameter in rZome: .700 in
    //  plastic shrinkage in rZome production: .994
    //    so actual ball diameter = .6958
    double RZOME_BLUE_DIAMETER_INCHES = 0.6958d;
    
    double RZOME_BLUE_DIAMETER_CM = 1.7673d;
    
    double RZOME_INCH_SCALING = RZOME_BLUE_DIAMETER_INCHES / VZOME_BLUE_DIAMETER;
    
    double RZOME_CM_SCALING = RZOME_BLUE_DIAMETER_CM / VZOME_BLUE_DIAMETER;
    
    double RZOME_MM_SCALING = RZOME_CM_SCALING * 10d;
    
    // This scale factor corresponds to a vZome model that uses a long blue as the radius of a ball.
    //  norm squared of diameter in vZome: 1967.87  => diameter == 44.36
    double VZOME_STRUT_MODEL_BALL_DIAMETER = 44.36d;

    double VZOME_STRUT_MODEL_INCH_SCALING = RZOME_BLUE_DIAMETER_INCHES / VZOME_STRUT_MODEL_BALL_DIAMETER;
}
