/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters2d {
    export class QuartzXMLExporter {
        /*private*/ output: java.io.PrintWriter;

        /*private*/ mSnapshot: com.vzome.core.exporters2d.Java2dSnapshot;

        /*private*/ height: number;

        public constructor(snapshot: com.vzome.core.exporters2d.Java2dSnapshot) {
            if (this.output === undefined) { this.output = null; }
            if (this.mSnapshot === undefined) { this.mSnapshot = null; }
            if (this.height === undefined) { this.height = 0; }
            this.mSnapshot = snapshot;
        }

        public exportQuartzXML(writer: java.io.Writer) {
            this.output = new java.io.PrintWriter(writer);
            this.height = (<any>Math).fround(this.mSnapshot.getRect().getHeight());
            const width: number = (<any>Math).fround(this.mSnapshot.getRect().getWidth());
            this.output.println$java_lang_Object("<?xml version=\'1.0\'?>");
            this.output.println$java_lang_Object("<quartz>");
            this.output.println$();
            this.output.println$java_lang_Object("  <beginPage height=\'" + this.height + "\' width=\'" + width + "\'/>");
            let rgb: number[] = this.mSnapshot.getBackgroundColor().getRGBColorComponents(null);
            this.setRGBFillColor(rgb[0], rgb[1], rgb[2]);
            this.beginPath();
            this.output.println$java_lang_Object("    <addRect x=\'0\' y=\'0\' width=\'" + width + "\' height=\'" + this.height + "\'/>");
            this.closePath();
            this.fillPath();
            this.output.println$();
            this.output.println$java_lang_Object("    <setLineWidth width=\'" + this.mSnapshot.getStrokeWidth() + "\'/>");
            for(let index=this.mSnapshot.getPolygons().iterator();index.hasNext();) {
                let polygon = index.next();
                {
                    this.renderPath(polygon.getPath());
                    rgb = polygon.getColor().getRGBColorComponents(null);
                    this.setRGBFillColor(rgb[0], rgb[1], rgb[2]);
                    this.fillPath();
                    this.renderPath(polygon.getPath());
                    this.strokePath();
                }
            }
            this.output.println$();
            this.output.println$java_lang_Object("  <endPage/>");
            this.output.println$java_lang_Object("</quartz>");
            this.output.flush();
            this.output.close();
        }

        public renderPath(path: java.awt.geom.GeneralPath) {
            this.beginPath();
            const segments: java.awt.geom.PathIterator = path.getPathIterator(null);
            while((!segments.isDone())) {{
                const coords: number[] = [0, 0, 0, 0, 0, 0];
                const step: number = segments.currentSegment(coords);
                switch((step)) {
                case java.awt.geom.PathIterator.SEG_MOVETO:
                    this.moveToPoint(coords[0], (<any>Math).fround(this.height - coords[1]));
                    break;
                case java.awt.geom.PathIterator.SEG_LINETO:
                    this.addLineToPoint(coords[0], (<any>Math).fround(this.height - coords[1]));
                    break;
                case java.awt.geom.PathIterator.SEG_CLOSE:
                    this.closePath();
                    break;
                default:
                    break;
                }
                segments.next();
            }};
        }

        /*private*/ setRGBFillColor(r: number, g: number, b: number) {
            this.output.println$();
            this.output.println$();
            this.output.print("    <setRGBFillColor r=\"");
            this.output.print(r + "\" g=\"");
            this.output.print(g + "\" b=\"");
            this.output.println$java_lang_Object(b + "\"/>");
        }

        /*private*/ beginPath() {
            this.output.println$java_lang_Object("    <beginPath/>");
        }

        /*private*/ moveToPoint(x: number, y: number) {
            this.output.print("        <moveToPoint ");
            this.output.println$java_lang_Object(" x=\"" + x + "\" y=\"" + y + "\"/>");
        }

        /*private*/ addLineToPoint(x: number, y: number) {
            this.output.print("        <addLineToPoint ");
            this.output.println$java_lang_Object(" x=\"" + x + "\" y=\"" + y + "\"/>");
        }

        /*private*/ closePath() {
            this.output.println$java_lang_Object("    <closePath/>");
        }

        /*private*/ fillPath() {
            this.output.println$java_lang_Object("    <fillPath/>");
        }

        /*private*/ strokePath() {
            this.output.println$java_lang_Object("    <strokePath/>");
        }
    }
    QuartzXMLExporter["__class"] = "com.vzome.core.exporters2d.QuartzXMLExporter";

}

