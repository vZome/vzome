/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.io {
    export class StringWriter extends java.io.Writer {
        /*private*/ baos: java.io.ByteArrayOutputStream;

        /*private*/ w: java.io.OutputStreamWriter;

        public constructor() {
            super();
            if (this.baos === undefined) { this.baos = null; }
            if (this.w === undefined) { this.w = null; }
            this.baos = new java.io.ByteArrayOutputStream();
            this.w = new java.io.OutputStreamWriter(this.baos);
        }

        public toString(): string {
            return this.baos.toString();
        }

        public flush() {
            try {
                this.w.flush();
            } catch(e) {
                console.error(e.message, e);
            }
        }

        public close() {
            try {
                this.w.close();
            } catch(e) {
                console.error(e.message, e);
            }
        }

        public write(cbuf: string[], off: number, len: number) {
            try {
                this.w.write(cbuf, off, len);
            } catch(e) {
                console.error(e.message, e);
            }
        }
    }
    StringWriter["__class"] = "java.io.StringWriter";
    StringWriter["__interfaces"] = ["java.lang.Appendable","java.io.Closeable","java.lang.AutoCloseable","java.io.Flushable"];


}

