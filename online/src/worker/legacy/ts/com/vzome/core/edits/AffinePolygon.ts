/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class AffinePolygon extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ errorMsg: java.lang.StringBuilder;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ chordRatio: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ mode: string;

        /*private*/ nSides: number;

        /**
         * Intended to be used for listing the valid modes for making an "Affine Polygon" submenu
         * @param {*} field
         * @return
         * @return {*}
         */
        public static getPolygonModes(field: com.vzome.core.algebra.AlgebraicField): java.util.SortedMap<number, string> {
            const modes: java.util.SortedMap<number, string> = <any>(new java.util.TreeMap<any, any>());
            modes.put(2, "Parabola");
            modes.put(3, "Triangle");
            modes.put(4, "Square");
            if (field.getGoldenRatio() != null){
                modes.put(5, "Pentagon");
            }
            modes.put(6, "Hexagon");
            if (field.getName() === ("heptagon")){
                modes.put(7, "Heptagon");
            } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(field.getName(), "polygon")){
                const polygonSides: number = (<com.vzome.core.algebra.PolygonField><any>field).polygonSides();
                for(let nSides: number = 7; nSides <= polygonSides; nSides++) {{
                    if ((polygonSides / nSides|0) * nSides === polygonSides){
                        let label: string;
                        switch((nSides)) {
                        case 7:
                            label = "Heptagon";
                            break;
                        case 8:
                            label = "Octagon";
                            break;
                        case 9:
                            label = "Nonagon";
                            break;
                        case 10:
                            label = "Decagon";
                            break;
                        case 12:
                            label = "Dodecagon";
                            break;
                        default:
                            label = nSides + "-gon";
                            break;
                        }
                        modes.put(nSides, label);
                    }
                };}
            }
            return modes;
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            this.setMode(<string>props.get("mode"));
            if (this.mode == null || /* isEmpty */(this.mode.length === 0)){
                this.errorMsg.append("\nMode is not specified.");
            }
            if (this.nSides < 2){
                this.errorMsg.append("\nThe number of sides must be greater than 1.");
            }
            if (this.chordRatio == null){
                this.errorMsg.append("\nUnsupported chord ratio.");
            }
            if (this.errorMsg.length() !== 0){
                this.errorMsg.insert(0, " configuration error(s)");
                this.errorMsg.insert(0, this.getXmlElementName());
            }
        }

        /*private*/ setMode(newMode: string) {
            this.mode = newMode;
            if (this.mode != null && !/* isEmpty */(this.mode.length === 0)){
                try {
                    this.nSides = javaemul.internal.IntegerHelper.parseInt(this.mode);
                } catch(e) {
                    if (com.vzome.core.editor.api.ChangeSelection.logger_$LI$().isLoggable(java.util.logging.Level.WARNING)){
                        com.vzome.core.editor.api.ChangeSelection.logger_$LI$().warning(this.getXmlElementName() + ": Invalid mode \"" + this.mode + "\".\nMode should be an integer greater than one.");
                    }
                }
            }
            switch((this.nSides)) {
            case 2:
                this.chordRatio = this.field['createRational$long'](3);
                break;
            case 3:
                this.chordRatio = this.field.zero();
                break;
            case 4:
                this.chordRatio = this.field.one();
                break;
            case 5:
                this.chordRatio = this.field.getGoldenRatio();
                break;
            case 6:
                this.chordRatio = this.field['createRational$long'](2);
                break;
            default:
                if (this.nSides === 7 && (this.field.getName() === ("heptagon"))){
                    this.chordRatio = this.field.getUnitTerm(2);
                } else if (this.nSides >= 7 && /* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(this.field.getName(), "polygon")){
                    const pField: com.vzome.core.algebra.PolygonField = <com.vzome.core.algebra.PolygonField><any>this.field;
                    const polygonSides: number = pField.polygonSides();
                    const step: number = (polygonSides / this.nSides|0);
                    if (step * this.nSides === polygonSides){
                        const diag0: com.vzome.core.algebra.AlgebraicNumber = pField.getUnitDiagonal(step - 1);
                        const diag2: com.vzome.core.algebra.AlgebraicNumber = pField.getUnitDiagonal((step * 3) - 1);
                        this.chordRatio = diag2.dividedBy(diag0);
                    }
                }
                break;
            }
        }

        /**
         * 
         */
        public perform() {
            if (this.errorMsg.length() !== 0){
                this.fail(this.errorMsg.toString());
            }
            this.errorMsg.append(this.getXmlElementName()).append(" requires two non-parallel struts with a common end point.\n");
            let strut1: com.vzome.core.model.Strut = null;
            let strut2: com.vzome.core.model.Strut = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (strut1 == null){
                            strut1 = <com.vzome.core.model.Strut><any>man;
                        } else if (strut2 == null){
                            strut2 = <com.vzome.core.model.Strut><any>man;
                        } else {
                            this.fail(this.errorMsg.append("\nToo many struts are selected.").toString());
                        }
                    }
                    this.unselect$com_vzome_core_model_Manifestation(man);
                }
            }
            if (strut2 == null){
                this.fail(this.errorMsg.append(strut1 == null ? "\nNo struts are selected." : "\nOnly one strut is selected.").toString());
            }
            let offset1: com.vzome.core.algebra.AlgebraicVector = strut1.getOffset();
            let offset2: com.vzome.core.algebra.AlgebraicVector = strut2.getOffset();
            if (com.vzome.core.algebra.AlgebraicVectors.areParallel(offset1, offset2)){
                this.fail(this.errorMsg.append("\nStruts are parallel or collinear.").toString());
            }
            let pCommon: com.vzome.core.construction.Point = null;
            let pStart: com.vzome.core.construction.Point = null;
            let pEnd: com.vzome.core.construction.Point = null;
            const seg1: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>strut1.getFirstConstruction();
            const seg2: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>strut2.getFirstConstruction();
            {
                const s1: com.vzome.core.algebra.AlgebraicVector = strut1.getLocation();
                const e1: com.vzome.core.algebra.AlgebraicVector = strut1.getEnd();
                const s2: com.vzome.core.algebra.AlgebraicVector = strut2.getLocation();
                const e2: com.vzome.core.algebra.AlgebraicVector = strut2.getEnd();
                if (s1.equals(s2)){
                    pStart = new com.vzome.core.construction.SegmentEndPoint(seg1, false);
                    pCommon = new com.vzome.core.construction.SegmentEndPoint(seg2, true);
                    pEnd = new com.vzome.core.construction.SegmentEndPoint(seg2, false);
                } else if (s1.equals(e2)){
                    offset2 = offset2.negate();
                    pStart = new com.vzome.core.construction.SegmentEndPoint(seg1, false);
                    pCommon = new com.vzome.core.construction.SegmentEndPoint(seg2, false);
                    pEnd = new com.vzome.core.construction.SegmentEndPoint(seg2, true);
                } else if (e1.equals(s2)){
                    offset1 = offset1.negate();
                    pStart = new com.vzome.core.construction.SegmentEndPoint(seg1, true);
                    pCommon = new com.vzome.core.construction.SegmentEndPoint(seg2, true);
                    pEnd = new com.vzome.core.construction.SegmentEndPoint(seg2, false);
                } else if (e1.equals(e2)){
                    offset1 = offset1.negate();
                    offset2 = offset2.negate();
                    pStart = new com.vzome.core.construction.SegmentEndPoint(seg1, true);
                    pCommon = new com.vzome.core.construction.SegmentEndPoint(seg2, false);
                    pEnd = new com.vzome.core.construction.SegmentEndPoint(seg2, true);
                } else {
                    this.errorMsg.append("\nStruts do not have a common end point.");
                    this.fail(this.errorMsg.toString());
                }
            };
            const vBegin: com.vzome.core.algebra.AlgebraicVector = pStart.getLocation();
            this.redo();
            const man0: com.vzome.core.model.Manifestation = this.manifestConstruction(pStart);
            const man1: com.vzome.core.model.Manifestation = this.manifestConstruction(seg1);
            if (this.nSides !== 2){
                this.select$com_vzome_core_model_Manifestation(man0);
                this.select$com_vzome_core_model_Manifestation(man1);
            }
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(pCommon));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(seg2));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(pEnd));
            for(let i: number = (this.nSides === 2) ? 1 : 2; i < this.nSides; i++) {{
                const translateByChordRatio: com.vzome.core.algebra.AlgebraicVector = offset2.scale(this.chordRatio);
                const pNew: com.vzome.core.construction.Point = new com.vzome.core.construction.TransformedPoint(new com.vzome.core.construction.Translation(translateByChordRatio), pStart);
                const segNew: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(pEnd, pNew);
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segNew));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(pNew));
                if (pNew.getLocation().equals(vBegin)){
                    if (i + 1 < this.nSides){
                        if (com.vzome.core.editor.api.ChangeSelection.logger_$LI$().isLoggable(java.util.logging.Level.INFO)){
                            com.vzome.core.editor.api.ChangeSelection.logger_$LI$().info(this.getXmlElementName() + ": actual reps = " + (i + 1) + ", not " + this.nSides + " as specified.");
                        }
                    }
                }
                pStart = pCommon;
                offset1 = offset2.negate();
                pCommon = pEnd;
                offset2 = segNew.getOffset();
                pEnd = pNew;
            };}
            this.redo();
        }

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
            this.errorMsg = new java.lang.StringBuilder();
            if (this.field === undefined) { this.field = null; }
            if (this.chordRatio === undefined) { this.chordRatio = null; }
            this.mode = null;
            this.nSides = 0;
            this.setOrderedSelection(true);
            this.field = editorModel['getSymmetrySystem$']().getSymmetry().getField();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "AffinePolygon";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.mode != null){
                element.setAttribute("mode", this.mode);
            }
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.setMode(xml.getAttribute("mode"));
        }
    }
    AffinePolygon["__class"] = "com.vzome.core.edits.AffinePolygon";

}

