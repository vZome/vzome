/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     */
    export interface Command {
        /**
         * Get the parameter signature for this command.
         * Parameter are an ordered list of pre-existing Constructions.
         * Each parameter has a name (for UI purposes), and an abstract Construction type
         * (Point, Line, Segment, Plane, ...).
         * @return {java.lang.Object[][]} an array of { String, Class } pairs, one for each parameter.
         */
        getParameterSignature(): any[][];

        /**
         * Get the attribute signature for this command.
         * Attributes are an unordered set of primitive values.
         * Each attribute has a name , and a primitive type
         * (GoldenNumber, GoldenVector, Axis, Direction, GoldenMatrix, ...).
         * @return {java.lang.Object[][]} an array of { String, Class } pairs, one for each attribute.
         */
        getAttributeSignature(): any[][];

        apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList;
    }

    export namespace Command {

        export const LOADING_FROM_FILE: string = "org.vorthmann.zome.editor.Command.LOADING_FROM_FILE";

        export const FIELD_ATTR_NAME: string = "org.vorthmann.zome.commands.Command.ALGEBRAIC_FIELD";

        export const GENERIC_PARAM_NAME: string = "org.vorthmann.zome.editor.Command.GENERIC_PARAM";
    }


    export namespace Command {

        export interface Registry {
            getCommand(name: string): com.vzome.core.commands.Command;
        }

        export interface FailureChannel {
            reportFailure(f: Command.Failure);
        }

        /**
         * @param {string} message
         * @param {java.lang.Throwable} cause
         * @class
         * @extends java.lang.Exception
         */
        export class Failure extends Error {
            static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (Failure.logger == null) { Failure.logger = java.util.logging.Logger.getLogger("org.vorthmann.zome.commands"); }  return Failure.logger; }

            public constructor(message?: any, cause?: any) {
                if (((typeof message === 'string') || message === null) && ((cause != null && cause instanceof <any>Error) || cause === null)) {
                    let __args = arguments;
                    super(message); this.message=message;
                    Failure.logger_$LI$().log(java.util.logging.Level.INFO, "command failure: " + message, cause);
                } else if (((typeof message === 'string') || message === null) && cause === undefined) {
                    let __args = arguments;
                    super(message); this.message=message;
                    if (Failure.logger_$LI$().isLoggable(java.util.logging.Level.FINE))Failure.logger_$LI$().log(java.util.logging.Level.FINE, "command failure: " + message);
                } else if (((message != null && message instanceof <any>Error) || message === null) && cause === undefined) {
                    let __args = arguments;
                    let cause: any = __args[0];
                    super(cause); this.message=cause;
                    Failure.logger_$LI$().log(java.util.logging.Level.INFO, "command failure", cause);
                } else if (message === undefined && cause === undefined) {
                    let __args = arguments;
                    super();
                } else throw new Error('invalid overload');
            }
        }
        Failure["__class"] = "com.vzome.core.commands.Command.Failure";
        Failure["__interfaces"] = ["java.io.Serializable"];


    }

}

