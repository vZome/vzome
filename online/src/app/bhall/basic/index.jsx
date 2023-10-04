
import { render } from 'solid-js/web';
import { ErrorBoundary, For, createSignal } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'
import Container from '@suid/material/Container'
import Paper from '@suid/material/Paper'
import Divider from '@suid/material/Divider'

import { Tab, Tabs } from '../../framework/tabs.jsx';

import { UrlViewer } from '../../../viewer/solid/index.jsx'
import { VZomeAppBar } from '../../classic/components/appbar.jsx';

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
      "description": "",
      config: { showScenes: false }
    },
  },
}

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

const VZomeViewer = (props) =>
{
  const url = () => props.name && new URL( `/bhall/basics/${props.name}.vZome`, window.location ) .toString();

  return (
    <>
      <Divider />
      <div style={viewerStyle}>
        <UrlViewer url={url()} config={props.config} />
      </div>
      <Typography gutterBottom align='center' variant="h6" >{props.name}</Typography>
      <Typography gutterBottom align='center' >Build with {props.parts}</Typography>
    </>
  )
}

const BHallBasic = () =>
{
  const [ difficulty, setDifficulty ] = createSignal( "easier" );

  const handleChange = (event, newValue) => {
    setDifficulty( newValue )
  };
  
  return (
    <ErrorBoundary fallback={err => <div>{err.toString()}</div>} >
      <VZomeAppBar title='Apps'
        about={ <>
          <Typography gutterBottom>
            vZome Online Apps are demonstrations
            of <Link target="_blank" rel="noopener" href="https://vzome.com/home/">vZome</Link> technology ported to the web,
            showing how to build web applications that incorporate interactive 3D views.
          </Typography>
        </> }
      />
      <Container maxWidth="md">
        <Paper>
          <Typography variant="h2" gutterBottom >
            Brian Hall's Zometool Designs
          </Typography>
          <Typography gutterBottom >
            Brian Hall is a professor of Mathematical Physics, and an expert
            with <Link target="_blank" rel="noopener" href="https://zometool.com" >Zometool</Link>.
            Using <Link target="_blank" rel="noopener" href="https://www.vzome.com" >vZome</Link> to
            explore different possibilities,
            Brian was able to create a number of beautiful and interesting designs,
            all constructible with fairly small Zometool sets.
            Here are a few of them for your enjoyment; try constructing them in the real world!
            Note that they are organized into three sections, by difficulty.
          </Typography>
          <Typography gutterBottom color='secondary' >
            The panels are interactive, not images; you can use your mouse to rotate, pan, and zoom.
          </Typography>
          <Tabs label="choose difficulty" values={ Object.keys( metadata )} value={difficulty()} onChange={handleChange}>
            <For each={ Object.keys( metadata ) }>{ tabname =>
              <Tab value={tabname}>
                <For each={ Object.keys( metadata[ tabname ] ) }>{ name =>
                  <VZomeViewer name={name} parts={metadata[ tabname ][name].parts} config={metadata[ tabname ][name].config || {}} />
                }</For>
              </Tab>
            }</For>
          </Tabs>
        </Paper>
      </Container>
    </ErrorBoundary>
  );
}

render( BHallBasic, document.getElementById( 'root' ) )
