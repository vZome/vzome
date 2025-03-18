/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.api {
    export interface Tool {
        apply(selectInputs: boolean, deleteInputs: boolean, createOutputs: boolean, selectOutputs: boolean, copyColors: boolean);

        selectParameters();

        isPredefined(): boolean;

        getId(): string;

        getCategory(): string;

        getLabel(): string;

        setLabel(label: string);

        isSelectInputs(): boolean;

        isDeleteInputs(): boolean;

        isCopyColors(): boolean;

        setInputBehaviors(selectInputs: boolean, deleteInputs: boolean);

        setCopyColors(value: boolean);

        isHidden(): boolean;

        setHidden(hidden: boolean);
    }

    export namespace Tool {

        export enum Kind {
            SYMMETRY, TRANSFORM, LINEAR_MAP
        }

        export interface Factory {
            addListener(listener: java.beans.PropertyChangeListener);

            createTool(): com.vzome.api.Tool;

            isEnabled(): boolean;

            getToolTip(): string;

            getLabel(): string;

            getId(): string;
        }

        export interface Source {
            getPredefinedTool(id: string): com.vzome.api.Tool;
        }
    }

}

