/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters2d {
    export class PDFExporter extends com.vzome.core.exporters2d.SnapshotExporter {
        /**
         * 
         * @param {number} r
         * @param {number} g
         * @param {number} b
         */
        setRGBStrokeColor(r: number, g: number, b: number) {
            this.output.print(" " + this.RGB_FORMAT.format(r) + " " + this.RGB_FORMAT.format(g) + " " + this.RGB_FORMAT.format(b) + " RG");
        }

        /**
         * 
         * @param {number} r
         * @param {number} g
         * @param {number} b
         */
        setRGBFillColor(r: number, g: number, b: number) {
            this.output.print(" " + this.RGB_FORMAT.format(r) + " " + this.RGB_FORMAT.format(g) + " " + this.RGB_FORMAT.format(b) + " rg");
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
            this.output.print(" " + this.XY_FORMAT.format(x) + " " + this.XY_FORMAT.format(y) + " m");
        }

        /**
         * 
         * @param {number} x
         * @param {number} y
         */
        addLineToPoint(x: number, y: number) {
            this.output.print(" " + this.XY_FORMAT.format(x) + " " + this.XY_FORMAT.format(y) + " l");
        }

        /**
         * 
         */
        closePath() {
            this.output.print(" h");
        }

        /**
         * 
         */
        fillPath() {
            this.output.print(" f");
        }

        /**
         * 
         */
        strokePath() {
            this.output.print(" S\n");
        }

        /*private*/ streamStart: number;

        /**
         * 
         * @param {java.awt.geom.Rectangle2D} rect
         * @param {number} strokeWidth
         */
        outputPrologue(rect: java.awt.geom.Rectangle2D, strokeWidth: number) {
            this.includeFile("com/vzome/core/exporters/prologue.pdf");
            this.streamStart = this.output.getBytesTotal();
            if (strokeWidth > 0)this.output.print(strokeWidth + " w 1 j\n");
            this.RGB_FORMAT.setMaximumFractionDigits(3);
            this.XY_FORMAT.setMaximumFractionDigits(2);
        }

        /**
         * 
         * @param {java.awt.Color} bgColor
         */
        outputBackground(bgColor: java.awt.Color) {
            const rgb: number[] = bgColor.getRGBColorComponents(null);
            this.setRGBFillColor(rgb[0], rgb[1], rgb[2]);
            this.beginPath();
            this.output.print(" 0 0 " + this.width + " " + this.height + " re\n");
            this.closePath();
            this.fillPath();
        }

        /**
         * 
         */
        outputPostlogue() {
            const streamLen: number = this.output.getBytesTotal() - this.streamStart;
            this.output.print("endstream\n");
            this.output.print("endobj\n");
            const sizeOffset: number = this.output.getBytesTotal();
            this.output.print("5 0 obj " + streamLen + " endobj\n");
            const boxOffset: number = this.output.getBytesTotal();
            this.output.print("6 0 obj [0 0 " + this.width + " " + this.height + "] endobj\n");
            const startXref: number = this.output.getBytesTotal();
            this.includeFile("com/vzome/core/exporters/postlogue.pdf");
            let num: string = /* toString */(''+(sizeOffset));
            for(let i: number = 0; i + num.length < 10; i++) {this.output.print("0");}
            this.output.print(num + " 00000 n \n");
            num = /* toString */(''+(boxOffset));
            for(let i: number = 0; i + num.length < 10; i++) {this.output.print("0");}
            this.output.print(num + " 00000 n \n");
            this.output.print("trailer\n");
            this.output.print("<< /Size 7 /Root 1 0 R >>\n");
            this.output.print("startxref\n");
            this.output.print(startXref + "\n");
            this.output.print("%%EOF\n");
        }

        constructor() {
            super();
            if (this.streamStart === undefined) { this.streamStart = 0; }
        }
    }
    PDFExporter["__class"] = "com.vzome.core.exporters2d.PDFExporter";

}

