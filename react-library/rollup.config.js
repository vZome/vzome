import babel from '@rollup/plugin-babel';
import external from 'rollup-plugin-peer-deps-external';
import del from 'rollup-plugin-delete';
import pkg from './package.json';
import url from '@rollup/plugin-url';
import json from "@rollup/plugin-json";

const config = {
    input: pkg.source,
    output: [
        { file: pkg.browser, format: 'umd', name: 'react-vzome' },
        { file: pkg.module, format: 'esm' }
    ],
    plugins: [
        external(),
        babel({
            exclude: 'node_modules/**',
            babelHelpers: "bundled"
        }),
        del({ targets: ['dist/*'] }),
        url({
            include: ['**/*.vef'],
            limit: 46000
        }),
        json(),
    ],
    external: [
        'react', 'react-dom',
        'three', 'react-three-fiber', 'drei', 'three/examples/jsm/controls/TrackballControls',
        '@material-ui/core/Fab', '@material-ui/icons/GetAppRounded'
    ]
};

export default config
