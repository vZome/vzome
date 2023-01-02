package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.editor.api.ActionEnum;
import com.vzome.core.editor.api.ChangeSelection;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

/**
 * @author David Hall
 * This class is designed to be a generalized replacement for the legacy DeselectByClass
 * It allows balls, struts and/or panels to be selected, deselected or ignored by class
 * It can be used in place of DeselectByClass including the ability to parse the legacy XML.
 * DeselectByClass has been renamed as AdjustSelectionByClass and modified with the additional functionality.
 */
public class AdjustSelectionByClass extends ChangeSelection
{
    private ActionEnum ballAction = ActionEnum.IGNORE;
    private ActionEnum strutAction = ActionEnum.IGNORE;
    private ActionEnum panelAction = ActionEnum.IGNORE;
    private final EditorModel editor;
    private boolean originLast = true; // default is the new behavior
        
    public AdjustSelectionByClass( EditorModel editor )
    {
        super( editor .getSelection() );
        this.editor = editor;
    }
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        String mode = (String) props .get( "mode" );
        if ( mode != null )
            switch ( mode ) {

            case "selectBalls":
                this.ballAction = ActionEnum.SELECT;
                break;

            case "selectStruts":
                this.strutAction = ActionEnum.SELECT;
                break;

            case "selectPanels":
                this.panelAction = ActionEnum.SELECT;
                break;

            case "deselectBalls":
            case "unselectBalls":
                this.ballAction = ActionEnum.DESELECT;
                break;

            case "deselectStruts":
                this.strutAction = ActionEnum.DESELECT;
                break;

            case "deselectPanels":
                this.panelAction = ActionEnum.DESELECT;
                break;

            case "unselectStruts": // TODO: this seems out of place. Shouldn't it be moved up with case "deselectStruts"? 
            case "unselectStrutsAndPanels":
                this.strutAction = ActionEnum.DESELECT;
                this.panelAction = ActionEnum.DESELECT; // legacy
                break;
            }
    }

    @Override
    public void perform() {
        // if any action is a SELECT, then we have to use model
        // otherwise we will use mSelection because it may be a shorter list
        Iterable<Manifestation> whichManifestationSet = (
                ballAction == ActionEnum.SELECT ||
                strutAction == ActionEnum.SELECT ||
                panelAction == ActionEnum.SELECT)
                ? this .editor .getRealizedModel()
                : mSelection;

        Connector originBall = null;
        for (Manifestation man : whichManifestationSet) {
            if ( man .isRendered() ) {
                if (man instanceof Connector) {
                	if (originLast && originBall == null && ballAction == ActionEnum.SELECT && man.getLocation().isOrigin()) {
                    	// The legacy behavior is used when originLast = false.  
                    	// This is important for edit sequences like (AdjustSelectionByClass(ballAction=SELECT), JoinPoints)
                    	// as noted in the recent similar fix to SelectAll.
                		// Save selection of origin for last to match the behavior as SelectAll
                        originBall = (Connector) man;
                    } else {
                    	adjustSelection(man, ballAction);
                    }
                } else if (man instanceof Strut) {
                    adjustSelection(man, strutAction);
                } else if (man instanceof Panel) {
                    adjustSelection(man, panelAction);
                }
            }
        }
        if (originBall != null) {
        	final boolean ignoreGroups = true;
            if (mSelection.manifestationSelected(originBall)) {
                unselect(originBall, ignoreGroups);
                redo(); // commit the current selection state of all manifestations 
            }
            // originBall is the last manifestation to be selected
            select(originBall, ignoreGroups);
        }
        redo();
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "balls", ballAction.name() );
        element .setAttribute( "struts", strutAction.name() );
        element .setAttribute( "panels", panelAction.name() );
        if ( this .originLast ) {
            element .setAttribute( "originLast", "true" );    
        }
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Command.Failure
    {
        if(xml.getLocalName().equals("DeselectByClass")) {
            // this allows the xml from the legacy DeselectByClass to be parsed and handled by this class
            if(xml.getAttribute( "class" ).equals( "balls" )) {
                ballAction = ActionEnum.DESELECT;
                strutAction = ActionEnum.IGNORE;
                panelAction = ActionEnum.IGNORE;
            } else {
                ballAction = ActionEnum.IGNORE;
                strutAction = ActionEnum.DESELECT;
                panelAction = ActionEnum.DESELECT;
            }
        } else {
            // this is the native xml for the command
            ballAction = ActionEnum.valueOf(xml.getAttribute( "balls" ));
            strutAction = ActionEnum.valueOf(xml.getAttribute( "struts" ));
            panelAction = ActionEnum.valueOf(xml.getAttribute( "panels" ));
        }
        String mode = xml .getAttribute( "originLast" );
        this .originLast = "true" .equals( mode );
    }

    @Override
    protected String getXmlElementName()
    {
        return "AdjustSelectionByClass";
    }

}
