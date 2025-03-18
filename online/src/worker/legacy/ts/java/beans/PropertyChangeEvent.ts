/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.beans {
    export class PropertyChangeEvent extends java.util.EventObject {
        static __java_beans_PropertyChangeEvent_serialVersionUID: number = 7042693688939648123;

        public constructor(source: any, propertyName: string, oldValue: any, newValue: any) {
            super(source);
            if (this.propertyName === undefined) { this.propertyName = null; }
            if (this.newValue === undefined) { this.newValue = null; }
            if (this.oldValue === undefined) { this.oldValue = null; }
            if (this.propagationId === undefined) { this.propagationId = null; }
            this.propertyName = propertyName;
            this.newValue = newValue;
            this.oldValue = oldValue;
        }

        public getPropertyName(): string {
            return this.propertyName;
        }

        public getNewValue(): any {
            return this.newValue;
        }

        public getOldValue(): any {
            return this.oldValue;
        }

        public setPropagationId(propagationId: any) {
            this.propagationId = propagationId;
        }

        public getPropagationId(): any {
            return this.propagationId;
        }

        /*private*/ propertyName: string;

        /*private*/ newValue: any;

        /*private*/ oldValue: any;

        /*private*/ propagationId: any;

        public toString(): string {
            const sb: java.lang.StringBuilder = new java.lang.StringBuilder(/* getName */(c => typeof c === 'string' ? c : c["__class"] ? c["__class"] : c["name"])((<any>this.constructor)));
            sb.append("[propertyName=").append(this.getPropertyName());
            this.appendTo(sb);
            sb.append("; oldValue=").append(this.getOldValue());
            sb.append("; newValue=").append(this.getNewValue());
            sb.append("; propagationId=").append(this.getPropagationId());
            sb.append("; source=").append(this.getSource());
            return sb.append("]").toString();
        }

        appendTo(sb: java.lang.StringBuilder) {
        }
    }
    PropertyChangeEvent["__class"] = "java.beans.PropertyChangeEvent";
    PropertyChangeEvent["__interfaces"] = ["java.io.Serializable"];


}

