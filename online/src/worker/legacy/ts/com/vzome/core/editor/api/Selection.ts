/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export interface Selection extends java.lang.Iterable<com.vzome.core.model.Manifestation> {
        clear();

        manifestationSelected(man: com.vzome.core.model.Manifestation): boolean;

        selectWithGrouping(mMan: com.vzome.core.model.Manifestation);

        unselectWithGrouping(mMan: com.vzome.core.model.Manifestation);

        select(mMan: com.vzome.core.model.Manifestation);

        unselect(mMan: com.vzome.core.model.Manifestation);

        getSingleSelection(kind: any): com.vzome.core.model.Manifestation;

        gatherGroup();

        gatherGroup211();

        scatterGroup();

        scatterGroup211();

        isSelectionAGroup(): boolean;

        size(): number;

        copy(bookmarkedSelection: java.util.List<com.vzome.core.model.Manifestation>);
    }

    export namespace Selection {

        export function biggestGroup(m: com.vzome.core.model.Manifestation): com.vzome.core.model.Group {
            let parent: com.vzome.core.model.Group = m.getContainer();
            let group: com.vzome.core.model.Group = parent;
            while((parent != null)) {{
                parent = group.getContainer();
                if (parent == null)break;
                group = parent;
            }};
            return group;
        }
    }

}

