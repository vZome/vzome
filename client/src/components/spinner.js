
import React from 'react'
import Loader from "react-loader-spinner";
import { connect } from 'react-redux'

export const Spinner = ( { visible, message } ) =>
{
  if ( visible )
    return (
      <div className="overlay">
        <div className="spinner">
          <Loader type="Grid" color="#CCCCCC" height={200} width={200} />
        </div>
        <h2 id="message">{message}</h2>
      </div>)
  else
    return null
}

const select = ( { progress } ) => ({
  visible: progress.showing,
  message: progress.message
})

export default connect( select )( Spinner )
