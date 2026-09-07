import { PropertyChangeListener } from "../../../java/beans/PropertyChangeListener.js";

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

    getOrder(): number;

    setOrder(order: number);
}

export namespace Tool {

    export enum Kind {
        SYMMETRY, TRANSFORM, LINEAR_MAP
    }

    export interface Factory {
        addListener(listener: PropertyChangeListener);

        createTool(): Tool;

        isEnabled(): boolean;

        getToolTip(): string;

        getLabel(): string;

        getId(): string;
    }

    export interface Source {
        getPredefinedTool(id: string): Tool;
    }
}
