

from build123d import *

obj = Part()
sphere = Sphere( sphereRadius )
for vertex in vertices:
    obj += Pos(vertex) * sphere

for edge in edges:
    # Make a line path
    line = Line( vertices[edge[0]], vertices[edge[1]])
    # Make a plane origin at the beginning ( line @ 0) of the line,
    # and looking along it (line % 0)
    plane = Plane(origin = line @ 0, z_dir=line % 0)
    # Create the profile to sweep along the line.
    profile = plane * Circle( stickRadius )
    obj += sweep(profile,path = line)

    
if "show_object" in locals():
    # Show in CQ-Editor
    show_object( obj.wrapped )
elif __name__ == "__main__":
    obj.export_step( "vZome-export.step" )
