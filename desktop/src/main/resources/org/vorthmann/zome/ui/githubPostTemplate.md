---
title: ${title}
image: ${siteUrl}/${imagePath}
layout: vzome
---

{% comment %}
 - [***web page generated from this source***][post]
 - [data assets and more info][github]

[post]: <${siteUrl}/${postPath}>
[github]: <${assetsUrl}>
{% endcomment %}

<vzome-viewer style="width: 100%; height: 65vh;"
       src="${siteUrl}/${designPath}" >
  <img src="${siteUrl}/${imagePath}" />
</vzome-viewer>