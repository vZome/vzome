
//(c) Copyright 2013, Scott Vorthmann.

package org.vorthmann.zome.app.impl;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel.OrbitSource;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.render.Shapes;

import java.util.StringTokenizer;

public class PartsController extends DefaultController implements RenderingChanges
{
    private OrbitSource oldOrbits, newOrbits;
    
    public PartsController( OrbitSource orbits )
    {
        this .oldOrbits = orbits;
        this .newOrbits = orbits;
    }
    
    public void startSwitch( OrbitSource switchTo )
    {
        this .newOrbits = switchTo;
    }
    
    public void endSwitch()
    {
        this .oldOrbits = this .newOrbits;
    }

    @Override
    public void manifestationAdded( RenderedManifestation rendered )
    {
        fireManifestationCountChanged( "add", rendered, newOrbits );
    }

    @Override
    public void manifestationRemoved( RenderedManifestation rendered )
    {
        fireManifestationCountChanged( "remove", rendered, oldOrbits );
    }

    private void fireManifestationCountChanged( String action, RenderedManifestation rendered, OrbitSource orbitSource )
    {
        Manifestation man = rendered .getManifestation();
        String partTypeName = null;
        PartInfo partInfo = null;

        if ( man instanceof Connector ) {
            partTypeName = "Ball";
            partInfo = new PartInfo((Connector) man);
        }
        else if ( man instanceof Strut ) {
            partTypeName = "Strut";
            AlgebraicNumber length = rendered .getShape() .getLength();
            partInfo = new PartInfo((Strut) man, orbitSource, length);
        }
        else if ( man instanceof Panel ) {
            partTypeName = "Panel";
            partInfo = new PartInfo((Panel) man, orbitSource);
        }
        if(partTypeName != null && partInfo != null) {
            String propertyName = action + partTypeName;
            switch(action) {
                case "add":
                    firePropertyChange( propertyName, null, partInfo );
                    break;
                case "remove":
                    firePropertyChange( propertyName, partInfo, null );
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported action: " + (action == null ? "<null>" : action) + ".");
            }
        }
    }

    /**
     * PartInfo is passed to the PartsPanel in the PropertyChangeEvent.
     */
    public static final class PartInfo {
        // Use immutable public final fields instead of the overhead of getter methods.
        public final String orbitStr;       // for indexing and tool-tip display
        public final int rgbColor;          // converts com.vzome.core.render.Color to java.awt.Color
        public final String sizeNameStr;    // for indexing and display
        public final String lengthStr;      // for indexing and display
        public final AlgebraicNumber strutLength; // for context menu select/deselect strut actions
        public final Integer automaticDirectionIndex; // for sorting and grouping
        public final Double realLength;     // for sorting
        public final Class<? extends Manifestation> partClass; // for sorting and grouping

        public PartInfo(String name, Class<? extends Manifestation> partType) {
            orbitStr = "";
            rgbColor = Color.WHITE.getRGB();
            sizeNameStr = name;
            lengthStr = "";
            strutLength = null;
            automaticDirectionIndex = -1;
            realLength = 0D;
            partClass = partType;
        }

        private PartInfo(Connector ball) {
            // Don't maintain any reference to the ball.
            orbitStr = "";
            rgbColor = Color.WHITE.getRGB();
            sizeNameStr = "";
            lengthStr = "";
            strutLength = null;
            automaticDirectionIndex = -1;
            realLength = 0D;
            partClass = ball.getClass();
        }

        private PartInfo(Strut strut, OrbitSource orbits, AlgebraicNumber length) {
            // Don't maintain any reference to the strut.
            Direction orbit = ((RenderedManifestation) strut.getRenderedObject()) .getStrutOrbit();
            orbitStr = orbit.getName();
            rgbColor = orbits.getColor( orbit ).getRGB();
            StringBuffer buf = new StringBuffer();
            orbit.getLengthExpression( buf, length );
            String lengthExpression = buf.toString();
            StringTokenizer tokens = new StringTokenizer(lengthExpression, ":" );
            sizeNameStr = tokens.nextToken();
            lengthStr = tokens.nextToken();
            strutLength = length;
            automaticDirectionIndex = orbit.isAutomatic()
                    ? Integer.parseInt(orbitStr)
                    : -1;
            realLength = length.evaluate();
            partClass = strut.getClass();
        }

        private PartInfo(Panel panel, OrbitSource orbits) {
            // Don't maintain any reference to the panel.
            String orbitName = "";
            Color color = Color.WHITE;
            int autoDirIdx = -1;
            AlgebraicVector normal = panel.getNormal();
            if ( !normal.isOrigin() ) {
                Axis axis = orbits .getAxis( normal );
                if ( axis != null ) {
                    Direction orbit = axis.getDirection();
                    orbitName = orbit.getName();
                    // actual panels are Pastel, 
                    // but I'll show the actual axis colors in the PartsPanel
                    // to make their relation to the strut colors more obvious.
                    color = orbits .getColor( orbit ); //.getPastel();
                    if( orbit.isAutomatic() ) {
                        autoDirIdx = Integer.parseInt(orbitName);
                    }
                }
            }
            orbitStr = orbitName;
            rgbColor = color.getRGB();
            sizeNameStr = "";
            lengthStr = "";
            strutLength = null;
            automaticDirectionIndex = autoDirIdx;
            realLength = 0D;
            partClass = panel.getClass();
        }
    }

    @Override
    public void reset()
    {}

    @Override
    public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
    {}

    @Override
    public void glowChanged( RenderedManifestation manifestation )
    {}

    @Override
    public void colorChanged( RenderedManifestation manifestation )
    {}

    @Override
    public void locationChanged( RenderedManifestation manifestation )
    {}

    @Override
    public void orientationChanged( RenderedManifestation manifestation )
    {}

    @Override
    public void shapeChanged( RenderedManifestation manifestation )
    {}

    @Override
    public boolean shapesChanged( Shapes shapes )
    {
        return false;
    }

}
