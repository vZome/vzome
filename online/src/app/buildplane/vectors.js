
export const difference = ( a, b ) => a .map( (x,i) => x - b[i] );

export const vlength = v => Math.sqrt( v .reduce( (sum,x) => sum + x**2, 0 ) );

export const vscale = ( v, s ) =>  v.map( x => x*s );

export const normalize = v => vscale( v, 1 / vlength( v ) );
