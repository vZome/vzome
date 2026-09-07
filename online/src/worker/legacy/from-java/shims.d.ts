// Supplemental ambient declarations for types that j4ts does not provide.
//
// These all sit on code paths that online vZome never executes (the generated
// JavaScript has the same gaps).  They exist only to satisfy `tsc`; there is no
// runtime counterpart, and none is needed.  java.io.FileWriter, for instance,
// appears in neither j4ts.d.ts nor the runtime bundle.js.

declare namespace java.math {
    class BigInteger {
        longValue(): number;
        intValue(): number;
        toString(): string;
    }
    class BigDecimal { }
}

declare namespace java.io {
    class FileWriter extends java.io.Writer {
        constructor(...args: any[]);
    }
}

declare namespace java.lang {
    class Thread {
        static currentThread(): any;
        getName(): string;
    }
}
