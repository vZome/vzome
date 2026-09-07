import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Command } from "../../commands/Command.js";
import { UndoableEdit } from "./UndoableEdit.js";
import { Element } from "../../../../../org/w3c/dom/Element.js";

export interface Context {
    createEdit(xml: Element): UndoableEdit;

    createLegacyCommand(cmdName: string): Command;

    performAndRecord(edit: UndoableEdit);

    doEdit(action: string, props: java.util.Map<string, any>): boolean;
}
