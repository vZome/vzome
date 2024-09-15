/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class UndoRedoController extends com.vzome.desktop.controller.DefaultController implements com.vzome.desktop.api.Controller {
        /*private*/ model: com.vzome.core.editor.EditHistory;

        public constructor(model: com.vzome.core.editor.EditHistory) {
            super();
            if (this.model === undefined) { this.model = null; }
            this.model = model;
        }

        /**
         * 
         * @param {string} key
         * @return {string}
         */
        public getProperty(key: string): string {
            switch((key)) {
            case "line.number":
                return /* toString */(''+(this.model.getNextLineNumber()));
            case "breakpoints":
                return <any>(this.model.getBreakpoints().stream().map<any>((x) => x.toString()).collect<any, any>(java.util.stream.Collectors.joining(",")));
            default:
                return super.getProperty(key);
            }
        }

        /**
         * 
         * @param {string} action
         */
        public doAction(action: string) {
            switch((action)) {
            case "undo":
                this.model.undo$boolean(true);
                break;
            case "redo":
                this.model.redo$boolean(true);
                break;
            case "redoToBreakpoint":
                this.model.redoToBreakpoint();
                break;
            case "undoAll":
                this.model.undoAll();
                break;
            case "redoAll":
                this.model.redoAll(-1);
                break;
            default:
                if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "setBreakpoints.")){
                    const breakpointList: string = action.substring("setBreakpoints.".length).trim();
                    const breakpoints: string[] = breakpointList.split(",");
                    const breakpointInts: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(breakpoints.length);
                    for(let i: number = 0; i < breakpointInts.length; i++) {{
                        const breakpoint: string = breakpoints[i];
                        let lineNum: number = -1;
                        try {
                            lineNum = javaemul.internal.IntegerHelper.parseInt(breakpoint);
                        } catch(ex) {
                            this.mErrors.reportError("\'" + breakpoint + "\' is not a valid integer. Line number must be a positive integer.", []);
                        }
                        if (lineNum <= 0){
                            this.mErrors.reportError("Edit number must be a positive integer.", []);
                        } else {
                            breakpointInts[i] = lineNum;
                        }
                    };}
                    this.model.setBreakpoints(breakpointInts);
                } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "redoUntilEdit.")){
                    const editNum: string = action.substring("redoUntilEdit.".length).trim();
                    if (!((editNum === ("null")) || (editNum === ("")))){
                        let eNum: number = -1;
                        try {
                            eNum = javaemul.internal.IntegerHelper.parseInt(editNum);
                        } catch(ex) {
                            this.mErrors.reportError("\'" + editNum + "\' is not a valid integer. Edit number must be a positive integer.", []);
                        }
                        if (eNum <= 0){
                            this.mErrors.reportError("Edit number must be a positive integer.", []);
                        } else {
                            this.model.redoAll(eNum);
                        }
                    }
                } else super.doAction(action);
                break;
            }
        }
    }
    UndoRedoController["__class"] = "com.vzome.desktop.controller.UndoRedoController";
    UndoRedoController["__interfaces"] = ["com.vzome.desktop.api.Controller"];


}

