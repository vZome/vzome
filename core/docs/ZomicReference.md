
# Introduction

This is a very brief description of the Zomic language.
Zomic is designed to allow the concise description of vZome model constructions.  A Zomic program effectively "draws" a model in 3D, by accumulating Zome struts or equivalent moves, rather than using absolute coordinates as you would see in 2D vector graphics. 

This reference is organized around a formal grammar for Zomic, with brief but complete descriptions of each language feature.
First, however, it is best to understand the execution model for Zomic, described in the next section.

# Virtual Machine

The Zomic virtual machine (or "VM" for short) operates on a virtual Zome model (a collection of struts and connector balls), and effects changes to the model through strut-building statements.
The action of these strut statements is adjusted according to four state variables in the VM:

Variable | Explanation
---------|-----------
location | A position in the virtual Zome space.  There may or may not be a connector ball in the model at that location.  A strut statement always starts at the current location, and sets the location to the end of the new strut.  The initial location is the origin, (0,0,0).
orientation | A linear mapping of the Zome axes.  Mathematically, this can be treated as the composition of a rotation around one of the 31 axes, and possibly a mirror reflection through one of the axes.  Either or both may be the identity mapping, of course.  The orientation mapping is applied to the axis of any strut statement before that statement is executed on the model.  The initial orientation is the identity mapping.
scale | A "magnification" to be applied to strut sizes.  The scale factor is applied for each strut statement, while computing the vector offset to add to the location variable.   The initial scale is one.
build mode | A "switch" that can be on or off.  When on, strut statements cause struts (and usually connector balls) to be added to the model.  When off, strut statements have no effect on the model, but the location variable is still affected.  Build mode is on by default.

Each of these variables can be modified independently of the others, and they can even be saved and restored independently.

# Zomic Grammar

The grammar is formatted as block quotes.
Grammar nonterminal symbols are shown in *italics*.
Keywords and literals are in **bold**.
Parentheses indicate grouping, "*?*" indicates an optional element,
"*+*" indicates one-or-more repetitions,
and "|" separates choices.


The following tokens are used throughout the grammar:

>*DIGIT* : **0**..**9**
>
>*INT* : *DIGIT*+
>
>*SIGNED_INT* : *(* **+** | **-** *)?* *INT*

Comments use the C++ style, both forms:

~~~
// a comment that goes to the end of the line

/* a comment that does not */
~~~

A Zomic program is just a series of statements.

> *compound_statement* : **{** *statement+* **}**
> 
> *statement* :
> > *direct_statement*
> > 
> > | *nested_statement*
> > 
> > | *compound_statement*

## Direct statements

Direct statements are non-nested statements that directly affect one of
the four VM variables: location, orientation, scale, or build/move mode.

>*direct_statement*
>
> >: *strut_statement*
> >
> >| *rotate_statement*
> >
> >| *scale_statement*
> >
> >| *build_statement*
> >
> >| *move_statement*

### Strut statement

> *strut_statement* : *size\_expr length\_expr?* **half***?* *axis\_expr*

If the build-mode VM variable is on, a strut statement makes a strut starting at the current location (VM variable), and sets the location to the end of the new strut.
Connector balls at each end are added automatically, as necessary.

If the build-mode variable is off, the location is adjusted in the same fashion, but no struts or connectors balls are added to the model.

Before the action is performed, the following adjustments are made:

- the stated strut size is adjusted by the current scale VM variable.
- the stated axis is mapped according to the current orientation VM variable.

The optional length expression allows struts of arbitrary length to be constructed.
The scaled strut size is multiplied by the indicated number before the build or move occurs.

The optional "half" modifier builds or moves by a half-strut length, and is allowed only for green and blue axes.

#### Size expression

> *size_expr*
> 
> >: **size** *SIGNED_INT*
> >
> >| **short**
> >
> >| **medium**
> >
> >| **long**

A size expression reflects an absolute scale for Zome struts.
The **short**,**medium**, and **long**
sizes correspond to the standard sizes in the original Zometool kits.
Modern Zometool kits have no **long** struts, and have super-short struts instead.

The **size** values are simply the powers of phi, with the unit length
(```size 0```) defined as the diameter of the Zome
connector ball in the blue direction.
In other words, a ```size 0 blue``` strut appears as two balls "kissing".
This implies that ```size 4``` is exactly equivalent to ```medium```.
Size values can be negative, to produce smaller strut lengths.

#### Length expression

>*length_expr*
> >
> >: *SIGNED_INT SIGNED_INT*?

A length expression allows strut sizes that are not simple powers of phi.
The two integers together form a "golden number", of the form A\*phi+B, where A is the optional second integer, and B is the first integer.
If the second integer is missing, therefore, the length multiplier is just an integer, 0\*phi + B.
If the length expression is omitted entirely, the length multiplier is one.

#### Axis expression

An Axis expression indicates the name of one of the fixed "directions" in the Zome system.

> *axis_expr* : *axis_name* *SIGNED_INT*
>
> *axis_name* : **red** | **blue** | **yellow** | **green** |
**orange** | **purple**

~~~
red +0
blue -4
green +22
~~~

The axis numbers use the indexing defined by Will Ackel for his Zomod program.  The axis numbers always have a sign, with ```blue +2``` going in the opposite direction from ```blue -2```.  The absolute values go from 0 to 5 for the red axes, 0 to 9 for the yellow axes, and 0 to 14 for the blue axes.  Green and orange axes are named by combining the numbers from the nearest red and yellow axes, with the red axis contributing the first digit *and* the sign.  Purple axes are named to match the green axes, matched in pairs straddling red axes.
A PDF cut-out paper model for an axis "key" can be found associated with this document.
This is really the only effective way to document the relative orientations of the red, yellow, and blue axes; a table of vectors would be accurate, but essentially useless.

The **orange** and **purple** directions are not (yet) available in the physical Zometool system, but they represent useful directions,
and they can be 3D-printed using Shapeways.
To see the relationships, run the following script (model credit to Fabien Vienne):

~~~
scale -2
repeat 5 // or as many times as you can stand!
{
	short blue +2  blue +2  long blue +2
	
	short blue +5  repeat 2 purple -0
	short blue +5  repeat 2 short red -0
	short blue +5  short green -0
	
	short blue +5  blue +5  long blue +5
	
	short blue +2  repeat 2 yellow -0
	short blue +2  repeat 2 short orange -0
	
	short blue +2
	scale +1
}
~~~

### Scale statement

> *scale_statement* : **scale** *SIGNED_INT (* **(** *length_expr*
**)** *)?*

A scale statement adjusts the scale VM variable, which modifies the sizes of struts and moves accomplished by the strut_statement.

The required integer is interpreted as a power of phi.
The optional *length_expr* between parentheses indicate a further
multiplicative scaling factor, with the same interpretation as used for
strut statements.

### Build statement

> *build_statement* : **build**

The build keyword sets the build-mode VM variable to "on" (building).
Subsequent *strut_statements* will add struts the model.

### Move statement

> *move_statement* : **move**

The move keyword sets the build-mode VM variable to "off" (just moving).
Subsequent *strut_statements* will NOT add struts the model, but
will still adjust the location VM variable.

### Rotate statement

> *rotate_statement* : **rotate** *SIGNED_INT?*  **around** *axis_expr*

The rotate statement adds a rotation to the current orientation VM variable.
The number of "steps" will be the given integer (or 1, if absent) modulo the number of possible rotations for the given axis.
E.g., ```rotate 6 around red +0``` is equivalent
to ```rotate 1 around red +0``` and ```rotate around red +0```.

The sense of the rotation is best stated by example:
```rotate around red +0``` maps ```blue +0``` to ```blue +4```.  Consult the Zomic key PDF to visualize this action.  Note that the sense of the rotation can be reversed by using the opposite axis: ```rotate around red -0``` maps ```blue +4``` to ```blue +0```.

## Nested Statements

Nested statements are of the form *&lt;modifier&gt;* *statement*, where the modifier has some temporary effect while the statement executes.
The statement is often compound ("{"..."}"), but need not be.
Note that a compound statement without a modifier has no implied semantics; it is just a grouping of statements.

> *nested_statement*
> >
> >: *branch_statement*
> >
> >| *from_statement*
> >
> >| *repeat_statement*
> >
> >| *symmetry_statement*
> >
> >| *save_statement*

### Save statement

> *save_statement* :
>
>> **save** *(* **location** | **scale** | **orientation** | **build** | **all** *)* *statement*

A save statement executes the inner (usually compound) statement with "protected" copies of the current VM variables.
Adjustments to the VM variables within the inner statement will not persist after the save statement, becuase the VM variables will be restored to their saved values.

All the VM variables will be protected if **save all** is used.
Otherwise, only the variable stated will be protected.  If two variables need to be saved, the statements can be nested:

~~~
save scale
  save orientation {
    ...
  }
~~~

### From statement

> *from_statement* : **from** *strut_statement*

This statement moves the location as if a strut were built, regardless of the current value of the build-mode VM variable.
The inner statement MUST be a strut statement.

This is shorthand for: ```save build { move``` *strut_statement* ```}```

### Branch statement

The branch statement executes the inner statement and then restores the current location VM variable.
The statement may be compound: "{" ... "}".

> *branch_statement* : **branch** *statement*

This is shorthand for: **save location** *statement*.

### Symmetry statement

This statement applies a symmetry operator to the inner statement.

>*symmetry_statement* :
>
> >**symmetry**
> >
> > >( **around** *axis_expr* | **through** *(* **center** | *SIGNED_INT )* )?
> > >
> > > *statement*

There are four variants: rotational, mirror reflections, central reflections, and icosahedral symmetry, explained in detail below.

#### Rotational Symmetry

~~~
symmetry around red +0 { blue +3 }
~~~

The inner statement is repeated enough times to generate a symmetric result around the stated axis: 2 times for blue, 3 times for yellow, and 5 times for red. A **save orientation** modifier is implicit; the example above is equivalent to:

~~~
symmetry around red +0
	save orientation
		{ blue +3 }
~~~

The location, scale, and build mode are NOT preserved, allowing helical or spiral models, for example.  This can be a bit surprising.  It is recommended that you always use a **branch** modifier around the inner statement, and use **repeat** and **rotate** when you want a helix or a spiral.  The examples above make a helix; you were probably expecting them to work like this example, producing a star shape:

~~~
symmetry around red +0 branch { blue +3 }
~~~

#### Mirror Symmetry

~~~
symmetry through -2 { red -4 }
~~~

The inner statement is repeated after a mirror reflection.
The mirror plane is orthogonal to the indicated blue axis, and passes through the current location.

As with rotation symmetry, mirror symmetry implies **save orientation**, but  does not save the other state variables.
Again, the example above means something different than the more common (and recommended) usage:

~~~
symmetry through -2 branch { red -4 }
~~~

#### Central Symmetry

~~~
symmetry through center { red +5 blue +5 }
~~~

Central symmetry repeats the inner statement twice, reversing all axes the second time, whether in strut statements, symmetry statements, rotate statements, or whatever.

As with the preceding two kinds of symmetry, mirror symmetry implies **save orientation**, but does not save the other state variables.
The example shown above produces a parallelogram.  For an "integral sign" shape, add the **branch** modifier:

~~~
symmetry through center
	branch { red +5 blue +5 }
~~~

#### Icosahedral Symmetry

~~~
symmetry { from long blue +0 short blue +8 }
~~~

Icosahedral symmetry repeats the inner statement sixty times, once for each of the orientations in the symmetry group.
No reflections are applied; this preserves handedness (or "chirality").
Full icosahedral symmetry, with reflections applied as well, can be achieved simply by adding **symmetry through center** before the symmetry statement:

~~~
symmetry through center
   symmetry { from long blue +0 short blue +8 }
~~~

For icosahedral symmetry, a **save all** modifier is implicit.
This means that the original location, orientation, scale, and build mode will be restored before reorienting for each of the 60 executions of the nested statement.  The main effect is that the model will be roughly "spherical" around a central point, the location before the start of the symmetry statement.

### Repeat statement

The repeat statement allows simple iterative execution of the inner statement.
This can be useful for building spirals, helixes, and space-filling structures.

> *repeat_statement* : **repeat** *INT* *statement*
