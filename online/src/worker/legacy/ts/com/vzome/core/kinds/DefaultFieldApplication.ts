/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.kinds {
    export class DefaultFieldApplication implements com.vzome.core.editor.FieldApplication {
        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ octahedralPerspective: com.vzome.core.editor.SymmetryPerspective;

        /*private*/ groups4d: java.util.Map<string, com.vzome.core.math.symmetry.CoxeterGroup>;

        /*private*/ pointsymm: com.vzome.core.commands.Command;

        /*private*/ mirrorsymm: com.vzome.core.commands.Command;

        /*private*/ translate: com.vzome.core.commands.Command;

        /*private*/ centroid: com.vzome.core.commands.Command;

        /*private*/ hideball: com.vzome.core.commands.Command;

        /*private*/ hide: com.vzome.core.commands.Command;

        /*private*/ panel: com.vzome.core.commands.Command;

        /*private*/ midpoint: com.vzome.core.commands.Command;

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            if (this.field === undefined) { this.field = null; }
            if (this.octahedralPerspective === undefined) { this.octahedralPerspective = null; }
            this.groups4d = <any>(new java.util.HashMap<string, com.vzome.core.math.symmetry.CoxeterGroup>());
            this.pointsymm = new com.vzome.core.commands.CommandCentralSymmetry();
            this.mirrorsymm = new com.vzome.core.commands.CommandMirrorSymmetry();
            this.translate = new com.vzome.core.commands.CommandTranslate();
            this.centroid = new com.vzome.core.commands.CommandCentroid();
            this.hideball = new com.vzome.core.commands.CommandHide();
            this.hide = new com.vzome.core.commands.CommandHide();
            this.panel = new com.vzome.core.commands.CommandPolygon();
            this.midpoint = new com.vzome.core.commands.CommandMidpoint();
            this.field = field;
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return this.field.getName();
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return null;
        }

        /**
         * 
         * @return {*}
         */
        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }

        /**
         * 
         * @return {*}
         */
        public getDefaultSymmetryPerspective(): com.vzome.core.editor.SymmetryPerspective {
            return this.getSymmetryPerspective("octahedral");
        }

        /**
         * 
         * @return {*}
         */
        public getSymmetryPerspectives(): java.util.Collection<com.vzome.core.editor.SymmetryPerspective> {
            return java.util.Arrays.asList<any>(this.getDefaultSymmetryPerspective());
        }

        /**
         * 
         * @param {string} symmName
         * @return {*}
         */
        public getSymmetryPerspective(symmName: string): com.vzome.core.editor.SymmetryPerspective {
            switch((symmName)) {
            case "octahedral":
                if (this.octahedralPerspective == null){
                    this.octahedralPerspective = new com.vzome.core.kinds.OctahedralSymmetryPerspective(this.field);
                }
                return this.octahedralPerspective;
            default:
                return null;
            }
        }

        /**
         * 
         * @param {string} name
         * @return {com.vzome.core.math.symmetry.QuaternionicSymmetry}
         */
        public getQuaternionSymmetry(name: string): com.vzome.core.math.symmetry.QuaternionicSymmetry {
            return null;
        }

        /**
         * 
         * @param {*} toolFactories
         * @param {com.vzome.core.editor.ToolsModel} tools
         */
        public registerToolFactories(toolFactories: java.util.Map<string, com.vzome.api.Tool.Factory>, tools: com.vzome.core.editor.ToolsModel) {
            toolFactories.put("SymmetryTool", new com.vzome.core.tools.OctahedralToolFactory(tools, null));
            toolFactories.put("RotationTool", new com.vzome.core.tools.RotationToolFactory(tools, null));
            toolFactories.put("ScalingTool", new com.vzome.core.tools.ScalingToolFactory(tools, null));
            toolFactories.put("InversionTool", new com.vzome.core.tools.InversionToolFactory(tools));
            toolFactories.put("LineReflectionTool", new com.vzome.core.tools.LineReflectionToolFactory(tools));
            toolFactories.put("MirrorTool", new com.vzome.core.tools.MirrorToolFactory(tools));
            toolFactories.put("TranslationTool", new com.vzome.core.tools.TranslationToolFactory(tools));
            toolFactories.put("ProjectionTool", new com.vzome.core.tools.ProjectionToolFactory(tools));
            toolFactories.put("PerspectiveProjectionTool", new com.vzome.core.tools.PerspectiveProjectionToolFactory(tools));
            toolFactories.put("BookmarkTool", new com.vzome.core.tools.BookmarkToolFactory(tools));
            toolFactories.put("LinearTransformTool", new com.vzome.core.tools.LinearMapToolFactory(tools, null, false));
            toolFactories.put("LinearMapTool", new com.vzome.core.tools.LinearMapToolFactory(tools, null, true));
            toolFactories.put("ModuleTool", new com.vzome.core.tools.ModuleToolFactory(tools));
            toolFactories.put("PlaneSelectionTool", new com.vzome.core.tools.PlaneSelectionToolFactory(tools));
        }

        /**
         * 
         * @param {string} groupName
         * @param {number} index
         * @param {number} edgesToRender
         * @param {com.vzome.core.algebra.AlgebraicNumber[]} edgeScales
         * @param {*} listener
         */
        public constructPolytope(groupName: string, index: number, edgesToRender: number, edgeScales: com.vzome.core.algebra.AlgebraicNumber[], listener: com.vzome.core.math.symmetry.WythoffConstruction.Listener) {
            let group: com.vzome.core.math.symmetry.CoxeterGroup = this.groups4d.get(groupName);
            if (group == null){
                switch((groupName)) {
                case "A4":
                    group = new com.vzome.core.math.symmetry.A4Group(this.field);
                    break;
                case "D4":
                    group = new com.vzome.core.math.symmetry.D4Group(this.field);
                    break;
                case "F4":
                    group = new com.vzome.core.math.symmetry.F4Group(this.field);
                    break;
                default:
                    group = new com.vzome.core.math.symmetry.B4Group(this.field);
                    break;
                }
                this.groups4d.put(groupName, group);
            }
            com.vzome.core.math.symmetry.WythoffConstruction.constructPolytope(group, index, edgesToRender, edgeScales, group, listener);
        }

        /**
         * 
         * @param {string} action
         * @return {*}
         */
        public getLegacyCommand(action: string): com.vzome.core.commands.Command {
            switch((action)) {
            case "pointsymm":
                return this.pointsymm;
            case "mirrorsymm":
                return this.mirrorsymm;
            case "translate":
                return this.translate;
            case "centroid":
                return this.centroid;
            case "hideball":
                return this.hideball;
            case "hide":
                return this.hide;
            case "panel":
                return this.panel;
            case "midpoint":
                return this.midpoint;
            case "octasymm":
                return this.getDefaultSymmetryPerspective().getLegacyCommand(action);
            default:
                return null;
            }
        }
    }
    DefaultFieldApplication["__class"] = "com.vzome.core.kinds.DefaultFieldApplication";
    DefaultFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];


}

