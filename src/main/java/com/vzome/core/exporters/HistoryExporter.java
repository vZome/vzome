
package com.vzome.core.exporters;

import java.io.File;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.math.DomUtils;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;

public class HistoryExporter extends Exporter3d
{
    public HistoryExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
    {
        super( scene, colors, lights, model );
    }

    @Override
    public void doExport( DocumentModel document, File file, File parentFile, Writer writer, int height, int width ) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
        factory .setNamespaceAware( true );
        DocumentBuilder builder = factory .newDocumentBuilder();
        Document dom = builder .newDocument();

        Element modelXml = document .getDetailsXml( dom );
        dom .appendChild( modelXml );

        DomUtils .serialize( dom, writer );
    }

    public void doExport( File directory, Writer writer, int height, int width )
            throws Exception
    {
        throw new IllegalArgumentException( "HistoryExporter not supported" );
    }

    public String getFileExtension()
    {
        return "history.xml";
    }

}
