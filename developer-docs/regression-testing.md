
# Regression Testing Ideas

## Testing Core Java

Right now, regression testing of the core code is severely limited,
since I cannot run Docker (and therefore Jenkins) on my NAS,
and the test files are not readily available on my desktop machine
thanks to DropBox's optimization to leave them in the cloud.

Option 1 is to set the vZome attachments folder to be always local
in the DropBox settings, and run regression manually from the command
line on my desktop.

Option 2 is to restore Docker on my NAS, where the test files are
always resident (no DropBox optimization supported there).

Option 3 is to set up a GitHub Action that pulls files using the
DropBox API.  This has the advantage of being fully automatable,
not to mention more parallelism.  (There will be limits to the
compute that Actions will allow, nonetheless.)

All options use the existing regression testing task configured in Gradle.

## Testing Core Javascript

Lack of automated regression testing on the JSweet transpiled code
is a huge exposure.

### Manual Regression

I have partially restored the regression testing by moving it out of
comments in `test/index.html` and into `test/regression.html`.
It only tests one file currently, since the web component events
are not being generated any more.

Step 1 is to restore that capability, for limited manual regression testing.

Step 2 is to parameterize it to use other GitHub repositories.

Step 3 is to modify it to support DropBox API use, and thus gain access to
all of the historical regression suite.

### Automated Regression

The manual regression script above could be adapted to remove the requirement
for a browser and run headless in Node or Deno.
For that purpose, I should adopt a Javascript testing framework that will
generate nice HTML reports, integrated with GitHub Actions if possible.
(I don't know if there is any specific support there for test reports.)

I know that the JSweet transpiled code omits certain features,
e.g. Zomic parsing.

## Testing the Web Component

Perhaps the most critical gap is regression testing of the `vzome-viewer`
web component.  For this, I need a web testing framework.
This, too, could be adapted from the `test/regression.html` page,
with more exercising of the web component's API;
I should be able to test the same data used for core regression testing.

