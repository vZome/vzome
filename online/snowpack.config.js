// Snowpack Configuration File
// See all supported options: https://www.snowpack.dev/reference/configuration

/** @type {import("snowpack").SnowpackUserConfig } */
module.exports = {
  mount: {
    src: '/app',   // This must come before public, so the *dev* copy of index.html is found here
    public: '/app',
    '../react-library/src': '/@vzome/react-vzome',
  },
  plugins: [
    // ["@snowpack/plugin-webpack"]
  ],
  // optimize: {
  //   entrypoints: [ 'app/index.html' ],
  //   bundle: true,
  //   minify: false,
  //   target: 'es2017',
  // },
  packageOptions: {
    /* ... */
  },
  devOptions: {
    /* ... */
  },
  buildOptions: {
    /* ... */
  },
  alias: {
    '@vzome/react-vzome': '../react-library/src'
  },
};
