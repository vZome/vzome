import React, { useEffect } from 'react'
import { useDispatch } from 'react-redux'
import { makeStyles } from '@material-ui/core/styles'
import Container from '@material-ui/core/Container'
import Paper from '@material-ui/core/Paper'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'

import VZomeAppBar from './components/appbar.js'
import ModelCanvas from './components/modelcanvas-three.js'

const useStyles = makeStyles( (theme) => ({
  viewer: {
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
  },
  paper: {
    paddingLeft: "2%",
    paddingRight: "2%",
  }
}));

const Article = () =>
{
  // const dispatch = useDispatch();
  // useEffect( () => {
  //   console.log( 'USED EFFECT' )
  //   const model = '120-cell'
  //   dispatch( fetchModel( `/app/models/${model}.vZome`, model ) )
  // })
  const classes = useStyles();
  return (
    <>
      <VZomeAppBar article/>
      <Container maxWidth="md">
        <Paper className={classes.paper}>
          <Typography variant="h2" gutterBottom >
            The 5-Cell and its Family
          </Typography>
          <Typography gutterBottom >
            The 5-cell is the simplest 4-dimensional regular polytope, consisting of 5 equivalent tetrahedral cells.
            We can experience this in three dimensions only by projecting from 4D down to 3D.
            There is a well-known orthogonal projection that can be built
            in <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>,
            shown below.
            You can count the five tetrahedra: the outer green one, and the four interior tetrahedra with three yellow edges
            foreshortened by the projection.
          </Typography>
          <div className={classes.viewer}>
            <ModelCanvas design='greenTetra' />
          </div>
          <Typography gutterBottom >
            Until 2007, my friend <Link target="_blank" href="https://homepages.wmich.edu/~drichter" rel="noopener" >David Richter</Link> and
            I believed that there was no other orthogonal 3D projection of the 5-cell 
            that could be constructed with Zome. This turns out to be false. I stumbled upon such a projection,
            and David saw immediately that this implied that the entire family of 9 uniform A4 polytopes could also be projected in this manner.
            Furthermore, David showed that there are two other rZome-constructible projections, one using red, yellow, and blue,
            and one using just yellow and blue.
          </Typography>
          <div className={classes.viewer}>
            <ModelCanvas design='A4_18' />
          </div>
          <Typography gutterBottom >
            The remarkable thing about this projection is that it exhibits "ghost symmetry". 
            This is our term for a phenomenon wherein the symmetry of a 4D object is subtly exposed in a 3D projection. 
            Specifically, in this example, the new projection of the 5-cell has a 3D symmetry group that is nearly as simple as you can get: 
            two orthogonal mirrors. It certainly does not have any overt fivefold symmetry in 3D. 
            Nonetheless, there are two axes along which one can project orthogonally to 2D and obtain perfect fivefold symmetry!
            You can see this clearly if you zoom out in the 3d view, using your scroll wheel.
          </Typography>
          <Typography gutterBottom >
            This article illustrates the "ghost symmetry" projection for a few of the 8 uniform polytopes
            that have the symmetry of the 5-cell, known as the A4 Coxeter group.
            For more discussion of this group, 
            see <Link target="_blank" href="https://homepages.wmich.edu/~drichter/a4polychorazome.htm" rel="noopener" >David's page</Link> on
            the other Zome projections of the A4 family.
          </Typography>
          <div className={classes.viewer}>
            <ModelCanvas design='A4_9' />
          </div>
          <Typography gutterBottom >
            All of these are constructible in real Zome, in principle. 
            However, several of them are challenging to build, due to the false intersections at inconvenient places, 
            or simply due to the high degree of interconnectedness, which makes it difficult to get all the struts in place at the same time. 
            One alternative is to build the "hidden cell removed" versions, where half of the interior struts are omitted. 
            This corresponds to projecting only one hemisphere of the 4D hypersphere into 3D.
            Unfortunately, doing so will destroy the 5-fold symmetric shadows.
          </Typography>
          <div className={classes.viewer}>
            <ModelCanvas design='A4_BD' />
          </div>
        </Paper>
      </Container>
    </>
  );
}

export default Article;
