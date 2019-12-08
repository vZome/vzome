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
import com.vzome.core.commands.CommandTauDivision;
import com.vzome.core.commands.CommandTranslate;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.A4Group;
import com.vzome.core.math.symmetry.B4Group;
import com.vzome.core.math.symmetry.CoxeterGroup;
import com.vzome.core.math.symmetry.D4Group;
import com.vzome.core.math.symmetry.F4Group;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.WythoffConstruction;
import com.vzome.core.tools.BookmarkTool;
import com.vzome.core.tools.InversionTool;
import com.vzome.core.tools.LinearMapTool;
import com.vzome.core.tools.MirrorTool;
import com.vzome.core.tools.ModuleTool;
import com.vzome.core.tools.OctahedralToolFactory;
import com.vzome.core.tools.PlaneSelectionTool;
import com.vzome.core.tools.ProjectionTool;
import com.vzome.core.tools.RotationTool;
import com.vzome.core.tools.ScalingTool;
import com.vzome.core.tools.TranslationTool;

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
    private final Command tauDivide = new CommandTauDivision();
    private final Command midpoint = new CommandMidpoint();

	public DefaultFieldApplication( AlgebraicField field )
	{
		super();
		this .field = field;
	}

	@Override
	public String getName()
	{
		return this .field .getName();
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
	    // Any SymmetryTool factory here is good enough
	    toolFactories .put( "SymmetryTool", new OctahedralToolFactory( tools, null ) );
	    toolFactories .put( "RotationTool", new RotationTool.Factory( tools, null ) );
	    toolFactories .put( "ScalingTool", new ScalingTool.Factory( tools, null ) );
	    toolFactories .put( "InversionTool", new InversionTool.Factory( tools ) );
	    toolFactories .put( "MirrorTool", new MirrorTool.Factory( tools ) );
        toolFactories .put( "TranslationTool", new TranslationTool.Factory( tools ) );
        toolFactories .put( "ProjectionTool", new ProjectionTool.Factory( tools ) );
	    toolFactories .put( "BookmarkTool", new BookmarkTool.Factory( tools ) );
	    toolFactories .put( "LinearTransformTool", new LinearMapTool.Factory( tools, null, false ) );
	
	    // These tool factories have to be available for loading legacy documents.
	    
	    toolFactories .put( "LinearMapTool", new LinearMapTool.Factory( tools, null, true ) );
	    toolFactories .put( "ModuleTool", new ModuleTool.Factory( tools ) );
	    toolFactories .put( "PlaneSelectionTool", new PlaneSelectionTool.Factory( tools ) );
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
	public Command getLegacyCommand( String name )
	{
		switch ( name ) {
		case "pointsymm": return pointsymm;
		case "mirrorsymm": return mirrorsymm;
		case "translate": return translate;
		case "centroid": return centroid;
		case "hideball": return hideball;
		case "hide": return hide;
		case "panel": return panel;
		case "tauDivide": return tauDivide;
		case "midpoint": return midpoint;
		default:
			return null;
		}
	}
}