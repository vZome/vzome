/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.beans {
    export abstract class ChangeListenerMap<L extends java.util.EventListener> {
        /*private*/ map: java.util.Map<string, L[]>;

        abstract newArray(length: number): L[];

        abstract newProxy(name: string, listener: L): L;

        public add(name: string, listener: L) {
            if (this.map == null){
                this.map = <any>(new java.util.HashMap<any, any>());
            }
            const array: L[] = this.map.get(name);
            const size: number = (array != null) ? array.length : 0;
            const clone: L[] = this.newArray(size + 1);
            clone[size] = listener;
            if (array != null){
                java.lang.System.arraycopy(array, 0, clone, 0, size);
            }
            this.map.put(name, clone);
        }

        public remove(name: string, listener: L) {
            if (this.map != null){
                const array: L[] = this.map.get(name);
                if (array != null){
                    for(let i: number = 0; i < array.length; i++) {{
                        if (listener.equals(array[i])){
                            const size: number = array.length - 1;
                            if (size > 0){
                                const clone: L[] = this.newArray(size);
                                java.lang.System.arraycopy(array, 0, clone, 0, i);
                                java.lang.System.arraycopy(array, i + 1, clone, i, size - i);
                                this.map.put(name, clone);
                            } else {
                                this.map.remove(name);
                                if (this.map.isEmpty()){
                                    this.map = null;
                                }
                            }
                            break;
                        }
                    };}
                }
            }
        }

        /**
         * Returns the list of listeners for the specified property.
         * 
         * @param {string} name
         * the name of the property
         * @return {L[]} the corresponding list of listeners
         */
        public get(name: string): L[] {
            return (this.map != null) ? this.map.get(name) : null;
        }

        /**
         * Sets new list of listeners for the specified property.
         * 
         * @param {string} name
         * the name of the property
         * @param {L[]} listeners
         * new list of listeners
         */
        public set(name: string, listeners: L[]) {
            if (listeners != null){
                if (this.map == null){
                    this.map = <any>(new java.util.HashMap<any, any>());
                }
                this.map.put(name, listeners);
            } else if (this.map != null){
                this.map.remove(name);
                if (this.map.isEmpty()){
                    this.map = null;
                }
            }
        }

        public getListeners$(): L[] {
            if (this.map == null){
                return this.newArray(0);
            }
            const list: java.util.List<L> = <any>(new java.util.ArrayList<any>());
            const listeners: L[] = this.map.get(null);
            if (listeners != null){
                for(let index = 0; index < listeners.length; index++) {
                    let listener = listeners[index];
                    {
                        list.add(listener);
                    }
                }
            }
            for(let index=this.map.entrySet().iterator();index.hasNext();) {
                let entry = index.next();
                {
                    const name: string = entry.getKey();
                    if (name != null){
                        {
                            let array = entry.getValue();
                            for(let index = 0; index < array.length; index++) {
                                let listener = array[index];
                                {
                                    list.add(this.newProxy(name, listener));
                                }
                            }
                        }
                    }
                }
            }
            return list.toArray<any>(this.newArray(list.size()));
        }

        public getListeners$java_lang_String(name: string): L[] {
            if (name != null){
                const listeners: L[] = this.get(name);
                if (listeners != null){
                    return (listeners).slice(0);
                }
            }
            return this.newArray(0);
        }

        /**
         * Returns listeners that have been associated with the named property.
         * 
         * @param {string} name
         * the name of the property
         * @return {L[]} an array of listeners for the named property
         */
        public getListeners(name?: any): L[] {
            if (((typeof name === 'string') || name === null)) {
                return <any>this.getListeners$java_lang_String(name);
            } else if (name === undefined) {
                return <any>this.getListeners$();
            } else throw new Error('invalid overload');
        }

        /**
         * Indicates whether the map contains at least one listener to be notified.
         * 
         * @param {string} name
         * the name of the property
         * @return {boolean} {@code true} if at least one listener exists or {@code false}
         * otherwise
         */
        public hasListeners(name: string): boolean {
            if (this.map == null){
                return false;
            }
            const array: L[] = this.map.get(null);
            return (array != null) || ((name != null) && (null != this.map.get(name)));
        }

        /**
         * Returns a set of entries from the map. Each entry is a pair consisted of
         * the property name and the corresponding list of listeners.
         * 
         * @return {*} a set of entries from the map
         */
        public getEntries(): java.util.Set<java.util.Map.Entry<string, L[]>> {
            return (this.map != null) ? this.map.entrySet() : java.util.Collections.emptySet<java.util.Map.Entry<string, L[]>>();
        }

        /**
         * Extracts a real listener from the proxy listener. It is necessary because
         * default proxy class is not serializable.
         * 
         * @return {*} a real listener
         * @param {*} listener
         */
        public abstract extract(listener: L): L;

        constructor() {
            if (this.map === undefined) { this.map = null; }
        }
    }
    ChangeListenerMap["__class"] = "java.beans.ChangeListenerMap";

}

