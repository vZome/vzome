// babel workaround
import "regenerator-runtime/runtime";

import React from 'react';
import { render } from 'react-dom'

import { VZomeAppBar } from '../components/appbar.jsx'
import { WorkerContext } from '../../ui/viewer/index.jsx'
import { DesignBrowser } from './browser.jsx';

const Browser = () => (
  <WorkerContext>
    <VZomeAppBar oneDesign={true} />
    <DesignBrowser />
  </WorkerContext>
)

render( <Browser />, document.getElementById( 'root' ) )
