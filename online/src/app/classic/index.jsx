// babel workaround
import "regenerator-runtime/runtime";

import React from 'react';
import { render } from 'react-dom'

import { VZomeAppBar } from '../components/appbar.jsx'
import { WorkerContext } from '../../ui/viewer/index.jsx'
import { ClassicEditor } from './classic.jsx';

const Classic = () => (
  <WorkerContext>
    <VZomeAppBar oneDesign={true} />
    <ClassicEditor />
  </WorkerContext>
)

render( <Classic />, document.getElementById( 'root' ) )
