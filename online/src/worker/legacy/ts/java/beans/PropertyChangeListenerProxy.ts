/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.beans {
    /**
     * Constructor which binds the {@code PropertyChangeListener}
     * to a specific property.
     * 
     * @param {string} propertyName  the name of the property to listen on
     * @param {*} listener      the listener object
     * @class
     * @extends java.util.EventListenerProxy
     */
    export class PropertyChangeListenerProxy extends java.util.EventListenerProxy<java.beans.PropertyChangeListener> implements java.beans.PropertyChangeListener {
        /*private*/ propertyName: string;

        public constructor(propertyName: string, listener: java.beans.PropertyChangeListener) {
            super(listener);
            if (this.propertyName === undefined) { this.propertyName = null; }
            this.propertyName = propertyName;
        }

        /**
         * Forwards the property change event to the listener delegate.
         * 
         * @param {java.beans.PropertyChangeEvent} event  the property change event
         */
        public propertyChange(event: java.beans.PropertyChangeEvent) {
            this.getListener().propertyChange(event);
        }

        /**
         * Returns the name of the named property associated with the listener.
         * 
         * @return {string} the name of the named property associated with the listener
         */
        public getPropertyName(): string {
            return this.propertyName;
        }
    }
    PropertyChangeListenerProxy["__class"] = "java.beans.PropertyChangeListenerProxy";
    PropertyChangeListenerProxy["__interfaces"] = ["java.util.EventListener","java.beans.PropertyChangeListener"];


}

