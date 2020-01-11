
package org.vorthmann.zome.render.java3d;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.LinearFog;
import javax.media.j3d.Node;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Screen3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.RenderingViewer;

public class Java3dRenderingViewer implements RenderingViewer
{
    public Java3dRenderingViewer( Java3dSceneGraph scene, CapturingCanvas3D canvas )
    {
        super();

        //        GraphicsConfiguration gc = SimpleUniverse .getPreferredConfiguration();
        mCanvas = canvas; // new CapturingCanvas3D( gc );

        mScene = scene;

        viewTransform = new TransformGroup();
        view = new View();
        mScene .addView( initView( view, viewTransform, mCanvas ) );

        mLights = mScene .getLightsGroup();
        mFog = mScene .getFog();

        mPickCanvas = new PickCanvas( mCanvas,  mScene .getRoot() );
        mPickCanvas .setMode( PickTool.GEOMETRY );
        mPickCanvas .setTolerance( 0.1f );
    }

    @Override
    public void setEye( int eye )
    {
        this .eye = eye;
    }

    private int eye = MONOCULAR;

    private Java3dSceneGraph mScene;

    protected View view;

    protected TransformGroup mLights, viewTransform;

    protected LinearFog mFog;

    private CapturingCanvas3D mCanvas;

    private PickCanvas mPickCanvas;


    private static BranchGroup initView( View view, TransformGroup trans, Canvas3D canvas )
    {
        ViewPlatform vp = new ViewPlatform();
        trans .setCapability( TransformGroup .ALLOW_TRANSFORM_WRITE );
        BranchGroup bg = new BranchGroup();
        bg .addChild( trans );
        trans .addChild( vp );
        view .addCanvas3D( canvas );
        view .setPhysicalBody( new PhysicalBody() );
        view .setPhysicalEnvironment( new PhysicalEnvironment() );
        view .attachViewPlatform( vp );
        view .setFrontClipPolicy( View.VIRTUAL_EYE );
        view .setBackClipPolicy( View.VIRTUAL_EYE );
        view .setScreenScalePolicy( View .SCALE_EXPLICIT );
        return bg;
    }

    /* (non-Javadoc)
     * @see org.vorthmann.zome.render.java3d.RenderingViewer#getCanvas()
     */
    public Component getCanvas()
    {
        return mCanvas;
    }

    /* (non-Javadoc)
     * @see org.vorthmann.zome.render.java3d.RenderingViewer#getRenderingChanges()
     */
    @Override
    public RenderingChanges getRenderingChanges()
    {
        return mScene;
    }

    @Override
    public void setViewTransformation( Matrix4d matrix, int eye )
    {
        if ( eye == this.eye )
        {
            Transform3D trans = new Transform3D( matrix );
            viewTransform .setTransform( trans );
            mLights .setTransform( trans );
        }
    }

    //    public void setLeftEyeViewTransformation( Transform3D trans )
    //    {
    //        mLeftEyeViewTransform .setTransform( trans );
    //    }

    @Override
    public void setPerspective( double fov, double aspectRatio, double near, double far )
    {
        view .setProjectionPolicy( View.PERSPECTIVE_PROJECTION );
        view .setScreenScale( 1d );
        view .setFieldOfView( fov );
        //        mLeftEyeView .setProjectionPolicy( View.PERSPECTIVE_PROJECTION );
        //        mLeftEyeView .setScreenScale( 1d );
        //        mLeftEyeView .setFieldOfView( fov );
        setDistances( near, far );
    }

    @Override
    public void setOrthographic( double halfEdge, double near, double far )
    {
        view .setProjectionPolicy( View.PARALLEL_PROJECTION );
        view .setScreenScale( 1 / ( 6 * halfEdge ) );  // WHY 6???
        //        mLeftEyeView .setProjectionPolicy( View.PARALLEL_PROJECTION );
        //        mLeftEyeView .setScreenScale( 1 / ( 6 * halfEdge ) );  // WHY 6???
        setDistances( near, far );
    }

    protected void setDistances( double near, double far )
    {
        view .setFrontClipDistance( near );
        view .setBackClipDistance( far );
        //        mLeftEyeView .setFrontClipDistance( near );
        //        mLeftEyeView .setBackClipDistance( far );
        double diff = (far-near)/5; // offset from near and far by 20%
        mFog .setBackDistance( far  );
        mFog .setFrontDistance( near + 2 * diff );
    }


    @Override
    public void pickPoint( MouseEvent e, Point3d virtualCursor, Point3d eyePt )
    {
        Transform3D imagePlateToVworld = new Transform3D();
        mCanvas .getImagePlateToVworld( imagePlateToVworld );

        mCanvas .getPixelLocationInImagePlate( e .getX(), e .getY(), virtualCursor );
        imagePlateToVworld .transform( virtualCursor );

        mCanvas .getCenterEyeInImagePlate( eyePt );
        imagePlateToVworld .transform( eyePt );
    }


    @Override
    public RenderedManifestation pickManifestation( MouseEvent e )
    {
        PickResult pickResult = null;
        try  {
            mPickCanvas .setShapeLocation( e );
            pickResult = mPickCanvas .pickClosest();
        } catch( Throwable t ) {
            t .printStackTrace();
        }
        Node node = pickResult == null? null : pickResult .getObject();

        // for scenegraph.Factory approach
        //        Node node = pickResult == null? null : pickResult .getNode( PickResult.BRANCH_GROUP );

        RenderedManifestation picked = node == null? null : (RenderedManifestation) node .getUserData();
        return picked;
    }

    @Override
    public void captureImage( int maxSize, boolean withAlpha, RenderingViewer.ImageCapture capture )
    {
        if ( mCanvas .isOffScreen() )
        {
            BufferedImage bImage = new BufferedImage( maxSize, maxSize * 4 / 5, BufferedImage.TYPE_INT_ARGB );

            ImageComponent2D buffer = new ImageComponent2D( ImageComponent.FORMAT_RGB, bImage );

            Screen3D sOff = mCanvas .getScreen3D();
            Dimension dim = new Dimension( 1024, 768 );
            sOff .setSize( dim );
            sOff .setPhysicalScreenWidth( 1024 );
            sOff .setPhysicalScreenHeight( 768 );

            mCanvas .setOffScreenLocation( 0, 0 );
            mCanvas .setOffScreenBuffer( buffer );
            mCanvas .renderOffScreenBuffer();
            mCanvas .waitForOffScreenRendering();
            capture .captureImage( mCanvas .getOffScreenBuffer() .getImage() );
        }
        else
        {
            mCanvas .maxImageSize = maxSize;
            mCanvas .m_imageHandler = capture;
            mCanvas .repaint();
        }
        //mCanvas .m_imageHandler = null; mCanvas .maxImageSize = -1; // will be done by canvas, on another thread
    }

    @Override
    public Collection<RenderedManifestation> pickCube()
    {
        PickResult[] pickResult = null;
        try  {
            mPickCanvas .setShapeBounds( new BoundingBox( new Point3d(0,0,-50), new Point3d(50,50,50) ), new Point3d(1d,1d,1d) );
            pickResult = mPickCanvas .pickAll();
        } catch( Throwable t ) {
            t .printStackTrace();
        }
        Collection<RenderedManifestation> result = new ArrayList<>();
        for ( int i = 0; i < pickResult.length; i++ )
        {
            Node node = pickResult[ i ] .getObject();
            RenderedManifestation picked = (RenderedManifestation) node .getUserData();
            result .add( picked );
        }

        // for scenegraph.Factory approach
        //        Node node = pickResult == null? null : pickResult .getNode( PickResult.BRANCH_GROUP );

        return result;
    }
}
