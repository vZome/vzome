# react-vzome

## Dev Workflow

Install VSCode, and open the root vZome folder.  This will be important to allow source debugging in the `react-library` and `online` folders simultaneously.  Also, you may need to install the Chrome debugging extension for VSCode, though it may now be built-in. 

First, install Node.js version 10.  Some steps may not work with more recent versions.

Next, `./build-library.bash` from the main vZome working directory.  You'll need to do this whenever the Java source code has changed, since it does the JSweet transpile.  You'll see 71 errors during the transpilation, but that is expected.

Now, open a new terminal window in the `online` subfolder, and do `npm i`, followed by `npm run start`.  This will open a new tab in your default browser, which you can close immediately.

Finally, copy `vscode-launch-template.json` as `.vscode/launch.json`.  This gives you two launch profiles, `online` and `react-vzome`.  Using the VSCode debugging view, launch the `online` profile.  This will start a dedicated Chrome window running vZome Online, with the ability to set breakpoints in the `online` and `react-library` source code.


## History

After failing with nwb and create-react-library, I found this [recent blog post][mehrahinem], and I have been following it.

[mehrahinem]: https://medium.com/@mehrahinam/build-a-private-react-component-library-cra-rollup-material-ui-github-package-registry-1e14da93e790

However, that approach does not let me debug effectively.  I explored Vite, and then settled on Snowpack, but *only for dev*.  I still use CRA to do the build,
since Snowpack does not really do what I want with dependencies.

