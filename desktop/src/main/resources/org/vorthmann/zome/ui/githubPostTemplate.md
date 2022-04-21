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

<vzome-viewer style="width: 100%; height: 65vh;"
       src="${siteUrl}/${designPath}" >
  <img src="${siteUrl}/${imagePath}" />
</vzome-viewer>