import React from 'react'
import { makeStyles } from '@material-ui/core/styles'
import Container from '@material-ui/core/Container'
import Paper from '@material-ui/core/Paper'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'
import Divider from '@material-ui/core/Divider'
import Tabs from '@material-ui/core/Tabs'
import Tab from '@material-ui/core/Tab'

import { VZomeAppBar } from './components/appbar.jsx'
import { DesignViewer } from '../ui/viewer/index.jsx'

const metadata = {
  easier: {
    "10sided_die1": {
      "parts": "10 long yellows, 10 short reds, and 12 balls",
      "description": ""
    },
    "10sided_die2": {
      "parts": "10 long reds, 10 medium yellows, and 12 balls.",
      "description": ""
    },
    "borromean1": {
      "parts": "12 short reds, 6 medium blues, 6 long blues, and 13 balls.",
      "description": ""
    },
    "4triangles1": {
      "parts": "24 medium blues, 12 short reds, and 25 balls.",
      "description": ""
    },
    "redTetra1": {
      "parts": "12 short reds, 12 long reds, and 20 balls.",
      "description": ""
    },
  },
  medium: {
    "borromean2": {
      "parts": "24 medium reds, 6 medium blues, and 25 balls.",
      "description": ""
    },
    "borro_with_cube": {
      "parts": "6 short blues, 12 medium blues, 6 long blues, and 20 medium yellows.",
      "description": ""
    },
    // "3exes1" :{
    //   "parts": "I think we are not using this one.",
    //   "description": ""
    // },
    "Tulip": {
      "parts": "5 short blues, 5 medium reds, 10 long reds, 15 short yellows, and 26 balls.",
      "description": ""
    },
    "doublePentagon2": {
      "parts": "5 long reds, 5 medium yellows, and 10 balls.",
      "description": ""
    },
    "4triangles2": {
      "parts": "12 medium blues, 12 long blues, and 16 balls.",
      "description": ""
    },
    "4triangles7": {
      "parts": "12 medium blues, 12 long blues, and 18 balls.",
      "description": ""
    }
  },
  harder: {
    "kissOcta": {
      "parts": "24 short blues and 20 balls. Some pairs of balls should just touch without overlapping.",
      "description": ""
    },
    "lotta_triangles": {
      "parts": "60 medium blues, 60 medium yellows, and 42 balls.",
      "description": ""
    },
    "redDiamonds": {
      "parts": "20 short reds, 20 long reds, and 36 balls.",
      "description": ""
    },
    "doubleSpiral": {
      "parts": "10 short reds, 10 medium reds, 20 long reds, 10 short yellows, and 47 balls.",
      "description": ""
    },
  },
}

const useStyles = makeStyles( (theme) => ({
  paper: {
    paddingLeft: "2%",
    paddingRight: "2%",
  }
}));

const viewerStyle = {
  height: "400px",
  minHeight: "400px",
  maxHeight: "60vh",
  marginLeft: "15%",
  marginRight: "15%",
  marginTop: "15px",
  marginBottom: "15px",
  borderWidth: "medium",
  borderRadius: "10px",
  border: "solid",
}

const VZomeViewer = ({ name, parts }) =>
{
  return (
    <>
      <Divider />
      <div style={viewerStyle}>
        <DesignViewer url={`https://vzome.com/bhall/basics/${name}.vZome`}/>
      </div>
      <Typography gutterBottom align='center' variant="h6" >{name}</Typography>
      <Typography gutterBottom align='center' >Build with {parts}</Typography>
    </>
  )
}

const Article = () =>
{
  const classes = useStyles()
  const [difficulty, setDifficulty] = React.useState( 0 );
  const labels = Object.keys( metadata )

  const handleChange = (event, newValue) => {
    console.log( newValue );
    setDifficulty( newValue )
  };
  return (
    <>
      <VZomeAppBar article/>
      <Container maxWidth="md">
        <Paper className={classes.paper}>
          <Typography variant="h2" gutterBottom >
            Brian Hall's Zometool Designs
          </Typography>
          <Typography gutterBottom >
            Brian Hall is a professor of Mathematical Physics, and an expert
            with <Link target="_blank" rel="noopener" href="https://zometool.com" >Zometool</Link>.
            Using <Link target="_blank" rel="noopener" href="https://vzome.com" >vZome</Link> to
            explore different possibilities,
            Brian was able to create a number of beautiful and interesting designs,
            all constructible with fairly small Zometool sets.
            Here are a few of them for your enjoyment; try constructing them with Zometool!
            Note that they are organized into three sections, by difficulty.
          </Typography>
          <Typography gutterBottom color='secondary' >
            The panels are not images; you can use your mouse to rotate, pan, and zoom.
          </Typography>
          <Paper square>
            <Tabs value={difficulty} onChange={handleChange} indicatorColor="primary" textColor="primary"
                centered aria-label="choose difficulty" >
              <Tab label={labels[0]} />
              <Tab label={labels[1]} />
              <Tab label={labels[2]} />
            </Tabs>
          </Paper>
          { Object.entries( metadata[ labels[ difficulty ] ] ).map( ([ name, value ]) => <VZomeViewer name={name} parts={value.parts} /> )}
        </Paper>
      </Container>
    </>
  );
}

export default Article
