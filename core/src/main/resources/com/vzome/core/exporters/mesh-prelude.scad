
// Thank you to @frisbfreek from the vZome Discord, who implemented this OpenSCAD.

// %%%%%%%%%%%%%%%%%%%%%%%%%% ADJUST THESE TO SUIT %%%%%%%%%%%%%%%%%%%%%%%%%%%%%

// Resolution of your objects
vertex_resolution = 20;
edge_resolution = 20;

// Final object size (for printing)
obj_size = 100;

// Width of your edges and vertices
edge_radius_min = 0.016; // defines how thick an edge is at the center of the object
edge_radius_max = 0.016; // defines how thick an edge is at the outermost points of the object
radius_exp = 1; // defines how the radius exponentially scales as one moves from the center to the outer points
vertex_radius_adj = 1.6; // defines how big the vertex sphere is relative to the edge size (multiplicative factor)


// %%%%%%%%%%%%%%%% NORMALLY NO NEED TO EDIT BELOW THIS LINE %%%%%%%%%%%%%%%%%%%


