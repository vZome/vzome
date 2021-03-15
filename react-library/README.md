# react-vzome


## History

After failing with nwb and create-react-library, I found this [recent blog post][mehrahinem], and I have been following it.

[mehrahinem]: https://medium.com/@mehrahinam/build-a-private-react-component-library-cra-rollup-material-ui-github-package-registry-1e14da93e790

However, that approach does not let me debug effectively.  I explored Vite, and then settled on Snowpack, but *only for dev*.  I still use CRA to do the build,
since Snowpack does not really do what I want with dependencies.

