import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { RealVector } from "../math/RealVector.js";
import { Color } from "../../../../java/awt/Color.js";
import { Dimension } from "../../../../java/awt/Dimension.js";
import { GeneralPath } from "../../../../java/awt/geom/GeneralPath.js";
import { Rectangle2D } from "../../../../java/awt/geom/Rectangle2D.js";

export class Java2dSnapshot {
    /*private*/ polygons: java.util.List<Java2dSnapshot.Polygon>;

    /*private*/ lines: java.util.List<Java2dSnapshot.LineSegment>;

    /*private*/ mRect: Rectangle2D;

    /*private*/ strokeWidth: number;

    /*private*/ backgroundColor: Color;

    public getBackgroundColor(): Color {
        return this.backgroundColor;
    }

    public isLineDrawing(): boolean {
        return !this.lines.isEmpty();
    }

    public addPolygon(polygon: Java2dSnapshot.Polygon) {
        this.polygons.add(polygon);
    }

    public addLineSegment(color: Color, start: RealVector, end: RealVector) {
        this.lines.add(new Java2dSnapshot.LineSegment(color, start, end));
    }

    public depthSort() {
        if (this.isLineDrawing())java.util.Collections.sort<any>(this.lines); else java.util.Collections.sort<any>(this.polygons);
    }

    public setRect(rect: Rectangle2D) {
        this.mRect = rect;
    }

    public setStrokeWidth(strokeWidth: number) {
        this.strokeWidth = strokeWidth;
    }

    public getRect(): Rectangle2D {
        return this.mRect;
    }

    public getStrokeWidth(): number {
        return this.strokeWidth;
    }

    public getDimension(): Dimension {
        return new Dimension((<number>this.mRect.getWidth()|0), (<number>this.mRect.getHeight()|0));
    }

    public getLines(): java.util.List<Java2dSnapshot.LineSegment> {
        return this.lines;
    }

    public getPolygons(): java.util.List<Java2dSnapshot.Polygon> {
        return this.polygons;
    }

    public clear() {
        this.lines.clear();
        this.polygons.clear();
    }

    public setBackgroundColor(backgroundColor: Color) {
        this.backgroundColor = backgroundColor;
    }

    constructor() {
        this.polygons = <any>(new java.util.ArrayList<any>());
        this.lines = <any>(new java.util.ArrayList<any>());
        if (this.mRect === undefined) { this.mRect = null; }
        if (this.strokeWidth === undefined) { this.strokeWidth = 0; }
        if (this.backgroundColor === undefined) { this.backgroundColor = null; }
    }
}
Java2dSnapshot["__class"] = "com.vzome.core.exporters2d.Java2dSnapshot";


export namespace Java2dSnapshot {

    export class LineSegment implements java.lang.Comparable<Java2dSnapshot.LineSegment> {
        mPath: GeneralPath;

        mDepth: number;

        mPolyColor: Color;

        public getPath(): GeneralPath {
            return this.mPath;
        }

        public constructor(color: Color, start: RealVector, end: RealVector) {
            if (this.mPath === undefined) { this.mPath = null; }
            if (this.mDepth === undefined) { this.mDepth = 0; }
            if (this.mPolyColor === undefined) { this.mPolyColor = null; }
            this.mPolyColor = color;
            this.mPath = new GeneralPath();
            this.mPath.moveTo(start.x, start.y);
            this.mPath.lineTo(end.x, end.y);
            this.mDepth = (<any>Math).fround(((<any>Math).fround(start.z + end.z)) / 2.0);
        }

        public getColor(): Color {
            return this.mPolyColor;
        }

        /**
         * 
         * @param {Java2dSnapshot.LineSegment} other
         * @return {number}
         */
        public compareTo(other: Java2dSnapshot.LineSegment): number {
            const otherZ: number = other.mDepth;
            if (this.mDepth > otherZ)return 1;
            if (this.mDepth < otherZ)return -1;
            return 0;
        }
    }
    LineSegment["__class"] = "com.vzome.core.exporters2d.Java2dSnapshot.LineSegment";
    LineSegment["__interfaces"] = ["java.lang.Comparable"];



    export class Polygon implements java.lang.Comparable<Java2dSnapshot.Polygon> {
        mPath: GeneralPath;

        mDepth: number;

        mSize: number;

        mPolyColor: Color;

        public getPath(): GeneralPath {
            return this.mPath;
        }

        public size(): number {
            return this.mSize;
        }

        public addVertex(vertex: RealVector) {
            ++this.mSize;
            if (this.mSize === 1){
                this.mPath.moveTo(vertex.x, vertex.y);
                this.mDepth = vertex.z;
            } else {
                this.mPath.lineTo(vertex.x, vertex.y);
                this.mDepth += vertex.z;
            }
        }

        public close() {
            this.mDepth /= this.mSize;
            this.mPath.closePath();
        }

        public constructor(color: Color) {
            if (this.mPath === undefined) { this.mPath = null; }
            if (this.mDepth === undefined) { this.mDepth = 0; }
            this.mSize = 0;
            if (this.mPolyColor === undefined) { this.mPolyColor = null; }
            this.mPolyColor = color;
            this.mPath = new GeneralPath();
        }

        public getColor(): Color {
            return this.mPolyColor;
        }

        /**
         * 
         * @param {Java2dSnapshot.Polygon} other
         * @return {number}
         */
        public compareTo(other: Java2dSnapshot.Polygon): number {
            const otherZ: number = other.mDepth;
            if (this.mDepth > otherZ)return 1;
            if (this.mDepth < otherZ)return -1;
            return 0;
        }

        public applyLighting(normal: RealVector, lightDirs: RealVector[], lightColors: Color[], ambient: Color) {
            let redIntensity: number = (<any>Math).fround(ambient.getRed() / 255.0);
            let greenIntensity: number = (<any>Math).fround(ambient.getGreen() / 255.0);
            let blueIntensity: number = (<any>Math).fround(ambient.getBlue() / 255.0);
            for(let i: number = 0; i < lightColors.length; i++) {{
                const intensity: number = (<any>Math).fround(Math.max(normal.dot(lightDirs[i]), 0.0));
                redIntensity += (<any>Math).fround(intensity * ((<any>Math).fround(lightColors[i].getRed() / 255.0)));
                greenIntensity += (<any>Math).fround(intensity * ((<any>Math).fround(lightColors[i].getGreen() / 255.0)));
                blueIntensity += (<any>Math).fround(intensity * ((<any>Math).fround(lightColors[i].getBlue() / 255.0)));
            };}
            const red: number = (<number>((<any>Math).fround(this.mPolyColor.getRed() * Math.min(redIntensity, 1.0)))|0);
            const green: number = (<number>((<any>Math).fround(this.mPolyColor.getGreen() * Math.min(greenIntensity, 1.0)))|0);
            const blue: number = (<number>((<any>Math).fround(this.mPolyColor.getBlue() * Math.min(blueIntensity, 1.0)))|0);
            this.mPolyColor = new Color(red, green, blue);
        }
    }
    Polygon["__class"] = "com.vzome.core.exporters2d.Java2dSnapshot.Polygon";
    Polygon["__interfaces"] = ["java.lang.Comparable"];


}
