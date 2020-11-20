
export const GRID_HOVER_STARTED = 'GRID_HOVER_STARTED'
export const GRID_HOVER_STOPPED = 'GRID_HOVER_STOPPED'
export const GRID_CLICKED = 'GRID_CLICKED'
export const SHAPE_CLICKED = 'SHAPE_CLICKED'
export const BACKGROUND_CLICKED = 'BACKGROUND_CLICKED'

export const doStartGridHover = position =>
{
  return { type: GRID_HOVER_STARTED, payload: position }
}

export const doStopGridHover = position =>
{
  return { type: GRID_HOVER_STOPPED, payload: position }
}

export const doGridClick = position =>
{
  return { type: GRID_CLICKED, payload: position }
}

export const doShapeClick = vectors =>
{
  return { type: SHAPE_CLICKED, payload: vectors }
}

export const doBackgroundClick = () =>
{
  return { type: BACKGROUND_CLICKED }
}
