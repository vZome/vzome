
export const assemblePartsList = shapes =>
{
  const bom = {};
  for (const key in shapes) {
    const { orbit, name, instances } = shapes[ key ];
    if ( !name ) continue;
    const row = name;
    for (const { color } of instances) {
      if ( ! bom[ row ] )
        bom[ row ] = 1;
      else
        bom[ row ] = bom[ row ] + 1;
    }
  }
  return bom;
}