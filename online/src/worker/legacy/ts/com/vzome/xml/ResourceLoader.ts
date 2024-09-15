/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.xml {
    export class ResourceLoader {
        static RESOURCE_LOADER: ResourceLoader; public static RESOURCE_LOADER_$LI$(): ResourceLoader { if (ResourceLoader.RESOURCE_LOADER == null) { ResourceLoader.RESOURCE_LOADER = new ResourceLoader(); }  return ResourceLoader.RESOURCE_LOADER; }

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (ResourceLoader.logger == null) { ResourceLoader.logger = java.util.logging.Logger.getLogger("com.vzome.xml.ResourceLoader"); }  return ResourceLoader.logger; }

        public static setResourceLoader(loader: ResourceLoader) {
            ResourceLoader.RESOURCE_LOADER = loader;
        }

        public static loadStringResource(path: string): string {
            return ResourceLoader.RESOURCE_LOADER_$LI$().loadTextResource(path);
        }

        public loadTextResource(path: string): string {
            try {
                const input: java.io.InputStream = ResourceLoader.getClassLoader().getResourceAsStream(path);
                if (input == null)return null;
                const out: java.io.ByteArrayOutputStream = new java.io.ByteArrayOutputStream();
                const buf: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(1024);
                let num: number;
                while(((num = input.read(buf, 0, 1024)) > 0)) {out.write(buf, 0, num)};
                input.close();
                return <string>new String(out.toByteArray());
            } catch(e) {
                if (ResourceLoader.logger_$LI$().isLoggable(java.util.logging.Level.FINE))ResourceLoader.logger_$LI$().fine("problem loading resource: " + path);
                return null;
            }
        }
    }
    ResourceLoader["__class"] = "com.vzome.xml.ResourceLoader";

}

