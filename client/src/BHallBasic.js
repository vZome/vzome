import React from 'react'
import { makeStyles } from '@material-ui/core/styles'
import Container from '@material-ui/core/Container'
import Paper from '@material-ui/core/Paper'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'
import Divider from '@material-ui/core/Divider';

import VZomeAppBar from './components/appbar.js'
import { UrlViewer } from '@vzome/react-vzome'

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

const VZomeViewer = ({ name }) =>
{
  return (
    <>
      <Divider />
      <div style={viewerStyle}>
        <UrlViewer url={`https://vzome.com/bhall/basics/${name}.vZome`}/>
      </div>
      <Typography gutterBottom align='center' variant="h6" >{name}</Typography>
    </>
  )
}

const Article = () =>
{
  const classes = useStyles()
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
          </Typography>
          <Typography gutterBottom color='secondary' >
            The panels are not images; you can use your mouse to rotate, pan, and zoom.
          </Typography>
          <VZomeViewer name={"10sided_die1"} />
          <VZomeViewer name={"10sided_die2"} />
          <VZomeViewer name={"4triangles1"} />
          <VZomeViewer name={"4triangles2"} />
          <VZomeViewer name={"4triangles7"} />
          <VZomeViewer name={"5cell"} />
          <VZomeViewer name={"Tulip"} />
          <VZomeViewer name={"borro_with_cube"} />
          <VZomeViewer name={"borromean1"} />
          <VZomeViewer name={"borromean2"} />
          <VZomeViewer name={"doublePentagon"} />
          <VZomeViewer name={"doublePentagon2"} />
          <VZomeViewer name={"doubleSpiral"} />
          {/* <VZomeViewer name={"kissOcta"} />
          <VZomeViewer name={"lotta_triangles"} />
          <VZomeViewer name={"redDiamonds"} />
          <VZomeViewer name={"redTetra1"} />
          <VZomeViewer name={"redTetra2"} />
          <VZomeViewer name={"redTetra3"} />
          <VZomeViewer name={"redTetra4"} />
          <VZomeViewer name={"redTetra5"} />
          <VZomeViewer name={"spiral1"} />
          <VZomeViewer name={"spiral2"} />
          <VZomeViewer name={"yellowTetra1"} />
          <VZomeViewer name={"yellowTetra2"} />
          <VZomeViewer name={"yellow_crystal"} /> */}
        </Paper>
      </Container>
    </>
  );
}

export default Article
