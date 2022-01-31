# vZome Online

For all the steps below, I find it convenient to keep two terminal windows open.
I'll call these the *main shell* and the *online shell*.
In the main shell, the working directory should be the root folder for the
Git repository; this is the parent of the `online` folder.
In the online shell, it should be the `online` subfolder.

## Dev Workflow

Install VS Code, and open the root vZome folder.

First, install Node.js version 16.  Some steps may not work with more recent versions.

Next, run `cicd/jsweet-legacy-code.bash` from the main shell.  You'll need to do this whenever the Java source code has changed, since it does the JSweet transpile.  You'll see 106 errors during the transpilation, but that is expected.

Now, in the online shell do `yarn install`, followed by `yarn run dev`.
This is a dev server, which means it will detect file changes and update the live server.
You can leave this running for hours or days as you do your development.

Finally, copy `vscode-launch-template.json` as `.vscode/launch.json` (relative to the main folder).
This gives you two launch profiles, `online` and `react-vzome`.  Using the VS Code debugging view, launch the `online` profile.  This will start a dedicated Chrome window running vZome Online, with the ability to set breakpoints in the `online` and `react-library` source code in VS Code.  Note that this only starts the client-side Chrome.  You must have the server started also, per the prior paragraph.

## Testing the Web Component

Periodically, it is a good idea to test the web component on its own.
In the online shell, run `yarn run wcdev`.  (You may find it necessary to do `yarn install` first.)
This runs a dev server that serves the `online/test` files rather than the usual vZome Online app.

With the dev server running, you can use the `react-vzome` launch profile in VS Code
to debug the client-side code.  This will launch a separate Chrome instance showing `online/test/index.html`.

## Jenkins

As with the desktop vZome builds, all official builds today for vZome Online are performed on
my local Jenkins server, which just performs `cicd/build-online.bash`.

Publishing the build to `https://vzome.com/app` is another Jenkins job.
That job runs:

```
#!/bin/bash
set -x
buildNum=$( echo ${VZOME_ONLINE_BUILD_URL} | tr '/' ' ' | awk '{print $5}' )
cd $JENKINS_HOME/jobs/vzome-online-build/builds/${buildNum}/archive/online/dist

sftp -b - scottvorthmann@sandy.dreamhost.com << END
cd vzome.com/
put online.tgz
END

ssh scottvorthmann@sandy.dreamhost.com './install-vzome-online.bash'
```
On the Dreamhost server, the `install-vzome-online.bash` script contains:

```
#!/bin/bash

cd vzome.com || exit $?

rm -f online.tar

gunzip online.tgz || exit $?

rm -rf app/* || exit $?
rm -rf modules/*.js || exit $?    # leave modules, it has old versions inside!

tar xvf online.tar || exit $?

revision=`cat modules/revision.txt`
mkdir modules/r$revision
cp modules/*.js modules/r$revision

chmod a+x app/embed.py
```

## History

After failing with `nwb` and `create-react-library`, I found this [recent blog post][mehrahinem], and I had been following it.

[mehrahinem]: https://medium.com/@mehrahinam/build-a-private-react-component-library-cra-rollup-material-ui-github-package-registry-1e14da93e790

However, that approach does not let me debug effectively.
I explored Vite, and then settled on Snowpack, but *only for dev*.  I still used CRA to do the build,
since Snowpack did not really do what I want with dependencies.

Now, however, I have switched completely to `esbuild`, after some help from Lucas Garron.
I'm building everything as ES6 modules, and `esbuild` does a great job with bundling and code splitting,
as well as being just plain fast.

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
