import { defineConfig } from 'vite'
import reactRefresh from '@vitejs/plugin-react-refresh'
import pkg from './package.json';

// https://vitejs.dev/config/
export default defineConfig( {
  plugins: [reactRefresh()],

  assetsInclude: [ "**/*.vef" ],

  build: {
    assetsInlineLimit: 46000,
    lib: {
      entry: pkg.source,
      name: pkg.name
    },
    rollupOptions: {
      // make sure to externalize deps that shouldn't be bundled
      // into your library
      external: Object.keys( pkg.peerDependencies || {} ),
      output: {
        // Provide global variables to use in the UMD build
        // for externalized deps
        globals: {
          react: 'React',
          'react-dom': 'ReactDom'
        }
      }
    }
  }
} )
