
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.exporters;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.editor.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;

public class PartGeometryExporter extends VefExporter
{
    Selection selection;

    public PartGeometryExporter( ViewModel scene, Colors colors,
            Lights lights, RenderedModel model, Selection selection )
    {
        super( scene, colors, lights, model );
        
        this.selection = selection;
    }

    public void doExport( File directory, Writer writer, Dimension screenSize ) throws IOException
    {
        AlgebraicField field = mModel .getField();
        AlgebraicNumber scale = field .createPower( -5 );
        VefModelExporter exporter = new VefModelExporter( writer, field, scale );
        
        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            Manifestation man = ((RenderedManifestation) rms .next()) .getManifestation();
            
            exporter .exportManifestation( man );
        }
        
        exporter .finish();
        
        boolean first = true;
        for( Iterator mans = selection .iterator(); mans .hasNext(); )
        {
            Manifestation man = (Manifestation) mans .next();
            exporter .exportSelectedManifestation( man, first );
            first = false;        
        }
    }
}
