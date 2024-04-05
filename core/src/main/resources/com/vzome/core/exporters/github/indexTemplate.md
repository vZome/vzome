---
title: ''
subtitle: ${title}
share-title: ${title}
share-description: An interactive 3D view, shared from vZome
image: ${siteUrl}/${imagePath}
layout: design
---

  ${viewerControls}
  <vzome-viewer style="width: 100%; height: 60vh" ${viewerParameters}
       src="${siteUrl}/${designPath}" >
    <img  style="width: 100%"
       src="${siteUrl}/${imagePath}" >
  </vzome-viewer>

[Source folder](<${assetsUrl}>)
