export class Dimension {
    /*private*/ width: number;

    /*private*/ height: number;

    public constructor(width: number, height: number) {
        if (this.width === undefined) { this.width = 0; }
        if (this.height === undefined) { this.height = 0; }
        this.width = width;
        this.height = height;
    }
}
Dimension["__class"] = "java.awt.Dimension";
