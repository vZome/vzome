
import React from 'react'
import { connect } from 'react-redux'

import * as designFns from '../bundles/designs.js'

import { makeStyles, withStyles } from '@material-ui/core/styles'
import MenuItem from '@material-ui/core/MenuItem'
import FormControl from '@material-ui/core/FormControl'
import Select from '@material-ui/core/Select'
import InputBase from '@material-ui/core/InputBase';

const BootstrapInput = withStyles((theme) => ({
  root: {
    'label + &': {
      marginTop: theme.spacing(3),
    },
  },
  input: {
    borderRadius: 4,
    position: 'relative',
    backgroundColor: theme.palette.background.paper,
    border: '1px solid #ced4da',
    fontSize: 16,
    padding: '10px 26px 10px 12px',
    transition: theme.transitions.create(['border-color', 'box-shadow']),
    // Use the system font instead of the default Roboto font.
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
      '"Apple Color Emoji"',
      '"Segoe UI Emoji"',
      '"Segoe UI Symbol"',
    ].join(','),
    '&:focus': {
      borderRadius: 4,
      borderColor: '#80bdff',
      backgroundColor: theme.palette.background.paper,
      boxShadow: '0 0 0 0.2rem rgba(0,123,255,.25)',
    },
  },
}))(InputBase)

const useStyles = makeStyles((theme) => ({
  formControl: {
    margin: theme.spacing(1),
    minWidth: '20%',
  },
}))

const DesignsSelect = ( { designs, current, doSelectModel } ) =>
{
  const classes = useStyles()

  const handleChange = event =>
  {
    doSelectModel( event.target.value )
  }

  if ( ! designs )
    return null

  return (
    <FormControl variant="filled" className={classes.formControl}>
      <Select value={current}
        id="designs-select"
        onChange={handleChange}
        label="Design"
        input={<BootstrapInput />}
      >
        { Object.keys( designs ).map( id =>
          <MenuItem key={id} value={id}>{designs[ id ].name}</MenuItem>
        ) }
      </Select>
    </FormControl>
  )
}


const select = ( state ) =>
{
  const { designs } = state
  return {
    designs: designs && designs.data,
    current: designs && designs.current
  }
}

const boundEventActions = {
  doSelectModel: designFns.switchModel,
}

export default connect( select, boundEventActions )( DesignsSelect )
