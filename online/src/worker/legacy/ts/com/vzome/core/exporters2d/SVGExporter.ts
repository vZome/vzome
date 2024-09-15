/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters2d {
    /**
     * @author scottv
     * @class
     * @extends com.vzome.core.exporters2d.SnapshotExporter
     */
    export class SVGExporter extends com.vzome.core.exporters2d.SnapshotExporter {
        /**
         * 
         * @param {java.awt.Color} bgColor
         */
        outputBackground(bgColor: java.awt.Color) {
            this.output.print("<rect fill=\'#");
            const color: number = bgColor.getRGB() & 16777215;
            this.output.print(javaemul.internal.IntegerHelper.toHexString(color));
            this.output.println$java_lang_Object("\' x=\'1\' y=\'1\' width=\'" + this.width + "\' height=\'" + this.height + "\'/>");
        }

        /**
         * 
         * @param {java.awt.geom.Rectangle2D} rect
         * @param {number} strokeWidth
         */
        outputPrologue(rect: java.awt.geom.Rectangle2D, strokeWidth: number) {
            this.output.println$java_lang_Object("<?xml version=\'1.0\'?>");
            this.output.println$java_lang_Object("<svg version=\'1.1\' xmlns=\'http://www.w3.org/2000/svg\'");
            if (strokeWidth > 0)this.output.println$java_lang_Object("   stroke=\'black\' stroke-linejoin=\'round\' stroke-width=\'" + strokeWidth + "\' ");
            this.output.println$java_lang_Object("   viewBox=\'0 0 " + this.width + " " + this.height + "\' >");
            this.XY_FORMAT.setMaximumFractionDigits(2);
        }

        /**
         * 
         * @param {com.vzome.core.exporters2d.Java2dSnapshot.LineSegment} line
         * @param {boolean} monochrome
         */
        outputLine(line: com.vzome.core.exporters2d.Java2dSnapshot.LineSegment, monochrome: boolean) {
            this.output.print("<path stroke=\'#");
            let color: number = line.getColor().getRGB() & 16777215;
            if (monochrome)color = java.awt.Color.BLACK_$LI$().getRGB() & 16777215;
            const hex: string = javaemul.internal.IntegerHelper.toHexString(color);
            for(let i: number = 0; i < 6 - hex.length; i++) {this.output.print('0');}
            this.output.print(hex);
            this.output.print("\' d=\'");
            this.renderPath(line.getPath());
            this.output.println$java_lang_Object("\'/>");
        }

        /**
         * 
         * @param {com.vzome.core.exporters2d.Java2dSnapshot.Polygon} polygon
         * @param {boolean} doOutline
         */
        outputPolygon(polygon: com.vzome.core.exporters2d.Java2dSnapshot.Polygon, doOutline: boolean) {
            this.output.print("<path fill=\'#");
            const color: number = polygon.getColor().getRGB() & 16777215;
            const hex: string = javaemul.internal.IntegerHelper.toHexString(color);
            for(let i: number = 0; i < 6 - hex.length; i++) {this.output.print('0');}
            this.output.print(hex);
            this.output.print("\' d=\'");
            this.renderPath(polygon.getPath());
            this.output.println$java_lang_Object("\'/>");
        }

        /**
         * 
         */
        outputPostlogue() {
            this.output.println$();
            this.output.println$java_lang_Object("</svg>");
        }

        /**
         * 
         * @param {number} r
         * @param {number} g
         * @param {number} b
         */
        setRGBStrokeColor(r: number, g: number, b: number) {
        }

        /**
         * 
         * @param {number} r
         * @param {number} g
         * @param {number} b
         */
        setRGBFillColor(r: number, g: number, b: number) {
        }

        /**
         * 
         */
        beginPath() {
        }

        /**
         * 
         * @param {number} x
         * @param {number} y
         */
        moveToPoint(x: number, y: number) {
            this.output.print("M " + this.XY_FORMAT.format(x) + " " + this.XY_FORMAT.format((<any>Math).fround(this.height - y)) + " ");
        }

        /**
         * 
         * @param {number} x
         * @param {number} y
         */
        addLineToPoint(x: number, y: number) {
            this.output.print("L " + this.XY_FORMAT.format(x) + " " + this.XY_FORMAT.format((<any>Math).fround(this.height - y)) + " ");
        }

        /**
         * 
         */
        closePath() {
            this.output.print(" z");
        }

        /**
         * 
         */
        fillPath() {
        }

        /**
         * 
         */
        strokePath() {
        }
    }
    SVGExporter["__class"] = "com.vzome.core.exporters2d.SVGExporter";

}

