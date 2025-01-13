# vZome Online

Online vZome is a web application, but also a set of related web applications (and web components) using shared code.

There are three ways you can work with this code.
Option 1 requires nothing but your existing web browser.
Options 2 and 3 both require [Visual Studio Code](https://code.visualstudio.com/)
installed on your local computer;
they differ in what other prerequisites must be installed.

## Option 1: GitHub Codespaces (Quick Start)

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/vZome/vzome)

The easiest way to experiment with this code is to click the badge above.
This will give you a [codespace](https://docs.github.com/en/codespaces),
which is a Visual Studio Code development environment running in your web browser,
backed by a Docker container running on a remote virtual machine somewhere in GitHub's cloud.
The downside here is the time to set up the codespace, and the lag you will see when testing.

> Note: the badge above opens a codespace on the official vZome repo.
> This is fine for experimentation, but *if you intend to contribute
> changes back*, you should fork that repo and open your codespace on your own fork, using the green
> "Code" button in GitHub, under the "Codespaces" tab there. 

If you're familiar with VS Code, you'll notices some minor differences in the web version.
Most importantly, the usual menubar seems to be missing, but you can find it by clicking on
the triple-bar icon in the upper left of the page.

Starting the codespace can take a couple of minutes, and there are several phases.
Once your codespace is completely ready, you can proceed to the next step.

### Start the Development Server

Find the "Terminal" menu in VS Code, and click on "Run Build Task..." (shift-command-B on a Mac),
then select `Build vZome for Development`.
This task builds all the online apps and components, then starts a dev server.
Finally, it will automatically open the test page (see below) in your browser.

### Testing

The test page contains links to the various web apps, as well as different web components and their configuration options,
for manual testing.
(Unfortunately, there is no automated testing implemented at the moment.)
You can see the [test page source here](./serve/app/test/index.html).

### Debugging

With the dev server running, you can go to "Run and Debug" in the activity bar of VS Code,
and you'll see a drop-down menu at the top, showing launch configurations.
Select the `TEST` configuration, and hit the green "play" button to debug the Javascript code for any
online vZome web application or web component.
If you have Chrome installed, VS Code will ask for permission (once)
then will launch a special Chrome instance, connected to the VS Code debugging framework and
showing the test page.  Breakpoints you set in VS Code will be triggered by that browser.

## Option 2: Local Docker

If you do lots of development, you may have [Docker](https://www.docker.com/) installed already.
If you do, you can skip installing other vZome prerequisite tools by using the same "dev container" that GitHub Codespaces would use, but
running on your own machine, in your Docker host.

In this case, when you open VS Code on this project it will offer to "Reopen in Container".
Accept that, and wait for the workspace to be ready.

Once the workspace is ready, you can [start the dev server](#start-the-development-server)
and continue with the workflow as documented in Option 1 above.

With the dev server running, you can [visit the test page here](http://localhost:8532/app/test/).

## Option 3: Local Node.js

If you don't want to install and manage Docker,
you'll need to install [Node.js](https://nodejs.org/en) and [Yarn](https://yarnpkg.com/getting-started/install);
the `online.bash` script uses `yarn` explicitly, not `npm`.

Node.js is only used as a tooling framework.  None of the vZome web apps require a server side running in Node.

With this project open in VS Code, you can [start the dev server](#start-the-development-server)
and continue with the workflow as documented in Option 1 above.

With the dev server running, you can [visit the test page here](http://localhost:8532/app/test/).

## Official Builds

The `Build vZome for Production` task in VS Code builds all the online apps and components, then prepares the files for
the production server, and creates an archive under the `online/dist` folder.

All official builds for online vZome are performed using GitHub Actions.  See `.github/workflows/online.yml`.

## Clean Up

The `vZome online: Clean` task removes all temporary files and build artifacts.

## Legacy Code Workflow

> This workflow is no longer viable unless you *already* have all of the JSweet components built and installed locally.
> The JSweet Artifactory server is offline for good, and so the build is not working.
> Nonetheless, I'm keeping these instructions here for my own use.

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

See [this dedicated document](./developer-docs/architecture.html).

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
