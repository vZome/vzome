import { DefaultController } from '../from-java/com/vzome/desktop/controller/DefaultController.js';
import { JsProperties } from '../jsweet2js.js';

export class PickingController extends DefaultController {
  constructor(renderedModel) {
    super();
    this.renderedModel = renderedModel;
  }

  // There are no implementors of doParamAction() in the Java code
  doParamAction(action, params) {
    const config = params.getConfig();

    // Marquee selection: one message for many objects, recorded as one SelectManifestation edit
    // each.  No new history element, so desktop can read the file; and since performAndRecord
    // merges consecutive selection edits into a BeginBlock/EndBlock, they undo together.
    if (action === 'SelectManifestations') {
      for (const id of config.ids || [])
        this.doParamAction('SelectManifestation', new JsProperties({ id }));
      return;
    }

    const { id } = config;
    if (id) {
      const rm = this.renderedModel.getRenderedManifestation(id);
      const picked = rm.getManifestation();
      const orbit = rm.getStrutOrbit();
      const length = rm.getStrutLength();
      super.doParamAction(action, new JsProperties({ ...config, picked, orbit, length }));
    }

    else
      super.doParamAction(action, params);
  }
}
