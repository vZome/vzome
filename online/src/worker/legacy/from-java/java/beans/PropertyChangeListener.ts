import { java, javaemul } from "../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { PropertyChangeEvent } from "./PropertyChangeEvent.js";

export interface PropertyChangeListener extends java.util.EventListener {
    propertyChange(evt: PropertyChangeEvent);
}
