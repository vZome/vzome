# Symmetry System Enhancements

### Problems with the Existing Approach

During development of a new field and its symmetries, or even just development of a new symmetry group in an existing field, there is a time of experimentation, during which the prototype vector and/or unit length of an orbit can evolve.
Even well after release of a particular symmetry system, it may be desirable to adjust the unit length; this occurred with the purple orbit well after its introduction.
That change broke existing Zomic files, which do not record any orbit data.

The main problem with such changes is that they can change the meaning of "createStrut" commands recorded in existing model files.
One obvious goal is this: once recorded, a vZome model file should always open successfully.
This goal is largely achieved by being careful about code changes, but symmetry orbits are easy to record as data rather than code.

Note that "unit length" is not an implicit assumption by the strutCreation command, which records an explicit length independent of the unit length.
The unit length of an orbit only informs the user experience (the length panel, parts panel, etc.) and the rendering of shapes.

### Per-Document State

The best way to minimize the impact of code changes is to limit the assumptions made in a document, and that means to store in the document everything needed to interpret it.
This is impossible in the limit, but very straightforward for obvious cases like "strutCreation".
vZome already stores all "automatic" orbits in the document, whether they are used for strut creation or not.
(An automatic orbit is created whenever vZome renders a vector it has never rendered before, usually to join to balls, or as a symmetry operation.)

Throughout the history of a vZome model document, the set of automatic struts can grow very large.
If any automatic orbit is left un-colored, and is not used for strut creation, there is no value to storing it with the model file.
To remedy this, vZome should replace or enhance the "isAutomatic" property with an "isNeeded" property, or similar.

To support the idea of an evolving symmetry system, vZome can store the prototype vector for any orbit needed for strut creation.
It can also store the unit length, but this is only to inform the user experience, making strut length more convenient and standardized.
The orbit color is similarly only used to inform rendering, but vZome already records it, allowing at least external adjustment of the color.
Eventually, vZome will gain the ability to adjust orbit colors in the UI.

Probably, it would be a good idea to separate the orbit data required to interpret commands (the name and prototype vector) from the data required only to preserve the user experience (color and unit length).
However, that would introduce undue complexity (i.e. referencing) in the file format.
It may be possible (and useful) to separate the in-memory storage of these data.

### Symmetry Groups as Data

One nice enhancement would be to support symmetry systems as entirely data-driven.
This would allow sharing of new symmetry systems without requiring new releases of vZome.

The main requirement would be to record the generators of the group, both as matrices and as corresponding permutations.
Given the generators, the code could easily find the closure, assuming the group was finite.

If nothing else were defined, this could inform a bare-bones symmetry system, with no symmetry tools supported other than the full symmetry and the rotations produced by the generators.

One obvious question is: how would this data be authored?
It is possible to define orthogonal rotations as linear mappings already.
It would be interesting to support definition of custom symmetry tools starting from those maps, being careful about closure, of course.
It is not clear how generation of the permutations would be accomplished.
It is also unclear to what extent vZome relies on the permutations.

Consider an interesting possibility: a user could define "stretched icosahedral" symmetry for the golden field.
This would have to be limited to symmetry tools, so that strut rendering continued to be as before.

Obviously, the end goal would be to capture an entire SymmetryPerspective as data.
There are many aspects to this, however, and it is unlikely that all aspects can be made data-driven.

### Algebraic Fields as Data

Clearly, it is even possible to define algebraic fields as data.
Again, the benefit would be to share new fields without requiring new vZome releases.

For either fields or symmetry systems, vZome could support a mechanism to capture either from a document, a slight enhancement of the current "save template" mechanism.

As with SymmetryPerspective, the ultimate goal would be to capture an entire FieldApplication as data.
This is probably outside the scope of sharing as a vZome model file, however, which really only needs a single SymmetryPerspective.

