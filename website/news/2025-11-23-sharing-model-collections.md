---
title: Sharing vZome Model Collections
description: An answer to the question "How can I share a collection of vZome models?" 
image: https://vorth.github.io/vzome-sharing/2025/07/06/11-17-36-Trussed-Buckyball/Trussed-Buckyball.png
published: false
layout: zometool
---

A user on the [vZome Discord server](https://discord.gg/vhyFsNAFPS) recently asked, "How can I share a collection of vZome models?".
For the past five years, I've worked hard to implement an effective sharing mechanism for vZome designs.
Many vZome users have been along for the ride, watching on Discord as the technology improved,
but if you're new to vZome, I want to provide a full answer to that question here.

Here are four examples of shared collections of vZome models, going from least effort to most effort.
Each figure is a link to an external web page.

The first example is just the vZome Browser, a webapp that I built, configured by the `?user=...` query string to list all the models shared to a particular GitHub account.  The models must be shared from vZome, or at least organized the same way vZome would organize them in a public `vzome-sharing` repo under that account.

<figure style="width: 93%; margin: 2%">
  
  <a href="https://www.vzome.com/app/browser/?user=tomgeometer" target="_blank" rel="noopener">
    <img style="width: 100%; border: 1px solid black; margin-bottom: 15px;" src="/assets/vzome-browser.png" >
  </a>

  <figcaption style="text-align: center; font-style: italic;">
    An entire GitHub repository full of vZome designs, displayed in the vZome GitHub Browser app.
  </figcaption>
</figure>

That app gives a "kitchen door" view of a `vzome-sharing` repository in GitHub.
The "front door" view of *the same repository* is a blog site
showing a collection of posts about geometry.
This is the way that vZome sharing is meant to be consumed on the web.
The blog itself is set up automatically when you follow the setup instructions for vZome sharing (see below). 

<figure style="width: 93%; margin: 2%">
  
  <a href="https://tomgeometer.github.io/vzome-sharing/" target="_blank" rel="noopener">
    <img style="width: 100%; border: 1px solid black; margin-bottom: 15px;" src="/assets/basic-vzome-blog.png" >
  </a>

  <figcaption style="text-align: center; font-style: italic;">
    A basic vZome sharing blog, with no customization.
  </figcaption>
</figure>

Note that the blog mechanism may be used to publish just a small subset of the vZome designs
uploaded to the repository.
Each time a design is uploaded from vZome, you decide whether you want to create a blog post;
regardless, a *separate* individual web page is always generated for the uploaded design,
shareable as an individual link, but the page is not discoverable.
Blog posts are distinct from those per-design pages, and are discoverable in the main index page of the blog.

The next example is another geometry blog, using the same technology as the prior link, but now heavily customized.
In addition, each blog article was carefully crafted, after the initial skeleton was created by vZome sharing.

<figure style="width: 93%; margin: 2%">
  
  <a href="https://joris1724.github.io/vzome-sharing/" target="_blank" rel="noopener">
    <img style="width: 100%; border: 1px solid black; margin-bottom: 15px;" src="/assets/joris-vzome-blog.png" >
  </a>

  <figcaption style="text-align: center; font-style: italic;">
    A highly customized vZome sharing blog, with in-depth posts including step-by-step instructions.
  </figcaption>
</figure>


This is another 
4.  https://vzome.github.io/johnson-solids/?ready=1

This is a custom web app, explicitly built around a particular collection of shared vZome models.  A handful of users here on Discord collaborated on the development, after I gave them a simple head start.  This app has been replicated
at least once for a different collection of models.

All of these are based on vZome's sharing feature.  Here is the documentation for setting it up:

https://www.vzome.com/docs/sharing/

If you're fairly technical, you can easily follow those steps and succeed in about 10-15 minutes; several folks here have done that.  Others are more comfortable with some assistance via screen-sharing, and I'm quite happy to do that for you.