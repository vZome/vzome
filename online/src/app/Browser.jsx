
import React from 'react'

import { VZomeAppBar } from './components/appbar.jsx'
import { WorkerContext } from '../ui/viewer/index.jsx'
import { DesignBrowser } from './components/browser.jsx';

export const Browser = () => (
  <WorkerContext>
    <VZomeAppBar oneDesign={true} />
    <DesignBrowser />
  </WorkerContext>
)