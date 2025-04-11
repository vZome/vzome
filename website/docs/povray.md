---
title: Using POV-Ray for Ray-Tracing on Mac OS X
description:
  Ray-traced vZome models are easy again, for Mac users!
image: https://user-images.githubusercontent.com/1584024/181154789-5d9a237d-0465-4c14-884e-9166108c7420.png
published: true
---

<img src="https://www.vzome.com/blog/wp-content/uploads/2018/02/povray-300x300.png" alt="" class="alignnone size-medium wp-image-84" />

Ray-traced vZome models are easy again, for Mac users!

vZome has had POV-Ray export support for a very long time. However, at least on the Mac, POV-Ray has been getting harder to run. There is no longer a Mac desktop application, and even the command-line Mac builds have disappeared. But at its heart, POV-Ray remains a command-line tool, designed for Unix, and that means it is easy to run in a Docker container.

<img src="https://www.vzome.com/blog/wp-content/uploads/2018/02/docker.png" alt="" class="alignnone size-full wp-image-88" />

 

Docker is a kind of virtual machine technology, and it is well supported on the Mac. If you're willing to install <a href="https://docs.docker.com/toolbox/" rel="noopener" target="_blank">Docker Toolbox</a>, you'll gain the ability to run lots of command-line tools and servers. In particular, you can run POV-Ray!

Docker Toolbox is not the latest version of Docker for the Mac, but it runs on a wider selection of machines and Mac OS X versions.  Docker Toolbox installs a "Docker Quickstart Terminal" application, and I have created a <a href="https://www.vzome.com/util/POVRay.app.zip">customized version of that app</a> that makes it easy to run POV-Ray. Simply drop a vZome-generated <model>.ini file onto the POVRay app, and it will launch Terminal and run the POV-Ray "container" in Docker. (In fact, the .ini file need not be vZome-generated, as long it has enough information to supply necessary arguments to POV-Ray.)

The first time you drop a file on it, my app will create a VirtualBox virtual Linux machine for running Docker, and it will install the POV-Ray software on it. That won't happen again unless you lose or destroy the virtual machine.

Since Docker Toolbox is a bit outdated, it will automatically install an older version of VirtualBox.  I found I had problems with that version, so I had to uninstall it, and install the <a href="https://www.virtualbox.org/wiki/Downloads" rel="noopener" target="_blank">latest VirtualBox</a>.  I imagine if you do this first, then Docker Toolbox will use the one you have installed.

My droplet app works by using AppleScript to handle the dropped file, and passes the file path to a Bash script that launches the POV-Ray container.  It should be possible to adapt this to the latest <a href="https://store.docker.com/editions/community/docker-ce-desktop-mac" rel="noopener" target="_blank">Docker for Mac</a>, but I don't have that solution in hand at the moment.  I'll update this post if I manage to get that working.﻿

<strong>Update</strong>

I have tried my POV-Ray wrapper app on a very recent version of Docker for Mac, and it works perfectly, although VirtualBox was also installed on this Mac.  I expect it will fail if VirtualBox is not present.  I still intend to provide a version of the app that will not require VirtualBox or docker-machine.
