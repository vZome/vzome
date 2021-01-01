package com.vzome.core.edits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Color;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Manifestations.ManifestationIterator;
import com.vzome.core.edits.ColorMappers.ColorMapper;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.xml.DomUtils;

/**
 * @author David Hall
 */
public class ManifestationColorMappers {

    // Static methods for public access to internal classes via their names
    private static final Map< String, ManifestationColorMapper > colorMappers = new HashMap<>();
    
    static {
        // Add only the subclasses that have parameterless c'tors and don't override initialize().
        // others will use public c'tors with parameters in DocumentModel.getColorMapper()
        // or new instance generated on demand in getColorMapper()
        RegisterMapper( new RadialCentroidColorMap() );
        RegisterMapper( new RadialStandardBasisColorMap() );
        RegisterMapper( new CanonicalOrientationColorMap() );
        RegisterMapper( new NormalPolarityColorMap() );
        RegisterMapper( new CentroidByOctantAndDirectionColorMap() );
        RegisterMapper( new CoordinatePlaneColorMap() );
        RegisterMapper( new Identity() );
        RegisterMapper( new ColorComplementor());
        RegisterMapper( new ColorInverter() );
        RegisterMapper( new ColorMaximizer() );
        RegisterMapper( new ColorSoftener() );
    }

    public static void RegisterMapper(ManifestationColorMapper mapper) {
        if(mapper != null) {
            colorMappers.put(mapper.getName(), mapper);
            if ( mapper .getName() .equals( "ColorComplementor" ) )
            	// accommodate original spelling
            	colorMappers.put( "ColorComplimentor", mapper);
        }
    }

    static ManifestationColorMapper getColorMapper( String mapperName )
    {
        // Handle all of the types with c'tor parameters, initialize() overridden, or other special handling.
        final String strTransparency = "TransparencyMapper@";
        if( mapperName.startsWith(strTransparency) ) {
            String strAlpha = mapperName.substring( strTransparency.length() );
            int alpha = Integer.parseInt(strAlpha);
            return new TransparencyMapper(alpha);
        }
        // Any class that overrides initialize() should get a fresh instance each time.
        switch(mapperName) {
            case "TransparencyMapper":
                return new TransparencyMapper(255); // alpha value will be initialized from the XML later
            case "DarkenWithDistance":
                return new DarkenWithDistance();
            case "DarkenNearOrigin":
                return new DarkenNearOrigin();
            case "CopyLastSelectedColor":
                return new CopyLastSelectedColor();
        }
        // try the re-usable static instances in the registered ColorMappers collection
        return colorMappers.get(mapperName);
    }

    static ManifestationColorMapper getColorMapper( String mapperName, OrbitSource symmetry )
    {
        ManifestationColorMapper colorMapper
            = mapperName.equals("SystemColorMap") ? new SystemColorMap(symmetry)
            : mapperName.equals("SystemCentroidColorMap") ? new SystemCentroidColorMap(symmetry)
            : mapperName.equals("NearestSpecialOrbitColorMap") ? new NearestSpecialOrbitColorMap(symmetry)
            : mapperName.equals("CentroidNearestSpecialOrbitColorMap") ? new CentroidNearestSpecialOrbitColorMap(symmetry)
            : mapperName.equals("NearestPredefinedOrbitColorMap") ? new NearestPredefinedOrbitColorMap(symmetry)
            : mapperName.equals("CentroidNearestPredefinedOrbitColorMap") ? new CentroidNearestPredefinedOrbitColorMap(symmetry)
            : ManifestationColorMappers.getColorMapper(mapperName);

        if (colorMapper == null) {
            // Provide a do-nothing placeholder for an unrecognized (future???) mapper
            colorMapper = new ManifestationColorMappers.Identity();
//            logger.warning("Substituting " + colorMapper.getName() + " for unknown mapperName " + mapperName);
        }
        return colorMapper;
    }

    /**
     * Common abstract base class adds xml persistence
     * and late loading of criteria based on selection and/or model
     */
    public static abstract class ManifestationColorMapper implements ColorMapper<Manifestation> {

        protected ManifestationColorMapper() {}

        /**
         * Optional opportunity to initialize parameters that were not available at time of the constructor
         * but are determined based on the selection or model iterator
         * just before apply is called on each individual manifestation.
         * @param selection
         * @param model
         */
        public void initialize(ManifestationIterator manifestations)
            throws Failure
        {}

        @Override
        public Color apply( Manifestation man ) {
            return (man == null || !man.isRendered())
                    ? null
                    : applyTo( man );
        }
        
        protected Color applyTo( Manifestation manifestation )
        {
            Color color = manifestation.getColor();
            if(color == null && Connector.class.equals( manifestation.getClass() ) ) {
                color = Color.WHITE; // provide default ball color so it can be manipulated
            }
            return color;
        }

        /**
         * subclasses should call {@code result.setAttribute()} if they have any parameters to persist
         * @param result 
         */
        protected void getXmlAttributes( Element result ) { }

        /**
         * subclasses should call {@code xml.getAttribute()} to retrieve any persisted parameters
         * @param xml
         */
        protected void setXmlAttributes( Element xml ) { }
    }

    /**
     * returns current color
     */
    public static class Identity extends ManifestationColorMapper {
        @Override
        protected Color applyTo(Manifestation rendered) {
            return rendered.getColor();
        }
    }

    /**
     * returns complementary color
     */
    public static class ColorComplementor extends ManifestationColorMapper {
        @Override
        protected Color applyTo(Manifestation rendered) {
            return Color.getComplement(super.applyTo(rendered) );
        }
    }

    /**
     * returns inverted color
     */
    public static class ColorInverter extends ManifestationColorMapper {
        @Override
        protected Color applyTo(Manifestation rendered) {
            return Color.getInverted( super.applyTo(rendered) );
        }
    }

    /**
     * returns maximized color
     */
    public static class ColorMaximizer extends ManifestationColorMapper {
        @Override
        protected Color applyTo(Manifestation rendered) {
            return Color.getMaximum( super.applyTo(rendered) );
        }
    }

    /**
     * returns pastel of current color
     */
    public static class ColorSoftener extends ManifestationColorMapper {
        @Override
        protected Color applyTo(Manifestation rendered) {
            return Color.getPastel( super.applyTo(rendered) );
        }
    }

    public static class TransparencyMapper extends ManifestationColorMapper {
        private int alpha;

        public TransparencyMapper(int alpha) {
            super();
            setAlpha(alpha);
        }

        private void setAlpha(int value) {
            // be sure value is in valid range
            // don't allow complete transparency for now, just so it's not confused with being hidden.
            this.alpha = Math.min(255, Math.max(1, value));
        }

        private static final String ALPHA_ATTR_NAME = "alpha";
        
        @Override
        protected void setXmlAttributes(Element xml) {
            alpha = Integer.parseInt( xml.getAttribute(ALPHA_ATTR_NAME) );
        }

        @Override
        protected void getXmlAttributes(Element result) {
            result.setAttribute(ALPHA_ATTR_NAME, Integer.toString(alpha));
        }

        @Override
        protected Color applyTo(Manifestation rendered) {
            Color color = super.applyTo(rendered);
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), this.alpha);
        }
    }

    public static class CopyLastSelectedColor extends ManifestationColorMapper {
        private Color color;

        @Override
        public boolean requiresOrderedSelection() {
            return true;
        }

        @Override
        public void initialize(ManifestationIterator selection)
        throws Failure {
            if(color == null) { // if not already set by XML
                Manifestation last = null;
                for(Manifestation man : selection) {
                    if(man != null && man.isRendered()) {
                        last = man;
                    }
                }
                if(last != null) {
                    this.color = last.getColor();
                }
            }
            if(color == null) {
                throw new Failure("select a ball, strut or panel as the color to be copied.");
            }
        }

        @Override
        protected void setXmlAttributes(Element xml) {
            String red = xml.getAttribute("red");
            String green = xml.getAttribute("green");
            String blue = xml.getAttribute("blue");
            String alphaStr = xml.getAttribute("alpha");
            int alpha = (alphaStr == null || alphaStr.isEmpty()) ? 0xff : Integer.parseInt(alphaStr);
            this.color = new Color(Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue), alpha);
        }

        @Override
        protected void getXmlAttributes(Element result) {
            result .setAttribute( "red", "" + color .getRed() );
            result .setAttribute( "green", "" + color .getGreen() );
            result .setAttribute( "blue", "" + color .getBlue() );
            int alpha = color .getAlpha();
            if ( alpha < 0xFF )
                result .setAttribute( "alpha", "" + alpha );
        }

        @Override
        protected Color applyTo(Manifestation rendered) {
            return color;
        }
    }

    protected static Color mapPolarity(AlgebraicVector vector, int alpha) {
        final int polarity = vector.compareTo(vector.negate());
        int mid = 128;
        int diff = 64;
        int shade = polarity < 0 ? mid - diff :
                    polarity > 0 ? mid + diff : mid;
        return new Color(shade, shade, shade, alpha); 
    }

    /**
     * @param vector could be midpoint, start, end, normal, or any basis for mapping to a color
     * @param alpha the transparency component of the resulting color.
     * @return
     */
    protected static Color mapRadially(AlgebraicVector vector, int alpha) {
        final int midPoint = 0x7F;
        int[] rgb = {midPoint, midPoint, midPoint};
        double[] parts = new double[rgb.length];
        int dimensions = Math.min(rgb.length, vector.dimension());
        double whole = 0.0;
        for (int i = 0; i < dimensions; i++) {
            AlgebraicNumber component = vector.getComponent(i);
            parts[i] = component.evaluate();
            whole += Math.abs(parts[i]);
        }
        if (whole != 0.0) { // this should only happen when location.isOrigin() == false
            for (int i = 0; i < parts.length; i++) {
                double part = (parts[i] / whole);
                Double contribution = part * midPoint;
                rgb[i] = contribution.intValue() + midPoint;
                // ... in case of any rounding errors...
                rgb[i] = Math.min(0xFF, rgb[i]);
                rgb[i] = Math.max(0x00, rgb[i]);
            }
        }
        return new Color(rgb[0], rgb[1], rgb[2], alpha);
    }

    /**
     * @param vector could be midpoint, start, end, normal, or any basis for mapping to a color
     * @param alpha the transparency component of the resulting color.
     * @param neg the R, G or B level of vectors with a negative value in the corresponding X, Y, or Z dimension.
     * @param zero the R, G or B level of vectors with a zero value in the corresponding X, Y, or Z dimension.
     * @param pos the R, G or B level of vectors with a positive value in the corresponding X, Y, or Z dimension.
     * @return
     */
    protected static Color mapToOctant(AlgebraicVector vector, int alpha, int neg, int zero, int pos ) {
        int[] src = new int[] { neg, zero, pos };
        int[] rgb = new int[src.length];
        int dimensions = Math.min(rgb.length, vector.dimension());
        for (int i = 0; i < dimensions; i++) {
            AlgebraicNumber component = vector.getComponent(i);
            Double dir = Math.signum(component.evaluate());
            int index = dir.intValue() + 1;
            rgb[i] = src[index];
        }
        return new Color(rgb[0], rgb[1], rgb[2], alpha);
    }

    /**
     * Handles getting the centroid and calling overloaded methods to map the subClass specific AlgebraicVector
     */
    public static abstract class CentroidColorMapper extends ManifestationColorMapper {

        protected CentroidColorMapper() {
            super();
        }

        @Override
        public Color applyTo(Manifestation rendered) {
            Color color = rendered.getColor();
            int alpha = color == null
                    ? 0xFF 
                    : color.getAlpha();
            return applyTo( rendered.getCentroid(), alpha );
        }

        protected abstract Color applyTo(AlgebraicVector centroid, int alpha);
    }
    
    protected static Color mapToMagnitude(AlgebraicVector vector, double offset, double fullScaleSquared, final Color initialColor) {
        if(vector == null || initialColor == null) {
            return initialColor;
        }
        double magnitudeSquared = AlgebraicVectors.getMagnitudeSquared(vector).evaluate();
        // don't allow divide by 0, but it's OK to divide by a tiny number
        // since we'll round back to an int between 0 and 255
        double denominator = (fullScaleSquared == 0.0) ? 0.0001 : fullScaleSquared;
        double scale = Math.abs(offset - magnitudeSquared) / denominator;
        return Color.getScaledTo(initialColor, scale);
    }

    /**
     * Scales the intensity of the current color of each Manifestation 
     * based on the distance of its centroid from the origin.
     * A position ranging from the origin to the fullScale vector position
     * adjusts the intensity of the current color from darkest to lightest.
     */
    public static class DarkenNearOrigin extends ManifestationColorMapper {
        protected double offset = 0;
        protected double fullScaleSquared = 0L;

        @Override
        public void initialize(ManifestationIterator manifestations) throws Failure {
            if(fullScaleSquared == 0L) {
                AlgebraicVector fullScale = getMostDistantPoint(manifestations);
                if (fullScale == null) {
                    throw new Failure("unable to determine most distant point");
                }
                if (fullScale.isOrigin()) {
                    throw new Failure("select at least one point other than the origin");
                }
                fullScaleSquared = AlgebraicVectors.getMagnitudeSquared(fullScale).evaluate();
            }
        }

        private static AlgebraicVector getMostDistantPoint(ManifestationIterator manifestations) {
            List<AlgebraicVector> centroids = new ArrayList<>();
            for(Manifestation man : manifestations) {
                centroids.add( man.getCentroid() );
            }
            if( centroids.isEmpty() ) {
                return null;
            }
            TreeSet<AlgebraicVector>  mostDistant = AlgebraicVectors.getMostDistantFromOrigin( centroids );
            return mostDistant.isEmpty()
                    ? null
                    : mostDistant.first();
        }

        @Override
        protected Color applyTo(Manifestation rendered) {
            AlgebraicVector centroid = rendered.getCentroid();
            Color initialColor = super.applyTo(rendered);
            return mapToMagnitude( centroid, offset, fullScaleSquared, initialColor );
        }

        private final String FULLSCALESQUARED_ATTR_NAME = "fullScaleSquared";

        @Override
        protected void getXmlAttributes( Element result ) {
            result.setAttribute(FULLSCALESQUARED_ATTR_NAME, Double.toString(fullScaleSquared));
        }

        @Override
        protected void setXmlAttributes( Element xml ) {
            String attr = xml.getAttribute(FULLSCALESQUARED_ATTR_NAME);
            fullScaleSquared = Double.parseDouble(attr);
        }
    }

    /**
     * Same as {@code DarkenNearOrigin} except that
     * the color mapping is reversed from lightest to darkest
     */
    public static class DarkenWithDistance extends DarkenNearOrigin {
        @Override
        public void initialize(ManifestationIterator manifestations) throws Failure {
            super.initialize(manifestations);
            this.offset = fullScaleSquared;
        }
    }

    /**
     * Maps vector XYZ components to RGB
     * such that each RGB component is weighted by the contribution
     * of the corresponding XYZ component
     * and offset by half of the color range so that a
     *  + directions map between 0x7F and 0xFF color element
     *  0 direction maps to a midrange    0x7F color element
     *  - directions map between 0x00 and 0x7F color element
     *
     * Polarity info IS retained by this mapping.
     */
    public static class RadialCentroidColorMap extends CentroidColorMapper {
        @Override
        protected Color applyTo(AlgebraicVector centroid, int alpha) {
            return mapRadially(centroid, alpha);
        }
    }

    /**
     * Polarity info is retained by this mapping 
     * so that inverted struts and panels will be mapped to inverted colors.
     */
    public static class RadialStandardBasisColorMap extends ManifestationSubclassColorMapper {

        @Override
        protected Color applyToBall(Connector ball, int alpha) {
            return applyTo(ball.getLocation(), alpha);
        }
        
        @Override
        protected Color applyToStrut(Strut strut, int alpha) {
            return applyTo(strut.getOffset(), alpha);
        }

        @Override
        protected Color applyToPanel(Panel panel, int alpha) {
            return applyTo(panel.getNormal(), alpha);
        }

        protected Color applyTo(AlgebraicVector vector, int alpha) {
            return mapRadially(vector, alpha);
        }
    }

    /**
     * Polarity info is intentionally removed by this mapping for struts and panels, but not balls
     * so that parallel struts and the panels normal to them will be the same color.
     */
    public static class CanonicalOrientationColorMap extends RadialStandardBasisColorMap {

        @Override
        protected Color applyToBall(Connector ball, int alpha) {
            return super.applyToBall(ball, alpha);
        }
        
        @Override
        protected Color applyTo(AlgebraicVector vector, int alpha) {
            return super.applyTo(AlgebraicVectors.getCanonicalOrientation(vector), alpha);
        }
    }
    
    /**
     * Polarity info is the ONLY basis for this mapping 
     */
    public static class NormalPolarityColorMap extends RadialStandardBasisColorMap {
        @Override
        protected Color applyTo(AlgebraicVector vector, int alpha) {
            return mapPolarity(vector, alpha);
        }
    }

    /**
     * Maps vector XYZ components to RGB by Octant
     *
     * Polarity info IS retained by this mapping.
     */
    public static class CentroidByOctantAndDirectionColorMap extends CentroidColorMapper {
        @Override
        protected Color applyTo(AlgebraicVector vector, int alpha) {
            return Color.getMaximum( mapToOctant(vector, alpha, 0x00, 0x7F, 0xFF ) );
        }
    }

    /**
     * Maps vector XYZ components to RGB
     * corresponding to the X, Y or Z coordinate plane.
     *
     * Polarity info IS NOT retained by this mapping.
     */
    public static class CoordinatePlaneColorMap extends CentroidColorMapper {
        @Override
        protected Color applyTo(AlgebraicVector vector, int alpha) {
            return Color.getInverted( mapToOctant(vector, alpha, 0x00, 0xFF, 0x00) );
        }
    }

    /**
     * Abstract base class which calls subclass specific abstract overloads for all known subtypes.
     */
    public static abstract class ManifestationSubclassColorMapper extends ManifestationColorMapper {

        @Override
        protected Color applyTo(Manifestation man) {
            Color color = man.getColor();
            int alpha = color == null
                    ? 0xFF
                    : color.getAlpha();
            if ( man instanceof Connector ) {
                return applyToBall( (Connector) man, alpha );
            } else if ( man instanceof Strut ) {
                return applyToStrut( (Strut) man, alpha );
            } else if ( man instanceof Panel ) {
                return applyToPanel( (Panel) man, alpha );
            }
            return null;
        }

        protected abstract Color applyToBall(Connector ball, int alpha);
        protected abstract Color applyToStrut(Strut strut, int alpha);
        protected abstract Color applyToPanel(Panel panel, int alpha);
    }

    /**
     * Gets standard color mapping from the OrbitSource
     */
    public static class SystemColorMap extends ManifestationSubclassColorMapper {

        protected final OrbitSource symmetrySystem;

        protected SystemColorMap(OrbitSource symmetry) {
            super();
            this.symmetrySystem = symmetry;
        }

        @Override
        protected Color applyToBall(Connector ball, int alpha) {
            return Color.WHITE;
        }

        @Override
        protected Color applyToStrut(Strut strut, int alpha) {
            return applyTo(strut.getOffset());
        }

        @Override
        protected Color applyToPanel(Panel panel, int alpha) {
            return applyTo(panel.getNormal()).getPastel();
        }

        protected Color applyTo(AlgebraicVector vector) {
            return symmetrySystem.getVectorColor(vector);
        }

        // Note that symmetrySystem is read from the XML and passed to the c'tor
        // unlike the normal pattern of deserializing the XML here.
        // See the explanation in DocumentModel.createEdit()
        @Override
        protected void getXmlAttributes(Element element) {
            if (symmetrySystem != null) {
                DomUtils.addAttribute(element, "symmetry", symmetrySystem.getName());
            }
        }
    }

    /**
     * Maps standard SymmetrySystem colors 
     * to the Manifestation's Centroid instead of the normal vector
     */
    public static class SystemCentroidColorMap extends CentroidColorMapper {

        protected final OrbitSource symmetrySystem;

        protected SystemCentroidColorMap(OrbitSource symmetry) {
            super();
            this.symmetrySystem = symmetry;
        }

        @Override
        protected Color applyTo(AlgebraicVector centroid, int alpha) {
            return symmetrySystem.getVectorColor(centroid);
        }
        // Note that symmetrySystem is read from the XML and passed to the c'tor
        // unlike the normal pattern of deserializing the XML here.
        // See the explanation in DocumentModel.createEdit()
        @Override
        protected void getXmlAttributes(Element element) {
            if (symmetrySystem != null) {
                DomUtils.addAttribute(element, "symmetry", symmetrySystem.getName());
            }
        }
    }

    /**
     * Gets standard color of the nearest special orbit using the standard color basis
     */
    public static class NearestSpecialOrbitColorMap extends SystemColorMap {
        protected Set<Direction> specialOrbits = new LinkedHashSet<>(); // maintains insert order.

        protected NearestSpecialOrbitColorMap(OrbitSource symm) {
            super(symm);
            specialOrbits.add( symm.getSymmetry().getSpecialOrbit( Symmetry.SpecialOrbit.BLUE ) );
            specialOrbits.add( symm.getSymmetry().getSpecialOrbit( Symmetry.SpecialOrbit.YELLOW ) );
            specialOrbits.add( symm.getSymmetry().getSpecialOrbit( Symmetry.SpecialOrbit.RED ) );
        }

        @Override
        protected Color applyToBall(Connector ball, int alpha) {
            return applyTo(ball.getLocation());
        }

        @Override
        protected Color applyToStrut(Strut strut, int alpha) {
            return applyTo(strut.getOffset());
        }

        @Override
        protected Color applyToPanel(Panel panel, int alpha) {
            return applyTo(panel.getNormal()).getPastel();
        }

        @Override
        protected Color applyTo(AlgebraicVector vector) {
            if(vector.isOrigin()) {
                return Color.WHITE;
            }
            Axis nearestSpecialOrbit = symmetrySystem.getSymmetry().getAxis(vector.toRealVector(), specialOrbits);
            AlgebraicVector normal = nearestSpecialOrbit.normal();
            return symmetrySystem.getVectorColor( normal );
        }
    }

    /**
     * Gets standard color of the nearest special orbit based on the Centroid
     */
    public static class CentroidNearestSpecialOrbitColorMap extends NearestSpecialOrbitColorMap {

        protected CentroidNearestSpecialOrbitColorMap(OrbitSource symm) {
            super(symm);
        }

        @Override
        protected Color applyToBall(Connector ball, int alpha) {
            return applyTo(ball.getCentroid());
        }

        @Override
        protected Color applyToStrut(Strut strut, int alpha) {
            return applyTo(strut.getCentroid());
        }

        @Override
        protected Color applyToPanel(Panel panel, int alpha) {
            return applyTo(panel.getCentroid()).getPastel();
        }
    }

    /**
     * Gets standard color of the nearest predefined orbit using the symmetry's standard color scheme
     */
    public static class NearestPredefinedOrbitColorMap extends NearestSpecialOrbitColorMap {
        protected NearestPredefinedOrbitColorMap(OrbitSource symm) {
            super(symm);
            // setting specialOrbits to null will use the predefined orbits of the symmetery
            specialOrbits = null;
        }
    }

    /**
     * Gets standard color of the nearest predefined orbit based on the centroid of each manifestation
     */
    public static class CentroidNearestPredefinedOrbitColorMap extends CentroidNearestSpecialOrbitColorMap {
        protected CentroidNearestPredefinedOrbitColorMap(OrbitSource symm) {
            super(symm);
            // setting specialOrbits to null will use the predefined orbits of the symmetery
            specialOrbits = null;
        }
    }
}
