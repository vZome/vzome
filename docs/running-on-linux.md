---
title: Running vZome on Linux
description:
  Instructions for how to install and run vZome on a Linux system
published: true
---

Since vZome 6.0 build 22, we have been shipping a [Linux version of vZome](https://www.vzome.com/download/7.0/latest/linux/vZome-Linux-7.0.zip).

This is a simpler distribution than either the Mac or Windows variant, since it does not include its own Java Runtime Environment.  It is simply a ZIP file.  Unzip it and you will find <strong>bin</strong> and <strong>lib</strong> folders.  In the <strong>bin</strong> folder, you will find the launch script, named "vZome-7.0".

If you have not installed Java on your Linux machine, you will see something like this when you run the script:

<a href="https://www.vzome.com/blog/2018/02/running-vzome-on-linux/linux-no-java/" rel="attachment wp-att-116"><img src="https://www.vzome.com/blog/wp-content/uploads/2018/02/linux-no-java.png" alt="" class="alignnone size-full wp-image-116" /></a>

You can [download the JRE from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html).
You'll end up with a file like "jre-8u162-linux-x64.tar.gz".  Assuming the file was saved to your Downloads folder, you can unpack it as follows:

```
cd ~/Downloads
tar xzvf jre-8u162-linux-x64.tar.gz
```

This will leave you with a folder named ~/Downloads/jre1.8.0_162.  To enable the vZome launch script, all you have to do is set the JAVA_HOME environment variable appropriately, before running the script:

```
export JAVA_HOME=~/Downloads/jre1.8.0_162
```

We won't be providing many guarantees about this distribution, since we don't have the time to do a lot of testing on Linux.  I have a virtual machine running Ubuntu 16.04 LTS, and I did some basic sanity testing of vZome 6.0 build 22.  Most features seem to work fine, but there is a nasty bug associated with article mode.  Since the first thing you see will be the "quick-start" article, this bug may cause a crash right away.  Launch vZome again, and you'll end up with a new document; if you stay out of article mode thereafter, you should be OK.
