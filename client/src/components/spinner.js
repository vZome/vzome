
import React from 'react'
import Loader from "react-loader-spinner";
import { connect } from 'react-redux'

export const Spinner = ( { visible } ) =>
{
  if ( visible )
    return (
      <div className="overlay">
        <Loader type="Rings" color="#CCCCCC" height={800} width={300} />
      </div>)
  else
    return <div/>
}

const select = ( { progress } ) => ({
  visible: progress
})

export default connect( select )( Spinner )
