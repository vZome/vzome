
package com.vzome.core.exporters;

import java.io.File;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.xml.DomSerializer;

/**
 * This is just a variant of the HistoryExporter that includes symmetries and tools,
 * so the resulting file can be interpreted.  We therefore use a different file extension.
 * @author vorth
 *
 */
public class SideEffectsExporter extends DocumentExporter
{
    @Override
    public void exportDocument( com.vzome.core.exporters.DocumentIntf document, File file, Writer writer, int height, int width ) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
        factory .setNamespaceAware( true );
        DocumentBuilder builder = factory .newDocumentBuilder();
        Document dom = builder .newDocument();

        Element modelXml = document .getDetailsXml( dom, true );
        dom .appendChild( modelXml );

        DomSerializer .serialize( dom, writer );
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width )
            throws Exception
    {
        throw new IllegalArgumentException( "SideEffectsExporter not supported" );
    }

    @Override
    public String getFileExtension()
    {
        return "effects.vZome";
    }

}
