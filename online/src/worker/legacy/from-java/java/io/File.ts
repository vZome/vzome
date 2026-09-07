export class File {
    public constructor(parent: File, name: string) {
    }

    public exists(): boolean {
        return false;
    }

    public getAbsolutePath(): string {
        return null;
    }

    public getName(): string {
        return null;
    }

    public getParentFile(): File {
        return null;
    }
}
File["__class"] = "java.io.File";
