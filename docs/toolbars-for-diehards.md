---
title: vZome Toolbars for Diehards
description:
  You can use modern vZome but still have the tools from older versions
published: true
---

<p>TLDR: You can use vZome 6.0 but still have the tools from older versions.</p>

<p><img src="https://www.vzome.com/blog/wp-content/uploads/2018/12/original-tools.png" alt="" class="alignnone size-full wp-image-168" /></p>

<p>Software applications can be complicated, and vZome is certainly no exception. Once we invest the time to learn an application, we don't like major changes. It can be frustrating when a trusted application suddenly has a new look, or features move or disappear. The transition from vZome 5.0 to vZome 6.0 was probably like that for some of you.</p>

<p>Before vZome 6.0, the toolbar had a fixed set of tools. All of the symmetry tools had implicit global parameters, the symmetry center and symmetry axis. If you wanted to apply icosahedral symmetry with two different centers, you had to keep switching the symmetry center before using the icosahedral symmetry command or button.</p>

<p>vZome 6.0 introduced new toolbars, with much more flexibility. You can create new tools, with fixed parameters that you choose. You can see the parameters after the fact, and configure the tools for different selection behaviors. There are still predefined tools, for the simple cases like symmetry around the initial ball. There are selection bookmarks, for saving and reusing selection state.</p>

<p>The fact is that vZome 6.0 can still expose the older toolbar and menu commands, with a single setting in your user preferences. <a href="https://www.vzome.com/blog/2018/02/vzome-content-workflows/">This blog post</a> explains how to find and edit the preferences file. Simply add the following line to the file:</p>

<pre><code> original.tools=true
</code></pre>

<p>After restarting vZome 6.0, you'll have the ugly old toolbar as well as the new ones, and the "Tools" menu will contain the original symmetry commands. Also, the pop-up menu will include options for setting the symmetry center and axis.</p>

<p>I encourage you to use this as an opportunity to begin learning the new tools. You'll find they are just as easy to use as the old ones, and in fact far more powerful and flexible. For a little help, consider the screen shot below, which shows the correspondences between the old and new tools.</p>

<p><a href="https://www.vzome.com/blog/tool-mapping/"><img src="https://www.vzome.com/blog/wp-content/uploads/2018/12/tool-mapping.jpg" alt="" class="alignnone size-full wp-image-178" /></a></p>

<p>Remember that the correspondence cannot be exact for the parameterized tools (red and yellow arrows), since the old tools had transient parameters, and the new tools have fixed parameters. The yellow arrows indicate predefined tools that are parameterized by the origin Zome ball; these are likely to be very useful. The red arrows indicate predefined tools that are parameterized by particular struts; these are less likely to be useful in general, and really are meant as examples. You will typically need to create your own tools from the factory bar to achieve the functionality of the old tool button.</p>
