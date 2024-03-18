package com.vzome.core.kinds;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandCentralSymmetry;
import com.vzome.core.commands.CommandCentroid;
import com.vzome.core.commands.CommandHide;
import com.vzome.core.commands.CommandMidpoint;
import com.vzome.core.commands.CommandMirrorSymmetry;
import com.vzome.core.commands.CommandPolygon;
import com.vzome.core.commands.CommandTranslate;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.SymmetryPerspective;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.A4Group;
import com.vzome.core.math.symmetry.B4Group;
import com.vzome.core.math.symmetry.CoxeterGroup;
import com.vzome.core.math.symmetry.D4Group;
import com.vzome.core.math.symmetry.F4Group;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.WythoffConstruction;
import com.vzome.core.tools.BookmarkToolFactory;
import com.vzome.core.tools.InversionToolFactory;
import com.vzome.core.tools.LinearMapToolFactory;
import com.vzome.core.tools.MirrorToolFactory;
import com.vzome.core.tools.ModuleToolFactory;
import com.vzome.core.tools.OctahedralToolFactory;
import com.vzome.core.tools.PerspectiveProjectionToolFactory;
import com.vzome.core.tools.PlaneSelectionToolFactory;
import com.vzome.core.tools.ProjectionToolFactory;
import com.vzome.core.tools.RotationToolFactory;
import com.vzome.core.tools.ScalingToolFactory;
import com.vzome.core.tools.TranslationToolFactory;

public class DefaultFieldApplication implements FieldApplication
{
	private final AlgebraicField field;
	private SymmetryPerspective octahedralPerspective;
	
	private Map<String, CoxeterGroup> groups4d = new HashMap<String, CoxeterGroup>();

    private final Command pointsymm = new CommandCentralSymmetry();
    private final Command mirrorsymm = new CommandMirrorSymmetry();
    private final Command translate = new CommandTranslate();
    private final Command centroid = new CommandCentroid();
    private final Command hideball = new CommandHide();
    private final Command hide = new CommandHide();
    private final Command panel = new CommandPolygon();
    private final Command midpoint = new CommandMidpoint();

	public DefaultFieldApplication( AlgebraicField field )
	{
		this .field = field;
	}

    @Override
    public String getName()
    {
        return this .field .getName();
    }

    @Override
    public String getLabel()
    {
        return null; // signals that the field is not to be shown in the "new design" menu
    }

	@Override
	public AlgebraicField getField()
	{
		return this .field;
	}

	@Override
	public SymmetryPerspective getDefaultSymmetryPerspective()
	{
		return this .getSymmetryPerspective( "octahedral" );
	}

	@Override
	public Collection<SymmetryPerspective> getSymmetryPerspectives()
	{
		return Arrays.asList( this .getDefaultSymmetryPerspective() );
	}

	@Override
	public SymmetryPerspective getSymmetryPerspective( String symmName )
	{
		switch ( symmName ) {
	
		case "octahedral":
			if ( this .octahedralPerspective == null ) {
				this .octahedralPerspective = new OctahedralSymmetryPerspective( this .field );
			}
			return this .octahedralPerspective;
	
		default:
			return null;
		}
	}

	@Override
	public QuaternionicSymmetry getQuaternionSymmetry( String name )
	{
		return null;
	}

	@Override
	public void registerToolFactories( Map<String, Factory> toolFactories, ToolsModel tools )
	{
	    // These symm parameters can be null since it will be overwritten by SymmetryTool.setXmlAttributes()
        // Any SymmetryTool factory here is good enough
	    toolFactories .put( "SymmetryTool", new OctahedralToolFactory( tools, null ) );
	    toolFactories .put( "RotationTool", new RotationToolFactory( tools, null ) );
	    toolFactories .put( "ScalingTool", new ScalingToolFactory( tools, null ) );
	    
	    toolFactories .put( "InversionTool", new InversionToolFactory( tools ) );
	    toolFactories .put( "MirrorTool", new MirrorToolFactory( tools ) );
        toolFactories .put( "TranslationTool", new TranslationToolFactory( tools ) );
        toolFactories .put( "ProjectionTool", new ProjectionToolFactory( tools ) );
        toolFactories .put( "PerspectiveProjectionTool", new PerspectiveProjectionToolFactory( tools ) );
	    toolFactories .put( "BookmarkTool", new BookmarkToolFactory( tools ) );
	    toolFactories .put( "LinearTransformTool", new LinearMapToolFactory( tools, null, false ) );
	
	    // These tool factories have to be available for loading legacy documents.
	    
	    toolFactories .put( "LinearMapTool", new LinearMapToolFactory( tools, null, true ) );
	    toolFactories .put( "ModuleTool", new ModuleToolFactory( tools ) );
	    toolFactories .put( "PlaneSelectionTool", new PlaneSelectionToolFactory( tools ) );
	}

	@Override
	public void constructPolytope( String groupName, int index, int edgesToRender, AlgebraicNumber[] edgeScales, WythoffConstruction.Listener listener )
	{
        CoxeterGroup group = this .groups4d .get( groupName );
        if ( group == null )
        {
            // Lazily create these groups... most users will never use them
            switch ( groupName ) {

            case "A4":
                group = new A4Group( this.field );
                break;

            case "D4":
                group = new D4Group( this.field );
                break;

            case "F4":
                group = new F4Group( this.field );
                break;

            default:
                group = new B4Group( this.field );
                break;
            }
            this .groups4d .put( groupName, group );
        }
        WythoffConstruction .constructPolytope( group, index, edgesToRender, edgeScales, group, listener );
	}

	@Override
	public Command getLegacyCommand( String action )
	{
		switch ( action ) {
		case "pointsymm": return pointsymm;
		case "mirrorsymm": return mirrorsymm;
		case "translate": return translate;
		case "centroid": return centroid;
		case "hideball": return hideball;
		case "hide": return hide;
		case "panel": return panel;
		case "midpoint": return midpoint;
		case "octasymm":
		    return getDefaultSymmetryPerspective().getLegacyCommand(action);
		default:
			return null;
		}
	}
}