package org.vorthmann.zome.ui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
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
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.Controller;

public class DocumentMenuBar extends JMenuBar implements PropertyChangeListener
{
	private static final int COMMAND = Platform.getKeyModifierMask();
	
	private static final int COMMAND_OPTION = COMMAND | InputEvent.ALT_MASK;

	private static final int COMMAND_SHIFT = COMMAND | InputEvent.SHIFT_MASK;

	private static final int CONTROL = InputEvent.CTRL_MASK;
	
	private static final int CONTROL_OPTION = InputEvent.CTRL_MASK | InputEvent.ALT_MASK;

    private static final long serialVersionUID = 1L;
	
    private final JMenuItem setColorMenuItem, showToolsMenuItem, zomicMenuItem, pythonMenuItem, importVEFItem;

	private final ControlActions actions;
	
	private final boolean fullPower;

	public DocumentMenuBar( final Controller controller, ControlActions actions )
	{
		this .actions = actions;

		controller .addPropertyListener( this );

        String initSystem = controller .getProperty( "symmetry" );

        String fieldName = controller .getProperty( "field.name" );

        // TODO: compute these booleans once in DocumentFrame, and don't recompute here
        
        boolean readerPreview = controller .propertyIsTrue( "reader.preview" );

        boolean isEditor = controller .userHasEntitlement( "model.edit" ) && ! readerPreview;

        this .fullPower = isEditor; // && controller .userHasEntitlement( "all.tools" );

        boolean developerExtras = controller .userHasEntitlement( "developer.extras" );

        boolean enable4d = developerExtras || ( fullPower && controller .userHasEntitlement( "4d.symmetries" ) );

        boolean metaModels = developerExtras || ( fullPower && controller .userHasEntitlement( "meta.models" ) );
        
        boolean canSave = controller .userHasEntitlement( "save.files" );

        boolean isGolden = "golden" .equals( fieldName );
        
        boolean isHeptagon = "heptagon" .equals( fieldName );

        boolean isSnubDodec = "snubDodec" .equals( fieldName );

        boolean isRootTwo = "rootTwo" .equals( fieldName );

        boolean isRootThree = "rootThree" .equals( fieldName );
        
        boolean oldTools = controller .propertyIsTrue( "original.tools" );

        // ----------------------------------------- File menu

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
            menu.add( createMenuItem( "New Model...", "new", KeyEvent.VK_N, COMMAND ) );
        }
        menu.add( createMenuItem( "Open...", "open", KeyEvent.VK_O, COMMAND ) );
        menu.add( createMenuItem( "Open URL...", "openURL" ) );
        menu.add( createMenuItem( "Open As New Model...", "newFromTemplate" ) );
        if ( developerExtras )
            menu.add( createMenuItem( "Open Deferred...", "openDeferringRedo" ) );
        menu.addSeparator();
        menu.add( createMenuItem( "Close", "close", KeyEvent.VK_W, COMMAND ) );
        menu .add( enableIf( canSave, createMenuItem( "Save...", "save", KeyEvent.VK_S, COMMAND ) ) );
        menu .add( enableIf( canSave, createMenuItem( "Save As...", "saveAs" ) ) );
        menu .add( enableIf( canSave, createMenuItem( "Save Template...", "saveDefault" ) ) );

        menu.addSeparator();

        importVEFItem = createMenuItem( "Import vZome VEF...", "import.vef" );
        menu .add( importVEFItem );

        JMenu submenu = new JMenu( "Export 3D Rendering..." );
        submenu .add( createMenuItem( "Collada DAE", "export.dae" ) );
        submenu .add( createMenuItem( "POV-Ray", "export.pov" ) );
        submenu .add( createMenuItem( "WebGL JSON", "export.json" ) );
        submenu .add( createMenuItem( "VRML", "export.vrml" ) );
        menu.add( submenu );
        submenu .setEnabled( fullPower && canSave );

        submenu = new JMenu( "Export 3D Geometry..." );
        submenu .add( createMenuItem( "StL", "export.StL" ) );
        submenu .add( createMenuItem( "PLY", "export.ply" ) );
        submenu .add( createMenuItem( "vZome VEF", "export.vef" ) );
        submenu .add( createMenuItem( "STEP", "export.step" ) );
        submenu .add( createMenuItem( "OFF", "export.off" ) );
        submenu .add( createMenuItem( "AutoCAD DXF", "export.dxf" ) );
        if ( controller .userHasEntitlement( "export.pdb" ) )
        {
            submenu .add( createMenuItem( "PDB", "export.pdb" ) );
        }
        if ( controller .userHasEntitlement( "export.seg" ) )
        {
            submenu .add( createMenuItem( "Mark Stock .seg", "export.seg" ) );
        }
        menu.add( submenu );
        submenu .setEnabled( fullPower && canSave );

        if ( developerExtras )
        {
            submenu = new JMenu( "Export Developer Extras..." );
            submenu .addSeparator();
            submenu.add( createMenuItem( "Second Life", "export.2life" ) );
            submenu.add( createMenuItem( "Maximum XYZ", "export.size" ) );
            submenu.add( createMenuItem( "vZome part geometry", "export.partgeom" ) );
            submenu.add( createMenuItem( "vZome history detail", "export.history" ) );
            submenu.add( createMenuItem( "bill of materials", "export.partslist" ) );
            menu.add( submenu );
            submenu .setEnabled( fullPower && canSave );
        }

        menu.addSeparator();

//        if ( controller .userHasEntitlement( "export.zomespace" ) )
//        {
//            submenu = new JMenu( "Export Article..." );
//            submenu .add( createMenuItem( "Zomespace", "export.zomespace" ) );
//            menu.add( submenu );
//            submenu .setEnabled( fullPower && canSave );
//        }

        submenu = new JMenu( "Capture Image..." );
        submenu .add( createMenuItem( "JPEG", "capture.jpg" ) );
        submenu .add( createMenuItem( "PNG", "capture.png" ) );
        submenu .add( createMenuItem( "GIF", "capture.gif" ) );
        submenu .add( createMenuItem( "BMP", "capture.bmp" ) );
        menu.add( submenu );

        menu .add( createMenuItem( "Capture Animation...", "capture-animation" ) );

        menu.add( enableIf( isEditor, createMenuItem( "Capture PDF or SVG...", "snapshot.2d" ) ) );

        menu.addSeparator();
        menu.add( createMenuItem( "Quit", "quit", KeyEvent.VK_Q, COMMAND ) );

        super .add( menu );

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Edit menu

        menu = new JMenu( "Edit" );
        menu.add( enableIf( isEditor, createMenuItem( "Undo", "undo", KeyEvent.VK_Z, COMMAND ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Redo", ( "redo" ), KeyEvent.VK_Y, COMMAND ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Undo All", ( "undoAll" ), KeyEvent.VK_Z, COMMAND_OPTION ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Redo All", ( "redoAll" ), KeyEvent.VK_Y, COMMAND_OPTION ) ) );
        if ( developerExtras )
        {
            menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            menu .add( createMenuItem( "Undo To Breakpoint", ( "undoToBreakpoint" ), KeyEvent.VK_B, COMMAND_SHIFT ) );
            menu .add( createMenuItem( "Redo To Breakpoint", ( "redoToBreakpoint" ), KeyEvent.VK_B, COMMAND_OPTION ) );
            menu .add( createMenuItem( "Set Breakpoint", ( "setBreakpoint" ), KeyEvent.VK_B, COMMAND ) );
            menu .add( createMenuItem( "Redo to Edit Number...", "redoUntilEdit" ) );
        }
        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Cut", ( "cut" ), KeyEvent.VK_X, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Copy", ( "copy" ), KeyEvent.VK_C, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Paste", ( "paste" ), KeyEvent.VK_V, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Delete", ( "delete" ), KeyEvent.VK_BACK_SPACE, 0 ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Select All", ( "selectAll" ), KeyEvent.VK_A, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Select Neighbors", ( "selectNeighbors" ), KeyEvent.VK_A, COMMAND_OPTION ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Invert Selection", ( "invertSelection" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Deselect Balls", ( "unselectBalls" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Deselect Struts and Panels", ( "unselectStruts" ) ) ) );
		
		submenu = new JMenu("Select...");
		submenu.add( enableIf( isEditor, createMenuItem( "All Collinear", ( "SelectCollinear" ) ) ) );
        submenu.add( enableIf( isEditor, createMenuItem( "Parallel Struts", ( "SelectParallelStruts" ) ) ) );
		submenu.add( enableIf( isEditor, createMenuItem( "Automatic Struts", ( "SelectAutomaticStruts" ) ) ) );
        // submenu .add( createMenuItem( "First Octant", getExclusiveAction( "test.pick.cube" ) ) );
		menu.add(submenu);

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Group", ( "group" ), KeyEvent.VK_G, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Ungroup", ( "ungroup" ), KeyEvent.VK_G, COMMAND_OPTION ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Hide", ( "hideball" ), KeyEvent.VK_H, CONTROL ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Show All Hidden", ( "showHidden" ), KeyEvent.VK_H, CONTROL_OPTION ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        setColorMenuItem = enableIf( isEditor, createMenuItem( "Set Color...", "setItemColor" ) );
        menu .add( setColorMenuItem );

        super .add( menu );

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Construct menu
        menu = new JMenu( "Construct" );

        menu.add( enableIf( isEditor, createMenuItem( "Loop Balls", "joinballs", KeyEvent.VK_J, COMMAND ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Chain Balls", "chainBalls", KeyEvent.VK_J, COMMAND_OPTION ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Join Balls to Last", "joinBallsAllToLast" ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Make All Possible Struts", "joinBallsAllPossible" ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( enableIf( isEditor, createMenuItem( "Panel", ( "panel" ), KeyEvent.VK_P, COMMAND ) ) );
        menu .add( enableIf( isEditor, createMenuItem( "Panel/Strut Vertices", ( "showVertices" ) ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu.add( enableIf( isEditor, createMenuItem( "Centroid", ( "centroid" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Strut Midpoint", ( "midpoint" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Line-Line Intersection", ( "lineLineIntersect" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Line-Plane Intersection", ( "linePlaneIntersect" ) ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Cross Product", ( "crossProduct" ) ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu.add( enableIf( isEditor, createMenuItem( "Ball At Origin", ( "ballAtOrigin" ) ) ) );
        if ( controller .propertyIsTrue( "original.tools" ) )
        	menu.add( enableIf( isEditor, createMenuItem( "Ball At Symmetry Center", ( "ballAtSymmCenter" ) ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//        menu.add( enableIf( isEditor, createMenuItem( "Affine Transform All", getExclusiveAction( "affineTransformAll" ) ) );
//        menuItem = enableIf( isEditor, createMenuItem( "Conjugate", getExclusiveAction( "conjugate" ) );
        if ( metaModels ) {
        	menu .add(  createMenuItem( "Meta-model", ( "realizeMetaParts" ) ) );
        }
        if ( isGolden ) {
            menu.add( enableIf( isEditor, createMenuItem( "\u03C6 Divide", ( "tauDivide" ) ) ) );
            menu.add( enableIf( isEditor, createMenuItem( "Affine Pentagon", ( "affinePentagon" ) ) ) );
        } else if ( isHeptagon ) {
            menu.add( enableIf( isEditor, createMenuItem( "1/\u03C3/\u03C1 Subdivisions", ( "heptagonDivide" ) ) ) );
            menu.add( enableIf( isEditor, createMenuItem( "Affine Heptagon", ( "affineHeptagon" ) ) ) );
        }

        if ( developerExtras ) {
            menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            menu.add( enableIf( isEditor, createMenuItem( "Assert Selection", ( "assertSelection" ) ) ) );

//            menu.add( enableIf( isEditor, createMenuItem( "6-Lattice", getExclusiveAction( "sixLattice" ) ) );
        }

        super .add( menu );


        // ----------------------------------------- Tools menu

        menu = new JMenu( "Tools" );
        
        if ( oldTools ) {
            menu.add( enableIf( isEditor, createMenuItem( "Set Center", "setSymmetryCenter" ) ) ); 
            menu.add( enableIf( isEditor, createMenuItem( "Set Axis", "setSymmetryAxis" ) ) ); 
            menu.addSeparator(); 
            
            showToolsMenuItem = enableIf( isEditor, createMenuItem( "Show Tools Panel", "showToolsPanel" ) );
            showToolsMenuItem .setEnabled( fullPower );
            menu .add( showToolsMenuItem );
            menu .addSeparator();
        }
        else
        	this .showToolsMenuItem = null;

        if ( isGolden ) {
            menu.add( enableIf( isEditor, createMenuItem( "Icosahedral Symmetry", "icosasymm-golden", KeyEvent.VK_I, COMMAND ) ) );
        } else if ( isSnubDodec )
            menu.add( enableIf( isEditor, createMenuItem( "Icosahedral Symmetry", "icosasymm-snubDodec", KeyEvent.VK_I, COMMAND ) ) );

        if ( developerExtras && isRootThree ) {
            menu.add( enableIf( isEditor, createMenuItem( "Dodecagonal Symmetry", "dodecagonsymm", KeyEvent.VK_D, COMMAND ) ) );
        }
        menu.add( enableIf( isEditor, createMenuItem( "Cubic / Octahedral Symmetry", "octasymm", KeyEvent.VK_C, COMMAND_OPTION ) ) );
        menu.add( enableIf( isEditor, createMenuItem( "Tetrahedral Symmetry", "tetrasymm", KeyEvent.VK_T, COMMAND_OPTION ) ) );
        if ( oldTools ) {
        	menu.add( enableIf( isEditor, createMenuItem( "Axial Symmetry", "axialsymm", KeyEvent.VK_R, COMMAND ) ) );
        	menu.add( enableIf( isEditor, createMenuItem( "Mirror Reflection", "mirrorsymm" , KeyEvent.VK_M, COMMAND ) ) );
        	menu.add( enableIf( isEditor, createMenuItem( "Translate", "translate", KeyEvent.VK_T, COMMAND ) ) );
        }
        menu.add( enableIf( isEditor, createMenuItem( "Point Reflection", "pointsymm" ) ) );
        
        menu .addSeparator();
        menu .add( enableIf( isEditor, createMenuItem( "Generate Polytope...", "showPolytopesDialog", KeyEvent.VK_P, COMMAND_OPTION ) ) );
        if ( enable4d ) {
            menu.add( enableIf( isEditor, createMenuItem( "H_4 Symmetry", "h4symmetry" ) ) );
            menu.add( enableIf( isEditor, createMenuItem( "H_4 Rotations", "h4rotations" ) ) );
            menu.add( enableIf( isEditor, createMenuItem( "I,T Symmetry", "IxTsymmetry" ) ) );
            menu.add( enableIf( isEditor, createMenuItem( "T,T Symmetry", "TxTsymmetry" ) ) );
        }

        super .add( menu );


        // ----------------------------------------- System menu

        menu = new JMenu( "System" );
        ButtonGroup group = new ButtonGroup();
        JMenuItem rbMenuItem;
        if ( isGolden || isSnubDodec ) {
            rbMenuItem = actions .setMenuAction( "setSymmetry.icosahedral", new JRadioButtonMenuItem( "Icosahedral System" ) );
            rbMenuItem .setSelected( "icosahedral".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }

        rbMenuItem = actions .setMenuAction( "setSymmetry.octahedral", new JRadioButtonMenuItem( "Octahedral System" ) );
        rbMenuItem .setSelected( "octahedral".equals( initSystem ) );
        rbMenuItem .setEnabled( fullPower );
        group.add( rbMenuItem );
        menu.add( rbMenuItem );

        if ( isRootThree )
        {
            rbMenuItem = actions .setMenuAction( "setSymmetry.dodecagonal", new JRadioButtonMenuItem( "Dodecagon System" ) );
            rbMenuItem .setSelected( "dodecagonal".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }
        else if ( isRootTwo )
        {
            rbMenuItem = actions .setMenuAction( "setSymmetry.synestructics", new JRadioButtonMenuItem( "Synestructics System" ) );
            rbMenuItem .setSelected( "synestructics".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }

        menu.addSeparator();
        
        if ( developerExtras )
        {
            JMenuItem wfMenuItem = actions .setMenuAction( "toggleWireframe", new JCheckBoxMenuItem( "Wireframe" ) );
            boolean isWireframe = "true".equals( controller .getProperty( "wireframe" ) );
            wfMenuItem .setSelected( isWireframe );
            menu.add( wfMenuItem );
        }

        menu.add( createMenuItem( "Shapes...", "configureShapes" ) );
        menu.add( createMenuItem( "Directions...", "configureDirections" ) );

        JMenuItem cbMenuItem = actions .setMenuAction( "toggleOrbitViews", new JCheckBoxMenuItem( "Show Directions Graphically" ) );
        boolean setting = "true".equals( controller .getProperty( "useGraphicalViews" ) );
        cbMenuItem .setSelected( setting );
        menu.add( cbMenuItem );
        cbMenuItem .setEnabled( fullPower );

        final JMenuItem showStrutScalesItem = actions .setMenuAction( "toggleStrutScales", new JCheckBoxMenuItem( "Show Strut Scales" ) );
        setting = "true" .equals( controller .getProperty( "showStrutScales" ) );
        showStrutScalesItem .setSelected( setting );
        showStrutScalesItem .setEnabled( fullPower );
        controller .addPropertyListener( new PropertyChangeListener(){
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

        cbMenuItem = actions .setMenuAction( "toggleFrameLabels", new JCheckBoxMenuItem( "Show Frame Labels" ) );
        setting = "true".equals( controller .getProperty( "showFrameLabels" ) );
        cbMenuItem .setSelected( setting );
        menu.add( cbMenuItem );

        cbMenuItem = actions .setMenuAction( "toggleOutlines", new JCheckBoxMenuItem( "Render Outlines" ) );
        setting = "true".equals( controller.getProperty( "drawOutlines" ) );
        cbMenuItem .setSelected( setting );
        menu.add( cbMenuItem );

        super.add( menu );

        // ----------------------------------------- Scripting menu

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


        // ----------------------------------------- Custom menu
        if ( developerExtras ) {
            menu = getCustomMenu();
            if(menu != null) {
                super .add( menu );
            }
        }


        // ----------------------------------------- Help menu
        menu = new JMenu( "Help" );
        if ( "G4G10" .equals( controller .getProperty( "licensed.user" ) ) )
            menu .add( createMenuItem( "Welcome G4G10 Participant...", "openResource-org/vorthmann/zome/content/welcomeG4G10.vZome" ) );
        menu .add( createMenuItem( "Quick Start...", "openResource-org/vorthmann/zome/content/welcomeDodec.vZome" ) );
        menu .addSeparator(); 
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
	
    private JMenuItem createMenuItem( String label, String command )
    {
    	return createMenuItem( label, command, KeyEvent .CHAR_UNDEFINED, 0 );
	}

    private JMenuItem createMenuItem( String label, String command, int key, int modifiers )
    {
    	JMenuItem menuItem = actions .setMenuAction( command, new JMenuItem( label ) );
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
     * The keys read from the file become the commands to be processed and the values become the menu text.

     For example, the file might contain these entries:

     assertSelection = Assert Selection
     sixLattice = 6-Lattice
     somePrereleasedFeature = TODO: Unreleased Feature
     test.pick.cube = First Octant

     */
    private JMenu getCustomMenu() {
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
                            // also note that we're swapping keys for elements
                            // as we move from the Properties collection to the sortedMenuCommands
                            sortedMenuCommands.put(menuText, actionName);
                        }
                    }
                    if (! sortedMenuCommands.isEmpty() ) {
                        JMenu menu = new JMenu("Custom");
                        for (String label : sortedMenuCommands.keySet()) {
                            menu.add(createMenuItem(label, sortedMenuCommands.get(label)));
                        }
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
