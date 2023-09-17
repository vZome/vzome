
package com.vzome.core.editor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumberImpl;
import com.vzome.core.algebra.EdPeggField;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.PlasticNumberField;
import com.vzome.core.algebra.PlasticPhiField;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;
import com.vzome.core.algebra.SnubCubeField;
import com.vzome.core.algebra.SnubDodecField;
import com.vzome.core.algebra.SuperGoldenField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.XmlSymmetryFormat;
import com.vzome.core.construction.Color;
import com.vzome.core.exporters.ColoredMeshJsonExporter;
import com.vzome.core.exporters.DaeExporter;
import com.vzome.core.exporters.DxfExporter;
import com.vzome.core.exporters.GeoGebraExporter;
import com.vzome.core.exporters.GeometryExporter;
import com.vzome.core.exporters.HistoryExporter;
import com.vzome.core.exporters.MathTableExporter;
import com.vzome.core.exporters.OffExporter;
import com.vzome.core.exporters.OpenScadExporter;
import com.vzome.core.exporters.OpenScadMeshExporter;
import com.vzome.core.exporters.POVRayExporter;
import com.vzome.core.exporters.PartGeometryExporter;
import com.vzome.core.exporters.PartsListExporter;
import com.vzome.core.exporters.PdbExporter;
import com.vzome.core.exporters.PlyExporter;
import com.vzome.core.exporters.PythonBuild123dExporter;
import com.vzome.core.exporters.STEPExporter;
import com.vzome.core.exporters.SegExporter;
import com.vzome.core.exporters.ShapesJsonExporter;
import com.vzome.core.exporters.SideEffectsExporter;
import com.vzome.core.exporters.SimpleMeshJsonExporter;
import com.vzome.core.exporters.StlExporter;
import com.vzome.core.exporters.VRMLExporter;
import com.vzome.core.exporters.VefExporter;
import com.vzome.core.exporters2d.PDFExporter;
import com.vzome.core.exporters2d.PostScriptExporter;
import com.vzome.core.exporters2d.SVGExporter;
import com.vzome.core.exporters2d.SnapshotExporter;
import com.vzome.core.kinds.DefaultFieldApplication;
import com.vzome.core.kinds.GoldenFieldApplication;
import com.vzome.core.kinds.HeptagonFieldApplication;
import com.vzome.core.kinds.PlasticPhiFieldApplication;
import com.vzome.core.kinds.PolygonFieldApplication;
import com.vzome.core.kinds.RootThreeFieldApplication;
import com.vzome.core.kinds.RootTwoFieldApplication;
import com.vzome.core.kinds.SnubCubeFieldApplication;
import com.vzome.core.kinds.SnubDodecFieldApplication;
import com.vzome.core.math.RealVector;
import com.vzome.core.render.Colors;
import com.vzome.core.viewing.Lights;
import com.vzome.fields.sqrtphi.SqrtPhiField;
import com.vzome.fields.sqrtphi.SqrtPhiFieldApplication;
import com.vzome.xml.DomParser;

public class Application implements AlgebraicField.Registry
{
    private final Map<String, Supplier<FieldApplication> > fieldAppSuppliers = new HashMap<>();

    private final Colors mColors;

    private final Command.FailureChannel failures;

    private final Properties properties;

    private final Map<String, GeometryExporter> exporters = new HashMap<>();

    private final Lights mLights = new Lights();

    private final Map<String, Supplier<SnapshotExporter>> exporters2d = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger( "com.vzome.core.editor" );

    public Application( boolean enableCommands, Command.FailureChannel failures, Properties overrides )
    {
        // This is executed on the EDT.  Should it be?

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
            float[] vec = mColors .getVectorPref( "direction.light." + i );
            RealVector dir = new RealVector( vec[0], vec[1], vec[2] );
            mLights.addDirectionLight( color, dir );
        }
        mLights .setAmbientColor( mColors .getColorPref( "light.ambient" ) );
        mLights .setBackgroundColor( mColors .getColor( Colors.BACKGROUND ) );

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        /*
         * These exporters fall in two categories: rendering and geometry.  The ones that support the currentSnapshot
         * (the current article page, or the main model) can do rendering export, and can work with just a rendered
         * model (a snapshot), which has lost its attached Manifestation objects.
         * 
         * The ones that require mRenderedModel need access to the RealizedModel objects hanging from it (the
         * Manifestations).  These are the geometry exporters.  They can be aware of the structure of field elements,
         * as well as the orbits and zones.
         * 
         * POV-Ray is a bit of a special case, but only because the .pov language supports coordinate values as expressions,
         * and supports enough modeling that the different strut shapes can be defined, and so on.
         * 
         * The POV-Ray export reuses shapes, etc. just as vZome does, so really works just with the RenderedManifestations
         * (except when the Manifestation is available for structured coordinate expressions).  Again, any rendering exporter
         * could apply the same reuse tricks, working just with RenderedManifestations, so the current limitations to
         * mRenderedModel for many of these is spurious.
         *
         * The base Exporter3d class now has a boolean needsManifestations() method which subclasses should override
         * if they don't rely on Manifestations and therefore can operate on article pages.
         */

        // need these all here just to find the extension in DocumentController.getProperty()
        this .exporters .put( "mesh", new SimpleMeshJsonExporter() );
        this .exporters .put( "cmesh", new ColoredMeshJsonExporter() );
        this .exporters .put( "shapes", new ShapesJsonExporter() );
 
        this .exporters .put( "ggb", new GeoGebraExporter() );
        this .exporters .put( "math", new MathTableExporter() );
        this .exporters .put( "pov", new POVRayExporter() );
        this .exporters .put( "dae", new DaeExporter() );
        this .exporters .put( "step", new STEPExporter() );
        this .exporters .put( "vrml", new VRMLExporter() );
        this .exporters .put( "off", new OffExporter() );
        this .exporters .put( "vef", new VefExporter() );
        this .exporters .put( "partgeom", new PartGeometryExporter() );
        this .exporters .put( "scad", new OpenScadMeshExporter() );
        this .exporters .put( "openscad", new OpenScadExporter() );
        this .exporters .put( "build123d", new PythonBuild123dExporter() );
        this .exporters .put( "partslist", new PartsListExporter() );
        this .exporters .put( "stl", new StlExporter() );
        this .exporters .put( "dxf", new DxfExporter() );
        this .exporters .put( "pdb", new PdbExporter() );
        this .exporters .put( "seg", new SegExporter() );
        this .exporters .put( "ply", new PlyExporter() );
        this .exporters .put( "history", new HistoryExporter() );
        this .exporters .put( "effects", new SideEffectsExporter() );

        this .exporters2d .put( "pdf", PDFExporter::new );
        this .exporters2d .put( "svg", SVGExporter::new );
        this .exporters2d .put( "ps",  PostScriptExporter::new );

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        this.fieldAppSuppliers.put( "golden",   () -> new GoldenFieldApplication( new PentagonField() ) );
        this.fieldAppSuppliers.put( "rootTwo",  () -> new RootTwoFieldApplication( new RootTwoField() ) );
        this.fieldAppSuppliers.put( "heptagon", () -> new HeptagonFieldApplication( new HeptagonField() ) );
        this.fieldAppSuppliers.put( "rootThree", () -> new RootThreeFieldApplication( new RootThreeField() ) );
        this.fieldAppSuppliers.put( "dodecagon", () -> new RootThreeFieldApplication( new RootThreeField() ) );
        this.fieldAppSuppliers.put( "snubCube", () -> new SnubCubeFieldApplication( new SnubCubeField( AlgebraicNumberImpl.FACTORY ) ) );
        this.fieldAppSuppliers.put( "snubDodec", () -> new SnubDodecFieldApplication( new SnubDodecField( AlgebraicNumberImpl.FACTORY ) ) );
        this.fieldAppSuppliers.put( "sqrtPhi", () -> new SqrtPhiFieldApplication( new SqrtPhiField( AlgebraicNumberImpl.FACTORY ) ) );
        // The fields commented out below are only available by the custom menu
        // See the note in getDocumentKind() before adding them here so they show up in the main memu
//        this.fieldAppSuppliers.put( "superGolden", () -> { return new DefaultFieldApplication ( new SuperGoldenField()); } );
//        this.fieldAppSuppliers.put( "plasticNumber", () -> { return new DefaultFieldApplication ( new PlasticNumberField()); } );
//        this.fieldAppSuppliers.put( "plasticPhi", () -> { return new PlasticPhiFieldApplication ( new PlasticPhiField()); } );
//        this.fieldAppSuppliers.put( "edPegg", () -> { return new DefaultFieldApplication ( new EdPeggField()); } );
    }

    public DocumentModel loadDocument( InputStream bytes ) throws Exception
    {
        String noLineNums = this .properties .getProperty( "no.line.numbers" );
        boolean captureLineNumbers = noLineNums == null || noLineNums .equals( "false" );

        Element element = DomParser .parseXml( bytes, captureLineNumbers );
        String tns = element .getNamespaceURI();
        XmlSymmetryFormat format = XmlSymmetryFormat .getFormat( tns );

        if ( format == null )
        {
            String version = element.getAttribute( "version" );
            String edition = element.getAttribute( "edition" );
            if ( edition .isEmpty() )
                edition = "vZome";
            String error = "Unknown " + edition + " file format.";
            if ( ! version .isEmpty() )
                error += "\n Cannot open files created by " + edition + " " + version;
            LOGGER .severe( error );
            throw new IllegalStateException( error );
        }
        else
            LOGGER .fine( "supported format: " + tns );

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
    
    @Override
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
        
        // Parameterized FieldApplications are generated on demand
        if(name.startsWith(PolygonField.FIELD_PREFIX)) {
            int nSides = Integer.parseInt(name.substring(PolygonField.FIELD_PREFIX.length()));
            PolygonField field = new PolygonField( nSides, AlgebraicNumberImpl.FACTORY );
            return new PolygonFieldApplication( field );
        }
//        if(name.startsWith(SqrtField.FIELD_PREFIX)) {
//            int nSides = Integer.parseInt(name.substring(SqrtField.FIELD_PREFIX.length()));
//            return new SqrtFieldApplication(nSides);
//        }

        // These fields are only available by the custom menu
        // unless they are eventually added to this.fieldAppSuppliers in the c'tor.
        // In that case, they will appear in the main menu and they can be removed here
        switch(name) {
        case "superGolden":
            return new DefaultFieldApplication ( new SuperGoldenField( AlgebraicNumberImpl.FACTORY ) );
        case "plasticNumber":
            return new DefaultFieldApplication ( new PlasticNumberField( AlgebraicNumberImpl.FACTORY ) );
        case "plasticPhi":
            return new PlasticPhiFieldApplication( new PlasticPhiField( AlgebraicNumberImpl.FACTORY ) );
        case "edPegg": 
            return new DefaultFieldApplication ( new EdPeggField( AlgebraicNumberImpl.FACTORY ) );
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
			if (in != null) {
				defaults.load(in);
			} else {
				LOGGER.warning("RESOURCE NOT FOUND. Ensure that the build path for the core project includes " + defaultRsrc);
			}
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
			if (in != null) {
				defaults.load(in);
			} else {
				// Gradle builds normally regenerate this file in core/build/buildPropsResource, 
				// but it's not generated in Eclipse builds.
				// Be sure to manually add build/buildPropsResource to both the core and desktop projects in Eclipse
				// then run gradlew core:recordBuildProperties desktop:recordBuildProperties from the command line
				// to generate them. Otherwise "about" version info and log file names will show up as "null".
				LOGGER.warning("RESOURCE NOT FOUND. Ensure that the build path for the core project includes " + defaultRsrc);
			}
        } catch ( IOException ioe ) {
            LOGGER.warning( "problem reading build properties: " + defaultRsrc );
        }
        return defaults;
    }

    public Colors getColors()
    {
        return this .mColors;
    }

    public GeometryExporter getExporter( String format )
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
