/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.render {
    export class Colors implements java.lang.Iterable<string> {
        public constructor(props: java.util.Properties) {
            this.mColors = <any>(new java.util.TreeMap<any, any>());
            this.mListeners = <any>(new java.util.ArrayList<any>());
            if (this.properties === undefined) { this.properties = null; }
            this.mNextNewColor = 3221;
            this.STEP = 73;
            this.properties = props;
        }

        public static RGB_CUSTOM: string = "rgb.custom";

        public static RGB_ORBIT: string = "rgb.orbit";

        public static PREFIX: string = "";

        public static BACKGROUND: string; public static BACKGROUND_$LI$(): string { if (Colors.BACKGROUND == null) { Colors.BACKGROUND = Colors.PREFIX + "background"; }  return Colors.BACKGROUND; }

        public static PANEL: string; public static PANEL_$LI$(): string { if (Colors.PANEL == null) { Colors.PANEL = Colors.PREFIX + "panel"; }  return Colors.PANEL; }

        public static HIGHLIGHT: string; public static HIGHLIGHT_$LI$(): string { if (Colors.HIGHLIGHT == null) { Colors.HIGHLIGHT = Colors.PREFIX + "highlight"; }  return Colors.HIGHLIGHT; }

        public static HIGHLIGHT_MAC: string; public static HIGHLIGHT_MAC_$LI$(): string { if (Colors.HIGHLIGHT_MAC == null) { Colors.HIGHLIGHT_MAC = Colors.HIGHLIGHT_$LI$() + ".mac"; }  return Colors.HIGHLIGHT_MAC; }

        public static CONNECTOR: string; public static CONNECTOR_$LI$(): string { if (Colors.CONNECTOR == null) { Colors.CONNECTOR = Colors.PREFIX + "connector"; }  return Colors.CONNECTOR; }

        public static DIRECTION: string; public static DIRECTION_$LI$(): string { if (Colors.DIRECTION == null) { Colors.DIRECTION = Colors.PREFIX + "direction."; }  return Colors.DIRECTION; }

        public static PLANE: string; public static PLANE_$LI$(): string { if (Colors.PLANE == null) { Colors.PLANE = Colors.DIRECTION_$LI$() + "plane."; }  return Colors.PLANE; }

        /*private*/ mColors: java.util.Map<string, com.vzome.core.construction.Color>;

        /*private*/ mListeners: java.util.List<Colors.Changes>;

        /*private*/ properties: java.util.Properties;

        public addColor(name: string, color: com.vzome.core.construction.Color) {
            this.mColors.put(name, color);
            for(let index=this.mListeners.iterator();index.hasNext();) {
                let next = index.next();
                {
                    next.colorAdded(name, color);
                }
            }
        }

        public setColor(name: string, color: com.vzome.core.construction.Color) {
            this.mColors.put(name, color);
            for(let index=this.mListeners.iterator();index.hasNext();) {
                let next = index.next();
                {
                    next.colorChanged(name, color);
                }
            }
        }

        public addListener(changes: Colors.Changes) {
            this.mListeners.add(changes);
        }

        public removeListener(changes: Colors.Changes) {
            this.mListeners.remove(changes);
        }

        /*private*/ mNextNewColor: number;

        /*private*/ STEP: number;

        static NO_VECTOR: number[]; public static NO_VECTOR_$LI$(): number[] { if (Colors.NO_VECTOR == null) { Colors.NO_VECTOR = [0.0, 0.0, 0.0]; }  return Colors.NO_VECTOR; }

        public getVectorPref(name: string): number[] {
            let result: number[] = Colors.NO_VECTOR_$LI$();
            const pref: string = this.properties.getProperty(name);
            if (pref == null || (pref === ("")))return result;
            result = /* clone */((o: any) => { if (o.clone != undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(result);
            const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(pref, ", ");
            let i: number = 0;
            while((tokens.hasMoreTokens())) {result[i++] = javaemul.internal.FloatHelper.parseFloat(tokens.nextToken())};
            return result;
        }

        public getColorPref(name: string): com.vzome.core.construction.Color {
            const percents: number[] = this.getVectorPref("color.percent." + name);
            if (percents !== Colors.NO_VECTOR_$LI$()){
                return new com.vzome.core.construction.Color(Math.round((<any>Math).fround((<any>Math).fround(percents[0] * 255) / 100)), Math.round((<any>Math).fround((<any>Math).fround(percents[1] * 255) / 100)), Math.round((<any>Math).fround((<any>Math).fround(percents[2] * 255) / 100)));
            }
            const pref: string = this.properties.getProperty("color." + name);
            return Colors.parseColor(pref);
        }

        public static parseColor(colorString: string): com.vzome.core.construction.Color {
            if (colorString == null || (colorString === ("")))return com.vzome.core.construction.Color.WHITE_$LI$();
            const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(colorString, ", ");
            const rgb: number[] = [0, 0, 0];
            let i: number = 0;
            while((tokens.hasMoreTokens())) {rgb[i++] = javaemul.internal.IntegerHelper.parseInt(tokens.nextToken())};
            return new com.vzome.core.construction.Color(rgb[0], rgb[1], rgb[2]);
        }

        public static getColorName(color: com.vzome.core.construction.Color): string {
            return Colors.RGB_ORBIT + " " + color.getRed() + " " + color.getGreen() + " " + color.getBlue();
        }

        public getColor(name: string): com.vzome.core.construction.Color {
            let color: com.vzome.core.construction.Color = this.mColors.get(name);
            if (color == null){
                if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(name, Colors.DIRECTION_$LI$())){
                    const prefName: string = name.substring(Colors.DIRECTION_$LI$().length);
                    color = this.getColorPref(prefName);
                } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(name, Colors.PLANE_$LI$())){
                    const prefName: string = name.substring(Colors.PLANE_$LI$().length);
                    color = this.getColorPref(prefName);
                    color = color.getPastel();
                } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(name, Colors.RGB_ORBIT) || /* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(name, Colors.RGB_CUSTOM)){
                    const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(name);
                    tokens.nextToken();
                    const r: number = javaemul.internal.IntegerHelper.parseInt(tokens.nextToken());
                    const g: number = javaemul.internal.IntegerHelper.parseInt(tokens.nextToken());
                    const b: number = javaemul.internal.IntegerHelper.parseInt(tokens.nextToken());
                    color = new com.vzome.core.construction.Color(r, g, b);
                } else if (name === Colors.CONNECTOR_$LI$())color = this.getColorPref("white"); else if (name === Colors.HIGHLIGHT_$LI$())color = this.getColorPref("highlight"); else if (name === Colors.HIGHLIGHT_MAC_$LI$())color = this.getColorPref("highlight.mac"); else if (name === Colors.PANEL_$LI$())color = this.getColorPref("panels"); else if (name === Colors.BACKGROUND_$LI$())color = this.getColorPref("background");
                if (color == null){
                    this.mNextNewColor = (this.mNextNewColor + this.STEP) % 4096;
                    let i: number = this.mNextNewColor;
                    const r: number = 135 + ((i % 16) << 3);
                    i = i >> 4;
                    const g: number = 135 + ((i % 16) << 3);
                    i = i >> 4;
                    const b: number = 135 + ((i % 16) << 3);
                    i = i >> 4;
                    color = new com.vzome.core.construction.Color(r, g, b);
                }
                this.addColor(name, color);
            }
            return color;
        }

        /**
         * 
         * @return {*}
         */
        public iterator(): java.util.Iterator<string> {
            return this.mColors.keySet().iterator();
        }

        public reset() {
        }
    }
    Colors["__class"] = "com.vzome.core.render.Colors";
    Colors["__interfaces"] = ["java.lang.Iterable"];



    export namespace Colors {

        /**
         * @author Scott Vorthmann
         * @class
         */
        export interface Changes {
            colorAdded(name: string, color: com.vzome.core.construction.Color);

            colorChanged(name: string, newColor: com.vzome.core.construction.Color);
        }
    }

}

