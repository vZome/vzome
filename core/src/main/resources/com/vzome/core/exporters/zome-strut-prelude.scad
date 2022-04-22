/****************************************************************

  Zome Strut Generator
  
  The basic syntax is:
  zome_strut(size);
  
  For example, to generate a scale 2 strut:
  zome_strut( 2 );
  
  To generate irregular-length struts, you can use the optional
  (real-valued) scalar parameter. To get a "half" scale-1 strut:
  zome_strut( 1, scalar = 0.5 );

****************************************************************/

// Remove the "*" preceding this line for a single scale-2 strut.
*zome_strut( 2 );

// Remove the "*" preceding this line for a pack of struts.
*zome_strut_multipack(3, 1);

// Remove the "*" preceding this line for a set of scale-0 to scale-3 struts.
*for (i = [0:3]) {
    translate([-10 * i, 0, 0])
    zome_strut( i );
}


// Main module

module zome_strut( size, scalar = 1.0 ) {
    
    // Raw data corresponds to size = 1, so
    // scale by a factor of phi^(size - 1).
    
    post_rotation_angle = 0;
    
    shifted_tip_vertex = tip_vertex * irrational^(size - 1) * scalar;
    shift_vector = shifted_tip_vertex - tip_vertex;
    shifted_vertices = [ for (vertex = floating_vertices) vertex + shift_vector ];
        
    if ($zome_debug) {
        scale(physical_scale)
        for (vertex = shifted_vertices) {
            color("green")
            translate(vertex)
            sphere(0.1, $fn = 32);
        }
    }
    
    // Now rotate to lie flat on the xy-plane
    //xyProjection = [shifted_tip_vertex.x, shifted_tip_vertex.y, 0];
    //normal = [-xyProjection.y, xyProjection.x, 0];
    //angle = atan(shifted_tip_vertex.z / sqrt(shifted_tip_vertex.x^2 + shifted_tip_vertex.y^2));
    
    //rotate(post_rotation_angle, xyProjection)

    polyhedron( concat( fixed_vertices, shifted_vertices ), faces );
    
}

// Multipack

module zome_strut_multipack(count, orbit, size, scalar = 1.0) {
    
    tip_vertex = gf_point_to_real(orbit[2]);
    normal = [-tip_vertex.y, tip_vertex.x];
    unit_normal = normal / norm(normal);

    for (n = [0:count-1]) {
        
        translate(unit_normal * 7 * n)
        zome_strut(orbit, size, scalar);
        
    }
    
}

$zome_debug = false;

// Strut data (exported from vZome by Scott Vorthmann)



