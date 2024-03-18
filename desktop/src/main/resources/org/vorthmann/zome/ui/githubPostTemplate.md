---
title: ${title}
description: ${description}
image: ${siteUrl}/${imagePath}
published: ${published}
layout: vzome
---

{% comment %}
 - [***web page generated from this source***](<${siteUrl}/${postPath}>)
 - [data assets and more info](<${assetsUrl}>)
 
{% endcomment %}

${description}

<figure style="width: 87%; margin: 5%">
  <vzome-viewer style="width: 100%; height: 60vh"
       src="${siteUrl}/${designPath}" >
    <img  style="width: 100%"
       src="${siteUrl}/${imagePath}" >
  </vzome-viewer>
  <figcaption style="text-align: center; font-style: italic;">
    ${title}
  </figcaption>
</figure>
