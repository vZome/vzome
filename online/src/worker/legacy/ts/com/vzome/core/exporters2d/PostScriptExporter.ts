/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters2d {
    export class PostScriptExporter extends com.vzome.core.exporters2d.SnapshotExporter {
        /**
         * 
         */
        setBlackStrokeColor() {
            this.output.print(" 0 setgray");
        }

        /**
         * 
         * @param {number} r
         * @param {number} g
         * @param {number} b
         */
        setRGBStrokeColor(r: number, g: number, b: number) {
            this.output.print(" " + this.RGB_FORMAT.format(r) + " " + this.RGB_FORMAT.format(g) + " " + this.RGB_FORMAT.format(b) + " setrgbcolor");
        }

        /**
         * 
         * @param {number} r
         * @param {number} g
         * @param {number} b
         */
        setRGBFillColor(r: number, g: number, b: number) {
            this.output.print(" " + this.RGB_FORMAT.format(r) + " " + this.RGB_FORMAT.format(g) + " " + this.RGB_FORMAT.format(b) + " setrgbcolor");
        }

        /**
         * 
         */
        beginPath() {
            this.output.print(" newpath");
        }

        /**
         * 
         * @param {number} x
         * @param {number} y
         */
        moveToPoint(x: number, y: number) {
            this.output.print(" " + this.XY_FORMAT.format(x) + " " + this.XY_FORMAT.format(y) + " moveto");
        }

        /**
         * 
         * @param {number} x
         * @param {number} y
         */
        addLineToPoint(x: number, y: number) {
            this.output.print(" " + this.XY_FORMAT.format(x) + " " + this.XY_FORMAT.format(y) + " lineto");
        }

        /**
         * 
         */
        closePath() {
            this.output.print(" closepath");
        }

        /**
         * 
         */
        fillPath() {
            this.output.print(" fill");
        }

        /**
         * 
         */
        strokePath() {
            this.output.print(" stroke\n");
        }

        /**
         * 
         * @param {java.awt.geom.Rectangle2D} rect
         * @param {number} strokeWidth
         */
        outputPrologue(rect: java.awt.geom.Rectangle2D, strokeWidth: number) {
            if (strokeWidth > 0)this.output.print(strokeWidth + " setlinewidth 1 setlinejoin\n");
            this.RGB_FORMAT.setMaximumFractionDigits(3);
            this.XY_FORMAT.setMaximumFractionDigits(3);
        }

        /**
         * 
         * @param {java.awt.Color} bgColor
         */
        outputBackground(bgColor: java.awt.Color) {
            const rgb: number[] = bgColor.getRGBColorComponents(null);
            this.setRGBFillColor(rgb[0], rgb[1], rgb[2]);
            this.beginPath();
            this.moveToPoint(0.0, 0.0);
            this.addLineToPoint(0.0, this.height);
            this.addLineToPoint(this.width, this.height);
            this.addLineToPoint(this.width, 0.0);
            this.addLineToPoint(0.0, 0.0);
            this.closePath();
            this.fillPath();
        }

        /**
         * 
         */
        outputPostlogue() {
        }
    }
    PostScriptExporter["__class"] = "com.vzome.core.exporters2d.PostScriptExporter";

}

