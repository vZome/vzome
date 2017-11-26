import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import registerServiceWorker from './registerServiceWorker';

ReactDOM.render(<App width={600} height={450} />, document.getElementById('root'));
registerServiceWorker();
