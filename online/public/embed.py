#!/usr/bin/python

# client request should look like:
#      "https://vzome.com/app/embed.py?url=" + modelUrl

import cgi
import os

form = cgi.FieldStorage()
url = form[ "url" ] .value

vZomeFile = os.path.basename(url)
title = vZomeFile .replace( ".vZome", "", 1 )
pngFile = vZomeFile .replace( ".vZome", ".png", 1 )
pngUrl = url .replace( vZomeFile, pngFile, 1 )

print "Content-type: text/html"
print

substTarget = "<title>vZome Online</title>"

replacement = "<title>" + title + "</title>"
replacement += "<meta name='twitter:card' content='summary_large_image'>"
replacement += "<meta property='og:title' content='" + title + "' />"
replacement += "<meta property='og:description' content='A vZome (https://vzome.com) 3-dimensional design. Interact by mouse or touch.' />"
replacement += "<meta property='og:type' content='article' />"
replacement += "<meta property='og:url' content='" + url + "' />"
replacement += "<meta property='og:image' content='" + pngUrl + "' />"

html = open( "../app/index.html", "r" ) .read()

html = html .replace( substTarget, replacement, 1 )

print(html)
