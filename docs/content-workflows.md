---
title: vZome Content Workflows
description:
  Have you ever wished that you could automatically capture an image with every vZome file you save?
published: true
---
Have you ever wished that you could automatically capture an image with every vZome file you save?

This is a simple example of a <em>content workflow</em>. More complex examples might include running a mesh conversion when you export a 3D model,
or uploading an exported model to a sharing service in the cloud. 
vZome supports such workflows in several ways, providing some simple hooks that you can use to trigger arbitrary automation.

## The Preferences File

To take advantage of these hooks, you must first locate your vZome preferences file.
The file is named <code>.vZome.prefs</code>, and is found in your home directory.
It is automatically created the first time you run vZome. When you open it in your text editor, you'll see something like this:

```
#
#Fri Jan 01 23:53:46 PST 2016

saw.welcome=true
```

Keep the editor open on this file; we're going to add some settings. 
Note that changes to this file only take effect when vZome is launched, 
so you'll need to quit and relaunch when you make changes.

## Adding Exports on Save

The simple example I gave above, capturing an image to go with any vZome file, is very easy to achieve. Add this line to your preference file:

```
save.exports=capture.png
```

<p>Now, whenever you save a vZome file like "myModel.vZome", vZome will automatically capture the current view as "myModel.png", in the same directory. You can use the following types of image capture:</p>

<ul>
    <li>capture.jpg</li>
    <li>capture.png</li>
    <li>capture.gif</li>
    <li>capture.bmp</li>
</ul>

<p>In addition to image captures, you can do various 3D export formats, some of which are not otherwise available:</p>

<ul>
    <li>export.mesh</li>
    <li>export.cmesh</li>
    <li>export.shapes</li>
    <li>export.trishapes</li>
    <li>export.ggb</li>
    <li>export.math</li>
    <li>export.pov</li>
    <li>export.dae</li>
    <li>export.step</li>
    <li>export.vrml</li>
    <li>export.off</li>
    <li>export.vef</li>
    <li>export.partgeom</li>
    <li>export.scad</li>
    <li>export.openscad</li>
    <li>export.build123d</li>
    <li>export.partslist</li>
    <li>export.stl</li>
    <li>export.dxf</li>
    <li>export.pdb</li>
    <li>export.seg</li>
    <li>export.ply</li>
    <li>export.history</li>
</ul>

<p>Finally, you can also capture 2D vector drawings:</p>

<ul>
    <li>export2d.pdf</li>
    <li>export2d.ps</li>
    <li>export2d.svg</li>
</ul>

<p>You can even combine several captures and exports, separating them with spaces:</p>

<pre><code>save.exports=capture.png export.dae export2d.pdf
</code></pre>

<p>Don't go crazy, though... vZome executes all of these, one by one, without any good visual feedback in the user interface, and this can be confusing.</p>

<h2>Performing Automatic Actions on Save</h2>

<p>Suppose you wanted to keep a safe copy of every vZome save, backed up on an external drive, and organized by date. If you can write a script to do the backup and organization, all you need is to have that script automatically triggered by a vZome save.</p>

<p>To demonstrate this, let's create a simple script. Save this text into a file named "custom-vZome-script.sh" in your home directory:</p>

<pre><code>#!/bin/bash

{
    echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
    echo "current working directory is $PWD"
    echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
    echo "arguments received: $*"
    echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'

} &gt;&gt; ~/vZome-scripts.log
</code></pre>

<p>There are a couple of things to notice about this sample script, to help you understand how to write your own. First, when the script is executed, the working directory will be the directory where you have saved your vZome file. Second, the vZome file path will be passed as the only argument to the script. Finally, notice that the script writes all of its output to a "vZome-scripts.log" file, in your home directory. This is not a requirement, but it is a good idea, since any output would otherwise be lost!</p>

<p>If you're not comfortable with Bash, you can write your script in any language that your OS can launch, such as Python, Perl, Ruby, and so on. On Mac and Linux, you need to learn about the <a href="https://en.wikipedia.org/wiki/Shebang_(Unix)">"shebang" comment</a> at the start of the file. I assume there is some similar mechanism for Windows, but I don't know.</p>

<p>Now, once you've saved the script, add a line in your vZome preferences file like this (adjusting "/Users/scott" to your own home directory):</p>

<pre><code>save.script=/Users/scott/custom-vZome-script.sh
</code></pre>

<p>Now, restart vZome so that it picks up this new setting, then save any model file. Open up the "vZome-scripts.log" file, and you should see something like this:</p>

<pre><code>%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
current working directory is /Users/vorth/2018/02-Feb/19-Scott-testScripts
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
arguments received: /Users/vorth/2018/02-Feb/19-Scott-testScripts/tetrahedral-notwist-yellow.vZome
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
</code></pre>

<h2>Performing Automatic Actions on Export</h2>

<p>Similar to the previous workflow, you might wish to copy any captured image into your photo sharing system, like Google Photos. The mechanism for triggering scripts for this case is very similar to the prior example.</p>

<p>For simplicity, we are going to use the same custom-vZome-script.sh you've already created. Since our sample script does nothing but echo its arguments and the working directory, we can reuse it.
Normally, you should never do this; always use different scripts for the two different purposes! For now, add the following line to your preferences file (adjusting "/Users/scott" to your own home directory):</p>

<pre><code>export.script=/Users/scott/custom-vZome-script.sh
</code></pre>

<p>Again, restart vZome to pick up this change, then try exporting any format. In this case, the script will be triggered by <em>any</em> export or image capture, including 2D exports (PDF, Postscript, SVG). You should see something like this appear in the "vZome-scripts.log" file:</p>

<pre><code>%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
current working directory is /Users/vorth/2018/02-Feb/19-Scott-testScripts
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
arguments received: /Users/vorth/2018/02-Feb/19-Scott-testScripts/tetrahedral-notwist-yellow.vZome.png
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
</code></pre>

<p>The usual practice will be to extract the file extension / suffix (like "png") inside the script, and perform the automated actions conditionally, since you probably don't want to send PDF or DAE files to Google Photos, for example! Remember this script will be called for <em>all</em> exports; it is up to you to make it specific.</p>

<h2>Interactions</h2>

<p>You might have noticed that you could combine the "save.exports", "save.script", and "export.script" features. For example, you could capture a PNG whenever you save a model, then run a script on the model file, and another script on the PNG file. This should work, but don't expect to predict the order in which the steps happen; all you can be sure of is that the export will be finished before the export.script runs, and the save will be finished before the save.script runs.</p>

<p>Also, all the time taken by these actions will interrupt your work in vZome, so try to limit the automated actions. The next section will give you a more controlled mechanism for triggering scripts.</p>

<h2>The Custom Menu</h2>

<p>There is another hidden feature in vZome that is very handy, the "Custom" menu. My collaborator, David Hall, added this so he could access features he has added on branches, not yet merged into the main source. It is also very handy for adding keyboard shortcuts to existing commands. Here, we will demonstrate how to use the Custom menu to trigger scripts explicitly, separate from any export or save event.</p>

<p>To enable the custom menu, save the following content as a file named <code>vZomeCustomMenu.properties</code>, under your vZome preferences folder. On a Mac, this folder is named <code>/Users/{username}/Library/Preferences/vZome</code>. On Windows, it would be something like <code>C:\Users\{username}\vZome-Preferences</code>.</p>

<pre><code>export.dae=Export Collada DAE ^7
execCommandLine//Users/scott/custom-vZome-script.sh\ 2\ {}=Script Action ^8
</code></pre>

<p>The content looks intimidating and complicated, but it is simple enough once you know how it is parsed. vZome loads the file using <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Properties.html#load(java.io.Reader)">java.util.Properties.load() syntax</a>. Let's look at the first line. Everything up to the first "=" is considered the property name, which vZome will treat as the command string, telling it exactly what to do. After the "=" is the property value, which vZome will use as the menu item label, what you'll see in the Custom menu. The "^7" is optional, and is interpreted as a keyboard equivalent, command-7 on the Mac and control-7 on Windows. Thus, this first line is present just to give us a keyboard shortcut for an existing export command, already available in the File menu but without a shortcut. Note that you can use any of the "export.<em>" or "capture.</em>" actions described above in this way, if you like.</p>

<p>The second line is the one we're more interested in, and it has a little more structure. Of course, it still has the "name=value" structure, but let's take a look at the "name" (the command string) in more detail:</p>

<pre><code>execCommandLine//Users/scott/custom-vZome-script.sh\ 2\ {}
</code></pre>

<p>We have to break this down a bit further. First, notice that the two space characters are preceded by "\" characters. This is required because the space character is actually an alternative to the "=" character for separating the name and value in the property syntax, so we have to "escape" the spaces so they won't give us the wrong name and value. The same is true of ":" characters; if you want them in the command string, you must escape them as ":".</p>

<p>Now, if we remove the escaping, we'll see what vZome gets as its command string:</p>

<pre><code>execCommandLine//Users/scott/custom-vZome-script.sh 2 {}
</code></pre>

<p>Now we can talk about the "execCommandLine" command, and how it is parsed. This is a command that means nothing by itself; it must be always followed by a "/" and additional text. Furthermore, if the additional text contains "{}", those two characters will be replaced by the file name of your current vZome model window. Note that this command will not run if you have an "untitled" model that has never been saved; you'll see an error to that effect.</p>

<p>When all of this parsing is finished, we have the final string that will be sent to the OS to be executed:</p>

<pre><code>/Users/scott/custom-vZome-script.sh 2 tetrahedral-notwist-yellow.vZome
</code></pre>

<p>The file name here is just an example, and will be different in your case. Note that we must provide a complete path to the script, as we did in the preference file.</p>

<p>Restart vZome so it picks up the vZomeCustomMenu.properties file, and you should see the new menu appear in the menu bar. Open a vZome file, then try both of the commands, with the mouse and with the keyboard equivalents. Check the "vZome-scripts.log" file, and you'll see something like this:</p>

<pre><code>%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
current working directory is /Users/vorth/2018/02-Feb/19-Scott-testScripts
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
arguments received: 2 tetrahedral-notwist-yellow.vZome
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
</code></pre>

<p>Notice that there is a departure here from the earlier scripts. First, both of the arguments that appeared in the command string have been passed. Second, the file name argument is <em>just</em> a file name, not a full path as in earlier examples.</p>

<p>Obviously, our silly little sample script does not do anything useful. Again, although we used the same script for all three purposes described in this post, you should create specific scripts for different cases. Importantly, bear in mind that you can use a different script for every "execCommandLine" entry in your custom menu.</p>

## Command-Line Support

vZome supports command-line invocation and automation, though not in a true "headless" fashion
where no windows would open.  Nonetheless, this feature allows you to batch-process hundreds of
vZome designs, doing exports or image captures for all.

#### Specifying a Design File

First, you can simply specify a vZome design file on the command line, and vZome will launch
*interactively* with that design opened.  On a Mac, it will be something like this:
```bash
/Applications/vZome-7.1.52.app/Contents/MacOS/vZome my-design.vZome
```
On Windows, it will be something like this:
```bash
"C:\Program Files\vZome\vZome" my-design.vZome
```
(Note the quotes necessary because of the space in "Program Files".)
The full path to the executable may vary depending on how you organize applications on your computer.
The file argument can be any relative or absolute path, interpreted relative to
the current working directory where you execute the command.

#### Automation

When you want fully automated processing using vZome, you must always specify a design file
as above.  In addition, you must use a *macro*, as follows.  (Mac command line shown.)
```bash
/Applications/vZome-7.1.52.app/Contents/MacOS/vZome my-design.vZome -macro capture.png,export.shapes
```
The value for the macro must be a comma-delimited list of commands;
see the earlier section for the [possible export and capture commands](#adding-exports-on-save).
In the example here, we are capturing a PNG image then exporting the `.shapes.json` format used
for GitHub sharing.

Any exported files will be stored next to the original vZome design file, with the same
file name but a different file extension.

After the commands are executed, vZome will quit itself automatically.

#### Image Size and Shape Control

When capturing images, you can control the size and aspect ratio of the images.
(The application path is elided here.)
```bash
/.../vZome my-design.vZome -macro capture.png -max.image.size 1200 -aspect.ratio 1.19
```
Aspect ratio is expressed as *width/height*, and it governs the shape of the entire design window,
NOT the 3D canvas rendering the vZome design itself.
A value near 1.19 currently works well for producing approximately square images on a Mac.
If you don't control the aspect ratio, your screen dimensions will govern the shape of exported images,
since windows fill the screen by default.

The value of `max.image.size` will adjust the width or the height of the image, whichever
is larger based on your selected (or the default) aspect ratio.

Both of these properties can be set in your preference file,
to control the behavior of normal, interactive vZome.  The aspect ratio property will
change your vZome windows from the default, full-screen shape.
The image size control will apply to any image captures you perform.

If either property is set in your preferences, you don't need to provide the command-line
argument for automated vZome.
You can also set the properties in both places;
the command-line values will always override the values from the preference file.

## Future Improvements

<p>There are some shortcomings in the features I've described above. Ideally, vZome would display a console window with the text output of these scripts. At the very least, the output should not be lost, even if you don't capture it as I have in these samples. There should also be some indication that vZome is busy while executing these scripts, possibly even preventing user interaction while they run.</p>

<p>
Obviously, we'd also like to see a truly headless scripting capability,
opening and processing multiple designs in turn.
</p>

<p>If you have other enhancements you'd like to see, please leave a comment.</p>

<p>Happy workflows!</p>
