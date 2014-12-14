
package com.vzome.core.exporters;

import java.awt.Dimension;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;

public class RulerExporter extends Exporter3d
{
    double maxX, maxY, maxZ;

    public RulerExporter( ViewModel scene, Colors colors,
            Lights lights, RenderedModel model )
    {
        super( scene, colors, lights, model );
        
        AlgebraicField field = model .getField();
        for ( Iterator rms = model .getRenderedManifestations(); rms .hasNext(); )
        {
            RenderedManifestation rm = (RenderedManifestation) rms .next();
            Manifestation man = rm .getManifestation();
            if ( man instanceof Connector )
            {
                int[] loc = ((Connector) man) .getLocation();
                RealVector rv = field .getRealVector( loc );
                double x = rv .x;
                if ( x > maxX )
                    maxX = x;
                double y = rv .y;
                if ( y > maxY )
                    maxY = y;
                double z = rv .z;
                if ( z > maxZ )
                    maxZ = z;
            }
        }
    }

    public void doExport( File directory, Writer writer, Dimension screenSize )
            throws Exception
    {
        PrintWriter pw = new PrintWriter( writer );
        pw .println( "max X = " + maxX );
        pw .println( "max Y = " + maxY );
        pw .println( "max Z = " + maxZ );
    }

    public String getFileExtension()
    {
        return "txt";
    }

}
