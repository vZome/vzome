package org.vorthmann.zome.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.Controller;

public class DocumentMenuBar extends JMenuBar implements PropertyChangeListener
{
    private static final int COMMAND = Platform.getKeyModifierMask();

    private static final int COMMAND_OPTION = COMMAND | InputEvent.ALT_DOWN_MASK;

    private static final int COMMAND_SHIFT = COMMAND | InputEvent.SHIFT_DOWN_MASK;

    private static final int CONTROL = InputEvent.CTRL_DOWN_MASK;

    private static final int CONTROL_OPTION = InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK;

    private static final long serialVersionUID = 1L;

    private final JMenuItem setColorMenuItem, showToolsMenuItem, zomicMenuItem, pythonMenuItem, importVEFItem;

    private final ControlActions actions;

    private final boolean fullPower;

    private final Controller controller;

    public DocumentMenuBar( final Controller controller, ControlActions actions )
    {
        this .controller = controller;
        this .actions = actions;

        controller .addPropertyListener( this );

        String initSystem = controller .getProperty( "symmetry" );

        String fieldName = controller .getProperty( "field.name" );

        // TODO: compute these booleans once in DocumentFrame, and don't recompute here

        boolean readerPreview = controller .propertyIsTrue( "reader.preview" );

        boolean isEditor = controller .userHasEntitlement( "model.edit" ) && ! readerPreview;

        this .fullPower = isEditor; // && controller .userHasEntitlement( "all.tools" );

        boolean developerExtras = controller .userHasEntitlement( "developer.extras" );

        boolean canSave = controller .userHasEntitlement( "save.files" );

        boolean isGolden = "golden" .equals( fieldName );

        boolean isHeptagon = "heptagon" .equals( fieldName );

        boolean isSqrtPhi = "sqrtPhi" .equals( fieldName );

        boolean isRootTwo = "rootTwo" .equals( fieldName );

        boolean isRootThree = "rootThree" .equals( fieldName );

        boolean oldTools = controller .propertyIsTrue( "original.tools" );

        List<String> symmetries = Arrays .asList( controller .getCommandList( "symmetryPerspectives" ) );

        boolean hasIcosahedral = symmetries .contains( "icosahedral" );

        boolean hasAntiprism = false;
        for (String symmName : symmetries) {
            if (symmName.startsWith("antiprism")) {
                hasAntiprism = true;
//                if("antiprism".equals(initSystem)) {
//                    // this is old code that's aparently unnecessary
//                    // but I'll leave it here as a comment for now in case I break something by removing it. - DJH
//                    initSystem = symmName; // effectively appends nSides to initSystem
//                }
                break;
            }
        }

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% File menu

        JMenu menu = new JMenu( "File" );

        if ( fullPower )
        {
            JMenu submenu = new JMenu( "New Model..." );
            submenu.add( enableIf( isEditor || readerPreview, createMenuItem( "Zome (Golden) Field", "new-golden", KeyEvent.VK_N, COMMAND ) ) );

            String[] fieldNames = controller .getCommandList( "fields" );
            for ( String fName : fieldNames ) {
                if ( controller .propertyIsTrue( "enable." + fName + ".field" ) ) {
                    String label = controller .getProperty( "field.label." + fName );
                    submenu .add( createMenuItem( label + " Field", "new-" + fName ) );
                }
            }
            menu.add( submenu );
        }
        else
        {
            menu .add( createMenuItem( "New Model...", "new", KeyEvent.VK_N, COMMAND ) );
        }
        menu .add( createMenuItem( "Open...", "open", KeyEvent.VK_O, COMMAND ) );
        menu .add( createMenuItem( "Open URL...", "openURL" ) );
        menu .add( createMenuItem( "Open As New Model...", "newFromTemplate" ) );
        if ( developerExtras )
            menu .add( createMenuItem( "Open Deferred...", "openDeferringRedo" ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        menu .add( createMenuItem( "Close", "close", KeyEvent.VK_W, COMMAND ) );
        menu .add( enableIf( canSave, createMenuItem( "Save...", "save", KeyEvent.VK_S, COMMAND ) ) );
        menu .add( enableIf( canSave, createMenuItem( "Save As...", "saveAs" ) ) );
        menu .add( enableIf( canSave, createMenuItem( "Save Template...", "saveDefault" ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        JMenu submenu = new JMenu( "Import 3D Mesh..." );
        submenu .add( createMenuItem( "Simple Mesh JSON", "ImportSimpleMeshJson/Quaternion" ) );
        submenu .add( createMenuItem( "Color Mesh JSON", "ImportColoredMeshJson/Quaternion" ) );
        importVEFItem = createMenuItem( "vZome VEF", "LoadVEF/Quaternion" );
        submenu .add( importVEFItem );
        menu .add( submenu );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        submenu = new JMenu( "Export 3D Rendering..." );
        submenu .add( createMenuItem( "Collada DAE", "export.dae" ) );
        submenu .add( createMenuItem( "POV-Ray", "export.pov" ) );
        submenu .add( createMenuItem( "vZome Shapes JSON", "export.shapes" ) );
        submenu .add( createMenuItem( "VRML", "export.vrml" ) );
        menu .add( submenu );
        submenu .setEnabled( fullPower && canSave );

        submenu = new JMenu( "Export 3D Panels..." );
        submenu .add( createMenuItem( "StL (mm)", "export.StL" ) );
        submenu .add( createMenuItem( "OFF", "export.off" ) );
        submenu .add( createMenuItem( "PLY", "export.ply" ) );
        menu .add( submenu );
        submenu = new JMenu( "Export 3D Mesh..." );
        submenu .add( createMenuItem( "Simple Mesh JSON", "export.mesh" ) );
        submenu .add( createMenuItem( "Color Mesh JSON", "export.cmesh" ) );
        submenu .add( createMenuItem( "AutoCAD DXF", "export.dxf" ) );
        if ( controller .userHasEntitlement( "export.pdb" ) )
        {
            submenu .add( createMenuItem( "PDB", "export.pdb" ) );
        }
        if ( controller .userHasEntitlement( "export.seg" ) )
        {
            submenu .add( createMenuItem( "Mark Stock .seg", "export.seg" ) );
        }
        menu .add( submenu );
        submenu .setEnabled( fullPower && canSave );

        if ( developerExtras )
        {
            submenu = new JMenu( "Export Developer Extras..." );
            submenu .add( createMenuItem( "vZome part geometry", "export.partgeom" ) );
            submenu .add( createMenuItem( "vZome history detail", "export.history" ) );
            submenu .add( createMenuItem( "bill of materials", "export.partslist" ) );
            submenu .add( createMenuItem( "STEP", "export.step" ) );
            menu .add( submenu );
            submenu .setEnabled( fullPower && canSave );
        }
        
        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        menu .add( enableIf( canSave, createMenuItem( "Share using GitHub...", "Share" ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        submenu = new JMenu( "Capture Image..." );
        submenu .add( createMenuItem( "JPEG", "capture.jpg" ) );
        submenu .add( createMenuItem( "PNG", "capture.png" ) );
        submenu .add( createMenuItem( "GIF", "capture.gif" ) );
        submenu .add( createMenuItem( "BMP", "capture.bmp" ) );
        menu.add( submenu );

        menu .add( createMenuItem( "Capture Animation...", "capture-animation" ) );

        submenu = new JMenu( "Capture Vector Drawing..." );
        submenu .add( createMenuItem( "PDF", "export2d.pdf" ) );
        submenu .add( createMenuItem( "SVG", "export2d.svg" ) );
        submenu .add( createMenuItem( "Postscript", "export2d.ps" ) );
        submenu .addSeparator();
        submenu .add( enableIf( isEditor, createMenuItem( "Customize...", "snapshot.2d" ) ) );
        menu .add( submenu );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        menu.add( createMenuItem( "Quit", "quit", KeyEvent.VK_Q, COMMAND ) );

        super .add( menu );

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Edit menu

        menu = new JMenu( "Edit" );
        menu .add( withAccelerator( KeyEvent.VK_Z, COMMAND, withAction( "undoRedo", "undo",  new JMenuItem( "Undo" ) ) ) );
        menu .add( withAccelerator( KeyEvent.VK_Y, COMMAND, withAction( "undoRedo", "redo",  new JMenuItem( "Redo" ) ) ) );
        menu .add( withAccelerator( KeyEvent.VK_Z, COMMAND_OPTION, withAction( "undoRedo", "undoAll",  new JMenuItem( "Undo All" ) ) ) );
        menu .add( withAccelerator( KeyEvent.VK_Y, COMMAND_OPTION, withAction( "undoRedo", "redoAll",  new JMenuItem( "Redo All" ) ) ) );
        if ( developerExtras )
        {
            menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            menu .add( withAction( new JMenuItem( "Redo to Edit Number..." ), new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    String number = JOptionPane.showInputDialog( null, "Enter the edit number.", "Set Edit Number",
                            JOptionPane.PLAIN_MESSAGE );
                    controller .getSubController( "undoRedo" ) .actionPerformed( DocumentMenuBar.this, "redoUntilEdit." + number );
                }
            } ) );
        }
        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Cut", ( "cut" ), KeyEvent.VK_X, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Copy", ( "copy" ), KeyEvent.VK_C, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Paste", ( "paste" ), KeyEvent.VK_V, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Delete", ( "Delete" ), KeyEvent.VK_BACK_SPACE, 0 ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Select All", ( "SelectAll" ), KeyEvent.VK_A, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Select Neighbors", ( "SelectNeighbors" ), KeyEvent.VK_A, COMMAND_OPTION ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Invert Selection", ( "InvertSelection" ) ) ) );

        submenu = new JMenu("Select...");
        submenu.add( enableIf( isEditor, createMenuItem( "Balls", ( "AdjustSelectionByClass/selectBalls" ) ) ) );
        submenu.add( enableIf( isEditor, createMenuItem( "Struts", ( "AdjustSelectionByClass/selectStruts" ) ) ) );
        submenu.add( enableIf( isEditor, createMenuItem( "Panels", ( "AdjustSelectionByClass/selectPanels" ) ) ) );
        submenu.add( enableIf( isEditor, createMenuItem( "Automatic Struts", ( "SelectAutomaticStruts" ) ) ) );
        menu.add(submenu);

        submenu = new JMenu("Deselect...");
        submenu.add( enableIf( isEditor, createMenuItem( "Balls", ( "AdjustSelectionByClass/deselectBalls" ) ) ) );
        submenu.add( enableIf( isEditor, createMenuItem( "Struts", ( "AdjustSelectionByClass/deselectStruts" ) ) ) );
        submenu.add( enableIf( isEditor, createMenuItem( "Panels", ( "AdjustSelectionByClass/deselectPanels" ) ) ) );
        submenu.add( enableIf( isEditor, createMenuItem( "All", ( "DeselectAll" ) ) ) );
        menu.add(submenu);

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu.add( enableIf( isEditor, createMenuItem( "Select Half Space", ( "SelectByPlane" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Select by Diameter", ( "SelectByDiameter" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Select by Radius", ( "SelectByRadius" ) ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Group", ( "GroupSelection/group" ), KeyEvent.VK_G, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Ungroup", ( "GroupSelection/ungroup" ), KeyEvent.VK_G, COMMAND_OPTION ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Hide", ( "hideball" ), KeyEvent.VK_H, CONTROL ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Show All Hidden", ( "ShowHidden" ), KeyEvent.VK_H, CONTROL_OPTION ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        this .setColorMenuItem = enableIf( isEditor, createMenuItem( "Set Color...", "setItemColor", KeyEvent.VK_C, COMMAND_SHIFT ) );
        menu .add( this .setColorMenuItem );
        final String MAP_TO_COLOR = "MapToColor/";
        {
            JMenu submenu2 = new JMenu("Set Opacity...");
            final String MAP_TO_TRANSPARENCY = MAP_TO_COLOR + "TransparencyMapper@";
            submenu2 .add( enableIf( isEditor, createMenuItem( "Opaque", MAP_TO_TRANSPARENCY + "255" ) ) );
            submenu2 .add( enableIf( isEditor, createMenuItem( "95%", MAP_TO_TRANSPARENCY + "242" ) ) );
            submenu2 .add( enableIf( isEditor, createMenuItem( "75%", MAP_TO_TRANSPARENCY + "192" ) ) );
            submenu2 .add( enableIf( isEditor, createMenuItem( "50%", MAP_TO_TRANSPARENCY + "127" ) ) );
            submenu2 .add( enableIf( isEditor, createMenuItem( "25%", MAP_TO_TRANSPARENCY + "63" ) ) );
            submenu2 .add( enableIf( isEditor, createMenuItem( "5%", MAP_TO_TRANSPARENCY + "13" ) ) );
            menu.add(submenu2);
        }
        menu .add( enableIf( isEditor, createMenuItem( "Copy Last Selected Color", MAP_TO_COLOR + "CopyLastSelectedColor" ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Reset Colors", MAP_TO_COLOR + "SystemColorMap" ) ) );
        
        if ( developerExtras ) {
            {
                JMenu submenu2 = new JMenu("Color Effects...");
                submenu2 .add( enableIf( isEditor, createMenuItem( "Complement", MAP_TO_COLOR + "ColorComplementor" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "Invert", MAP_TO_COLOR + "ColorInverter" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "Maximize", MAP_TO_COLOR + "ColorMaximizer" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "Soften", MAP_TO_COLOR + "ColorSoftener" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "Darken with Distance", MAP_TO_COLOR + "DarkenWithDistance" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "Darken near Origin", MAP_TO_COLOR + "DarkenNearOrigin" ) ) );
                menu.add(submenu2);
            }
            {
                JMenu submenu2 = new JMenu("Map Colors...");
                submenu2 .add( enableIf( isEditor, createMenuItem( "To Centroid", MAP_TO_COLOR + "RadialCentroidColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "To Direction", MAP_TO_COLOR + "RadialStandardBasisColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "To Canonical Orientation", MAP_TO_COLOR + "CanonicalOrientationColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "To Normal Polarity", MAP_TO_COLOR + "NormalPolarityColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "To Octant", MAP_TO_COLOR + "CentroidByOctantAndDirectionColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "To Coordinate Plane", MAP_TO_COLOR + "CoordinatePlaneColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "System by Centroid", MAP_TO_COLOR + "SystemCentroidColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "Nearest Predefined Orbit", MAP_TO_COLOR + "NearestPredefinedOrbitColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "Nearest Predefined Orbit by Centroid", MAP_TO_COLOR + "CentroidNearestPredefinedOrbitColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "Nearest Special Orbit", MAP_TO_COLOR + "NearestSpecialOrbitColorMap" ) ) );
                submenu2 .add( enableIf( isEditor, createMenuItem( "Nearest Special Orbit by Centroid", MAP_TO_COLOR + "CentroidNearestSpecialOrbitColorMap" ) ) );
                menu.add(submenu2);
            }
        }

        super .add( menu );

        
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Construct menu
        
        menu = new JMenu( "Construct" );

        menu.add( enableIf( isEditor, createMenuItem( "Loop Balls", "JoinPoints/CLOSED_LOOP", KeyEvent.VK_J, COMMAND ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Chain Balls", "JoinPoints/CHAIN_BALLS", KeyEvent.VK_J, COMMAND_OPTION ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Join Balls to Last", "JoinPoints/ALL_TO_LAST" ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Make All Possible Struts", "JoinPoints/ALL_POSSIBLE" ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Panel", ( "panel" ), KeyEvent.VK_P, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Panel/Strut Vertices", ( "ShowVertices" ) ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Panel Normals", ( "ShowNormals" ) ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu.add( enableIf( isEditor, createMenuItem( "Centroid", ( "NewCentroid" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Strut Midpoint", ( "midpoint" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Line-Line Intersection", ( "StrutIntersection" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Line-Plane Intersection", ( "LinePlaneIntersect" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Panel-Panel Projection", ( "PanelPanelIntersection" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Cross Product", ( "CrossProduct" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Normal to Skew Lines", ( "JoinSkewLines" ) ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu.add( enableIf( isEditor, createMenuItem( "Ball At Origin", ( "ShowPoint/origin" ) ) ) );
        if ( controller .propertyIsTrue( "original.tools" ) )
            menu.add( enableIf( isEditor, createMenuItem( "Ball At Symmetry Center", ( "ShowPoint/symmCenter" ) ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu.add( enableIf( isEditor, createMenuItem( "2D Convex Hull", ( "ConvexHull2d" ) ) ) );
        submenu = new JMenu("3D Convex Hull...");
        submenu.add( enableIf( isEditor, createMenuItem( "Complete", ( "ConvexHull3d" ) ) ) );
        submenu.add( enableIf( isEditor, createMenuItem( "Panels Only", ( "ConvexHull3d/onlyPanels" ) ) ) );
        submenu.add( enableIf( isEditor, createMenuItem( "Struts Only", ( "ConvexHull3d/noPanels" ) ) ) );
        menu.add(submenu);

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu.add( enableIf( isEditor, createMenuItem( "Parallelepiped", "Parallelepiped", KeyEvent.VK_P, COMMAND_SHIFT ) ) );
        if ( isGolden ) {
            menu.add( enableIf( isEditor, createMenuItem( "\u03C6 Divide", ( "tauDivide" ) ) ) );
            menu.add( enableIf( isEditor, createMenuItem( "Affine Pentagon", ( "AffinePentagon" ) ) ) );
        } else if ( isHeptagon ) {
            menu.add( enableIf( isEditor, createMenuItem( "1/\u03C3/\u03C1 Subdivisions", ( "HeptagonSubdivision" ) ) ) );
            menu.add( enableIf( isEditor, createMenuItem( "Affine Heptagon", ( "AffineHeptagon" ) ) ) );
        }

        if ( developerExtras ) {
            menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            menu.add( enableIf( isEditor, createMenuItem( "Assert Selection", ( "ValidateSelection" ) ) ) );

            //            menu.add( enableIf( isEditor, createMenuItem( "6-Lattice", getExclusiveAction( "sixLattice" ) ) );
        }

        super .add( menu );


        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Tools menu

        menu = new JMenu( "Tools" );

        if ( oldTools ) {
            menu.add( enableIf( isEditor, createMenuItem( "Set Center", "SymmetryCenterChange" ) ) ); 
            menu.add( enableIf( isEditor, createMenuItem( "Set Axis", "SymmetryAxisChange" ) ) ); 
            menu.addSeparator(); 

            showToolsMenuItem = enableIf( isEditor, createMenuItem( "Show Tools Panel", "showToolsPanel" ) );
            showToolsMenuItem .setEnabled( fullPower );
            menu .add( showToolsMenuItem );

            menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        }
        else
            this .showToolsMenuItem = null;

        Controller symmetryController = controller .getSubController( "symmetry" );
        // TODO: replace legacy commands with default tools
        if ( hasIcosahedral ) {
            menu.add( enableIf( isEditor, createMenuItem( "Icosahedral Symmetry", "icosasymm", symmetryController, KeyEvent.VK_I, COMMAND ) ) );
        }
        if ( developerExtras && isRootThree ) {
            menu.add( enableIf( isEditor, createMenuItem( "Dodecagonal Symmetry", "DodecagonSymmetry", symmetryController, KeyEvent.VK_D, COMMAND ) ) );
        }
        menu.add( enableIf( isEditor, createMenuItem( "Cubic / Octahedral Symmetry", "octasymm", symmetryController, KeyEvent.VK_C, COMMAND_OPTION ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Tetrahedral Symmetry", "tetrasymm", symmetryController, KeyEvent.VK_T, COMMAND_OPTION ) ) );
        if ( oldTools ) {
            menu.add( enableIf( isEditor, createMenuItem( "Axial Symmetry", "axialsymm", symmetryController, KeyEvent.VK_R, COMMAND ) ) );
            menu.add( enableIf( isEditor, createMenuItem( "Mirror Reflection", "mirrorsymm" , symmetryController, KeyEvent.VK_M, COMMAND ) ) );
            menu.add( enableIf( isEditor, createMenuItem( "Translate", "translate", symmetryController, KeyEvent.VK_T, COMMAND ) ) );
        }
        menu.add( enableIf( isEditor, createMenuItem( "Point Reflection", "pointsymm" ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        menu.add( enableIf( isEditor, createMenuItem( "Replace With Panels", "ReplaceWithShape", controller, KeyEvent .CHAR_UNDEFINED, 0 ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        menu .add( enableIf( isEditor, createMenuItem( "Generate Polytope...", "showPolytopesDialog") ) );
        
	menu .add( enableIf( isEditor, createMenuItem( "Generate Polytope (New)...", "showNewPolytopesDialog", KeyEvent.VK_P, COMMAND_OPTION ) ) );
        
        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
        menu .add( enableIf( isEditor, createMenuItem( "Validate Paneled Surface", "Validate2Manifold" ) ) );

        super .add( menu );


        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% System menu

        menu = new JMenu( "System" );
        ButtonGroup group = new ButtonGroup();
        JMenuItem rbMenuItem;
        if ( isSqrtPhi ) {
            rbMenuItem = actions .setMenuAction( "setSymmetry.pentagonal", controller, new JRadioButtonMenuItem( "Pentagonal System" ) );
            rbMenuItem .setSelected( "pentagonal".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }
        if ( hasIcosahedral ) {
            rbMenuItem = actions .setMenuAction( "setSymmetry.icosahedral", controller, new JRadioButtonMenuItem( "Icosahedral System" ) );
            rbMenuItem .setSelected( "icosahedral".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }
        else if ( isHeptagon ) {
            rbMenuItem = actions .setMenuAction( "setSymmetry.heptagonal antiprism corrected", controller, new JRadioButtonMenuItem( "Heptagonal Antiprism System" ) );
            rbMenuItem .setSelected( "heptagonal antiprism corrected".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
            rbMenuItem = actions .setMenuAction( "setSymmetry.triangular antiprism", controller, new JRadioButtonMenuItem( "Triangular Antiprism System" ) );
            rbMenuItem .setSelected( "triangular antiprism".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            // DISABLED until the symmetry group has been properly implemented
            // menu.add( rbMenuItem );
        }
        if ( hasAntiprism ) {
            // Specify the antiprism's nSides here since not all code paths go thru setSymmetrySystem()
            String antiprismName = "antiprism";
            // AntiprismSymmetry takes a PolygonField in it's c'tor
            // so no other field is using AntiprismSymmetry at this time,
            // but I'll add the check anyway in case antiprismSymmetry ever gets generalized to accept other fields
            if(fieldName.startsWith("polygon")) {
                antiprismName += fieldName.substring("polygon".length());
            }
            rbMenuItem = actions .setMenuAction( "setSymmetry." + antiprismName, controller, new JRadioButtonMenuItem( "Antiprism System" ) );
            rbMenuItem .setSelected( initSystem.startsWith("antiprism") );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );        
        }

        rbMenuItem = actions .setMenuAction( "setSymmetry.octahedral", controller, new JRadioButtonMenuItem( "Octahedral System" ) );
        rbMenuItem .setSelected( "octahedral".equals( initSystem ) );
        rbMenuItem .setEnabled( fullPower );
        group.add( rbMenuItem );
        menu.add( rbMenuItem );

        if ( isRootThree )
        {
            rbMenuItem = actions .setMenuAction( "setSymmetry.dodecagonal", controller, new JRadioButtonMenuItem( "Dodecagon System" ) );
            rbMenuItem .setSelected( "dodecagonal".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }
        else if ( isRootTwo )
        {
            rbMenuItem = actions .setMenuAction( "setSymmetry.synestructics", controller, new JRadioButtonMenuItem( "Synestructics System" ) );
            rbMenuItem .setSelected( "synestructics".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        if ( developerExtras )
        {
            JMenuItem wfMenuItem = actions .setMenuAction( "toggleWireframe", controller, new JCheckBoxMenuItem( "Wireframe" ) );
            boolean isWireframe = "true".equals( controller .getProperty( "wireframe" ) );
            wfMenuItem .setSelected( isWireframe );
            menu.add( wfMenuItem );
        }

        menu.add( createMenuItem( "Shapes...", "configureShapes" ) );
        menu.add( createMenuItem( "Directions...", "configureDirections" ) );

        Controller sbController = controller .getSubController( "strutBuilder" );
        JMenuItem cbMenuItem = actions .setMenuAction( "toggleOrbitViews", sbController, new JCheckBoxMenuItem( "Show Directions Graphically" ) );
        boolean setting = "true".equals( sbController .getProperty( "useGraphicalViews" ) );
        cbMenuItem .setSelected( setting );
        menu.add( cbMenuItem );
        cbMenuItem .setEnabled( fullPower );

        final JMenuItem showStrutScalesItem = actions .setMenuAction( "toggleStrutScales", sbController, new JCheckBoxMenuItem( "Show Strut Scales" ) );
        setting = "true" .equals( sbController .getProperty( "showStrutScales" ) );
        showStrutScalesItem .setSelected( setting );
        showStrutScalesItem .setEnabled( fullPower );
        sbController .addPropertyListener( new PropertyChangeListener(){
            @Override
            public void propertyChange( PropertyChangeEvent chg )
            {
                if ( "showStrutScales" .equals( chg .getPropertyName() ) )
                    showStrutScalesItem .setSelected(((Boolean) chg .getNewValue()));
            }} );
        menu .add( showStrutScalesItem );

        //        cbMenuItem = enabler .enableMenuAction( "toggleOneSidedPanels", new JCheckBoxMenuItem( "Show Panels One-sided" ) );
        //        setting = "true".equals( controller.getProperty( "oneSidedPanels" ) );
        //        cbMenuItem.setSelected( setting );
        //        menu.add( cbMenuItem );

        cbMenuItem = actions .setMenuAction( "toggleFrameLabels", controller, new JCheckBoxMenuItem( "Show Frame Labels" ) );
        setting = "true".equals( controller .getProperty( "showFrameLabels" ) );
        cbMenuItem .setSelected( setting );
        menu.add( cbMenuItem );

        cbMenuItem = actions .setMenuAction( "toggleNormals", controller, new JCheckBoxMenuItem( "Show Panel Normals" ) );
        setting = "true".equals( controller .getProperty( "showNormals" ) );
        cbMenuItem .setSelected( setting );
        menu.add( cbMenuItem );

        super.add( menu );

        
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Scripting menu

        menu = new JMenu( "Scripting" );
        menu .setEnabled( fullPower );
        pythonMenuItem = createMenuItem( "Python...", "showPythonWindow" );
        pythonMenuItem .setEnabled( fullPower );
        if ( developerExtras )
            menu .add( pythonMenuItem );
        zomicMenuItem = createMenuItem( "Zomic...", "showZomicWindow" );
        zomicMenuItem .setEnabled( fullPower );
        menu .add( zomicMenuItem );
        super .add( menu );


        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Custom menu

        menu = getCustomMenu( symmetryController );
        if(menu != null) {
            super .add( menu );
        }


        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Help menu
        menu = new JMenu( "Help" );
        menu .add( createMenuItem( "Quick Start...", "openResource-org/vorthmann/zome/content/welcomeDodec.vZome" ) );
        {
            JMenu submenu3d = new JMenu( "Symmetry Starters..." );
            submenu3d .add( createMenuItem( "Icosahedral / Dodecahedral", "newFromResource-com/vzome/starters/symmetry/icosahedral/starter.vZome" ) );
            submenu3d .add( createMenuItem( "Cubic / Octahedral", "newFromResource-com/vzome/starters/symmetry/octahedral/starter.vZome" ) );
            submenu3d .add( createMenuItem( "Cubic / Octahedral " + controller .getProperty( "field.label.sqrt2" ), "newFromResource-com/vzome/starters/symmetry/octahedral/sqrt2/starter.vZome" ) );
            submenu3d .add( createMenuItem( "Tetrahedral", "newFromResource-com/vzome/starters/symmetry/tetrahedral/starter.vZome" ) );
            menu.add( submenu3d );
        }
        {
            JMenu submenu3d = new JMenu( "3D Printing Starters..." );
            submenu3d .add( createMenuItem( "Red-tip Struts", "newFromResource-org/vorthmann/zome/print3d/redStruts/struts-template-enlarged.vZome" ) );
            submenu3d .add( createMenuItem( "Yellow-tip Struts", "newFromResource-org/vorthmann/zome/print3d/yellowStruts/struts-template-enlarged.vZome" ) );
            submenu3d .add( createMenuItem( "Blue-tip Struts", "newFromResource-org/vorthmann/zome/print3d/blueStruts/struts-template-enlarged.vZome" ) );
            menu.add( submenu3d );
        }

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
        menu .add( createMenuItem( "vZome Online (web app)...", "browse-https://vzome.com/app" ) );
        menu .add( createMenuItem( "vZome Home...", "browse-https://vzome.com" ) );
        menu .add( createMenuItem( "Sharing vZome Files Online...", "browse-https://vorth.github.io/vzome-sharing/" ) );
        menu .add( createMenuItem( "vZome Tips on YouTube...", "browse-https://www.youtube.com/c/Vzome" ) );
        {
            JMenu submenu3d = new JMenu( "Social Media" );
            submenu3d .add( createMenuItem( "Blog...", "browse-https://vzome.com/blog" ) );
            submenu3d .add( createMenuItem( "Facebook Page...", "browse-https://www.facebook.com/vZome" ) );
            submenu3d .add( createMenuItem( "Twitter Page...", "browse-https://twitter.com/vZome" ) );
            submenu3d .add( createMenuItem( "Discord Server...", "browse-https://discord.com/invite/vhyFsNAFPS" ) );
            menu.add( submenu3d );
        }
        {
            JMenu submenu3d = new JMenu( "Misc. Online Documentation" );
            submenu3d .add( createMenuItem( "The Direction (Orbit) Triangle...", "browse-https://vzome.com/blog/2019/07/vzome-icosahedral-orbits/" ) );
            submenu3d .add( createMenuItem( "Capturing Vector Graphics...", "browse-https://vzome.com/blog/2018/12/capturing-vector-graphics/" ) );
            submenu3d .add( createMenuItem( "Toolbars for Diehards...", "browse-https://vzome.com/blog/2018/12/toolbars-for-diehards/" ) );
            submenu3d .add( createMenuItem( "Content Workflows...", "browse-https://vzome.com/blog/2018/02/vzome-content-workflows/" ) );
            menu.add( submenu3d );
        }
        {
            JMenu submenu3d = new JMenu( "Other Links" );
            submenu3d .add( createMenuItem( "GitHub Source...", "browse-https://github.com/vZome/vzome" ) );
            submenu3d .add( createMenuItem( "Logo T-Shirt...", "browse-https://www.neatoshop.com/product/vZome-tetrahedron" ) );
            submenu3d .add( createMenuItem( "3D-Printed Parts at Shapeways...", "browse-https://www.shapeways.com/shops/vzome" ) );
            submenu3d .add( createMenuItem( "Models on SketchFab...", "browse-https://sketchfab.com/scottvorthmann" ) );
            submenu3d .add( createMenuItem( "Observable Notebooks...", "browse-https://observablehq.com/collection/@vorth/vzome" ) );
            menu.add( submenu3d );
        }
        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
        menu .add( createMenuItem( "About vZome...", "showAbout" ) );
        super .add( menu );
    }

    @Override
    public void propertyChange( PropertyChangeEvent e )
    {
        if ( "editor.mode" .equals( e .getPropertyName() ) )
        {
            String mode = (String) e .getNewValue();
            if ( "article" .equals( mode ) )
            {
                setColorMenuItem .setEnabled( false );
                if ( showToolsMenuItem != null )
                    showToolsMenuItem .setEnabled( false );
                pythonMenuItem .setEnabled( false );
                zomicMenuItem .setEnabled( false );
                importVEFItem .setEnabled( false );
            }
            else
            {                   
                setColorMenuItem .setEnabled( true );
                if ( showToolsMenuItem != null )
                    showToolsMenuItem .setEnabled( fullPower );
                pythonMenuItem .setEnabled( fullPower );
                zomicMenuItem .setEnabled( fullPower );
                importVEFItem .setEnabled( fullPower );
            }
        }
    }

    private JMenuItem enableIf( boolean enable, JMenuItem control )
    {
        control .setEnabled( enable );
        return control;
    }

    private JMenuItem withAction( JMenuItem menuItem, ActionListener action )
    {
        menuItem .setEnabled( true );
        menuItem .addActionListener( action );
        return menuItem;
    }

    private JMenuItem withAction( String controllerName, String action, JMenuItem menuItem )
    {
        menuItem .setActionCommand( action );
        Controller subc = this .controller;
        if ( controllerName != null )
            subc = subc .getSubController( controllerName );
        if ( subc != null ) {
            menuItem .setEnabled( true );
            menuItem .addActionListener( new ControllerActionListener( subc ) );
        }
        else
            menuItem .setEnabled( false );
        return menuItem;
    }

    private JMenuItem withAccelerator( int key, int modifiers, JMenuItem menuItem )
    {
        menuItem .setAccelerator( KeyStroke.getKeyStroke( key, modifiers ) );
        return menuItem;
    }

    private JMenuItem createMenuItem( String label, String command )
    {
        return createMenuItem( label, command, KeyEvent .CHAR_UNDEFINED, 0 );
    }

    private JMenuItem createMenuItem( String label, String command, int key, int modifiers )
    {
        return this .createMenuItem( label, command, this .controller, key, modifiers );
    }

    private JMenuItem createMenuItem( String label, String command, Controller controller, int key, int modifiers )
    {
        JMenuItem menuItem = actions .setMenuAction( command, controller, new JMenuItem( label ) );
        menuItem .setEnabled( true );
        if ( key != KeyEvent .CHAR_UNDEFINED )
            menuItem .setAccelerator( KeyStroke.getKeyStroke( key, modifiers ) );
        return menuItem;
    }

    /**
     * @return JMenu with custom menu items added or null if no custom menu items are defined.
     *
     * Creates a custom menu from a properties formatted file in the user's preferences folder.
     *
     * There is no regard for how the custom commands will be processed at runtime.
     * The menu items are added unconditionally and are not context sensitive.
     * For example, they are loaded without regard for whether they will work with a particular model's field.
     *
     * The keys read from the file become the commands to be processed and the values become the menu item's text.
     *
     * If the menu item's text contains a caret immediately followed by a number (e.g. "^6")
     * then that number with the COMMAND key modifier are enabled as a menu accelerator.
     * The caret and number are removed from the menu item's text.
     * The caret can occur anywhere in the menu item's text.
     * Sorting of the menu items occurs before the menu accelerators are processed,
     * so they can optionally be used to sort the menu items depending on their placement in the string.
     * No checks are made for duplicate menu accelerator assignments, but Java handles them reasonably if they occur.

     For example, the file might contain these entries:

     assertSelection = Assert Selection
     sixLattice = 6-Lattice ^6
     somePrereleasedFeature = TODO: Unreleased Feature
     test.pick.cube = ^8 First Octant

     */
    private JMenu getCustomMenu( Controller controller ) {
        File customMenuFile = new File(Platform.getPreferencesFolder(), "vZomeCustomMenu.properties");
        try {
            if (customMenuFile.exists()) {
                Properties customMenuItems = new Properties();
                customMenuItems.load(new FileInputStream(customMenuFile));
                if (!customMenuItems.isEmpty()) {
                    // Properties collection is unaffected by the order the items occur in the file
                    //  so we'll sort them by menuText for consistent placement on the menu.
                    // Note that a case insensitive sort is specified.
                    // Menu items in the properties file can be prefixed with numbers to generate a particular display order
                    Map<String, String> sortedMenuCommands  = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    for (Object key : customMenuItems.keySet()) {
                        String actionName = key.toString();
                        String menuText = customMenuItems.getProperty(actionName).trim();
                        if(!menuText.isEmpty()) {
                            // Swapping keys for elements
                            // as we move from the Properties collection to the sortedMenuCommands
                            sortedMenuCommands.put(menuText, actionName);
                        }
                    }
                    if (! sortedMenuCommands.isEmpty() ) {
                        JMenu menu = new JMenu("Custom");
                        StringBuilder logMsg = new StringBuilder();
                        for (String label : sortedMenuCommands.keySet()) {
                            JMenuItem menuItem = null;
                            String command = sortedMenuCommands.get(label);
                            logMsg .append("\n  ")
                                .append(command)
                                .append(" = ")
                                .append(label);
                            int caretPos = label.indexOf("^");
                            if(caretPos > -1 && caretPos < label.length()-1) {
                                int key = label.charAt(caretPos + 1);
                                if(key >= KeyEvent.VK_0 && key <= KeyEvent.VK_9) {
                                    label = label.replace("^" + (key - KeyEvent.VK_0), "").trim();
                                    menuItem = createMenuItem(label, command, controller, key, COMMAND);
                                }                                
                            }
                            if(menuItem == null) {
                                menuItem = createMenuItem(label, command, controller, KeyEvent .CHAR_UNDEFINED, 0 );
                            }
                            menu.add(menuItem);
                        }
                        Logger.getLogger( getClass().getName() ) .log( Level.INFO, "custom menu items: " + logMsg.toString() );
                        return menu;
                    }
                }
            }
        } catch (Exception ex) {
            log("Error loading custom menu from " + customMenuFile.getAbsolutePath(), ex);
        }
        return null;
    }

    private void log(String msg, Exception ex) {
        // TODO: cleanup this error reporting... Maybe pass this info back to the controller via reportError()
        Logger.getLogger(getClass().getName()).log(Level.WARNING, msg, ex);
    }
}
