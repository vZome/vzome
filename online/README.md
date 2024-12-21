# vZome Online

Online vZome is a web application, but also a set of related web applications (and web components) using shared code.

## Quick Start

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/vZome/vzome)

The easiest way to experiment with this code is to click the badge above.
This will give you a Visual Studio Code development environment running in your web browser,
backed by a Docker container running on a remote virtual machine.

> Note: the badge above opens a codespace on the official vZome repo.  If you intend to contribute
> changes back, you should fork that repo and open your codespace on your own fork, using the green
> "Code" button in GitHub. 

Once your codespace is up and running, proceed to the [workflow section below](#vs-code-workflow).

### Local Docker

If you have [Docker](https://www.docker.com/) and [Visual Studio Code](https://code.visualstudio.com/)
installed on your local computer,
when you open VS Code on this project, it will offer to "Reopen in Container".
This lets you avoid setting up [prerequisites](#prerequisites) for local development.
This is using the same "dev container" that GitHub Codespaces would use, but
the container runs on your own machine.

Once the workspace is ready, proceed to the [workflow section below](#vs-code-workflow).

### VS Code Workflow

This repository has a [`tasks.json`](/.vscode/tasks.json) file
that registers tasks for VS code.
You can run these in several ways; the most obvious is the "Run Task..." item in the "Terminal" menu.
See [the documentation](https://code.visualstudio.com/Docs/editor/tasks) for more ways to run tasks.

#### vZome online: Start Dev Server

This task builds all the online apps and components, then starts a dev server.
VS Code will prompt you to open the [test page](#testing) in your browser.

#### vZome online: Build for Production

This task builds all the online apps and components, then prepares the files for
the production server, and creates an archive.

#### vZome online: Clean

This task removes all temporary files and build artifacts.

### Testing

Unfortunately, there is no automated testing implemented.

You can manually test the various apps, as well as different web component scenarios,
using the [test page](./serve/app/test/index.html).
This is just a set of links to launch other webapps or pages for testing different components and their configurations.

### Debugging

With the dev server running, you can use the `TEST` launch profile in VS Code
to debug the client-side code.  This will launch a separate Chrome instance
showing the test page.  Breakpoints you set in VS Code will be triggered by that browser.


## Local Machine Development

### Prerequisites

You'll need [Node.js](https://nodejs.org/en) and [Yarn](https://yarnpkg.com/getting-started/install);
the `online.bash` script uses `yarn` explicitly.

### Workflow

For local machine development, you can use the same [VS Code tasks defined above](#vs-code-workflow).


## Official Builds

All official builds for online vZome are performed using GitHub Actions.  See `.github/workflows/online.yml`.

## Legacy Code Dev Workflow

> This workflow is no longer viable unless you *already* have all of the JSweet components built and installed locally.
> The JSweet Artifactory server is offline for good, and so the build is not working.

You'll need [Maven](https://maven.apache.org/) installed for these steps.
You will also require an installation of [JDK 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html).

The vZome legacy code is part of the Java code from this repository (for desktop vZome)
that has been transpiled into Javascript using
[JSweet](https://www.jsweet.org/).  This transpilation step complicates the development workflow considerably,
particularly because it requires my own branches of several GitHub repositories:

 - [vorth/jsweet](https://github.com/vorth/jsweet)
 - [vorth/jsweet-maven-plugin](https://github.com/vorth/jsweet-maven-plugin)
 - [vorth/j4ts](https://github.com/vorth/j4ts)
 - [vorth/jsweet-gradle-plugin](https://github.com/vorth/jsweet-gradle-plugin)

These branches contain defect fixes that have been submitted to the corresponding
upstream projects but not yet merged, or merged but no release has been
published, since the JSweet maintainers have largely
moved on to other projects.

For these steps, the working directory should be the root folder for the
Git repository; this is the parent of the `online` folder.

First, execute this script:
```
cicd/online.bash prepareJSweet
```
This will create a `jsweet-branches` folder as a sibling of `online`,
check out the four branches listed above, and build them in sequence.
When the script completes, you can delete the `jsweet-branches` folder
if you wish, since the tools and library will already be installed in your
local Maven cache.
Since this steps is not touching any code in this repository,
you should not need to repeat it unless you set up another machine to work on vZome Online.

Next, run:
```
cicd/online.bash java
```
You'll need to do this whenever the Java source code has changed, since it does the JSweet transpile.  You'll see a number of errors during the transpilation, but that is expected.  The script checks for the expected number of errors, and fails if there are more errors or fewer.

This command also starts a dev server, which means it will detect file changes and update the live server.
You can leave this running for hours or days as you do your development.
However, it is not set up for hot module reloading, so you'll need to refresh your browser
pages manually.

Finally, copy `vscode-launch-template.json` as `.vscode/launch.json` (relative to the main folder).
This gives you several launch profiles.  Using the VS Code debugging view, launch the `TEST` profile.  This will start a dedicated Chrome window running vZome Online, with the ability to set breakpoints in the `online` source code in VS Code.  Note that this only starts the client-side Chrome.  You must have the dev server started also, per the prior paragraph.

If you cancel the running dev server, and want to test Javascript-only changes,
you can simply run:
```
cicd/online.bash dev
```
This is only appropriate if you have not touched any Java code.
This is my most common workflow step, as I'm usually working on Javascript code, not Java code.
Like the `java` subcommand, it leaves a dev server running.

## Architecture Notes

The web components and most of the apps support viewing vZome designs in two formats.
The URL provided for a design is always for the `.vZome` legacy file format,
but if a similar URL with a `.shapes.json` suffix is found, and the app does not need to edit the design,
that JSON file format will be used.
In either case, the loading is performed by a web worker, to keep the UI responsive.
If the `.shapes.json` preview URL fails, the worker will load
the vZome legacy code dynamically, in order to parse and interpret the `.vZome` design.
This legacy code must also be loaded if the app is creating a new design
or loading an existing design for editing.

Each application has a dedicated ES module, and there are several shared modules, 
as well as
additional chunks generated by `esbuild` bundling with code-splitting.
See `scripts/esbuild-config.mjs` for the breakdown of modules.

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
I'm not sure I'd be able to use `solid-three` in the worker, though I don't know why not.


## History

After failing with `nwb` and `create-react-library`, I found this [recent blog post][mehrahinem], and I had been following it.

[mehrahinem]: https://medium.com/@mehrahinam/build-a-private-react-component-library-cra-rollup-material-ui-github-package-registry-1e14da93e790

However, that approach did not let me debug effectively.
I explored Vite, and then settled on Snowpack, but *only for dev*.  I still used CRA to do the build,
since Snowpack did not really do what I want with dependencies.

Now, however, I have switched completely to `esbuild`, after some help from Lucas Garron.
I'm building everything as ES6 modules, and `esbuild` does a great job with bundling and code splitting,
as well as being just plain fast.

I have also switched from React to SolidJS.  I found it to be very light and fast, and a better fit
for the event-based state management I need to do, where everything flows between the main context
and the web worker, getting mapped to the Controller architecture in the legacy code.
