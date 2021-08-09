# react-vzome

For all the steps below, I find it convenient to keep three terminal windows open.
I'll call these the *main shell*, the *online shell*, and the *library shell*.
In the main shell, the working directory should be the root folder for the
Git repository; this is the parent of the `react-library` and `online` folders.  In the library shell,
the working directory should be the `react-library` subfolder.  In the online shell, it should be the `online` subfolder.

## Dev Workflow

Install VS Code, and open the root vZome folder.  This will be important to allow source debugging in the `react-library` and `online` folders simultaneously.  Also, you may need to install the Chrome debugging extension for VS Code, though it may now be built-in. 

First, install Node.js version 14.  Some steps may not work with more recent versions.

Next, run `./build-library.bash` from the main shell.  You'll need to do this whenever the Java source code has changed, since it does the JSweet transpile.  You'll see 71 errors during the transpilation, but that is expected.

Now, in the online shell do `npm i`, followed by `npm run start`.  This will open a new tab in your default browser, which you can close immediately, since the next step below will launch a debuggable Chrome process.
This is a dev server, which means it will detect file changes and update the live server.
You can leave this running for hours or days as you do your development.

Finally, copy `vscode-launch-template.json` as `.vscode/launch.json` (relative to the main folder).
This gives you two launch profiles, `online` and `react-vzome`.  Using the VS Code debugging view, launch the `online` profile.  This will start a dedicated Chrome window running vZome Online, with the ability to set breakpoints in the `online` and `react-library` source code in VS Code.  Note that this only starts the client-side Chrome.  You must have the server started also, per the prior paragraph.

## Testing the React Component

Periodically, it is a good idea to test the React component on its own.
In the library shell, run `npm run dev`.  (You may find it necessary to do `npm i` first.)
That will launch the `react-library/public/index.html` page in your default browser.

With the library dev server running, you can use the `react-vzome` launch profile in VS Code
to debug the client-side code.  This will launch a separate Chrome instance.

## Shipping a Build

Before you push your last commit to GitHub, you need to bump the version number on the React component,
then publish the component to NPM.  In the library shell, do:
```
npm version patch
npm clean-install
npm run build
npm publish --access public
```
(Note that this is an "official" build of the React component, performed manually. You must be logged into NPM, I believe.)

Next, update the dependency in `online/package.json`, to match the version you just published:
```
  "@vzome/react-vzome": "^0.9.20",
```
Finally, run `./build-online.bash` in the main shell.  This should update `online/package-lock.json`.

Now you are ready to commit and push.

## Jenkins

As with the desktop vZome builds, all official builds today for vZome Online are performed on
my local Jenkins server, which just performs `./build-online.bash` as above.

Publishing the build to `https://vzome.com/app` is another Jenkins job.
That job runs:
```
#!/bin/bash
set -x
buildNum=$( echo ${VZOME_ONLINE_BUILD_URL} | tr '/' ' ' | awk '{print $5}' )
cd $JENKINS_HOME/jobs/vzome-online-build/builds/${buildNum}/archive/online

sftp -b - ${DREAMHOST_USER}@${DREAMHOST_SERVER} << END
cd vzome.com/
put app.tgz
END

ssh ${DREAMHOST_USER}@${DREAMHOST_SERVER} './install-vzome-online.bash'
```
On the Dreamhost server, the `install-vzome-online.bash` script contains:
```
#!/bin/bash

cd vzome.com || exit $?

rm -f app.tar

gunzip app.tgz || exit $?

tar xvf app.tar || exit $?
```

## History

After failing with `nwb` and `create-react-library`, I found this [recent blog post][mehrahinem], and I have been following it.

[mehrahinem]: https://medium.com/@mehrahinam/build-a-private-react-component-library-cra-rollup-material-ui-github-package-registry-1e14da93e790

However, that approach does not let me debug effectively.
I explored Vite, and then settled on Snowpack, but *only for dev*.  I still use CRA to do the build,
since Snowpack does not really do what I want with dependencies.

## Notes

Any given command may do significant computation, such as computing a convex hull or a 4D polytope.
That cost is compounded by the new fields of high order, and the arbitrary precision arithmetic.
The bottom line is that each command really should be performed in a Web Worker;
there is no other way to guarantee that rendering is not impacted,
except to break up the processing into small chunks using setTimeout(0),
and that would be invasive in the legacy Java code.

Web Workers do not share any memory with the main Javascript thread.
Any necessary data flows as serialized copies in the messages passed back and forth.
This means that we must take one of two approaches: either the Web Worker is essentially stateless,
or it maintains the mesh state for every design.  For the stateless approach, the entire mesh would
have to flow in and out in the messages.  The stateful approach would be more similar to the client-server
approach I used for the Unity implementation, where only rendering state is stored with the main thread,
and "render events" would flow back from the Web Worker.

A third approach would be to use offline canvas, essentially moving all the processing *and* rendering
to the worker.  This sounds pretty attractive, but presents two obstacles at the moment.
First, offline canvas is only supported in Chromium browsers today.  Second, and more critically,
it appears that `react-three-fiber` does not support rendering to offscreen canvases.
