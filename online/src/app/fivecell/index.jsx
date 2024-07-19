
import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'
import Container from '@suid/material/Container'
import Paper from '@suid/material/Paper'

import { UrlViewer } from '../../viewer/index.jsx'
import { VZomeAppBar } from '../classic/components/appbar.jsx';

const viewerStyle = {
  height: "400px",
  "min-height": "400px",
  "max-height": "60vh",
  "margin-left": "15%",
  "margin-right": "15%",
  "margin-top": "15px",
  "margin-bottom": "15px",
  "border-width": "medium",
  "border-radius": "10px",
  border: "solid",
}

const VZomeViewer = props =>
{
  const url = () => props.name && new URL( `/models/2007/04-Apr/5cell/${props.name}.vZome`, window.location ) .toString();

  return (
    <div style={viewerStyle}>
      <UrlViewer url={url()} />
    </div>
  )
}

const Article = () =>
{
  return (
    <ErrorBoundary fallback={err => <div>{err.toString()}</div>} >
      <VZomeAppBar title='Apps'
        about={ <>
          <Typography gutterBottom>
            vZome Apps are demonstrations
            of <Link target="_blank" rel="noopener" href="https://vzome.com/home/">vZome</Link> technology ported to the web,
            showing how to build web applications that incorporate interactive 3D views.
          </Typography>
        </> }
      />
      <Container maxWidth="md">
        <Paper>
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
          <VZomeViewer name="greenTetra" />
          <Typography gutterBottom >
            Until 2007, my friend <Link target="_blank" href="https://homepages.wmich.edu/~drichter" rel="noopener" >David Richter</Link> and
            I believed that there was no other orthogonal 3D projection of the 5-cell 
            that could be constructed with Zome. This turns out to be false. I stumbled upon such a projection,
            and David saw immediately that this implied that the entire family of 9 uniform A4 polytopes could also be projected in this manner.
            Furthermore, David showed that there are two other rZome-constructible projections, one using red, yellow, and blue,
            and one using just yellow and blue.
          </Typography>
          <VZomeViewer name="A4_18" />
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
          <VZomeViewer name="A4_9" />
          <Typography gutterBottom >
            All of these are constructible in real Zome, in principle. 
            However, several of them are challenging to build, due to the false intersections at inconvenient places, 
            or simply due to the high degree of interconnectedness, which makes it difficult to get all the struts in place at the same time. 
            One alternative is to build the "hidden cell removed" versions, where half of the interior struts are omitted. 
            This corresponds to projecting only one hemisphere of the 4D hypersphere into 3D.
            Unfortunately, doing so will destroy the 5-fold symmetric shadows.
          </Typography>
          <VZomeViewer name="A4_BD" />
        </Paper>
      </Container>
    </ErrorBoundary>
  );
}

render( Article, document.getElementById( 'root' ) );
