/*
 * Created on Jun 30, 2003
 */
package org.vorthmann.zome.render.java3d;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.Group;
import javax.media.j3d.Light;
import javax.media.j3d.LineArray;
import javax.media.j3d.LinearFog;
import javax.media.j3d.Locale;
import javax.media.j3d.Node;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.vorthmann.ui.Controller;

import com.sun.j3d.utils.geometry.Text2D;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;

/**
 * @author vorth
 */
public class Java3dSceneGraph implements RenderingChanges, PropertyChangeListener
{
    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.render.java3d" );

    private final Locale mLocale;

    private final BranchGroup mScene, mRoot;

    private final TransformGroup mLights;

    private final LinearFog mFog;

    private final Light ambientForGlow, ambientForOutlines, directionalForOutlines;

    private int mGlowCount = 0;

    private final Background mBackground;

    private final Java3dFactory mFactory;

    //private final SharedGroup mBallGroup = null;

    private final BoundingSphere mEverywhere = new BoundingSphere( new Point3d( 0.0, 0.0, 0.0 ), Double.MAX_VALUE );

    private final boolean isSticky;

    private boolean drawNormals;

    private boolean drawOutlines;

    private FrameLabels frameLabels = null;

    private IcosahedralLabels icosahedralLabels = null;

    public BranchGroup getRoot()
    {
        return mRoot; // or mScene?
    }

    void addView( BranchGroup view )
    {
        mLocale.addBranchGraph( view );
    }

    TransformGroup getLightsGroup()
    {
        return mLights;
    }

    /**
     * @param factory
     * @param lights
     * @param isSticky true if each RenderedManifestation should record the resulting graphics object
     * @param controller
     */
    public Java3dSceneGraph( Java3dFactory factory, Lights lights, boolean isSticky, Controller controller )
    {
        mFactory = factory;
        this .isSticky = isSticky;

        lights .addPropertyListener( new PropertyChangeListener(){

            @Override
            public void propertyChange( PropertyChangeEvent chg )
            {
                if ( "backgroundColor" .equals( chg .getPropertyName() ) )
                {
                    int rgb =  Integer .parseInt( (String) chg .getNewValue(), 16 );
                    Color newColor = new Color( rgb );
                    backgroundColorChanged( newColor );
                }
            }} );

        mFactory.getColors().addListener( new Colors.Changes()
        {
            @Override
            public void colorChanged( String name, Color newColor )
            {
                if ( name.equals( Colors.BACKGROUND ) )
                    backgroundColorChanged( newColor );
            }

            @Override
            public void colorAdded( String name, Color color )
            {
            }
        } );

        mRoot = new BranchGroup();
        mRoot.setCapability( Group.ALLOW_CHILDREN_EXTEND );
        mRoot.setCapability( Group.ALLOW_CHILDREN_READ );
        mRoot.setCapability( Group.ALLOW_CHILDREN_WRITE );
        mRoot.setCapability( Node.ENABLE_PICK_REPORTING );

        // background and lighting should be in a separate model object

        // without lights, the model will be invisible... I tried it

        mLights = new TransformGroup();
        mLights.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
        mLights.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
        mLights.setCapability( Group.ALLOW_CHILDREN_EXTEND );
        mLights.setCapability( Group.ALLOW_CHILDREN_READ );
        mLights.setCapability( Group.ALLOW_CHILDREN_WRITE );
        mLights.setCapability( Node.ENABLE_PICK_REPORTING );
        mRoot.addChild( mLights );

        float[] rgb = new float[3];
        Color3f color = new Color3f( lights.getAmbientColor().getRGBColorComponents( rgb ) );
        ambientForGlow = new AmbientLight( color );
        ambientForGlow .setInfluencingBounds( mEverywhere );
        ambientForGlow .setEnable( true );
        ambientForGlow .setCapability( Light.ALLOW_STATE_WRITE );
        mLights .addChild( ambientForGlow );
        ambientForOutlines = new AmbientLight( color );
        ambientForOutlines .setInfluencingBounds( mEverywhere );
        ambientForOutlines .setEnable( controller .propertyIsTrue( "drawOutlines" ) );
        ambientForOutlines .setCapability( Light.ALLOW_STATE_WRITE );
        mLights .addChild( ambientForOutlines );

        if(lights.size() <= 0) {
            throw new IllegalArgumentException("Expected lights.size() to be greater than 0.");
        }
        Light light = null;
        for ( int i = 0; i < lights.size(); i++ ) {
            Vector3f direction = new Vector3f();
            color = new Color3f( lights.getDirectionalLight( i, direction ).getRGBColorComponents( rgb ) );
            light = new DirectionalLight( color, direction );
            light .setInfluencingBounds( mEverywhere );
            light .setEnable( true );
            mLights .addChild( light );
        }
        // TODO: model this better in core Lights... no overloading
        // use the last light in the array for the directional light
        directionalForOutlines = light;
        directionalForOutlines .setEnable( controller .propertyIsTrue( "drawOutlines" ) );
        directionalForOutlines.setCapability(Light.ALLOW_STATE_WRITE);
        // ---------------------------------------------------

        mScene = new BranchGroup();
        mScene.setCapability( Group.ALLOW_CHILDREN_EXTEND );
        mScene.setCapability( Group.ALLOW_CHILDREN_READ );
        mScene.setCapability( Group.ALLOW_CHILDREN_WRITE );
        mScene.setCapability( Node.ENABLE_PICK_REPORTING );
        mRoot.addChild( mScene );

        Color bg = lights .getBackgroundColor();
        bg.getRGBColorComponents( rgb );
        mBackground = new Background( new Color3f( rgb ) );
        mBackground.setCapability( Background.ALLOW_COLOR_WRITE );
        mBackground.setApplicationBounds( mEverywhere );
        mRoot.addChild( mBackground );

        mFog = new LinearFog( rgb[0], rgb[1], rgb[2], 0d, 40d );
        mFog.setInfluencingBounds( mEverywhere );
        mFog.setCapability( LinearFog.ALLOW_COLOR_WRITE );
        mFog.setCapability( LinearFog.ALLOW_DISTANCE_WRITE );
        mRoot.addChild( mFog );

        //        if ( 1 == 0 ) {
        //            // create axes
        //            Color3f red = new Color3f( 1.0f, 0.0f, 0.0f );
        //            Color3f green = new Color3f( 0.0f, 1.0f, 0.0f );
        //            Color3f blue = new Color3f( 0.0f, 0.0f, 1.0f );
        //            Color3f black = new Color3f( 0.0f, 0.0f, 0.0f );
        //
        //            // create line for X axis
        //            LineArray axisXLines = new LineArray( 2, LineArray.COORDINATES | LineArray.COLOR_3 );
        //            mRoot.addChild( new Shape3D( axisXLines ) );
        //
        //            axisXLines.setCoordinate( 0, new Point3f( - 10.0f, 0.0f, 0.0f ) );
        //            axisXLines.setCoordinate( 1, new Point3f( 10.0f, 0.0f, 0.0f ) );
        //
        //            axisXLines.setColor( 1, red );
        //            axisXLines.setColor( 0, black );
        //
        //            // create line for Y axis
        //            LineArray axisYLines = new LineArray( 2, LineArray.COORDINATES | LineArray.COLOR_3 );
        //            mRoot.addChild( new Shape3D( axisYLines ) );
        //
        //            axisYLines.setCoordinate( 0, new Point3f( 0f, - 10.0f, 0f ) );
        //            axisYLines.setCoordinate( 1, new Point3f( 0.0f, 10.0f, 0.0f ) );
        //
        //            axisYLines.setColor( 1, green );
        //            axisYLines.setColor( 0, black );
        //
        //            // create line for Z axis
        //            Point3f z1 = new Point3f( 0.0f, 0.0f, - 10.0f );
        //            Point3f z2 = new Point3f( 0.0f, 0.0f, 10.0f );
        //
        //            LineArray axisZLines = new LineArray( 2, LineArray.COORDINATES | LineArray.COLOR_3 );
        //            mRoot.addChild( new Shape3D( axisZLines ) );
        //
        //            axisZLines.setCoordinate( 0, new Point3f( 0f, 0f, - 10.0f ) );
        //            axisZLines.setCoordinate( 1, new Point3f( 0f, 0f, 10.0f ) );
        //
        //            axisZLines.setColor( 1, blue );
        //            axisZLines.setColor( 0, black );
        //        }

        if ( controller .propertyIsTrue( "drawNormals" ) )
            propertyChange("drawNormals", true );

        if ( controller .propertyIsTrue( "drawOutlines" ) )
            propertyChange("drawOutlines", true );

        if ( controller .propertyIsTrue( "showFrameLabels" ) )
            propertyChange("showFrameLabels", true );

        if ( controller .propertyIsTrue( "showIcosahedralLabels" ) )
            propertyChange("showIcosahedralLabels", true );

        VirtualUniverse vu = new VirtualUniverse();
        mLocale = new Locale( vu );
        mLocale.addBranchGraph( mRoot );
    }

    private class FrameLabels extends BranchGroup {
        public FrameLabels() {
            // <editor-fold defaultstate="collapsed">
            setCapability(BranchGroup.ALLOW_DETACH);
            LineArray frameEdges = new LineArray(24, LineArray.COORDINATES | LineArray.COLOR_3);
            int nextEdge = 0;
            addChild(new Shape3D(frameEdges));
            String[] labels = {"-", "0", "+"};
            Color3f frameColor = new Color3f(0.9f, 1.0f, 0.0f);
            float scale = 30f;
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        if (x != 0 || y != 0 || z != 0) {
                            int parity = (x + y + z) % 2;
                            String label = labels[x + 1] + labels[y + 1] + labels[z + 1];
                            Text2D text2D = new Text2D(label, frameColor, "Helvetica", 72, Font.PLAIN);
                            text2D.setRectangleScaleFactor(0.02f);
                            text2D.getGeometry().setCapability(Geometry.ALLOW_INTERSECT);
                            OrientedShape3D os3D = new OrientedShape3D();
                            os3D.setGeometry(text2D.getGeometry());
                            os3D.setAppearance(text2D.getAppearance());
                            os3D.setAlignmentMode(OrientedShape3D.ROTATE_ABOUT_POINT);
                            Transform3D move = new Transform3D();
                            move.setTranslation(new Vector3d(scale * x, scale * y, scale * z));
                            TransformGroup tg = new TransformGroup(move);
                            tg.addChild(os3D);
                            addChild(tg);

                            if (parity == 0) {
                                // odd parity means this is an edge center, so let's render the edge of the cube
                                int zeroCoord = (x == 0) ? 0 : ((y == 0) ? 1 : 2);
                                float[] start = {scale * x, scale * y, scale * z};
                                float[] end = {scale * x, scale * y, scale * z};
                                start[zeroCoord] = scale;
                                end[zeroCoord] = -scale;

                                frameEdges.setCoordinate(nextEdge, new Point3f(start));
                                frameEdges.setColor(nextEdge++, frameColor);
                                frameEdges.setCoordinate(nextEdge, new Point3f(end));
                                frameEdges.setColor(nextEdge++, frameColor);
                            }
                        }
                    }
                }
            }
            // </editor-fold>
        }
    }

    private class IcosahedralLabels extends BranchGroup {
        public IcosahedralLabels() {
            // <editor-fold defaultstate="collapsed">
            setCapability(BranchGroup.ALLOW_DETACH);
            Color3f minusColor = new Color3f(1.0f, 0.0f, 0.0f);
            Color3f plusColor = new Color3f(0.0f, 0.0f, 1.0f);
            AlgebraicField field = new PentagonField();
            IcosahedralSymmetry symm = new IcosahedralSymmetry(field, "temp");
            // we use black for index numbers, but turqouise for location of the text
            Direction indexOrbit = symm.getDirection("black");
            Direction locationOrbit = symm.getDirection("turquoise");
            for (Axis axis : indexOrbit) {
                boolean negative = axis.getSense() == Symmetry.MINUS;
                Color3f numColor = negative ? minusColor : plusColor;
                AlgebraicVector v = locationOrbit.getAxis(axis.normal().toRealVector()).normal();
                RealVector location = v.toRealVector().scale( 11f );
                String label = (negative ? "-" : "") + axis.getOrientation();
                Text2D text2D = new Text2D(label, numColor, "Helvetica", 120, negative ? Font.PLAIN : Font.BOLD);
                text2D.setRectangleScaleFactor(0.02f);
                text2D.getGeometry().setCapability(Geometry.ALLOW_INTERSECT);
                OrientedShape3D os3D = new OrientedShape3D();
                os3D.setGeometry(text2D.getGeometry());
                os3D.setAppearance(text2D.getAppearance());
                os3D.setAlignmentMode(OrientedShape3D.ROTATE_ABOUT_POINT);
                Transform3D move = new Transform3D();
                move.setTranslation(new Vector3d(location.x, location.y, location.z));
                TransformGroup tg = new TransformGroup(move);
                tg.addChild(os3D);
                addChild(tg);
            }
            // </editor-fold>
        }
    }

    @Override
    public void manifestationAdded( RenderedManifestation rm )
    {
        //        int[] /* AlgebraicVector */location = rm.getManifestation().getLocation();
        //        if ( location == null )
        //            location = rm.getShape().getField().origin( 3 );

        RealVector loc = rm .getLocation();
        if ( loc == null )
            loc = new RealVector( 0d, 0d, 0d );

        Appearance appearance = mFactory .getAppearance( rm.getColor(), rm.getGlow() > 0f, rm.getTransparency() > 0f );
        Geometry geom = mFactory .makeSolidGeometry( rm );

        if ( logger .isLoggable( Level.FINEST )
                && rm .getManifestation() == null )
        {
            Direction orbit = rm .getShape() .getOrbit();
            String shape = ( orbit == null )? "BALL" : orbit .getName() + " strut";
            logger .finest( shape + " at " + loc );
        }

        // if we rendering wireframe, we're using absolute coordinates
        if ( ( geom instanceof PointArray ) || ( geom instanceof LineArray ) )
            //            location = rm.getShape().getField().origin( 3 );
            loc = new RealVector( 0d, 0d, 0d );

        Shape3D solidPolyhedron = new Shape3D( geom );
        solidPolyhedron .setCapability( Shape3D.ALLOW_APPEARANCE_WRITE );
        solidPolyhedron .setAppearance( appearance );
        solidPolyhedron .setUserData( rm );

        // omit this if trying to pre-optimize with makeGeometryAt
        Transform3D move = new Transform3D();
        move.setTranslation( new Vector3d( loc.x, loc.y, loc.z ) );
        TransformGroup tg = new TransformGroup( move );
        tg.setCapability( Group.ALLOW_CHILDREN_EXTEND );
        tg.setCapability( Group.ALLOW_CHILDREN_READ );
        tg.setCapability( Group.ALLOW_CHILDREN_WRITE );
        tg.setCapability( BranchGroup.ALLOW_DETACH );
        tg.setCapability( Shape3D.ENABLE_PICK_REPORTING );
        tg.setPickable( true );
        tg.addChild( solidPolyhedron );

        if ( drawOutlines ) {
            geom = mFactory .makeOutlineGeometry( rm );
            Shape3D outlinePolyhedron = new Shape3D( geom );
            outlinePolyhedron .setAppearance( mFactory .getOutlineAppearance() );
            tg .addChild( outlinePolyhedron );
        }

        if(drawNormals && rm.getShape().isPanel()) {
            geom = mFactory .makePanelNormalGeometry( rm );
            Shape3D normalPolyhedron = new Shape3D( geom );
            normalPolyhedron .setAppearance( mFactory .getPanelNormalAppearance() );
            tg .addChild( normalPolyhedron );
        }

        // Create a Text2D leaf node, add it to the scene graph.
        // Text2D text2D = new Text2D( "     label", new Color3f( 0.9f, 1.0f,
        // 0.0f),
        // "Helvetica", 36, Font.ITALIC );
        // text2D .setRectangleScaleFactor( 0.02f );
        // text2D .getGeometry() .setCapability( Geometry .ALLOW_INTERSECT );
        // OrientedShape3D os3D = new OrientedShape3D();
        // os3D .setGeometry( text2D.getGeometry() );
        // os3D .setAppearance( text2D.getAppearance() );
        // os3D .setAlignmentMode( OrientedShape3D.ROTATE_ABOUT_POINT );
        // tg .addChild( os3D );

        BranchGroup group = new BranchGroup();
        group.setCapability( Group.ALLOW_CHILDREN_EXTEND );
        group.setCapability( Group.ALLOW_CHILDREN_READ );
        group.setCapability( Group.ALLOW_CHILDREN_WRITE );
        group.setCapability( Node.ENABLE_PICK_REPORTING );
        group.setCapability( BranchGroup.ALLOW_DETACH );
        group.addChild( tg );

        mScene.addChild( group );
        if ( this .isSticky )
            rm.setGraphicsObject( group );
    }

    @Override
    public void reset()
    {
        mScene.removeAllChildren();
    }

    /* (non-Javadoc)
     * @see com.vzome.core.render.RenderingChanges#manifestationSwitched(com.vzome.core.render.RenderedManifestation, com.vzome.core.render.RenderedManifestation)
     */
    @Override
    public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
    {
        // This call is the only reason we ever do setGraphicsObject,
        //   all in support of the setUserData below.
        BranchGroup target = (BranchGroup) from .getGraphicsObject();

        if ( target == null ) {
            return;
        }
        TransformGroup tg = (TransformGroup) target .getChild( 0 );
        Shape3D poly = (Shape3D) tg .getChild( 0 );
        poly .setUserData( to );  // This keeps picking consistent
        if ( this .isSticky )
        {
            // 
            to .setGraphicsObject( target );
            from .setGraphicsObject( null );
        }
    }

    @Override
    public void manifestationRemoved( RenderedManifestation rm )
    {
        BranchGroup target = (BranchGroup) rm.getGraphicsObject();
        if ( target == null ) {
            return;
        }

        if ( logger .isLoggable( Level.FINEST )
                && rm .getManifestation() == null )
        {
            Direction orbit = rm .getShape() .getOrbit();
            String shape = ( orbit == null )? "BALL" : orbit .getName() + " strut";
            logger .finest( shape + " at " + rm .getLocation() );
        }

        for ( int i = 0; i < mScene.numChildren(); i++ ) {
            Object bg = mScene.getChild( i );
            // Group tg = (Group) bg .getChild( 0 );
            if ( target == bg /* tg .getChild( 0 ) */) {
                mScene.removeChild( i );
                if ( this .isSticky )
                    rm .setGraphicsObject( null );
                return;
            }
        }
        throw new RuntimeException( "polyhedron not in scene!" );
    }

    @Override
    public void glowChanged( RenderedManifestation rm )
    {
        boolean glowOn = rm.getGlow() > 0f;
        if ( mFactory.hasEmissiveColor() )
            if ( glowOn ) {
                ++ mGlowCount;
                ambientForGlow.setEnable( false );
            } else if ( -- mGlowCount == 0 ) {
                ambientForGlow.setEnable( true );
            }
        colorChanged( rm );
    }

    @Override
    public void colorChanged( RenderedManifestation rm )
    {
        Group group = (Group) rm.getGraphicsObject();
        if ( group == null )
            return;
        group = (Group) group.getChild( 0 );

        Node child = group.getChild( 0 );
        if ( ! ( child instanceof Shape3D ) )
            return;

        Shape3D polyhedron = (Shape3D) group.getChild( 0 );
        if ( polyhedron != null ) {
            Appearance newAppearance = mFactory.getAppearance( rm.getColor(), rm.getGlow() > 0f, rm.getTransparency() > 0f );
            polyhedron.setAppearance( newAppearance );
        }
    }

    @Override
    public void locationChanged( RenderedManifestation manifestation )
    {
    }

    @Override
    public void orientationChanged( RenderedManifestation manifestation )
    {
    }

    @Override
    public void shapeChanged( RenderedManifestation rm )
    {
        Group group = (Group) rm.getGraphicsObject();
        if ( group == null )
            return;
        group = (Group) group.getChild( 0 );

        Node child = group.getChild( 0 );

        //        
        // if ( child instanceof Link ) {
        // if ( mWhichShape != 0 )
        // return; // already switched all the balls
        // Switch ballSwitch = (Switch) mBallGroup .getChild( 0 );
        //
        // Geometry newShape = mFactory .makeGeometry( rm );
        // newShape .setCapability( Geometry .ALLOW_INTERSECT );
        // Shape3D polyhedron = new Shape3D( newShape );
        // Appearance appearance = mFactory .getAppearance( rm .getColorName(),
        // rm .getGlow() > 0f, rm .getTransparency() > 0f );
        // polyhedron .setAppearance( appearance );
        // group = new BranchGroup();
        // group .addChild( polyhedron );
        // ballSwitch .addChild( group );
        // mWhichShape = 1;
        // ballSwitch .setWhichChild( mWhichShape );
        // return;
        // }
        //        

        Shape3D polyhedron = (Shape3D) child;
        Polyhedron oldShape = rm .getShape();
        if ( oldShape == null )
        {
            logger .severe( "no shape for: " + rm .getManifestation() .toString() );
        }
        else if ( polyhedron != null ) {
            Geometry newShape = mFactory.makeSolidGeometry( rm );
            newShape.setCapability( Geometry.ALLOW_INTERSECT );
            polyhedron.setGeometry( newShape );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.vorthmann.zome.render.Colors.Changes#colorChanged(java.lang.Object,
     * org.vorthmann.zome.render.Color)
     */
    public void backgroundColorChanged( Color newColor )
    {
        float[] rgb = new float[3];
        Color3f color = new Color3f( newColor.getRGBColorComponents( rgb ) );
        mBackground.setColor( color );
        mFog.setColor( color );
    }

    public LinearFog getFog()
    {
        return mFog;
    }

    private void refreshPolygonOutlines() {
        Collection<BranchGroup> bgs = new ArrayList<>();
        for ( int i = 0; i < mScene .numChildren(); i++ ) {
            BranchGroup bg = (BranchGroup) mScene .getChild( i );
            bgs .add( bg );
        }
        for (BranchGroup branchGroup : bgs) {
            TransformGroup tg = (TransformGroup) branchGroup .getChild( 0 );
            Shape3D solidPoly = (Shape3D) tg .getChild( 0 );
            RenderedManifestation rm = (RenderedManifestation) solidPoly .getUserData();
            if ( rm != null ) {
                this .manifestationRemoved( rm );
                this .manifestationAdded( rm );
            }
        }
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        propertyChange(evt.getPropertyName(), evt.getNewValue(), evt.getOldValue());
    }

    // properties that don't care about oldValue can call this overloaded method
    protected void propertyChange(String propertyName, Object newValue) {
        propertyChange(propertyName, newValue, null);
    }

    protected void propertyChange(String propertyName, Object newValue, Object oldValue) {
        switch (propertyName) {
        case "drawNormals":
            drawNormals = (Boolean) newValue;
            refreshPolygonOutlines();
            break;

        case "drawOutlines":
            drawOutlines = (Boolean) newValue;
            ambientForOutlines.setEnable(drawOutlines);
            directionalForOutlines.setEnable(drawOutlines);
            refreshPolygonOutlines();
            break;

        case "showFrameLabels":
            if ((Boolean) newValue) {
                showFrameLabels();
            } else {
                hideFrameLabels();
            }
            break;

        case "showIcosahedralLabels":
            if ((Boolean) newValue) {
                showIcosahedralLabels();
            } else {
                hideIcosahedralLabels();
            }
            break;

        default:
            break;
        }
    }

    private void showFrameLabels() {
        if (frameLabels == null) {
            // lazy creation upon first use
            frameLabels = new FrameLabels();
        }
        mRoot.addChild(frameLabels);
    }

    private void hideFrameLabels() {
        mRoot.removeChild(frameLabels);
    }

    private void showIcosahedralLabels() {
        if (icosahedralLabels == null) {
            // lazy creation upon first use
            icosahedralLabels = new IcosahedralLabels();
        }
        mRoot.addChild(icosahedralLabels);
    }

    private void hideIcosahedralLabels() {
        mRoot.removeChild(icosahedralLabels);
    }
}
