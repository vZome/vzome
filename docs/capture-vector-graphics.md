---
title: Capturing Vector Graphics (e.g. PDF)
description: The ins and outs of exporting vector graphic snapshots of vZome designs
published: true
---
<p>Are you still capturing JPEG images to share your vZome creations on social media? Or worse, doing screen capture? There's a better way, for most cases: vector graphics.</p>

<p>
Actually, the best way to share is to use vZome's <a href="./sharing.html">web-based sharing</a>, since your
readers get an interactive 3D view, not just a flat image.
That said, if you're not ready to try that, or you're creating artwork to print, read on!
</p>

<p>Formats like PDF, SVG, and Postscript are vector graphics formats. This means they record drawing commands like "move here", "draw a line here", and so on. Such drawings are inherently scalable -- you can zoom in as much as you like, and you will never see pixellation as you would with an image format like JPEG or PNG. The files also tend to be more compact than images.</p>

<p><a href="https://www.vzome.com/blog/2018/12/capturing-vector-graphics/sample/"><img src="https://www.vzome.com/blog/wp-content/uploads/2018/12/sample.svg" alt="" class="alignnone size-full wp-image-155" /></a></p>

<p>The drawing above is not an image, but a rendered SVG file. If you shrink the size of this browser window, you'll see that the drawing shrinks also (as do the images below). More importantly, if you click on the drawing to view it in the WordPress asset viewer, then click again on that, you will see <a href="https://www.vzome.com/blog/wp-content/uploads/2018/12/sample.svg">it displays</a> as large as your browser window, unlike the image assets. You could print the drawing as a billboard and it would still look great.</p>

<p>vZome has long had the ability to capture PDF, Postscript, and SVG files. With vZome 6.0 build 41, we have enhanced the capability so you have three quick commands, which do their best to capture the view in the main window, and one customizable view that gives you more interactive control.</p>

<p><a href="https://www.vzome.com/blog/2018/12/capturing-vector-graphics/capture-vector-drawing/" rel="attachment wp-att-143"><img src="https://www.vzome.com/blog/wp-content/uploads/2018/12/capture-vector-drawing.png" alt="" class="alignnone size-full wp-image-143" /></a></p>

<p>The first three menu commands shown above will do an immediate export of the indicated file type, rendering the exact same scene you have in your main vZome window. The saved drawing will include the background, will use shaded panels, and will outline the panels or not depending on your current setting in the "System" menu.</p>

<p>If you want more control, select the "Customize..." menu command. This will open a window giving you some options to control the drawing settings.</p>

<p><a href="https://www.vzome.com/blog/2018/12/capturing-vector-graphics/customize-window/" rel="attachment wp-att-153"><img src="https://www.vzome.com/blog/wp-content/uploads/2018/12/customize-window.png" alt="" class="alignnone size-full wp-image-153" /></a></p>

<p>The menu selected in the screenshot above offers five different rendering modes. They are generally self-explanatory, with the exception of "colored lines" and "black lines". These two options draw Zome struts as simple line segments, and Zome balls are not drawn at all. A popular option is to draw colored lines with the background disabled, as shown below.</p>

<p><a href="https://www.vzome.com/blog/2018/12/capturing-vector-graphics/colored-lines/" rel="attachment wp-att-152"><img src="https://www.vzome.com/blog/wp-content/uploads/2018/12/colored-lines.png" alt="" class="alignnone size-full wp-image-152" /></a></p>

<p>For the best results in your 2D drawings, you may want to use the camera "snap" checkbox in the main window, or disable the perspective camera to get a parallel (orthographic) projection. If you keep the 2D snapshot window open, you can click the "refresh" button if you have changed the main view or the model it shows, and the 2D snapshot will be updated.</p>

<h2>Automatic Capture on Save</h2>

<p>Now you can automatically save a PDF, Postscript, or SVG drawing whenever you save your vZome models. See this <a href="https://www.vzome.com/blog/2018/02/vzome-content-workflows/">updated post for details</a>.</p>

<h2>Issues</h2>

<p>If your vZome model mixes panels with balls and struts, you'll get a disappointing result in any 2D snapshot. This is because each polygon or line segment in the snapshot is drawn in its entirety, without computing any intersections with other polygons or segments. The elements are drawn using the <a href="https://en.wikipedia.org/wiki/Painter%27s_algorithm">painter's algorithm</a>, so everything looks fine as long as there are no intersecting parts, and panels <em>always</em> intersect with any adjacent balls or struts. You'll get the best results by using only panels, or by using balls and struts with no "impossible" intersections.</p>
