import { com } from '../core-java.js';
import { JsProperties } from '../jsweet2js.js';

export class PickingController extends com.vzome.desktop.controller.DefaultController {
  constructor(renderedModel) {
    super();
    this.renderedModel = renderedModel;
  }

  // There are no implementors of doParamAction() in the Java code
  doParamAction(action, params) {
    const config = params.getConfig();
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
