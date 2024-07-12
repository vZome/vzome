
export const assemblePartsList = shapes =>
{
  const bom = {};
  for (const id in shapes) {
    const { orbit, length, name, instances } = shapes[ id ];
    if ( !name && !orbit ) continue;
    const row = name || `${orbit}:${length}`
    for (const { color } of instances) {
      if ( ! bom[ row ] )
        bom[ row ] = 1;
      else
        bom[ row ] = bom[ row ] + 1;
    }
  }
  return bom;
}