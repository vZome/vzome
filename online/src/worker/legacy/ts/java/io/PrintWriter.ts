/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.io {
    export class PrintWriter extends java.io.Writer {
        /*private*/ w: java.io.Writer;

        public constructor(w: java.io.Writer) {
            super();
            if (this.w === undefined) { this.w = null; }
            this.w = w;
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

        public write$char_A$int$int(cbuf: string[], off: number, len: number) {
            try {
                this.w.write(cbuf, off, len);
            } catch(e) {
                console.error(e.message, e);
            }
        }

        public write(cbuf?: any, off?: any, len?: any) {
            if (((cbuf != null && cbuf instanceof <any>Array && (cbuf.length == 0 || cbuf[0] == null ||(typeof cbuf[0] === 'string'))) || cbuf === null) && ((typeof off === 'number') || off === null) && ((typeof len === 'number') || len === null)) {
                return <any>this.write$char_A$int$int(cbuf, off, len);
            } else if (((typeof cbuf === 'string') || cbuf === null) && off === undefined && len === undefined) {
                return <any>this.write$java_lang_String(cbuf);
            } else throw new Error('invalid overload');
        }

        public write$java_lang_String(str: string) {
            try {
                this.w.write(str);
            } catch(e) {
                console.error(e.message, e);
            }
        }

        public println$() {
            this.print("\n");
        }

        public print(x: any) {
            if (!(typeof x === 'string'))x = x.toString();
            const chars: string[] = /* toCharArray */((<string>x)).split('');
            this.write$char_A$int$int(chars, 0, chars.length);
        }

        public println$java_lang_Object(x: any) {
            this.print(x);
            this.println$();
        }

        public println(x?: any) {
            if (((x != null) || x === null)) {
                return <any>this.println$java_lang_Object(x);
            } else if (x === undefined) {
                return <any>this.println$();
            } else throw new Error('invalid overload');
        }
    }
    PrintWriter["__class"] = "java.io.PrintWriter";
    PrintWriter["__interfaces"] = ["java.lang.Appendable","java.io.Closeable","java.lang.AutoCloseable","java.io.Flushable"];


}

