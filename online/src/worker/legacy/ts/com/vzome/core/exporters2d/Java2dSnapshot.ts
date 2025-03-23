/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters2d {
    export class Java2dSnapshot {
        /*private*/ polygons: java.util.List<Java2dSnapshot.Polygon>;

        /*private*/ lines: java.util.List<Java2dSnapshot.LineSegment>;

        /*private*/ mRect: java.awt.geom.Rectangle2D;

        /*private*/ strokeWidth: number;

        /*private*/ backgroundColor: java.awt.Color;

        public getBackgroundColor(): java.awt.Color {
            return this.backgroundColor;
        }

        public isLineDrawing(): boolean {
            return !this.lines.isEmpty();
        }

        public addPolygon(polygon: Java2dSnapshot.Polygon) {
            this.polygons.add(polygon);
        }

        public addLineSegment(color: java.awt.Color, start: com.vzome.core.math.RealVector, end: com.vzome.core.math.RealVector) {
            this.lines.add(new Java2dSnapshot.LineSegment(color, start, end));
        }

        public depthSort() {
            if (this.isLineDrawing())java.util.Collections.sort<any>(this.lines); else java.util.Collections.sort<any>(this.polygons);
        }

        public setRect(rect: java.awt.geom.Rectangle2D) {
            this.mRect = rect;
        }

        public setStrokeWidth(strokeWidth: number) {
            this.strokeWidth = strokeWidth;
        }

        public getRect(): java.awt.geom.Rectangle2D {
            return this.mRect;
        }

        public getStrokeWidth(): number {
            return this.strokeWidth;
        }

        public getDimension(): java.awt.Dimension {
            return new java.awt.Dimension((<number>this.mRect.getWidth()|0), (<number>this.mRect.getHeight()|0));
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

        public setBackgroundColor(backgroundColor: java.awt.Color) {
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
            mPath: java.awt.geom.GeneralPath;

            mDepth: number;

            mPolyColor: java.awt.Color;

            public getPath(): java.awt.geom.GeneralPath {
                return this.mPath;
            }

            public constructor(color: java.awt.Color, start: com.vzome.core.math.RealVector, end: com.vzome.core.math.RealVector) {
                if (this.mPath === undefined) { this.mPath = null; }
                if (this.mDepth === undefined) { this.mDepth = 0; }
                if (this.mPolyColor === undefined) { this.mPolyColor = null; }
                this.mPolyColor = color;
                this.mPath = new java.awt.geom.GeneralPath();
                this.mPath.moveTo(start.x, start.y);
                this.mPath.lineTo(end.x, end.y);
                this.mDepth = (<any>Math).fround(((<any>Math).fround(start.z + end.z)) / 2.0);
            }

            public getColor(): java.awt.Color {
                return this.mPolyColor;
            }

            /**
             * 
             * @param {com.vzome.core.exporters2d.Java2dSnapshot.LineSegment} other
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
            mPath: java.awt.geom.GeneralPath;

            mDepth: number;

            mSize: number;

            mPolyColor: java.awt.Color;

            public getPath(): java.awt.geom.GeneralPath {
                return this.mPath;
            }

            public size(): number {
                return this.mSize;
            }

            public addVertex(vertex: com.vzome.core.math.RealVector) {
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

            public constructor(color: java.awt.Color) {
                if (this.mPath === undefined) { this.mPath = null; }
                if (this.mDepth === undefined) { this.mDepth = 0; }
                this.mSize = 0;
                if (this.mPolyColor === undefined) { this.mPolyColor = null; }
                this.mPolyColor = color;
                this.mPath = new java.awt.geom.GeneralPath();
            }

            public getColor(): java.awt.Color {
                return this.mPolyColor;
            }

            /**
             * 
             * @param {com.vzome.core.exporters2d.Java2dSnapshot.Polygon} other
             * @return {number}
             */
            public compareTo(other: Java2dSnapshot.Polygon): number {
                const otherZ: number = other.mDepth;
                if (this.mDepth > otherZ)return 1;
                if (this.mDepth < otherZ)return -1;
                return 0;
            }

            public applyLighting(normal: com.vzome.core.math.RealVector, lightDirs: com.vzome.core.math.RealVector[], lightColors: java.awt.Color[], ambient: java.awt.Color) {
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
                this.mPolyColor = new java.awt.Color(red, green, blue);
            }
        }
        Polygon["__class"] = "com.vzome.core.exporters2d.Java2dSnapshot.Polygon";
        Polygon["__interfaces"] = ["java.lang.Comparable"];


    }

}

