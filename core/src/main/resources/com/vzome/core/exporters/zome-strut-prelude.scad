/****************************************************************

  Zome Strut Generator, by Aaron Siegel and Scott Vorthmann
  
  The basic syntax is:
  %%ORBIT%%_strut(size);
  
  For example, to generate a scale 2 strut:
   %%ORBIT%%_strut( 2 );
  
  To generate irregular-length struts, you can use the optional
  (real-valued) scalar parameter. To get a "half" scale-1 strut:
   %%ORBIT%%_strut( 1, scalar = 0.5 );

****************************************************************/

// Remove the "*" preceding this line for a single scale-2 strut.
*%%ORBIT%%_strut( 2 );

// Remove the "*" preceding this line for a pack of 6 identical scale-1 struts.
*for (i = [0:6]) {
    %%ORBIT%%_strut( 1, scalar=1, offsets=i );
}

// Remove the "*" preceding this line for a set of scale-0 to scale-3 struts.
*for (i = [0:3]) {
    %%ORBIT%%_strut( i, scalar=1, offsets=i );
}


// Core reusable module

module zome_strut( tip_vertex, fixed_vertices, floating_vertices, faces, bottom_face_normal, size, scalar = 1.0, offsets = 0 )
{    
    // Raw data corresponds to size = 1, so
    // scale by a factor of phi^(size - 1).
        
    shifted_tip_vertex = tip_vertex * irrational^(size - 1) * scalar;
    shift_vector = shifted_tip_vertex - tip_vertex;
    shifted_vertices = [ for (vertex = floating_vertices) vertex + shift_vector ];
        
    if ($zome_debug) {
        for (vertex = shifted_vertices) {
            color("green")
            translate(vertex)
            sphere(0.1, $fn = 32);
        }
    }
    
    // Now rotate to lie flat on the xy-plane
    rotation_axis = cross( bottom_face_normal, [ 0, 0, -1 ] );
    rotation_angle = acos( bottom_face_normal * [ 0, 0, -1 ] );

    offset = cross( tip_vertex, bottom_face_normal );
    unit_offset = offset / norm(offset);

    rotate( rotation_angle, rotation_axis )

    translate( unit_offset * 7 * offsets )

    polyhedron( concat( fixed_vertices, shifted_vertices ), faces );
}

$zome_debug = false;

// Strut data exported from vZome, https://vzome.com, by Scott Vorthmann

