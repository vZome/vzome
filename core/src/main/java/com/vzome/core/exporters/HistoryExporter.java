
package com.vzome.core.exporters;

import java.io.File;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.editor.DocumentModel;
import com.vzome.xml.DomSerializer;

public class HistoryExporter extends DocumentExporter
{
    @Override
    public void exportDocument( DocumentModel document, File file, Writer writer, int height, int width ) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
        factory .setNamespaceAware( true );
        DocumentBuilder builder = factory .newDocumentBuilder();
        Document dom = builder .newDocument();

        Element modelXml = document .getDetailsXml( dom, false );
        dom .appendChild( modelXml );

        DomSerializer .serialize( dom, writer );
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width )
            throws Exception
    {
        throw new IllegalArgumentException( "HistoryExporter not supported" );
    }

    @Override
    public String getFileExtension()
    {
        return "history.xml";
    }

}
