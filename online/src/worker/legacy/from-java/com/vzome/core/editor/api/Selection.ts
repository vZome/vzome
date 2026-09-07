import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Group } from "../../model/Group.js";
import { Manifestation } from "../../model/Manifestation.js";

export interface Selection extends java.lang.Iterable<Manifestation> {
    clear();

    manifestationSelected(man: Manifestation): boolean;

    selectWithGrouping(mMan: Manifestation);

    unselectWithGrouping(mMan: Manifestation);

    select(mMan: Manifestation);

    unselect(mMan: Manifestation);

    getSingleSelection(kind: any): Manifestation;

    gatherGroup();

    gatherGroup211();

    scatterGroup();

    scatterGroup211();

    isSelectionAGroup(): boolean;

    size(): number;

    copy(bookmarkedSelection: java.util.List<Manifestation>);
}

export namespace Selection {

    export function biggestGroup(m: Manifestation): Group {
        let parent: Group = m.getContainer();
        let group: Group = parent;
        while((parent != null)) {{
            parent = group.getContainer();
            if (parent == null)break;
            group = parent;
        }};
        return group;
    }
}
