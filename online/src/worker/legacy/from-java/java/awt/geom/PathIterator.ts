export interface PathIterator {
    isDone(): boolean;

    currentSegment(coords: number[]): number;

    next();
}

export namespace PathIterator {

    export const SEG_MOVETO: number = 0;

    export const SEG_LINETO: number = 1;

    export const SEG_CLOSE: number = 4;
}
