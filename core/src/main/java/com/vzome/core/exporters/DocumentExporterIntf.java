package com.vzome.core.exporters;

import java.io.File;
import java.io.Writer;

public interface DocumentExporterIntf {

    /**
     * Subclasses can override this if they need to export history, the lesson model, or the selection.
     */
    void exportDocument(DocumentIntf doc, File file, Writer writer, int height, int width) throws Exception;

}