---
title: Sharing vZome Designs

---

[vZome][vzome] supports sharing designs using [Github Pages][pages].
In addition to vZome itself, all you need is a [GitHub][github] account (these are free),
and a GitHub repository named `vzome-sharing` with a `main` branch.

# Simple setup:

1. Log into your GitHub account
2. Fork this [template repository](https://github.com/vZome/vzome-sharing) in your account.  The repo is pre-configured for vZome sharing; your fork should keep the name `vzome-sharing`.
3. Verify that the default branch in your new repo is called `main`, not `master`.  If you have been a GitHub user for a while, your user setting for the default branch name may still be the older `master`.  You can change the default branch name to `main` under "Settings", under the "Branches" tab on the left.
4. Go to the "Settings" tab for the repository, and click on "Pages" option on the left.  Select the `main` branch, with the default "/ (root)" folder, and click "Save".  Within a few minutes you should see a green banner that provides a link to the page for the repository.

# How it works

vZome will publish designs for sharing by adding dated, timestamped folders to your `vzome-sharing` repository.
[Here is an example][example] of such a folder.

While sharing, vZome creates the folder, then uploads several files:
 - the `.vZome` design file itself
 - a `.shapes.json` 3D preview file, for fast rendering online
 - a `.png` thumbnail image, for embedding links in social media
 - an `index.md` that is the source for a new web page
 - a `README.md` that explains how to share your design and web page
That `README.md` will be rendered by default, as in the example above, if you click on the "View GitHub Folder" link
in the dialog that appears after you share a design in vZome.

Each time you share a design in this way, GitHub Pages will automatically rebuild the website corresponding to your repository,
including a new web page dedicated to your shared vZome design, which is generated from the `index.md` content.
There is a link to this new page in the `README.md`

The website rebuild may take a few minutes, and there is a limit on how many times the website will be rebuilt within an hour, so **make sure** that
you new web page is available before you share its link on social media!
If you share it before the rebuild is complete,
the link will not get embedded as a preview -- your social media post won't show the title, description, and preview image.

# Customizing your page

You can use your `vzome-sharing` repository to build a custom website, which is the intended purpose of GitHub Pages.
That is how this page you are reading was created.  You probably want to choose a *theme* in the "Pages" settings for your repository,
and perhaps set some *theme properties* in the the `_config.yml` file that will appear in your repository.
Then add as many HTML or Markdown pages as you like.  Best of all, those pages can link to your vZome designs in the same repository,
as raw vZome files and thumbnail images, as links to the generated `README.md` files, or even as links to additional webpages you author
in the generated folders.




[vzome]: https://vzome.com/home/
[pages]: https://pages.github.com/
[github]: https://github.com/
[example]: https://github.com/vorth/vzome-sharing/tree/main/2021/10/28/14-19-45-THC-SV-colored
[gists]: https://gist.github.com/
[online]: https://vzome.com/app/
[bhall]: https://vzome.com/app/bhall/basic
