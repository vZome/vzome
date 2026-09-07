import { java, javaemul } from "../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";

/**
 * A minimal java.util.Properties, enough for the .properties files vZome ships
 * (shape colour overrides, and the symmetry/save-format properties).
 *
 * This was a do-nothing stub -- load() ignored its input, getProperty() returned
 * "" and isEmpty() always returned true -- which silently disabled every shape
 * colour override.  In bigzome, for instance, blue struts are meant to render
 * white and connectors silver; online drew them blue while desktop drew them
 * white.
 *
 * Supported syntax is the subset the shipped files actually use: "key = value",
 * blank lines, and comments introduced by # or !.  ':' is accepted as a
 * separator alongside '=', as java.util.Properties does.  Line continuations,
 * unicode escapes and escaped separators are NOT supported; no shipped file
 * uses them.
 */
export class Properties {
    /*private*/ map: { [key: string]: string } = {};

    public getProperty(key: string): string {
        const value = this.map[ key ];
        return value === undefined ? null : value;
    }

    public setProperty(key: string, value: string) {
        this.map[ key ] = value;
    }

    public load(inStream: java.io.InputStream) {
        if (inStream == null) return;
        //  The callers build a ByteArrayInputStream from a JS string, one char per
        //  byte, so read it back the same way.
        const bytes: number[] = [];
        let b: number;
        while ((b = inStream.read()) >= 0) bytes.push(b);
        const text: string = bytes.map(c => String.fromCharCode(c)).join('');
        for (const rawLine of text.split(/\r\n|\r|\n/)) {
            const line = rawLine.trim();
            if (line.length === 0) continue;
            if (line.charAt(0) === '#' || line.charAt(0) === '!') continue;
            let sep = -1;
            for (let i = 0; i < line.length; i++) {
                const c = line.charAt(i);
                if (c === '=' || c === ':') { sep = i; break; }
            }
            if (sep < 0) {
                //  A bare key with no separator is a key with an empty value.
                this.map[ line ] = '';
                continue;
            }
            const key = line.substring(0, sep).trim();
            const value = line.substring(sep + 1).trim();
            if (key.length > 0) this.map[ key ] = value;
        }
    }

    public isEmpty(): boolean {
        return Object.keys(this.map).length === 0;
    }
}
Properties["__class"] = "java.util.Properties";
