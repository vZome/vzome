/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    /**
     * @author Scott Vorthmann
     * @class
     */
    export abstract class ManifestationImpl implements com.vzome.core.model.GroupElement, com.vzome.core.model.Manifestation, com.vzome.core.model.HasRenderedObject {
        mManifests: java.util.List<com.vzome.core.construction.Construction>;

        mRendered: com.vzome.core.render.RenderedManifestation;

        /*private*/ hidden: boolean;

        /*private*/ mId: number;

        /*private*/ color: com.vzome.core.construction.Color;

        static NO_ID: number = -1;

        static NEXT_ID: number = 0;

        resetId() {
            ManifestationImpl.NEXT_ID = 0;
            this.mId = ManifestationImpl.NO_ID;
        }

        getId(): number {
            if (this.mId === ManifestationImpl.NO_ID)this.mId = ManifestationImpl.NEXT_ID++;
            return this.mId;
        }

        public addConstruction(c: com.vzome.core.construction.Construction) {
            this.mManifests.add(c);
        }

        public removeConstruction(c: com.vzome.core.construction.Construction) {
            this.mManifests.remove(c);
        }

        public getConstructions(): java.util.Iterator<com.vzome.core.construction.Construction> {
            return this.mManifests.iterator();
        }

        /**
         * This is different from toConstruction, because we must support
         * the legacy behavior, which used the iterator.
         * @return
         * @return {com.vzome.core.construction.Construction}
         */
        public getFirstConstruction(): com.vzome.core.construction.Construction {
            if (this.mManifests.isEmpty())return null;
            return this.mManifests.iterator().next();
        }

        public isUnnecessary(): boolean {
            return this.mManifests.isEmpty();
        }

        public getColor(): com.vzome.core.construction.Color {
            if (this.color == null && this.mRendered != null){
                this.color = this.mRendered.getColor();
            }
            return this.color;
        }

        public setColor(color: com.vzome.core.construction.Color) {
            this.color = color;
        }

        public setRenderedObject(obj: com.vzome.core.model.RenderedObject) {
            this.mRendered = <com.vzome.core.render.RenderedManifestation><any>obj;
            if (this.mRendered != null)this.color = this.mRendered.getColor();
        }

        public getRenderedObject(): com.vzome.core.model.RenderedObject {
            return this.mRendered;
        }

        public isHidden(): boolean {
            return this.hidden;
        }

        public abstract getLocation(): com.vzome.core.algebra.AlgebraicVector;

        public abstract getCentroid(): com.vzome.core.algebra.AlgebraicVector;

        /**
         * This is guaranteed to return a 3D construction,
         * and will return the same object as getFirstConstruction()
         * when possible.
         * @return
         * @return {com.vzome.core.construction.Construction}
         */
        public abstract toConstruction(): com.vzome.core.construction.Construction;

        /*private*/ mContainer: com.vzome.core.model.Group;

        public getContainer(): com.vzome.core.model.Group {
            return this.mContainer;
        }

        /**
         * 
         * @param {com.vzome.core.model.Group} container
         */
        public setContainer(container: com.vzome.core.model.Group) {
            this.mContainer = container;
        }

        public setHidden(hidden: boolean) {
            this.hidden = hidden;
        }

        public isRendered(): boolean {
            return this.mRendered != null;
        }

        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            return this.toConstruction().getXml(doc);
        }

        public abstract getLabel(): any;
        public abstract setLabel(label?: any): any;
        constructor() {
            this.mManifests = <any>(new java.util.ArrayList<any>());
            this.mRendered = null;
            this.hidden = false;
            this.mId = ManifestationImpl.NO_ID;
            if (this.color === undefined) { this.color = null; }
            if (this.mContainer === undefined) { this.mContainer = null; }
        }
    }
    ManifestationImpl["__class"] = "com.vzome.core.model.ManifestationImpl";
    ManifestationImpl["__interfaces"] = ["com.vzome.core.model.HasRenderedObject","com.vzome.core.model.GroupElement","com.vzome.core.model.Manifestation"];


}

