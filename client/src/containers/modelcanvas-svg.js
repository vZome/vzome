
import React from 'react';
import { connect } from 'react-redux'

const Strut = ({ start, end, color }) => {
  return (
    <line
      x1={ start.x } y1={ start.y }
      x2={ end.x } y2={ end.y }
      strokeWidth="4" stroke={color}/>
  )
}

let ModelCanvas = ({ enabled, width, height, scale, segments }) => {
  
  const center = { x: width/2, y: height/2 };
  const transform = (point) => {
    return { x: scale*point.x + center.x, y: scale*point.y + center.y }
  }

  return (
    enabled?
      <svg width={width} height={height}>
        <rect x="0" y="0" width={width} height={height}
          style={{stroke: "#000088", strokeWidth: 3, fill: 'none'}} />
        {
          segments.map( segment =>
            <Strut key={segment.id}
              start={transform(segment.start)}
              end={transform(segment.end)}
              color={segment.color}
            />
          )
        }
      </svg>
    :
      <div/>
  )
}

const mapStateToProps = (state) => ({
  enabled: state.connectionLive,
  segments: state.segments
})

ModelCanvas = connect(mapStateToProps)(ModelCanvas)

export default ModelCanvas
