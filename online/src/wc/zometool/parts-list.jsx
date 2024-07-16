import { render } from "solid-js/web";
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@suid/material";

import { instructionsCSS } from "./zometool.css.js";

import ballSvg from './parts/ball.svg';
import b0Svg from './parts/b0.svg';
import b1Svg from './parts/b1.svg';
import b2Svg from './parts/b2.svg';
import y0Svg from './parts/y0.svg';
import y1Svg from './parts/y1.svg';
import y2Svg from './parts/y2.svg';
import r00Svg from './parts/r00.svg';
import r0Svg from './parts/r0.svg';
import r1Svg from './parts/r1.svg';
import r2Svg from './parts/r2.svg';
import g0Svg from './parts/g0.svg';
import g1Svg from './parts/g1.svg';
import g2Svg from './parts/g2.svg';
import hg0Svg from './parts/hg0.svg';
import hg1Svg from './parts/hg1.svg';
import hg2Svg from './parts/hg2.svg';

const svgStrings = {
  ball: ballSvg,
  b0  : b0Svg,
  b1  : b1Svg,
  b2  : b2Svg,
  y0  : y0Svg,
  y1  : y1Svg,
  y2  : y2Svg,
  r00 : r00Svg,
  r0  : r0Svg,
  r1  : r1Svg,
  r2  : r2Svg,
  g0  : g0Svg,
  g1  : g1Svg,
  g2  : g2Svg,
  hg0 : hg0Svg,
  hg1 : hg1Svg,
  hg2 : hg2Svg,
}

const getSvgNode = key =>
{
  let doc = new DOMParser().parseFromString( svgStrings[ key ], 'application/xml' );
  return document .importNode( doc.documentElement, true );
}

const debug = false;

const ZometoolParts = props =>
{
  const partSx = { height: '24px', padding: '0', width: '210px', lineHeight: '0' };
  return (
    <div class='zometool-parts-table'>
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 250 }} aria-label="parts table" size="small">
          <TableHead>
            <TableRow>
              <TableCell sx={{ fontWeight: 'bold' }}>Part</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Length</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }} align="right">Count</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
          <For each={props.bom}>{ ({ key, count }) =>
            <TableRow sx={{ "&:last-child td, &:last-child th": { border: 0 } }} >
              <TableCell align="left" component="th" scope="row" sx={partSx}>
                {getSvgNode( key )}
              </TableCell>
              <TableCell component="th" scope="row">{ ((key==='ball')? '' : key) .toUpperCase() }</TableCell>
              <TableCell align="right">{count}</TableCell>
            </TableRow>
          }</For>
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}

const renderComponent = ( container, bom ) =>
  {
    const bindComponent = () =>
    {
      return (
        <ZometoolParts bom={bom}>
        </ZometoolParts>
      );
    }
  
    // Apply external override styles to the shadow dom
    // const linkElem = document.createElement("link");
    // linkElem .setAttribute("rel", "stylesheet");
    // linkElem .setAttribute("href", "./zometool-styles.css");
    // container .appendChild( linkElem );
  
    render( bindComponent, container );
  }

export class ZometoolPartsElement extends HTMLElement
{
  #instructionsId;
  #instructions;

  constructor()
  {
    super();

    debug && console.log( 'ZometoolPartsElement constructed' );
  }

  connectedCallback()
  {
    if ( !! this.#instructionsId ) {
      this.#instructions = document .querySelector( `#${this.#instructionsId}` );
      if ( ! this.#instructions ) {
        console.error( `No zometool-instructions with id "${this.#instructionsId}" found.` );
      } else if ( this.#instructions .getParts === undefined ) {
        console.error( `Element with id "${this.#instructionsId}" is not a zometool-instructions.` );
        return;
      }
    }
    if ( ! this.#instructions ) {
      this.#instructions = document .querySelector( 'zometool-instructions' );
    }
    if ( ! this.#instructions ) {
      console.error( `No zometool-instructions found.` );
      return;
    }

    this.#instructions .addEventListener( "zometool-instructions-loaded", (e) => {
      debug && console.log( JSON.stringify( e.detail ) );

      this .appendChild( document.createElement("style") ).textContent = instructionsCSS;
      const container = document.createElement("div");
      container .classList .add( 'zometool-parts-container' );
      this .appendChild( container );
  
      renderComponent( container, e.detail )
    } );

    debug && console.log( 'ZometoolPartsElement connected' );
  }

  static get observedAttributes()
  {
    return [ "instructions" ];
  }

  attributeChangedCallback( attributeName, _oldValue, _newValue )
  {
    debug && console.log( 'ZometoolPartsElement attribute changed' );
    switch (attributeName) {

    case "instructions":
      this.#instructionsId = _newValue;
      break;
    }
  }
}

