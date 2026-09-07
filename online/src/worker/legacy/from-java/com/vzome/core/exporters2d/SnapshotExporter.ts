import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Java2dSnapshot } from "./Java2dSnapshot.js";
import { ResourceLoader } from "../../xml/ResourceLoader.js";
import { Color } from "../../../../java/awt/Color.js";
import { GeneralPath } from "../../../../java/awt/geom/GeneralPath.js";
import { PathIterator } from "../../../../java/awt/geom/PathIterator.js";
import { Rectangle2D } from "../../../../java/awt/geom/Rectangle2D.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";
import { NumberFormat } from "../../../../java/text/NumberFormat.js";

/**
 * Exports a Java2dSnapshot to a character Writer.
 * Subclassed by SVGExporter and PDFExporter.
 * 
 * @author Scott Vorthmann
 * @class
 */
export abstract class SnapshotExporter {
    output: SnapshotExporter.CountingPrintWriter;

    height: number;

    width: number;

    RGB_FORMAT: NumberFormat;

    XY_FORMAT: NumberFormat;

    public includeFile(rsrcName: string) {
        const boilerplate: string = ResourceLoader.loadStringResource(rsrcName);
        this.output.write$java_lang_String(boilerplate);
    }

    public export(snapshot: Java2dSnapshot, writer: java.io.Writer, doOutlines: boolean, monochrome: boolean, showBackground: boolean) {
        this.XY_FORMAT.setGroupingUsed(false);
        this.XY_FORMAT.setMaximumFractionDigits(2);
        this.RGB_FORMAT.setMaximumFractionDigits(3);
        this.output = new SnapshotExporter.CountingPrintWriter(writer);
        const rect: Rectangle2D = snapshot.getRect();
        this.height = (<any>Math).fround(rect.getHeight());
        this.width = (<any>Math).fround(rect.getWidth());
        const lines: java.util.List<Java2dSnapshot.LineSegment> = snapshot.getLines();
        let strokeWidth: number = snapshot.getStrokeWidth();
        if (!snapshot.isLineDrawing() && !doOutlines)strokeWidth = -1.0;
        this.outputPrologue(snapshot.getRect(), strokeWidth);
        const bgColor: Color = snapshot.getBackgroundColor();
        if (bgColor != null && showBackground)this.outputBackground(bgColor);
        if (!lines.isEmpty())for(let index=lines.iterator();index.hasNext();) {
            let line = index.next();
            {
                this.outputLine(line, monochrome);
            }
        } else for(let index=snapshot.getPolygons().iterator();index.hasNext();) {
            let polygon = index.next();
            {
                this.outputPolygon(polygon, strokeWidth > 0);
            }
        }
        this.outputPostlogue();
        this.output.flush();
        this.output.close();
    }

    /**
     * @param height2
     * @param width
     * @param {Color} bgColor
     */
    abstract outputBackground(bgColor: Color);

    abstract outputPrologue(rect: Rectangle2D, strokeWidth: number);

    abstract outputPostlogue();

    outputLine(line: Java2dSnapshot.LineSegment, monochrome: boolean) {
        this.renderPath(line.getPath());
        const rgb: number[] = line.getColor().getRGBColorComponents(null);
        if (!monochrome)this.setRGBStrokeColor(rgb[0], rgb[1], rgb[2]);
        this.strokePath();
    }

    outputPolygon(polygon: Java2dSnapshot.Polygon, doOutline: boolean) {
        this.renderPath(polygon.getPath());
        const rgb: number[] = polygon.getColor().getRGBColorComponents(null);
        this.setRGBFillColor(rgb[0], rgb[1], rgb[2]);
        this.fillPath();
        if (doOutline){
            this.renderPath(polygon.getPath());
            this.setBlackStrokeColor();
            this.strokePath();
        }
    }

    renderPath(path: GeneralPath) {
        this.beginPath();
        const segments: PathIterator = path.getPathIterator(null);
        while((!segments.isDone())) {{
            const coords: number[] = [0, 0, 0, 0, 0, 0];
            const step: number = segments.currentSegment(coords);
            switch((step)) {
            case PathIterator.SEG_MOVETO:
                this.moveToPoint(coords[0], (<any>Math).fround(this.height - coords[1]));
                break;
            case PathIterator.SEG_LINETO:
                this.addLineToPoint(coords[0], (<any>Math).fround(this.height - coords[1]));
                break;
            case PathIterator.SEG_CLOSE:
                this.closePath();
                break;
            default:
                break;
            }
            segments.next();
        }};
    }

    setBlackStrokeColor() {
    }

    abstract setRGBStrokeColor(r: number, g: number, b: number);

    abstract setRGBFillColor(r: number, g: number, b: number);

    abstract beginPath();

    abstract moveToPoint(x: number, y: number);

    abstract addLineToPoint(x: number, y: number);

    abstract closePath();

    abstract fillPath();

    abstract strokePath();

    constructor() {
        if (this.output === undefined) { this.output = null; }
        if (this.height === undefined) { this.height = 0; }
        if (this.width === undefined) { this.width = 0; }
        this.RGB_FORMAT = NumberFormat.getNumberInstance(java.util.Locale.US);
        this.XY_FORMAT = NumberFormat.getNumberInstance(java.util.Locale.US);
    }
}
SnapshotExporter["__class"] = "com.vzome.core.exporters2d.SnapshotExporter";


export namespace SnapshotExporter {

    export class CountingPrintWriter extends PrintWriter {
        mTotal: number;

        public constructor(writer: java.io.Writer) {
            super(writer);
            this.mTotal = 0;
        }

        public write$char_A$int$int(buf: string[], offset: number, len: number) {
            super.write$char_A$int$int(buf, offset, len);
            this.mTotal += len;
        }

        /**
         * 
         * @param {char[]} buf
         * @param {number} offset
         * @param {number} len
         */
        public write(buf?: any, offset?: any, len?: any) {
            if (((buf != null && buf instanceof <any>Array && (buf.length == 0 || buf[0] == null ||(typeof buf[0] === 'string'))) || buf === null) && ((typeof offset === 'number') || offset === null) && ((typeof len === 'number') || len === null)) {
                return <any>this.write$char_A$int$int(buf, offset, len);
            } else if (((typeof buf === 'string') || buf === null) && offset === undefined && len === undefined) {
                return <any>this.write$java_lang_String(buf);
            } else throw new Error('invalid overload');
        }

        public write$java_lang_String(str: string) {
            super.write$java_lang_String(str);
            this.mTotal += str.length;
        }

        public getBytesTotal(): number {
            return this.mTotal;
        }
    }
    CountingPrintWriter["__class"] = "com.vzome.core.exporters2d.SnapshotExporter.CountingPrintWriter";
    CountingPrintWriter["__interfaces"] = ["java.lang.Appendable","java.io.Closeable","java.lang.AutoCloseable","java.io.Flushable"];


}
