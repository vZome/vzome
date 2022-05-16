package com.vzome.desktop.awt;

import java.awt.event.MouseEvent;

import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.desktop.api.Controller;

/**
 * Controller for handling contextual menu commands that apply only when a Manifestation can be picked.
 * 
 * We can assume that enableContextualCommands() will always be called before doAction() or getProperty(),
 * so the relevant commands will only be enabled if the pickedManifestation was set.
 * 
 * @author vorth
 *
 */
public class PickingController extends DefaultGraphicsController implements Controller
{
	private final RenderingViewer viewer;
	private final DocumentController delegate;
	
	private transient Manifestation pickedManifestation;
    private RenderedManifestation pickedRM;

	public PickingController( RenderingViewer viewer, DocumentController delegate )
	{
		this.viewer = viewer;
		this.delegate = delegate;
	}

	@Override
	public void doAction( String action ) throws Exception
	{
        switch ( action ) {

        case "undoToManifestation":
        case "symmTool-icosahedral":
        case "SymmetryAxisChange":
        case "SymmetryCenterChange":
        case "setWorkingPlaneAxis":
        case "setWorkingPlane":
        case "lookAtThis":
        case "SelectCollinear":
        case "SelectParallelStruts":
        case "AdjustSelectionByOrbitLength/selectSimilarStruts":
		case "ReplaceWithShape":
            this .delegate .doManifestationAction( this .pickedManifestation, action );
            break;
            
        case "setBuildOrbitAndLength":
        case "CreateStrutAxisPlus0":
        case "CreateStrutPrototype":
        case "testMoveAndRotate":
            this .delegate .doManifestationAction( this .pickedRM, action );
            break;
            
		default:
			this .delegate .doAction( action );
		}

		this .pickedManifestation = null;
	}

	@Override
	public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
	{
        pickedRM = this .viewer .pickManifestation( e );
        pickedManifestation = null;
        if ( pickedRM != null && pickedRM.isPickable() )
        	pickedManifestation = pickedRM.getManifestation();

        boolean[] result = this .delegate .enableContextualCommands( menu, e );
        for ( int i = 0; i < menu.length; i++ ) {
            String menuItem = menu[i];
            switch ( menuItem ) {

            case "undoToManifestation":
            case "lookAtThis":
                result[ i ] = pickedManifestation != null;
                break;

            case "symmTool-icosahedral":
            case "SymmetryCenterChange":
                result[ i ] = pickedManifestation instanceof Connector;
                break;

            case "SymmetryAxisChange":
            case "setWorkingPlaneAxis":
            case "SelectCollinear":
            case "SelectParallelStruts":
            case "AdjustSelectionByOrbitLength/selectSimilarStruts":
            case "setBuildOrbitAndLength":
            case "CreateStrutAxisPlus0":
            case "CreateStrutPrototype":
            case "testMoveAndRotate":
                result[ i ] = pickedManifestation instanceof Strut;
                break;

            case "ReplaceWithShape":
                result[ i ] = pickedManifestation instanceof Strut || pickedManifestation instanceof Connector;
                break;

            case "setWorkingPlane":
            case "showPanelVertices":
                result[ i ] = pickedManifestation instanceof Panel;
                break;

            case "showProperties-monocular":
            case "showProperties-leftEye":
            case "showProperties-rightEye":
                result[ i ] = true;
                break;

            default:
                if ( menuItem .startsWith( "showProperties-" ) )
                    result[i] = pickedManifestation != null;
                break;
            }
        }
        return result;
    }

	@Override
	public String getProperty( String propName )
	{
		switch ( propName ) {

		case "objectProperties":
		case "objectColor":
			return this .delegate .getManifestationProperty( this .pickedManifestation, propName );

		default:
	 		return this .delegate .getProperty( propName );
		}
	}
}
