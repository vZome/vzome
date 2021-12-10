---
title: Sharing vZome Designs
description: vZome supports sharing 3D designs using GitHub Pages.  This is an explanation of how to use that capability.

---

# {{ page.title }}

[vZome][vzome] supports sharing designs using [Github Pages][pages].
In addition to vZome itself, all you need is a free [GitHub][github] account,
and a GitHub repository named `vzome-sharing` with a `main` branch.

## Simple Setup

Before you can share any designs from vZome, you need to do a one-time setup in GitHub.
Obviously, you have to first sign up for a [GitHub][github] account if you don't have one.

1. Sign in to your GitHub account
2. If you don't already have a `vzome-sharing` GitHub repo, fork this [template repository](https://github.com/vZome/vzome-sharing) in your account.  (The "Fork" button is in the upper right of that repo page.)  The repo is pre-configured for vZome sharing, and your fork should keep the name `vzome-sharing`.  If you already had a `vzome-sharing` repo, don't fork the template; instead, copy the `_layouts/vzome.html`, `_config.yml`, and `index.md` files to your own repo.
3. [Follow instructions][enable] to enable GitHub Pages for your repo.  Within a few minutes you should see a green banner that provides a link to the website built from the repository.

Next, there is a little preparation you need to do in vZome itself, so it is authorized to upload
files to your new GitHub repository.

1. In your web browser, sign in to your GitHub account.
1. Save any vZome design to your file system, thereby giving it a name.
2. Click on the share icon in the upper right.  vZome will connect to GitHub (assuming you are online),
and return with an authorization code.
3. Click on the "Copy code and authorize" button.  Your default browser will launch, and will show you
the device activation page.  Paste the authorization code into the form, and click "Continue".
4. GitHub will now ask you to click one more button, authorizing the vZome application to upload
files to your repository.
5. vZome will automatically continue the sharing process you initiated; see below for further steps.

Now you have a GitHub authorization that should last you for a while.  vZome actually stores the authorization code,
so you won't have to authorize again until the code expires some months later.
(vZome will prompt you to reauthorize when it becomes necessary.)

## Sharing Designs

In the upper right corner of the toolbar area in vZome you will find the share button.
Sharing a design requires a file name, so the button will only be enabled when you have saved
a design at least once.  (vZome does NOT try to ensure that your latest changes are saved before you share!)

When you click on the share button, you will see a dialog message while the files are generated and uploaded to GitHub.
In a few seconds the process should complete, and you'll see a link to visit the folder
in GitHub where the assets landed.
If you click that link, your default web browser will open onto that folder in GitHub.
(Occasionally GitHub may require you to sign in first, if your session had already expired.)

GitHub will display a list of several files, including a `README.md`, and below the list it will
render the `README.md` as styled text.  (This is standard behavior for GitHub whenever a
folder contains such a file.)  The `README.md` contains several links, but the first two
are the most important: a link to your generated, custom web page for the shared design,
and a link to edit the *source Markdown file* from which that page is generated.

The first link is the one that you can copy and share on social media or email.
***However***, that link won't work immediately, as explained below, so *always*
try the link and wait for it to render a correct page before you copy it and share it.

It is very important to understand the distinction between the *generated web page*
and the *source file from which it is generated*.
Although you can view both as web pages in your web browser, the first one is
meant to be shared and read by others, and the second one is meant only for you.
It is like the distinction between a published book
and the original book manuscript given to a publisher.
And, just as a publisher takes some weeks or months to actually publish a book,
GitHub Pages takes a few minutes to generate the custom web page.

## Customizing the Generated Page

The second link in the `README.md` takes you right into edit mode for `index.md`,
the source for your custom web page.
The raw `index.md` content will look something like this:

{% raw %}
```markdown
---
title: Sample vZome Share
image: https://vorth.github.io/vzome-sharing/2021/11/29/08-01-41-sample-vZome-share/sample-vZome-share.png
layout: vzome
---

{% comment %}
 - [***web page generated from this source***][post]
 - [data assets and more info][github]

[post]: https://vorth.github.io/vzome-sharing/2021/11/29/sample-vZome-share-08-01-41.html
[github]: https://github.com/vorth/vzome-sharing/tree/main/2021/11/29/08-01-41-sample-vZome-share/
{% endcomment %}

<vzome-viewer style="width: 100%; height: 65vh;"
       src="https://vorth.github.io/vzome-sharing/2021/11/29/08-01-41-sample-vZome-share/sample-vZome-share.vZome" >
  <img src="https://vorth.github.io/vzome-sharing/2021/11/29/08-01-41-sample-vZome-share/sample-vZome-share.png" />
</vzome-viewer>
```
{% endraw %}

For the moment, you can ignore everything above the `endcomment` tag.
Immediately below it, and above the `vzome-viewer` element that actually displays your design,
is the perfect place to write whatever you want to say about your design.
The `index.md` file will be interpreted as [Markdown][markdown], so you can do some formatted content,
but you can also simply type, without worrying about line breaks, text fill, or other formatting.

The `index.md` source file is yours to change however you like, just like the rest of the files in
your entire `vzome-sharing` repo.
You can add images, links to other web sites (like YouTube)... whatever you like.
You can add a few more `vzome-viewer` elements for other designs you have shared,
to make a complete article about some topic.  Run with it!

Check out [this example][postsrc] of a customized `index.md` page source file.  In particular,
note the use of `{{ page.description }}`.
There are some conventions and mechanisms in `index.md` that you will want to understand before you
change them; [see below](#metadata-for-embedding) for a full explanation.

After you commit your changes to `index.md` (or any other file in the repo),
the website rebuild may take a few minutes,
and there is a limit on how many times the website will be rebuilt within an hour,
so **make sure** that your new or modified web page is available before you share its link on social media!
If you share it before the rebuild is complete,
the link will not get embedded as a preview -- your social media post won't show the title, description, and preview image.

## Customizing your site

Your `vzome-sharing` repository creates an entire website, and there is no reason to limit yourself
to just the plain blog index that you get from the template repo.
You can choose a different *theme* in the "Pages" settings for your repository,
and perhaps set some *theme properties* in the the `_config.yml` file that will appear in your repository.
(Note that many of the themes do not generate a post index on the home page; this is the reason that
the template `vzome-sharing` repo uses the `minima` theme by default.)

You can create blog posts and regular web pages very easily, and make the website your own.
Add as many HTML or Markdown pages as you like.  Best of all, those pages can show your vZome designs in the same repository,
using the same `vzome-viewer` custom element used in the generated, design-specific pages.

GitHub Pages puts a lot of power in your hands, as we'll see below,
so you are limited only by your imagination!

## How It Works

[GitHub Pages][pages] is based on [Jekyll][jekyll], a static website generator.
Jekyll is used successfully to generate a [wide variety of websites][showcase],
so there is a lot you can learn, but we'll just cover the basics here, and how
vZome sharing makes use of Jekyll.

When you enable GitHub Pages on a repository, that means that somewhere in the cloud
a Jekyll server walks over your repo and generates a website,
which is itself hosted in the cloud (on the `<yourusername>.github.io` domain).
Jekyll will rebuild that website whenever you commit some change to your repository,
including when vZome uploads a design to share.

When vZome uploads a design to GitHub, it creates a dated, timestamped folder [like this][example] in your `vzome-sharing` repository.
In that folder, vZome uploads several files:
 - the `.vZome` design file itself
 - a `.shapes.json` 3D preview file, for fast rendering online
 - a `.png` thumbnail image, for embedding links in social media
 - a `README.md` that explains how to share your design and web page

This folder will be included in the full website generated by Jekyll, but only as file *assets*;
your `vzome-sharing` repository has been specially configured so that Jekyll won't generate any web page
from the `README.md` file.

vZome also creates a new *post* [like this][postsrc], a specially-named Markdown file under the `_posts` folder in your repo.
(It will create the `_posts` folder, too, so you don't need to worry about that.)
These files are treated specially by Jekyll, and converted into web pages corresponding to blog posts.

## `index.md` Details

### Metadata for Embedding
Each generated `index.md` file starts with [Jekyll front matter][fmatter]:
```markdown
---
title: Sample vZome Share
image: https://vorth.github.io/vzome-sharing/2021/11/29/08-01-41-sample-vZome-share/sample-vZome-share.png
layout: vzome
---
```
The first two variables, `title` and `image`, are used by the [`jekyll-seo-tag` plugin][seo],
which GitHub Pages apparently uses automatically.
These variables inform *metadata* included in the heading of your generated web page.
This metadata helps somewhat with search engine optimization (SEO), but, more importantly,
it gets used by social media platforms to render a preview card for your page.

The third variable, `layout: vzome`, is very important for enabling the use
of the `vzome-viewer` custom element in this web page, so you don't want to remove it.
If you learn about the `vzome` custom layout provided with the `vzome-sharing` repo template,
and the supporting Jekyll mechanisms, you can potentially create your own layouts for different
purposes.

You will find that your generated pages all have a generic `description` in the HTML metadata.
It is a very good idea to actually customize that description for each page:
```
description:
  This is my cool model of some geometry.  I made it in vZome,
  and I'm very proud of it.
```
That variable definition must go in the front matter with the others, before the second `---` separator.
The indentation is very important; read about [YAML][yaml], the syntax for front matter, if you're curious.

The [sample post source][postsrc] illustrates how you can also render the description as part of your page
text, using `\{\{ page.description \}\}`.  This is a [Liquid expression][liquid], used to substitute
[variables][variables] by Jekyll.

### Navigation Links
After the front matter, you will see a Jekyll comment section:
{% raw %}
```markdown
{% comment %}
 - [***web page generated from this source***][post]
 - [data assets and more info][github]

[post]: https://vorth.github.io/vzome-sharing/2021/11/29/sample-vZome-share-08-01-41.html
[github]: https://github.com/vorth/vzome-sharing/tree/main/2021/11/29/08-01-41-sample-vZome-share/
{% endcomment %}
```
{% endraw %}
This section is there just to help you navigate from the source `index.md` to the other two
important places: the actual generated web page, and the supporting assets folder.
Jekyll will completely ignore this section, since it is marked as a `comment`,
so none of it will appear in the final web page.

### `vzome-viewer` Instance

This, of course, is the reason for the whole enterprise,
a custom HTML element that renders an interactive 3D viewer for your vZome design:
```html
<vzome-viewer style="width: 100%; height: 65vh;"
       src="https://vorth.github.io/vzome-sharing/2021/11/29/08-01-41-sample-vZome-share/sample-vZome-share.vZome" >
  <img src="https://vorth.github.io/vzome-sharing/2021/11/29/08-01-41-sample-vZome-share/sample-vZome-share.png" />
</vzome-viewer>
```
The `src` attribute on the main element is the most important thing, here.
It links to your vZome design file, uploaded into the associated assets folder.

Next, notice the `style` attribute.
This must contain valid CSS for styling the viewer element.
It is entirely optional, so you can remove it if you are OK with the default style of the viewer.
Lots of things are possible, here, but you should experiment at least with
different approaches to `height` and `width`.
See this [CSS tutorial][css] to learn more.

Finally, notice there is an `image` tag nested inside the `vzome-viewer` custom element.
This provides a "fall-back", something to render on the page even when the Javascript definition
of the `vzome-viewer` failed to load correctly, or when the user has Javascript disabled
in their browser.
You will also see the image flash on the generated page initially, as the Javascript is loading.

The `vzome-viewer` web component will continue to evolve, gaining new features.
Your generated web pages will pick up these enhancements automatically, giving your readers
new capabilities for interacting with your designs.
In some cases, we will add *optional* features that won't be used unless you specifically
*opt in* by setting some attribute on the `vzome-viewer` element.
This document will be updated soon with a link to full documentation for the `vzome-viewer` web component.

## Template `vzome-sharing` Repository Details

Coming soon.

[vzome]: https://vzome.com/home/
[pages]: https://pages.github.com/
[enable]: https://docs.github.com/en/pages/getting-started-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site
[jekyll]: https://jekyllrb.com/
[fmatter]: https://jekyllrb.com/docs/front-matter/
[liquid]: https://jekyllrb.com/docs/liquid/
[variables]: https://jekyllrb.com/docs/variables/
[seo]: https://github.com/jekyll/jekyll-seo-tag/blob/master/docs/usage.md
[yaml]: https://yaml.org/
[github]: https://github.com/
[example]: https://github.com/vorth/vzome-sharing/tree/main/2021/11/29/08-01-41-sample-vZome-share
[postsrc]: https://github.com/vorth/vzome-sharing/blob/main/_posts/2021-11-29-sample-vZome-share-08-01-41.md
[showcase]: https://jekyllrb.com/showcase/
[markdown]: https://github.github.com/gfm/
[css]: https://developer.mozilla.org/en-US/docs/Web/CSS/Tutorials
