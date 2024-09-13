/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic {
    /**
     * An exception thrown by the Zomic parser or interpreter.
     * A ZomeException may wrap another exception.
     * Handlers are obligated to "unwrap" a ZomeException
     * before reporting it, by calling getCulprit().
     * @param {java.lang.Exception} culprit
     * @class
     * @extends java.lang.Exception
     */
    export class ZomicException extends Error {
        /*private*/ m_culprit: Error;

        public constructor(culprit?: any) {
            if (((culprit != null && culprit instanceof <any>Error) || culprit === null)) {
                let __args = arguments;
                super("wrapped"); this.message="wrapped";
                if (this.m_culprit === undefined) { this.m_culprit = null; } 
                this.m_culprit = culprit;
            } else if (((typeof culprit === 'string') || culprit === null)) {
                let __args = arguments;
                let msg: any = __args[0];
                super(msg); this.message=msg;
                if (this.m_culprit === undefined) { this.m_culprit = null; } 
            } else throw new Error('invalid overload');
        }

        /**
         * Return the original culprit wrapped by this ZomeException.
         * Arbitrarily deep wrapping will be unwrapped by a single call
         * to getCulprit.  If there is no culprit, returns this ZomeException.
         * @return {java.lang.Exception}
         */
        public getCulprit(): Error {
            if (this.m_culprit == null){
                return this;
            }
            if (this.m_culprit != null && this.m_culprit instanceof <any>com.vzome.core.zomic.ZomicException){
                return (<ZomicException>this.m_culprit).getCulprit();
            } else {
                return this.m_culprit;
            }
        }
    }
    ZomicException["__class"] = "com.vzome.core.zomic.ZomicException";
    ZomicException["__interfaces"] = ["java.io.Serializable"];


}

