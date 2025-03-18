/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @param {*} projection
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandImportVEFData extends com.vzome.core.commands.AbstractCommand {
        public static X: number = 0;

        public static Y: number = 1;

        public static Z: number = 2;

        public static W: number = 3;

        public static VEF_STRING_ATTR_NAME: string = "org.vorthmann.zome.commands.CommandImportVEFData.vef.string";

        public static FIELD_ATTR_NAME: string = "org.vorthmann.zome.commands.CommandImportVEFData.field";

        public static NO_INVERSION_ATTR_NAME: string = "org.vorthmann.zome.commands.CommandImportVEFData.no.inversion";

        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandImportVEFData.PARAM_SIGNATURE == null) { CommandImportVEFData.PARAM_SIGNATURE = [[com.vzome.core.commands.Command.GENERIC_PARAM_NAME, com.vzome.core.construction.Construction]]; }  return CommandImportVEFData.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandImportVEFData.ATTR_SIGNATURE == null) { CommandImportVEFData.ATTR_SIGNATURE = [[CommandImportVEFData.VEF_STRING_ATTR_NAME, String], [com.vzome.core.commands.Command.FIELD_ATTR_NAME, java.io.InputStream], [CommandImportVEFData.NO_INVERSION_ATTR_NAME, java.io.InputStream]]; }  return CommandImportVEFData.ATTR_SIGNATURE; }

        /*private*/ mProjection: com.vzome.core.math.Projection;

        public constructor(projection?: any) {
            if (((projection != null && (projection.constructor != null && projection.constructor["__interfaces"] != null && projection.constructor["__interfaces"].indexOf("com.vzome.core.math.Projection") >= 0)) || projection === null)) {
                let __args = arguments;
                super();
                if (this.mProjection === undefined) { this.mProjection = null; } 
                this.quaternionVector = null;
                this.mProjection = projection;
            } else if (projection === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let projection: any = null;
                    super();
                    if (this.mProjection === undefined) { this.mProjection = null; } 
                    this.quaternionVector = null;
                    this.mProjection = projection;
                }
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandImportVEFData.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandImportVEFData.ATTR_SIGNATURE_$LI$();
        }

        /**
         * 
         * @param {string} attrName
         * @return {boolean}
         */
        public attributeIs3D(attrName: string): boolean {
            return !("symmetry.axis.segment" === attrName);
        }

        /*private*/ quaternionVector: com.vzome.core.algebra.AlgebraicVector;

        /**
         * Only called when migrating a 2.0 model file.
         * @param {com.vzome.core.algebra.AlgebraicVector} offset
         */
        public setQuaternion(offset: com.vzome.core.algebra.AlgebraicVector) {
            this.quaternionVector = offset;
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         * @return {com.vzome.core.commands.AttributeMap}
         */
        public setXml(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat): com.vzome.core.commands.AttributeMap {
            const attrs: com.vzome.core.commands.AttributeMap = super.setXml(xml, format);
            this.quaternionVector = format.parseRationalVector(xml, "quaternion");
            return attrs;
        }

        /**
         * 
         * @param {*} result
         * @param {com.vzome.core.commands.AttributeMap} attributes
         */
        public getXml(result: org.w3c.dom.Element, attributes: com.vzome.core.commands.AttributeMap) {
            if (this.quaternionVector != null)com.vzome.xml.DomUtils.addAttribute(result, "quaternion", this.quaternionVector.toParsableString());
            super.getXml(result, attributes);
        }

        /**
         * 
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setFixedAttributes(attributes: com.vzome.core.commands.AttributeMap, format: com.vzome.core.commands.XmlSaveFormat) {
            if (!attributes.containsKey(CommandImportVEFData.FIELD_ATTR_NAME))attributes.put(CommandImportVEFData.FIELD_ATTR_NAME, format.getField());
            super.setFixedAttributes(attributes, format);
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const result: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            let field: com.vzome.core.algebra.AlgebraicField = <com.vzome.core.algebra.AlgebraicField><any>attributes.get(CommandImportVEFData.FIELD_ATTR_NAME);
            if (field == null)field = <com.vzome.core.algebra.AlgebraicField><any>attributes.get(com.vzome.core.commands.Command.FIELD_ATTR_NAME);
            const symmAxis: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
            const vefData: string = <string>attributes.get(CommandImportVEFData.VEF_STRING_ATTR_NAME);
            const noInversion: boolean = <boolean>attributes.get(CommandImportVEFData.NO_INVERSION_ATTR_NAME);
            let projection: com.vzome.core.math.Projection = this.mProjection;
            if (projection == null){
                let quaternion: com.vzome.core.algebra.AlgebraicVector = this.quaternionVector;
                if (quaternion == null)quaternion = (symmAxis == null) ? null : symmAxis.getOffset();
                if (quaternion != null)quaternion = quaternion.scale(field['createPower$int'](-5));
                projection = quaternion == null ? null : new com.vzome.core.math.QuaternionProjection(field, null, quaternion);
            }
            if (noInversion != null && noInversion)new CommandImportVEFData.VefToModelNoInversion(this, projection, field, effects).parseVEF(vefData, field); else new com.vzome.core.construction.VefToModel(projection, effects, field['createPower$int'](5), null).parseVEF(vefData, field);
            return result;
        }
    }
    CommandImportVEFData["__class"] = "com.vzome.core.commands.CommandImportVEFData";
    CommandImportVEFData["__interfaces"] = ["com.vzome.core.commands.Command"];



    export namespace CommandImportVEFData {

        export class VefToModelNoInversion extends com.vzome.core.construction.VefToModel {
            public __parent: any;
            mProjected: com.vzome.core.algebra.AlgebraicVector[][];

            mUsedPoints: java.util.Set<com.vzome.core.construction.Point>;

            public constructor(__parent: any, projection: com.vzome.core.math.Projection, field: com.vzome.core.algebra.AlgebraicField, effects: com.vzome.core.construction.ConstructionChanges) {
                super(projection, effects, field['createPower$int'](5), null);
                this.__parent = __parent;
                if (this.mProjected === undefined) { this.mProjected = null; }
                this.mUsedPoints = <any>(new java.util.HashSet<any>());
            }

            /**
             * 
             * @param {number} index
             * @param {com.vzome.core.algebra.AlgebraicVector} location
             */
            addVertex(index: number, location: com.vzome.core.algebra.AlgebraicVector) {
                if (this.scale != null){
                    location = location.scale(this.scale);
                }
                if (this.mProjection != null)location = this.mProjection.projectImage(location, this.wFirst());
                this.mVertices[index] = new com.vzome.core.construction.FreePoint(location);
            }

            /**
             * 
             * @param {number} numEdges
             */
            startEdges(numEdges: number) {
                this.mProjected = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([numEdges, 2]);
            }

            /**
             * 
             * @param {number} index
             * @param {number} v1
             * @param {number} v2
             */
            addEdge(index: number, v1: number, v2: number) {
                const p1: com.vzome.core.construction.Point = this.mVertices[v1];
                const p2: com.vzome.core.construction.Point = this.mVertices[v2];
                if (p1 == null || p2 == null)return;
                const seg: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(p1, p2);
                const pr1: com.vzome.core.algebra.AlgebraicVector = p1.getLocation().projectTo3d(this.wFirst()).negate();
                const pr2: com.vzome.core.algebra.AlgebraicVector = p2.getLocation().projectTo3d(this.wFirst()).negate();
                for(let i: number = 0; i < index; i++) {{
                    if (pr1.equals(this.mProjected[i][0]) && pr2.equals(this.mProjected[i][1]))return;
                    if (pr2.equals(this.mProjected[i][0]) && pr1.equals(this.mProjected[i][1]))return;
                };}
                this.mProjected[index][0] = pr1.negate();
                this.mProjected[index][1] = pr2.negate();
                this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](seg);
                this.mUsedPoints.add(p1);
                this.mUsedPoints.add(p2);
            }

            /**
             * 
             */
            endEdges() {
                for(let index=this.mUsedPoints.iterator();index.hasNext();) {
                    let point = index.next();
                    {
                        this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](point);
                    }
                }
            }
        }
        VefToModelNoInversion["__class"] = "com.vzome.core.commands.CommandImportVEFData.VefToModelNoInversion";

    }

}

