
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.editor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javax.vecmath.Vector3f;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.exporters.ColoredMeshJsonExporter;
import com.vzome.core.exporters.DaeExporter;
import com.vzome.core.exporters.DxfExporter;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.exporters.HistoryExporter;
import com.vzome.core.exporters.OffExporter;
import com.vzome.core.exporters.OpenGLExporter;
import com.vzome.core.exporters.POVRayExporter;
import com.vzome.core.exporters.PartsListExporter;
import com.vzome.core.exporters.PdbExporter;
import com.vzome.core.exporters.PlyExporter;
import com.vzome.core.exporters.RulerExporter;
import com.vzome.core.exporters.STEPExporter;
import com.vzome.core.exporters.SecondLifeExporter;
import com.vzome.core.exporters.SegExporter;
import com.vzome.core.exporters.ShapesJsonExporter;
import com.vzome.core.exporters.SimpleMeshJsonExporter;
import com.vzome.core.exporters.StlExporter;
import com.vzome.core.exporters.VRMLExporter;
import com.vzome.core.exporters.VefExporter;
import com.vzome.core.exporters.WebviewJsonExporter;
import com.vzome.core.exporters2d.PDFExporter;
import com.vzome.core.exporters2d.PostScriptExporter;
import com.vzome.core.exporters2d.SVGExporter;
import com.vzome.core.exporters2d.SnapshotExporter;
import com.vzome.core.kinds.GoldenFieldApplication;
import com.vzome.core.kinds.HeptagonFieldApplication;
import com.vzome.core.kinds.RootThreeFieldApplication;
import com.vzome.core.kinds.RootTwoFieldApplication;
import com.vzome.core.kinds.SnubDodecFieldApplication;
import com.vzome.core.model.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.viewing.Lights;
import com.vzome.fields.sqrtphi.SqrtPhiFieldApplication;
import com.vzome.xml.DomUtils;

public class Application implements AlgebraicField.Registry
{
    private final Map<String, Supplier<FieldApplication> > fieldAppSuppliers = new HashMap<>();

    private final Colors mColors;

    private final Command.FailureChannel failures;

    private final Properties properties;

    private final Map<String, Exporter3d> exporters = new HashMap<>();

    private final Lights mLights = new Lights();

    private final Map<String, Supplier<SnapshotExporter>> exporters2d = new HashMap<>();

    private static final Logger logger = Logger.getLogger( "com.vzome.core.editor" );

    public Application( boolean enableCommands, Command.FailureChannel failures, Properties overrides )
    {
        this .failures = failures;

        properties = loadDefaults();
        if ( overrides != null )
        {
            properties .putAll( overrides );
        }
        properties .putAll( loadBuildProperties() );

        mColors = new Colors( properties );

        for ( int i = 1; i <= 3; i++ ) {
            Color color = mColors .getColorPref( "light.directional." + i );
            Vector3f dir = new Vector3f( mColors .getVectorPref( "direction.light." + i ) );
            mLights.addDirectionLight( color, dir );
        }
        mLights .setAmbientColor( mColors .getColorPref( "light.ambient" ) );
        mLights .setBackgroundColor( mColors .getColor( Colors.BACKGROUND ) );

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        this .exporters .put( "mesh", new SimpleMeshJsonExporter() );
        this .exporters .put( "cmesh", new ColoredMeshJsonExporter() );
        this .exporters .put( "shapes", new ShapesJsonExporter() );

        this .exporters .put( "pov", new POVRayExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "opengl", new OpenGLExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "dae", new DaeExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "json", new WebviewJsonExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "step", new STEPExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "vrml", new VRMLExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "off", new OffExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "2life", new SecondLifeExporter( null, this .mColors, this .mLights, null ) );
        Exporter3d vefExporter = new VefExporter( null, this .mColors, this .mLights, null );
        this .exporters .put( "vef", vefExporter );
        this .exporters .put( "partgeom", vefExporter ); // need this here just to find the extension in DocumentController.getProperty()
        this .exporters .put( "partslist", new PartsListExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "size", new RulerExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "stl", new StlExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "dxf", new DxfExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "pdb", new PdbExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "seg", new SegExporter( null, this .mColors, this .mLights, null ) );
        this .exporters .put( "ply", new PlyExporter( this .mColors, this .mLights ) );
        this .exporters .put( "history", new HistoryExporter( null, this .mColors, this .mLights, null ) );

        this .exporters2d .put( "pdf", PDFExporter::new );
        this .exporters2d .put( "svg", SVGExporter::new );
        this .exporters2d .put( "ps",  PostScriptExporter::new );

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        this.fieldAppSuppliers.put("golden", GoldenFieldApplication::new);
        this.fieldAppSuppliers.put("rootTwo", RootTwoFieldApplication::new);
        this.fieldAppSuppliers.put("rootThree", RootThreeFieldApplication::new);
        this.fieldAppSuppliers.put("dodecagon", RootThreeFieldApplication::new);
        this.fieldAppSuppliers.put("heptagon", HeptagonFieldApplication::new);
        this.fieldAppSuppliers.put("snubDodec", SnubDodecFieldApplication::new);
        this.fieldAppSuppliers.put( "sqrtPhi", SqrtPhiFieldApplication::new);
    }

    public DocumentModel loadDocument( InputStream bytes ) throws Exception
    {
        String noLineNums = this .properties .getProperty( "no.line.numbers" );
        boolean captureLineNumbers = noLineNums == null || noLineNums .equals( "false" );

        Element element = DomUtils .parseXml( bytes, captureLineNumbers );
        String tns = element .getNamespaceURI();
        XmlSaveFormat format = XmlSaveFormat .getFormat( tns );

        if ( format == null )
        {
            String version = element.getAttribute( "version" );
            String edition = element.getAttribute( "edition" );
            if ( edition .isEmpty() )
                edition = "vZome";
            String error = "Unknown " + edition + " file format.";
            if ( ! version .isEmpty() )
                error += "\n Cannot open files created by " + edition + " " + version;
            logger .severe( error );
            throw new IllegalStateException( error );
        }
        else
            logger .fine( "supported format: " + tns );

        String fieldName = element .getAttribute( "field" );
        if ( fieldName .isEmpty() )
            // field is qualified in the Zome interchange format
            fieldName = element .getAttributeNS( XmlSaveFormat.CURRENT_FORMAT, "field" );
        if ( fieldName .isEmpty() )
            fieldName = "golden";
        FieldApplication kind = this .getDocumentKind( fieldName );

        return new DocumentModel( kind, failures, element, this );
    }

    public DocumentModel createDocument( String fieldName )
    {
        FieldApplication kind = this .getDocumentKind( fieldName );
        return new DocumentModel( kind, failures, null, this );
    }

    public DocumentModel importDocument( String content, String extension )
    {
        String fieldName = "golden";
        // TODO: use fieldName from VEF input
        FieldApplication kind = this .getDocumentKind( fieldName );
        DocumentModel result = new DocumentModel( kind, failures, null, this );
        Map<String,Object> props = new HashMap<>();
        props .put( "script", content );
        result .doEdit( extension, props );
        return result;
    }
    
    public AlgebraicField getField( String name )
    {
        return this .getDocumentKind( name ) .getField();
    }

    public FieldApplication getDocumentKind( String name )
    {
        Supplier<FieldApplication> supplier = fieldAppSuppliers.get(name);
        if( supplier != null ) {
            return supplier.get();
        }
        // maybe because default.field is invalid in your prefs file?
        String msg = "Unknown Application Type " + name;
        failures.reportFailure(new Failure(msg));
        throw new IllegalArgumentException(msg);
    }

    public Set<String> getFieldNames()
    {
        return fieldAppSuppliers.keySet();
    }

    public static Properties loadDefaults()
    {
        String defaultRsrc = "com/vzome/core/editor/defaultPrefs.properties";
        Properties defaults = new Properties();
        try {
            ClassLoader cl = Application.class.getClassLoader();
            InputStream in = cl.getResourceAsStream( defaultRsrc );
            if ( in != null )
                defaults .load( in );
        } catch ( IOException ioe ) {
            System.err.println( "problem reading default preferences: " + defaultRsrc );
        }
        return defaults;
    }

    public static Properties loadBuildProperties()
    {
        String defaultRsrc = "vzome-core-build.properties";
        Properties defaults = new Properties();
        try {
            ClassLoader cl = Application.class.getClassLoader();
            InputStream in = cl.getResourceAsStream( defaultRsrc );
            if ( in != null )
                defaults .load( in );
        } catch ( IOException ioe ) {
            logger.warning( "problem reading build properties: " + defaultRsrc );
        }
        return defaults;
    }

    public Colors getColors()
    {
        return this .mColors;
    }

    public Exporter3d getExporter( String format )
    {
        return this .exporters .get( format );
    }

    public Lights getLights()
    {
        return this .mLights;
    }

    public String getCoreVersion()
    {
        return this .properties .getProperty( "version" );
    }

    public SnapshotExporter getSnapshotExporter( String format )
    {
        Supplier<SnapshotExporter> supplier = this .exporters2d .get( format );
        if ( supplier == null )
            throw new RuntimeException();
        else
            return supplier.get();
    }
}
