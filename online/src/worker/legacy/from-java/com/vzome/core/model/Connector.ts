import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Manifestation } from "./Manifestation.js";

export interface Connector extends Manifestation, java.lang.Comparable<Connector> {
    compareTo(other: Connector): number;
}
