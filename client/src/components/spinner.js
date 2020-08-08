
import React from 'react'
import Loader from "react-loader-spinner";
import { connect } from 'react-redux'

export const Spinner = ( { visible } ) =>
{
  if ( visible )
    return (
      <div className="overlay">
        <div className="spinner">
          <Loader type="Grid" color="#CCCCCC" height={200} width={200} />
        </div>
      </div>)
  else
    return <div/>
}

const select = ( { progress } ) => ({
  visible: progress
})

export default connect( select )( Spinner )
