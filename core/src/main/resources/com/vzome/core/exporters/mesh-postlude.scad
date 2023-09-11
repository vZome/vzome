


function to_matrix(A) = [for (i = [0:len(A)-1]) [A[i]]];
function to_vector(A) = [for (i = [0:len(A)-1]) A[i][0]];
function identity(dim) = [for (i = [0:dim-1]) [for (j = [0:dim-1]) i==j ? 1 : 0]];
// A should be in matrix form
function transpose(A) = [for (j = [0:len(A[0])-1]) [for(i = [0:len(A)-1]) A[i][j]]];
// A and B should be in matrix form
function dot(A, B) = (transpose(A)*B)[0][0];

// Assuming you're at origin, get 3x3 rotation matrix going from P to Q.
// P and Q must be unit vectors, so please pre-normalize.
function get_rotation(P, Q) = let (PQ = P+Q, PdotQ = dot(P, Q)) PdotQ == -1 ? -identity(len(P)) : identity(len(P)) - ((PQ)/(1+PdotQ))*transpose(PQ) + 2*Q*transpose(P);

// Attach transform vector T to 3x3 rotation matrix R
// Outputs an affine transform 3x4 matrix that can be used by multmatrix()
function get_affine(R, T) = [for (i = [0:2]) [for (j = [0:3]) (j == 3 ? T[i] : R[i][j])]];

// Return 3x4 affine transform matrix M that correctly rotates and moves a cylinder.
// Assume cylinder starts at default position (origin in the direction of [0,0,1]) and
// ends up at P in the direction toward Q.
function get_affine_edge_transform(P, Q) = get_affine(get_rotation(to_matrix([0, 0, 1]), to_matrix((Q-P)/norm(Q-P))), P);

// Applies a rotation matrix that rotates from [0,0,1] (which is the default direction for a cylinder) to [point2]-[point1].
module rotate_edge(point1, point2) {
    M = get_affine_edge_transform(point1, point2);
    multmatrix(M)
    scale([1, 1, norm(point1-point2)])
    children();
}


vn = len(vertices);

// Re-size the 3D object down to unit max coordinates
max_radius_temp = max([for (i = [0:vn-1]) max(vertices[i])]);
vertices_3d = [for (i = [0:vn-1]) vertices[i]/max_radius_temp];

// Needed for vertex/edge radius and color computations
max_object_radius = max([for (i = [0:vn-1]) norm(vertices_3d[i])]);
// Get radius relative to the max object radius
function get_radius(point) = let (ratio = min(1, norm(point)/max_object_radius)) edge_radius_min + (edge_radius_max - edge_radius_min)*ratio^radius_exp;

obj_size_ratio = obj_size/(2+2*edge_radius_max*vertex_radius_adj);
scale([obj_size_ratio, obj_size_ratio, obj_size_ratio])
union() {
    // Create spheres for vertices
    for (i = [0:vn-1]) {
        vertex_3d = vertices_3d[i];
        translate(vertex_3d)
        sphere(r=get_radius(vertex_3d)*vertex_radius_adj, $fn=vertex_resolution);
    }
    
    // Create cylinders for edges
    for (edge = edges) {
        vertex1_3d = vertices_3d[edge[0]];
        vertex2_3d = vertices_3d[edge[1]];
        rotate_edge(vertex1_3d, vertex2_3d)
        cylinder(r1=get_radius(vertex1_3d), r2=get_radius(vertex2_3d), $fn=edge_resolution); // default h=1
    }
}