import { ToolsModel } from "../editor/ToolsModel.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { RenderedModel } from "../render/RenderedModel.js";
import { CameraIntf } from "../viewing/CameraIntf.js";
import { Lights } from "../viewing/Lights.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export interface DocumentIntf {
    getCameraModel(): CameraIntf;

    getSceneLighting(): Lights;

    getRenderedModel(): RenderedModel;

    getToolsModel(): ToolsModel;

    getDetailsXml(dom: Document, b: boolean): Element;

    getEditorModel(): EditorModel;
}
