/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.beans {
    export class PropertyChangeSupport {
        /*private*/ map: PropertyChangeSupport.PropertyChangeListenerMap;

        public constructor(sourceBean: any) {
            this.map = new PropertyChangeSupport.PropertyChangeListenerMap();
            if (this.source === undefined) { this.source = null; }
            if (sourceBean == null){
                throw new java.lang.NullPointerException();
            }
            this.source = sourceBean;
        }

        public addPropertyChangeListener$java_beans_PropertyChangeListener(listener: java.beans.PropertyChangeListener) {
            if (listener == null){
                return;
            }
            if (listener != null && listener instanceof <any>java.beans.PropertyChangeListenerProxy){
                const proxy: java.beans.PropertyChangeListenerProxy = <java.beans.PropertyChangeListenerProxy><any>listener;
                this.addPropertyChangeListener$java_lang_String$java_beans_PropertyChangeListener(proxy.getPropertyName(), proxy.getListener());
            } else {
                this.map.add(null, listener);
            }
        }

        public removePropertyChangeListener$java_beans_PropertyChangeListener(listener: java.beans.PropertyChangeListener) {
            if (listener == null){
                return;
            }
            if (listener != null && listener instanceof <any>java.beans.PropertyChangeListenerProxy){
                const proxy: java.beans.PropertyChangeListenerProxy = <java.beans.PropertyChangeListenerProxy><any>listener;
                this.removePropertyChangeListener$java_lang_String$java_beans_PropertyChangeListener(proxy.getPropertyName(), proxy.getListener());
            } else {
                this.map.remove(null, listener);
            }
        }

        public getPropertyChangeListeners$(): java.beans.PropertyChangeListener[] {
            return this.map.getListeners$();
        }

        public addPropertyChangeListener$java_lang_String$java_beans_PropertyChangeListener(propertyName: string, listener: java.beans.PropertyChangeListener) {
            if (listener == null || propertyName == null){
                return;
            }
            listener = this.map.extract$java_beans_PropertyChangeListener(listener);
            if (listener != null){
                this.map.add(propertyName, listener);
            }
        }

        public addPropertyChangeListener(propertyName?: any, listener?: any) {
            if (((typeof propertyName === 'string') || propertyName === null) && ((listener != null && (listener.constructor != null && listener.constructor["__interfaces"] != null && listener.constructor["__interfaces"].indexOf("java.beans.PropertyChangeListener") >= 0)) || listener === null)) {
                return <any>this.addPropertyChangeListener$java_lang_String$java_beans_PropertyChangeListener(propertyName, listener);
            } else if (((propertyName != null && (propertyName.constructor != null && propertyName.constructor["__interfaces"] != null && propertyName.constructor["__interfaces"].indexOf("java.beans.PropertyChangeListener") >= 0)) || propertyName === null) && listener === undefined) {
                return <any>this.addPropertyChangeListener$java_beans_PropertyChangeListener(propertyName);
            } else throw new Error('invalid overload');
        }

        public removePropertyChangeListener$java_lang_String$java_beans_PropertyChangeListener(propertyName: string, listener: java.beans.PropertyChangeListener) {
            if (listener == null || propertyName == null){
                return;
            }
            listener = this.map.extract$java_beans_PropertyChangeListener(listener);
            if (listener != null){
                this.map.remove(propertyName, listener);
            }
        }

        public removePropertyChangeListener(propertyName?: any, listener?: any) {
            if (((typeof propertyName === 'string') || propertyName === null) && ((listener != null && (listener.constructor != null && listener.constructor["__interfaces"] != null && listener.constructor["__interfaces"].indexOf("java.beans.PropertyChangeListener") >= 0)) || listener === null)) {
                return <any>this.removePropertyChangeListener$java_lang_String$java_beans_PropertyChangeListener(propertyName, listener);
            } else if (((propertyName != null && (propertyName.constructor != null && propertyName.constructor["__interfaces"] != null && propertyName.constructor["__interfaces"].indexOf("java.beans.PropertyChangeListener") >= 0)) || propertyName === null) && listener === undefined) {
                return <any>this.removePropertyChangeListener$java_beans_PropertyChangeListener(propertyName);
            } else throw new Error('invalid overload');
        }

        public getPropertyChangeListeners$java_lang_String(propertyName: string): java.beans.PropertyChangeListener[] {
            return this.map.getListeners$java_lang_String(propertyName);
        }

        public getPropertyChangeListeners(propertyName?: any): java.beans.PropertyChangeListener[] {
            if (((typeof propertyName === 'string') || propertyName === null)) {
                return <any>this.getPropertyChangeListeners$java_lang_String(propertyName);
            } else if (propertyName === undefined) {
                return <any>this.getPropertyChangeListeners$();
            } else throw new Error('invalid overload');
        }

        public firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(propertyName: string, oldValue: any, newValue: any) {
            if (oldValue == null || newValue == null || !/* equals */(<any>((o1: any, o2: any) => o1 && o1.equals ? o1.equals(o2) : o1 === o2)(oldValue,newValue))){
                this.firePropertyChange$java_beans_PropertyChangeEvent(new java.beans.PropertyChangeEvent(this.source, propertyName, oldValue, newValue));
            }
        }

        public firePropertyChange$java_lang_String$int$int(propertyName: string, oldValue: number, newValue: number) {
            if (oldValue !== newValue){
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(propertyName, javaemul.internal.IntegerHelper.valueOf(oldValue), javaemul.internal.IntegerHelper.valueOf(newValue));
            }
        }

        public firePropertyChange(propertyName?: any, oldValue?: any, newValue?: any) {
            if (((typeof propertyName === 'string') || propertyName === null) && ((typeof oldValue === 'number') || oldValue === null) && ((typeof newValue === 'number') || newValue === null)) {
                return <any>this.firePropertyChange$java_lang_String$int$int(propertyName, oldValue, newValue);
            } else if (((typeof propertyName === 'string') || propertyName === null) && ((typeof oldValue === 'boolean') || oldValue === null) && ((typeof newValue === 'boolean') || newValue === null)) {
                return <any>this.firePropertyChange$java_lang_String$boolean$boolean(propertyName, oldValue, newValue);
            } else if (((typeof propertyName === 'string') || propertyName === null) && ((oldValue != null) || oldValue === null) && ((newValue != null) || newValue === null)) {
                return <any>this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(propertyName, oldValue, newValue);
            } else if (((propertyName != null && propertyName instanceof <any>java.beans.PropertyChangeEvent) || propertyName === null) && oldValue === undefined && newValue === undefined) {
                return <any>this.firePropertyChange$java_beans_PropertyChangeEvent(propertyName);
            } else throw new Error('invalid overload');
        }

        public firePropertyChange$java_lang_String$boolean$boolean(propertyName: string, oldValue: boolean, newValue: boolean) {
            if (oldValue !== newValue){
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(propertyName, javaemul.internal.BooleanHelper.valueOf(oldValue), javaemul.internal.BooleanHelper.valueOf(newValue));
            }
        }

        public firePropertyChange$java_beans_PropertyChangeEvent(event: java.beans.PropertyChangeEvent) {
            const oldValue: any = event.getOldValue();
            const newValue: any = event.getNewValue();
            if (oldValue == null || newValue == null || !/* equals */(<any>((o1: any, o2: any) => o1 && o1.equals ? o1.equals(o2) : o1 === o2)(oldValue,newValue))){
                const name: string = event.getPropertyName();
                const common: java.beans.PropertyChangeListener[] = this.map.get(null);
                const named: java.beans.PropertyChangeListener[] = (name != null) ? this.map.get(name) : null;
                PropertyChangeSupport.fire(common, event);
                PropertyChangeSupport.fire(named, event);
            }
        }

        static fire(listeners: java.beans.PropertyChangeListener[], event: java.beans.PropertyChangeEvent) {
            if (listeners != null){
                for(let index = 0; index < listeners.length; index++) {
                    let listener = listeners[index];
                    {
                        listener.propertyChange(event);
                    }
                }
            }
        }

        public fireIndexedPropertyChange$java_lang_String$int$java_lang_Object$java_lang_Object(propertyName: string, index: number, oldValue: any, newValue: any) {
            if (oldValue == null || newValue == null || !/* equals */(<any>((o1: any, o2: any) => o1 && o1.equals ? o1.equals(o2) : o1 === o2)(oldValue,newValue))){
                this.firePropertyChange$java_beans_PropertyChangeEvent(new java.beans.IndexedPropertyChangeEvent(this.source, propertyName, oldValue, newValue, index));
            }
        }

        public fireIndexedPropertyChange$java_lang_String$int$int$int(propertyName: string, index: number, oldValue: number, newValue: number) {
            if (oldValue !== newValue){
                this.fireIndexedPropertyChange$java_lang_String$int$java_lang_Object$java_lang_Object(propertyName, index, javaemul.internal.IntegerHelper.valueOf(oldValue), javaemul.internal.IntegerHelper.valueOf(newValue));
            }
        }

        public fireIndexedPropertyChange(propertyName?: any, index?: any, oldValue?: any, newValue?: any) {
            if (((typeof propertyName === 'string') || propertyName === null) && ((typeof index === 'number') || index === null) && ((typeof oldValue === 'number') || oldValue === null) && ((typeof newValue === 'number') || newValue === null)) {
                return <any>this.fireIndexedPropertyChange$java_lang_String$int$int$int(propertyName, index, oldValue, newValue);
            } else if (((typeof propertyName === 'string') || propertyName === null) && ((typeof index === 'number') || index === null) && ((typeof oldValue === 'boolean') || oldValue === null) && ((typeof newValue === 'boolean') || newValue === null)) {
                return <any>this.fireIndexedPropertyChange$java_lang_String$int$boolean$boolean(propertyName, index, oldValue, newValue);
            } else if (((typeof propertyName === 'string') || propertyName === null) && ((typeof index === 'number') || index === null) && ((oldValue != null) || oldValue === null) && ((newValue != null) || newValue === null)) {
                return <any>this.fireIndexedPropertyChange$java_lang_String$int$java_lang_Object$java_lang_Object(propertyName, index, oldValue, newValue);
            } else throw new Error('invalid overload');
        }

        public fireIndexedPropertyChange$java_lang_String$int$boolean$boolean(propertyName: string, index: number, oldValue: boolean, newValue: boolean) {
            if (oldValue !== newValue){
                this.fireIndexedPropertyChange$java_lang_String$int$java_lang_Object$java_lang_Object(propertyName, index, javaemul.internal.BooleanHelper.valueOf(oldValue), javaemul.internal.BooleanHelper.valueOf(newValue));
            }
        }

        public hasListeners(propertyName: string): boolean {
            return this.map.hasListeners(propertyName);
        }

        /*private*/ source: any;

        static serialVersionUID: number = 6401253773779951803;
    }
    PropertyChangeSupport["__class"] = "java.beans.PropertyChangeSupport";


    export namespace PropertyChangeSupport {

        export class PropertyChangeListenerMap extends java.beans.ChangeListenerMap<java.beans.PropertyChangeListener> {
            static EMPTY: java.beans.PropertyChangeListener[]; public static EMPTY_$LI$(): java.beans.PropertyChangeListener[] { if (PropertyChangeListenerMap.EMPTY == null) { PropertyChangeListenerMap.EMPTY = []; }  return PropertyChangeListenerMap.EMPTY; }

            /**
             * 
             * @param {number} length
             * @return {java.beans.PropertyChangeListener[]}
             */
            newArray(length: number): java.beans.PropertyChangeListener[] {
                return (0 < length) ? (s => { let a=[]; while(s-->0) a.push(null); return a; })(length) : PropertyChangeListenerMap.EMPTY_$LI$();
            }

            public newProxy$java_lang_String$java_beans_PropertyChangeListener(name: string, listener: java.beans.PropertyChangeListener): java.beans.PropertyChangeListener {
                return new java.beans.PropertyChangeListenerProxy(name, listener);
            }

            /**
             * 
             * @param {string} name
             * @param {*} listener
             * @return {*}
             */
            public newProxy(name?: any, listener?: any): any {
                if (((typeof name === 'string') || name === null) && ((listener != null && (listener.constructor != null && listener.constructor["__interfaces"] != null && listener.constructor["__interfaces"].indexOf("java.beans.PropertyChangeListener") >= 0)) || listener === null)) {
                    return <any>this.newProxy$java_lang_String$java_beans_PropertyChangeListener(name, listener);
                } else if (((typeof name === 'string') || name === null) && ((listener != null) || listener === null)) {
                     throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); 
                } else throw new Error('invalid overload');
            }

            public extract$java_beans_PropertyChangeListener(listener: java.beans.PropertyChangeListener): java.beans.PropertyChangeListener {
                while((listener != null && listener instanceof <any>java.beans.PropertyChangeListenerProxy)) {{
                    listener = (<java.beans.PropertyChangeListenerProxy><any>listener).getListener();
                }};
                return listener;
            }

            public extract(listener?: any): any {
                if (((listener != null && (listener.constructor != null && listener.constructor["__interfaces"] != null && listener.constructor["__interfaces"].indexOf("java.beans.PropertyChangeListener") >= 0)) || listener === null)) {
                    return <any>this.extract$java_beans_PropertyChangeListener(listener);
                } else if (((listener != null) || listener === null)) {
                     throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); 
                } else throw new Error('invalid overload');
            }

            constructor() {
                super();
            }
        }
        PropertyChangeListenerMap["__class"] = "java.beans.PropertyChangeSupport.PropertyChangeListenerMap";

    }

}

