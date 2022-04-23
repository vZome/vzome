package com.vzome.core.exporters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;

// Combines features from DaeExporter and OffExporter
public class GeoGebraExporter extends Exporter3d
{
    private static final String GEOGEBRA_TEMPLATE_PATH = "com/vzome/core/exporters/geogebra/";
    private static final String GEOGEBRA_XML = "geogebra.xml";

    public GeoGebraExporter(Camera scene, Colors colors, Lights lights, RenderedModel model) {
        super(scene, colors, lights, model);
    }
    
    private void addFixedResourceFiles(ZipOutputStream zos) throws IOException {
        final String[] FIXED_RESOURCES = { 
            "geogebra_defaults2d.xml", 
            "geogebra_defaults3d.xml",
            "geogebra_javascript.js", 
            "geogebra_thumbnail.png" // TODO: generate the png from the model instead of a fixed resource
        };
        byte data[] = new byte[1024];
        for (String name : FIXED_RESOURCES) {
            InputStream bytes = getClass().getClassLoader().getResourceAsStream(GEOGEBRA_TEMPLATE_PATH + name);
            zos.putNextEntry(new ZipEntry(name));
            int count;
            while ((count = bytes.read(data, 0, data.length)) != -1) {
                zos.write(data, 0, count);
            }
            zos.closeEntry();
        }
    }

    @Override
    public void doExport( File zipFile, Writer dontUseThisWriter, int height, int width ) 
            throws IOException, XPathExpressionException 
    {
        final Map<AlgebraicVector, Connector> balls = new HashMap<>();
        final Map<AlgebraicVector, String> labels = new HashMap<>();
        final SortedSet<AlgebraicVector> vertices = new TreeSet<>();

        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm.getManifestation();
            if (man instanceof Connector) {
                Connector ball = (Connector) man;
                balls.put(ball.getLocation(), ball);
                vertices.add(ball.getLocation());
            } else if (man instanceof Strut) {
                vertices.add(man.getLocation());
                vertices.add(((Strut) man).getEnd());
            } else if (man instanceof Panel) {
                for (AlgebraicVector vertex : (Panel) man) {
                    vertices.add(vertex);
                }
            }
        }
        // Now that we have all of the unique vertices sorted in vertices,
        // we'll need their index for labels, so we copy them into an ArrayList, preserving their sorted order.
        // so we can get their index into that array.
        ArrayList<AlgebraicVector> indices = new ArrayList<>(vertices);

        GeoGebraDocument doc = new GeoGebraDocument();
        doc.setScene(mScene, mLights.getBackgroundColor());

        Embedding embedding = mModel.getEmbedding();
        int pointIndex = 0;
        // add all of the points first
        for (AlgebraicVector v : vertices) {
            Connector ball = balls.get(v); // may be null;
            String label = "P" + pointIndex++;
            labels.put(v, label);
            doc.addVertex(v, ball, label, embedding);
        }
        {
            // add all of the struts second
            int strutIndex = 0;
            for (RenderedManifestation rm : mModel) {
                Manifestation man = rm.getManifestation();
                if (man instanceof Strut) {
                    doc.addStrut((Strut) man, labels, "strut" + strutIndex++);
                }
            }
        }
        {
            // add all of the panels last
            int panelIndex = 0;
            for (RenderedManifestation rm : mModel) {
                Manifestation man = rm.getManifestation();
                if (man instanceof Panel) {
                    doc.addPanel((Panel) man, labels, "panel" + panelIndex++);
                }
            }
        }
        
        // A try-with-resources block closes the resource even if an exception occurs
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                addFixedResourceFiles(zos);
                doc.write(zos);
            } // auto close zos
        } // auto close fos

    }
    
    @Override
    public String getFileExtension() {
        return "ggb";
    }

    @Override
    public String getContentType() {
        // I'm not sure if either of these is right, but they might not matter
        return "application/vnd.geogebra.file"; // This agrees with https://www.freeformatter.com/mime-types-list.html
        // return "application-x/geogebra-file"; // I found this one somewhere too...
    }
    
    private class GeoGebraDocument {
        private Document doc;
        private XPath xpath;
        private Element constructionNode;
        private Element pointTemplate;
        private Element point3dTemplate;
        private Element segmentCommandTemplate;
        private Element segment3dTemplate;
        private Element polygonCommandTemplate;
        private Element polygon3dTemplate;
        private Element auxiliaryTemplate;

        GeoGebraDocument() {
            // A try-with-resources block closes the resource even if an exception occurs
            try (InputStream bytes = getClass().getClassLoader()
                    .getResourceAsStream(GEOGEBRA_TEMPLATE_PATH + GEOGEBRA_XML)) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                // factory .setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                doc = builder.parse(bytes);
                // Create XPathFactory object
                XPathFactory xpathFactory = XPathFactory.newInstance();
                // Create XPath object
                xpath = xpathFactory.newXPath();

                constructionNode = (Element) xpath.evaluate("//geogebra//construction", doc.getDocumentElement(),
                        XPathConstants.NODE);

                pointTemplate = getTemplate("//expression[@type='point']", constructionNode);
                point3dTemplate = getTemplate("//element[@type='point3d']", constructionNode);

                segmentCommandTemplate = getTemplate("//command[@name='Segment']", constructionNode);
                segment3dTemplate = getTemplate("//element[@type='segment3d']", constructionNode);

                polygonCommandTemplate = getTemplate("//command[@name='Polygon']", constructionNode);
                polygon3dTemplate = getTemplate("//element[@type='polygon3d']", constructionNode);
                
                auxiliaryTemplate = getTemplate("//element[@type='segment3d']//auxiliary", constructionNode);

                // remove the remaining child nodes. They are not needed as templates.
                while (constructionNode.hasChildNodes()) {
                    constructionNode.removeChild(constructionNode.getLastChild());
                }
            } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        
        private Element getTemplate(String expression, Element parent) throws XPathExpressionException {
            Element template = (Element) xpath.evaluate(expression, parent, XPathConstants.NODE);
            if (template != null) {
                // parentNode may not be parent parameter when nodes are nested 
                template.getParentNode().removeChild(template);
            }
            return template;
        }
        
        private void setScene(Camera scene, Color color) throws XPathExpressionException {
            Element euclidianView3DNode = (Element) xpath.evaluate("//geogebra//euclidianView3D",
                    doc.getDocumentElement(), XPathConstants.NODE);
            { // coordSystem
                float viewDistance = scene.getViewDistance();
                Element coordSystem = (Element) xpath.evaluate("coordSystem", euclidianView3DNode, XPathConstants.NODE);
                coordSystem.setAttribute("scale", Float.toString(viewDistance / 3.2f)); // seems close w/o zoom
                // TODO: Adjust for the zoom level
            }
            { // bgColor
                Element bgColor = (Element) xpath.evaluate("bgColor", euclidianView3DNode, XPathConstants.NODE);
                bgColor.setAttribute("r", Integer.toString(color.getRed()));
                bgColor.setAttribute("g", Integer.toString(color.getGreen()));
                bgColor.setAttribute("b", Integer.toString(color.getBlue()));
                // No alpha for bg color
            }
            { // projection
                Element projection = (Element) xpath.evaluate("projection", euclidianView3DNode, XPathConstants.NODE);
                projection.setAttribute("type", scene.isPerspective() ? "1" : "0");
            }
        }

        private void addVertex(AlgebraicVector v, Connector ball, String label, Embedding embedding)
                throws XPathExpressionException 
        {
            // Be sure to use doubles here, not floats (as in a RealVector)
            // because GeoGebra requires the vertices to use double precision 
            // or else when rendering panels with more than 3 vertices,
            // it may decide they are non-coplanar and it won't render them
            // but it doesn't give any warning either.
            // It will render triangles and polygon edges and struts 
            // because they are never non-coplanar. 
            // The problem originally showed up with pentagonal panels not being rendered
            // but their edges and everything else were being shown.  
            double[] rvDouble = embedding.isTrivial() ? v.to3dDoubleVector() : embedding.embedInR3Double(v);
            final String x = Double.toString(rvDouble[0]);
            final String y = Double.toString(rvDouble[1]);
            final String z = Double.toString(rvDouble[2]);
            { // point
                Element point = (Element) pointTemplate.cloneNode(true);
                point.setAttribute("label", label);
                final String exp = "(" + x + "," + y + "," + z + ")";
                point.setAttribute("exp", exp);
                constructionNode.appendChild(point);
            }
            { // point3d
                Element point3d = (Element) point3dTemplate.cloneNode(true);
                point3d.setAttribute("label", label);
                // show
                Element show = (Element) xpath.evaluate("show", point3d, XPathConstants.NODE);
                show.setAttribute("object", ball == null ? "false" : "true");
                // pointSize
                Element pointSize = (Element) xpath.evaluate("pointSize", point3d, XPathConstants.NODE);
                pointSize.setAttribute("val", ball == null ? "3" : "9");
                // color
                setColor((ball == null) ? Color.WHITE : ball.getColor(), point3d);
                // coords
                Element coords = (Element) xpath.evaluate("coords", point3d, XPathConstants.NODE);
                coords.setAttribute("x", x);
                coords.setAttribute("y", y);
                coords.setAttribute("z", z);
                constructionNode.appendChild(point3d);
            }
        }
        
        private void addStrut(Strut strut, Map<AlgebraicVector, String> labels, String label)
                throws XPathExpressionException 
        {
            { // command
                Element command = (Element) segmentCommandTemplate.cloneNode(true);
                Element input = (Element) xpath.evaluate("input", command, XPathConstants.NODE);
                input.setAttribute("a0", labels.get(strut.getLocation()));
                input.setAttribute("a1", labels.get(strut.getEnd()));
                Element output = (Element) xpath.evaluate("output", command, XPathConstants.NODE);
                output.setAttribute("a0", label);
                constructionNode.appendChild(command);
            }
            addSegment3d(label, strut.getColor(), false);
        }

        private void addSegment3d(String label, Color color, boolean auxiliary) throws XPathExpressionException {
            Element segment3d = (Element) segment3dTemplate.cloneNode(true);
            segment3d.setAttribute("label", label);
            if(auxiliary) {
                Element auxiliaryElement = (Element) auxiliaryTemplate.cloneNode(true);
                auxiliaryElement.setAttribute("val",  "false");
                segment3d.appendChild(auxiliaryElement);
            }
            setColor(color, segment3d);
            Element lineStyle = (Element) xpath.evaluate("lineStyle", segment3d, XPathConstants.NODE);
            lineStyle.setAttribute("thickness", auxiliary ? "5" : "10");
            constructionNode.appendChild(segment3d);
        }

        private void addPanel(Panel panel, Map<AlgebraicVector, String> labels, String label)
                throws XPathExpressionException 
        {
            { // command
                Element command = (Element) polygonCommandTemplate.cloneNode(true);
                Element input = (Element) xpath.evaluate("input", command, XPathConstants.NODE);
                Element output = (Element) xpath.evaluate("output", command, XPathConstants.NODE);
                int arg = 0;
                output.setAttribute("a0", label);
                for (AlgebraicVector vertex : panel) {
                    input.setAttribute("a" + arg++, labels.get(vertex));
                    // yes, the coresponding input and output arg indices are offset by 1 here
                    output.setAttribute("a" + arg, label + "_{" + labels.get(vertex) + "}");
                }
                constructionNode.appendChild(command);
            }
            Color color = panel.getColor();
            { // polygon3d
                Element polygon3d = (Element) polygon3dTemplate.cloneNode(true);
                polygon3d.setAttribute("label", label);
                setColor(color, polygon3d);
                constructionNode.appendChild(polygon3d);
            }
            for (AlgebraicVector vertex : panel) {
                addSegment3d(label + "_{" + labels.get(vertex) + "}", color, true);
            }
        }
        
        private void setColor(Color color, Element parent) throws XPathExpressionException {
            Element objColor = (Element) xpath.evaluate("objColor", parent, XPathConstants.NODE);
            objColor.setAttribute("r", Integer.toString(color.getRed()));
            objColor.setAttribute("g", Integer.toString(color.getGreen()));
            objColor.setAttribute("b", Integer.toString(color.getBlue()));
            objColor.setAttribute("alpha", Float.toString(color.getAlpha() / 255f));
            ;
        }
        
        // This is mostly copied from DaeExporter
        private void write(ZipOutputStream zos) {
            // Pretty-prints a DOM document to XML using DOM Load and Save's LSSerializer.
            // Note that the "format-pretty-print" DOM configuration parameter can only be
            // set in JDK 1.6+.
            DOMImplementation domImplementation = doc.getImplementation();
            if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0")) {
                DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS", "3.0");
                LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
                DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
                if (domConfiguration.canSetParameter("format-pretty-print", Boolean.TRUE)) {
                    lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
                    LSOutput lsOutput = domImplementationLS.createLSOutput();
                    lsOutput.setEncoding("UTF-8");
                    StringWriter sw = new StringWriter();
                    lsOutput.setCharacterStream(sw);
                    lsSerializer.write(doc, lsOutput);
                    try {
                        sw.close();
                        zos.putNextEntry(new ZipEntry(GEOGEBRA_XML));
                        String xml = sw.toString();
                        System.out.println(xml);
                        zos.write(xml.getBytes());
                        zos.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new RuntimeException("DOMConfiguration 'format-pretty-print' parameter isn't settable.");
                }
            } else {
                throw new RuntimeException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
            }
        }
    }
}
