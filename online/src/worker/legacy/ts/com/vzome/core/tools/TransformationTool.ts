/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export abstract class TransformationTool extends com.vzome.core.editor.Tool {
        /**
         * 
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public prepare(applyTool: com.vzome.core.editor.api.ChangeManifestations) {
        }

        /**
         * 
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public complete(applyTool: com.vzome.core.editor.api.ChangeManifestations) {
        }

        /**
         * 
         * @return {boolean}
         */
        public needsInput(): boolean {
            return true;
        }

        transforms: com.vzome.core.construction.Transformation[];

        originPoint: com.vzome.core.construction.Point;

        public constructor(id: string, tools: com.vzome.core.editor.ToolsModel) {
            super(id, tools);
            if (this.transforms === undefined) { this.transforms = null; }
            if (this.originPoint === undefined) { this.originPoint = null; }
            this.originPoint = tools.getOriginPoint();
        }

        /**
         * 
         * @param {*} that
         * @return {boolean}
         */
        public equals(that: any): boolean {
            if (this === that){
                return true;
            }
            if (that == null){
                return false;
            }
            if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })((<any>that.constructor),(<any>this.constructor)))){
                return false;
            }
            const other: TransformationTool = <TransformationTool>that;
            if (this.originPoint == null){
                if (other.originPoint != null){
                    return false;
                }
            } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.originPoint,other.originPoint))){
                return false;
            }
            if (!java.util.Arrays.equals(this.transforms, other.transforms)){
                return false;
            }
            return true;
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} c
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public performEdit(c: com.vzome.core.construction.Construction, applyTool: com.vzome.core.editor.api.ChangeManifestations) {
            for(let index = 0; index < this.transforms.length; index++) {
                let transform = this.transforms[index];
                {
                    const result: com.vzome.core.construction.Construction = transform.transform$com_vzome_core_construction_Construction(c);
                    if (result == null)continue;
                    const color: com.vzome.core.construction.Color = c.getColor();
                    result.setColor(color);
                    const m: com.vzome.core.model.Manifestation = applyTool.manifestConstruction(result);
                    if (m != null)if (color != null)applyTool.colorManifestation(m, c.getColor());
                }
            }
            applyTool.redo();
        }

        /**
         * 
         * @param {*} man
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public performSelect(man: com.vzome.core.model.Manifestation, applyTool: com.vzome.core.editor.api.ChangeManifestations) {
        }

        public unselect$com_vzome_core_model_Manifestation$boolean(man: com.vzome.core.model.Manifestation, ignoreGroups: boolean) {
            const c: com.vzome.core.construction.Construction = man.getFirstConstruction();
            this.addParameter(c);
            super.unselect$com_vzome_core_model_Manifestation$boolean(man, ignoreGroups);
        }

        /**
         * 
         * @param {*} man
         * @param {boolean} ignoreGroups
         */
        public unselect(man?: any, ignoreGroups?: any) {
            if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ((typeof ignoreGroups === 'boolean') || ignoreGroups === null)) {
                return <any>this.unselect$com_vzome_core_model_Manifestation$boolean(man, ignoreGroups);
            } else if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ignoreGroups === undefined) {
                return <any>this.unselect$com_vzome_core_model_Manifestation(man);
            } else throw new Error('invalid overload');
        }

        isAutomatic(): boolean {
            return /* contains */(this.getId().indexOf(".auto/") != -1);
        }
    }
    TransformationTool["__class"] = "com.vzome.core.tools.TransformationTool";
    TransformationTool["__interfaces"] = ["com.vzome.api.Tool"];


}

