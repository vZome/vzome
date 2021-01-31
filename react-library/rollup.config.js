import babel from '@rollup/plugin-babel';
import external from 'rollup-plugin-peer-deps-external';
import del from 'rollup-plugin-delete';
import pkg from './package.json';
import url from '@rollup/plugin-url';

export default {
    input: pkg.source,
    output: [
        { file: pkg.browser, format: 'umd', sourcemap: true, name: 'react-vzome' },
        { file: pkg.module, format: 'esm', sourcemap: true }
    ],
    plugins: [
        external(),
        babel({
            exclude: 'node_modules/**'
        }),
        del({ targets: ['dist/*'] }),
        url({
            include: ['**/*.vef'],
            limit: 46000
        }),
    ],
    external: Object.keys(pkg.peerDependencies || {}),
};
