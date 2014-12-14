
//(c) Copyright 2006, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

public interface Version
{
    String formatIsSupported = "true";
    
/*

TODO browser ala TextWrangler; use with a summary view for filechecker

TODO e8 applications

TODO H4 symm on segments

TODO - fix pentagonal prismatic symmetry

TODO - fix Affine600Cell command

TODO - migrate BuildAnchoredSegment to StrutCreation, if possible

TODO - fix QuaternionSymmetry (migration); use Wythoff if possible

TODO - migrate H4Polytope to Polytope4d, if possible

TODO - migrate B4Polytope to Polytope4d, if possible

TODO - fix all the problems showing up in old attachments

TODO - background thread for MouseTool actions


TODO - change LengthModel getXml() next time I make a new format version

TODO - arbitrary lengths

TODO - remove derivation model, no-op AddConstruction

TODO -  length and direction panels out of sync after dir dialog removal of selected dir


- TODO complete length panel

- TODO complete heptagon symmetry

- TODO complete panel orientation support

- TODO finish GA implementation

- TODO fix quaternion symmetry command

    */

	int BUILD_NUMBER = 0;

	int SVN_REVISION = 9999;

    String edition = "vZome";

    String label = "4.0, build 0"; } /* 

- added logsFolder .mkdir()

- corrections for history and selection state after failed commands

- fixed internal error when panel command had < 3 vertices

- Preferences/vZome/Shapes checked for VEF models

- preferences ordered correctly, now supported in the right Mac folder

- strut colors now come from symmetry controller

- initial work on controller for custom lengths (algebraic numbers)

- custom colors now preserved across style and symmetry changes

- octahedral black now double length (Fixes #14)

    String label = "3.0 beta 9"; } /* 

- adding "Build With This" contextual command

- showing length in properties contextual command

- renamed Version.stable as Version.formatIsSupported

- fixed RunZomodScript

- restored two-sided panels; some prelim work on differential two-sided rendering (e.g. transparency on back)

    String label = "3.0 beta 8"; } /* 

- added scale display to strut length panel

- fixed picking for contextual menus

- (disabled) beginning on length display in strut length panel

- refactored and fixed ZoneChooserTrackball: created PreviewStrut, ZoneVectorBall, and ManifestationPicker

- fixed load of RunZomicScript cmd

- can load files with "heptagon" field as well as "heptagon6"

- fixed load of 2.x files with tetrahedral symm, quaternion symm, H4 polytope commands

- made AbstractCommand .setFixedAttributes() more O-O

    String label = "3.0 beta 7"; } /* 

- stereo now switchable and persistent in notes pages, and effective scaling is right

- restored System menu

- restored oriented struts

- restored stereo capability, but hardwired

    String label = "3.0 beta 6"; } /* 

- experimented with StrutGeometry to eliminate the scaling (division)

- StL exporter for Sam... may be finished if panels are correctly oriented

- (work in progress) developerExtras to reverse panels, render panels one-sided

- developerExtras for H_4 rotations, IxT, TxT quaternion symmetries

- refactored to remove static INSTANCE variables
    - problem remains with CommandQuaternionSymmetry load, parsing symm names
    
- added heptagon, heptagon6 fields, and initial support for documents over the heptagon6 field

   String label = "3.0 beta 5 + REV 401"; } /*   NOT RELEASED!
 
- initial work on support for Zome interchange format

- orbit panel contextual menu

- orbit panel now more forgiving for fast clicks, uses LeftMouseDragAdapter

- cmd-opt-A as keyboard shortcut for selectNeighbors
    
- added cross-product command

    String label = "3.0 beta 5"; } /* 

- direction and length panels now in sync after file open

- direction panel "single" state stored in the file

- no more RuntimeException for cancel of file dialog

    String label = "3.0 beta 4 + REV 395"; } /*   NOT RELEASED!

- added Breakpoint command, with support in redoAll

    String label = "3.0 beta 4"; } /*
    
- improved logging (more robust) on Windows

    String label = "3.0 beta 3 + REV 392"; } /*   NOT RELEASED!
    
- attempted to prevent spurious new document when launched with an "openDocument" AppleEvent or JNLP equivalent

    String label = "3.0 beta 3 + REV 391"; } /*   NOT RELEASED!

- fixed (hopefully) Brian's "save as JPEG" bug on Windows

- added (developer-mode only) OpenGL ES exporter

- fixed hash on length for strut shapes

- removed unused right-click menu items: mark strut, create direction

    String label = "3.0 beta 3 + REV 390"; } /*

- fixed bug: save after open of model with pending redos

- fixed bug: extra "vZome/" in prefs proto file path

- fixed bug: saving orbit sizes correctly, with half sizes supported

    String label = "3.0 beta 3 + REV 389"; } /*

- "Save Default", new == newFromTemplate if prototype exists

- newFromTemplate never shows migration dialogs

- improved NewLengthPanel:
    mouse wheel support; better event architecture; XML load/save in legacy format;
    still no arbitrary length support

- added Dodecagon red orbit

- coloring for trackball models

- NewLengthPanel

- logging error when Polyhedron.Face size smaller than getVertex() index

- fixed duplicate "internal error" dialogs: no reportError() from DeferredEdit.redo()

- all redo() methods now throw Command.Failure

- fixed green axis rotation for rootTwo, rootThree

- fixed Connector "show properties" to display with field metadata
    
    String label = "3.0 beta 3"; } /*

- down to just 3 failures on ZomeGurus files from Brendo!

- fixed XmlSaveFormat.groupingDoneInSelection(), added XmlSaveFormat.groupingRecursive()

- fixed default Projection to use wFirst

- AlgebraicField .inflateTo4d to always make a copy

- fixed VefToModel to inflateTo4d

- added RunZomicScript, RunZomodScript commands

- added LoadVEF edit, supporting scale & offset

- SymmetryAxisChange loads symm axis as 4D; commands that use it projectTo3D if necessary

- migrating symm axis from 2.0 format always creates a setSymmetry cmd, even if the cmd is a 4d cmd

- XmlSaveFormat .parseSegment() makes a 4d start if the start is null and the end is 4d

- SymmetryController ignores unknown dirs on load if they have no prototype

- better uniqueness for color names for auto orbits

- added logging for auto orbits in SymmetryController

- storing automatic directions in the file (see Weaire-Phelan.vZome)

- made FileChecker, LoggingErrorChannel toplevel classes

- added AbstractCommand .quaternionsNeedShifting() to address discrepancies in quaternionFailures.vZome-files

- now can checkAllFiles with ".vZome-files" files

- fixed the loadXml loop stopping before redoing the last edit

- fixed the premature deselect bug with a synch block

- supporting interim file format 4.0.0, with SelectManifestation/polygon

- added stats summary at end of checkAllFiles

- fixed problem with SetItemColor during "no-render" operation

- fixed problem with static CommandRotate reused across documents

- enabled deferred redo opening of 2.0 files

- fixed intersection problems by normalizing line & plane normals to axis prototypes

- fixed *Rotated4D constructions to use projectTo3d

- supporting old interim 2.1.0 format (just 8 models!)

- fixed missing addEdit() in CommandEdit .loadAndPerform()

- fixed Projection.Default to ignore wFirst parameter

- more logging: Platform

- migrating CommandObliquePentagon to AffinePentagon

- log warning when stripping non=zero W during XmlSaveFormat.parseAlgebraicVector()

- fixed Projection to add "wFirst" param to projectImage()

- added AbstractCommand .setQuaternion()

- EditorController .loadXml() no longer needs to redo everything: all DeferredEdits;
    2.0 file migration is now using DeferredEdits also; CommandEdit is now normalized to perform()
    like any other UndoableEdit, and migration is pushed inside setXml() where it belongs
    
- normalized error reporting inside DeferredEdit .redo()
    
- added UndoableEdit .perform(), for logic common to edit creation and edit load

- fixed equality comparison for setSymmetryCenter and setSymmetryAxis

- fixed transpose/conjugate bug in Quaternion leftMultiply()

- added "threeD" parameter to XmlSaveFormat .loadCommandAttributes()

- fixed B4Group index problems (partially?)

- migrated octahedral and icosahedral trackball models

- removed generic W==0 stripping from XmlSaveFormat.parseAlgebraicVector()

- added capability to migrate CommandEdits to non-CommandEdits

- added "checkAllFiles" property, to run headless test of all files in a folder

- fixed show/hide when running headless (no rendering)

- fixed VEF import to handle zero balls correctly (different from no balls section)

- rendered turquoise strut for default shapes

- first attempt at change-of-basis command, exploring the workflow for parameterized transformations

   - adding math/algebra/oo, to address the need for Gauss-Jordan reduction of matrices over algebraic fields

- bug fix: using Projection.Default when there's no QuaternionProjection

- bug fix: AffinePentagon was missing Arrays.equals

    
    String label = "3.0 beta 2"; } /*

- selection after symmetry commands is complete again

- quaternion added to importVEF command, for E_8

- open pre-2.1.2 in compatible mode w.r.t. ChangeSelection

- fixed bug in select-neighbors (replay)

- fixed bug in show-properties

- implemented simple cases for line-line intersection

- debugging tools:

    + deferred.redo when opening files
    
    + display history edit number, on redo failure
    
    - "set breakpoint": stop when indicated object is created
    
    + "set breakpoint": stop when edit number is reached (before redo) (no UI yet)
    
    - XML display, on redo failure or breakpoint
    
    - look at breakpoint object on breakpoint
    
    + "redo all"


- fixed line-line intersection equality check (though not ill-conditioning)
       
- fixed orbit / length panels to handle auto directions correctly

- fixed rootTwo symm ops (wrong field)

- fixed orbit graphic for octahedral systems

- better trackball for dodecagonal

        "3.0 beta 1"; } /*
       
- added cinnamon, spruce zones; adjusted sand zone length

- migrated CommandEdits to compact format

- fixed AffinePentagon NPE

- restored "conjugate" command, now for all order-2 fields (may be wrong)

- completed some rational vector port work

- fixed F4 symmetry, and now it works for all fields
M      math/src/org/vorthmann/zome/math/symmetry/F4Group.java

- finished XML read for migrated 2.1 model history
M      editor/src/org/vorthmann/zome/editor/XmlSaveFormat.java

- fixed VRML custom color export (from 2.1.2)
M      application/src/org/vorthmann/zome/export/vrml/VRMLExporter.java

- fixed default strut geometry
M      shapes/src/org/vorthmann/zome/parts/DefaultStrutGeometry.java
M      math/src/org/vorthmann/zome/algebra/PentagonField.java
M      math/src/org/vorthmann/zome/algebra/RootThreeField.java
M      math/src/org/vorthmann/zome/algebra/RootTwoField.java
M      math/src/org/vorthmann/zome/algebra/AlgebraicField.java

- scalable VEF strut models
- fixed VEF import and export bugs

- added stack trace for caught exception
M      org.vorthmann.j3d/src/org/vorthmann/ui/DefaultController.java

A      shapes/src/org/vorthmann/zome/parts/dodecagon3d/*

A      shapes/src/org/vorthmann/zome/parts/octahedralFast/*.vef

A      shapes/src/org/vorthmann/zome/parts/rootTwoBig/*.vef

A      shapes/src/org/vorthmann/zome/parts/octahedral/*

A      shapes/src/org/vorthmann/zome/parts/rootTwoSmall

A      shapes/src/org/vorthmann/zome/parts/rootThreeOctaSmall

A      shapes/src/org/vorthmann/zome/parts/rootTwo/*

- made brown orbit default enabled in rootTwo field
M      math/src/org/vorthmann/zome/math/symmetry/OctahedralSymmetry.java

M      math/src/org/vorthmann/zome/math/RootTwoField.java

M      math/src/org/vorthmann/zome/math/Polyhedron.java

M      math/src/org/vorthmann/zome/math/VefParser.java

- "new model" offers field choice
M      userInterface/src/org/vorthmann/zome/ui/ApplicationUI.java
M      userInterface/src/org/vorthmann/zome/ui/DocumentFrame.java

- reusable Eclipse launcher
A      userInterface/modeler-3.0.launch

M      commands/src/org/vorthmann/zome/commands/CommandImportVEFData.java
A      application/src/org/vorthmann/zome/app/impl/ExportedVEFShapes.java
M      application/src/org/vorthmann/zome/app/impl/LessonController.java
M      application/src/org/vorthmann/zome/app/impl/DefaultApplication.java
A      application/src/org/vorthmann/zome/app/impl/ExportedVEFStrutGeometry.java
M      application/src/org/vorthmann/zome/app/impl/ModeledShapes.java
M      application/src/org/vorthmann/zome/app/impl/EditorController.java
M      application/src/org/vorthmann/zome/export/vef/VefExporter.java
A      application/src/org/vorthmann/zome/export/vef/PartGeometryExporter.java

- scaled down OctahedralShapes connectors
M      viewing/src/org/vorthmann/zome/viewing/OctahedralShapes.java

- added VEF import error messages (Ezra's suggestion)
M      math/src/org/vorthmann/zome/math/VefParser.java

- fixed group / ungroup to support recursion

- POV-Ray export supports setColor

        "3.0 alpha 6"; } /*

- read and write panel selections

        "3.0 alpha 5"; } /*

- view per notes page

        "3.0 alpha 4"; } /*

- initial "notes" support

- logging to vZomeLog.log files

        "3.0 alpha 3"; } /*

- initial "notes" support

- logging to vZomeLog.log files

- POV-Ray exporter restored

- demo of label capability

- autoFormatConversion preference

- life size VRML export

- set color works

        "3.0 alpha 2"; } /*

- separated "rationalVectors" format from "actionHistory" format

- rootThree field, and decagon symmetry
        
        "3.0 alpha 1"; } /*

- turquoise in octahedral

- support for generalized algebraic fields

- set color
      
        "2.1"; } /*

- B4, F4 polytopes uniform in root-two system

        "2.1 Beta 19"; } /*

- fixed line-line command load

        "2.1 Beta 18"; } /*

- line-plane with 3 points and a strut

- b4, f4 polytopes in root-two system

- preference for show directions graphically

- line-line intersection

        "2.1 Beta 17"; } /*

- fixed quit menu item

- added secondLife export

- added color to linedrawing export

- doing painter's algorithm for linedrawing

- refactored Gosset enumeration

- added recent docs (in progress)

- added affine transform command

- fixed group load bug

        "2.1 Beta 16"; } /*
        
- added Construct menu

- added affinePentagon command

- added F4 polytopes

- fixed axial symm for octa system

        "2.1 Beta 15"; } /*
        
- added JoinPoints ala Henri Piccioto

- fixed selectNeighbors: ignore hidden

- fixed selectManifestation: save serialization state

- showHidden command

- show properties

- moved "graphical orbits" to the menu

- single is the default for build orbits

- added A4 polytopes

        "2.1 Beta 14"; } /*
        
- fixed symmetry load bug (unnamed directions)

- fixed symmetry command save bug (parameters not saved)

- disabled dead commands

- reorg rootTwo styles

- migrated parts models

- rolling up selection edits

- invert selection command

- new from template
        
- added VEF export

        "2.1 Beta 13"; } /*
        
- fixed ViewModel load (parseBoolean)

- fixed rootTwo system

        "2.1 Beta 12"; } /*
        
- restored SVG / PDF export

- antialiasing

- fixed jagged intersections

- fixed B4Polytope command save

- some ZomeCAD import work

        "2.1 Beta 11"; } /*

- saving views, symm settings

- fixed H4 polytope save

- prelim. work on Zomic open
        
- view bookmark controls
        
- list/graphical orbit view choice

- size.canvas works

- prelim work on marquee selection

        "2.1 Beta 10"; } /*

- show origin, show symm center

- per-orbit length control

- noButtonImages option
      
        "2.1 Beta 9"; } /*

- new symmetry controls
      
        "2.1 Beta 8"; } /*

- new orbit control
      
        "2.1 Beta 7"; } /*
        
- strict MVC: removing Application, Document interfaces; removing DocumentImpl
        
- added blue/black octahedral orbit
        
- fixed octahedral symmetry save
        
- import VEF with divisors

- reinstated length panel

- DefaultController
      
        "2.1 Beta 6"; } /*
        
- view bookmarks
        
- work on Gosset polytopes

- initial work on AlgebraicNumber, AlgebraicField, RationalMatrix
      
        "2.1 Beta 5"; } /*
        
- direction control restored
        
- B4 polytopes

- group / ungroup selection

- lemon => sulfur

- 10-cube generation
      
        "2.1 Beta 4"; } /*
        
- fixed abstract symm save bug
        
- doing printStackTrace to find George/ChrisP bug
      
        "2.1 Beta 3"; } /*
        
- fixed roottwo octa, tetra symm
        
- fixed preview strut bug
        
- added red, purple to octahedral

- added multiselect.with.shift preference

- fixed octahedral style bug

        "2.1 Beta 2"; } /*
        
- compact file format
        
- undoable selection changes

- fixed hide

- added .off export

- fixed translate

        "2.1 Beta 1"; } /*

- massive MVC refactoring

- strut builder trackball starts uniformly

- fixed spurious strut builds

- fixed "save again" bug

- ask to save changes on close or quit

- symmetry axis / center changes undoable, saved


        "2006-05-14"; } /*

- fixed VRML export (except for half of black struts)

- fixed "save as": window rename (forget old title)

- fixed "save as": cannot undercut an open document

- fixed "save as": extension appended to title

- restored Zomic window

- consolidated popup menus

- added custom panel coloring

- zone coloring in popup

- trackball is symmetry group representative

- implemented ModelPanel

- size.canvas preference partially respected


        "2006-04-06"; } /*

- added license JAR for modeler


        "2006-03-31"; } /*
        
- fixed GoldenNumber parse bug

- added custom ball colors (transient)


        "2006-03-05"; } /*

- Version interface hardwires the version string

- reader does not require signed JARs

- command line args passed through as Properties

- Panels are now colored as a pastel version of the color of the corresponding normal strut, rather than all having the same color

- VEF files can now have faces, which will be manifested as panels when possible

*/
