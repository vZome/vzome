package com.vzome.core.exporters;

import java.io.IOException;

import com.vzome.xml.ResourceLoader;

public class GitHubShare
{
    private String designName;
    private String date;
    private String time;

    private String xml;
    private String png;
    private String shapesJson;
    
    private EntryHandler handler;

    public GitHubShare( String fileName, String date, String time, String xml, String png, String shapesJson )
    {
        this.date = date;
        this.time = time;
        this.xml = xml;
        this.png = png;
        this.shapesJson = shapesJson;
        
        this.designName = fileName .trim() .replaceAll( " ", "-" ); // we don't want spaces in our URLs
        int index = designName .toLowerCase() .lastIndexOf( ".vZome" .toLowerCase() );
        if ( index > 0 )
            designName = designName .substring( 0, index );
        while ( designName .startsWith( "-" ) ) // leading hyphens can break things for Jekyll posts
            designName = designName .substring( 1 );
        while ( designName .endsWith( "-" ) ) // so can trailing hyphens
            designName = designName .substring( 0, designName .lastIndexOf( '-' ) );
    }

    public String getDesignName()
    {
        return this.designName;
    }
    
    public interface EntryHandler
    {
        void addEntry( String path, String content, String encoding ) throws IOException;
    }

    public void setEntryHandler( EntryHandler handler )
    {
        this.handler = handler;        
    }

    public String generateContent( String orgName, String repoName, String branchName, String title, String description, boolean blog, boolean publish, String style ) throws IOException
    {
        // prepare the substitutions
        String dateFolder = this.date .replace( '-', '/' );
        
        String postSrcPath = "_posts/" + date + "-" + designName + "-" + time + ".md";// e.g. _posts/2021-11-29-sample-vZome-share-08-01-41.md
        String postPath = dateFolder + "/" + designName + "-" + time + ".html";       // e.g. 2021/11/29/sample-vZome-share-08-01-41.html
        String assetPath = dateFolder + "/" + time + "-" + designName + "/";          // e.g. 2021/11/29/08-01-41-sample-vZome-share

        String designPath = assetPath + designName + ".vZome"; // e.g. 2021/11/29/08-01-41-sample-vZome-share/sample-vZome-share.vZome
        String imagePath = assetPath + designName + ".png";    // e.g. 2021/11/29/08-01-41-sample-vZome-share/sample-vZome-share.png
        
        this.handler .addEntry( designPath, this.xml, "utf-8"  );
        
        this.handler .addEntry( imagePath, png, "base64"  );

        this.handler .addEntry( assetPath + designName + ".shapes.json", shapesJson, "utf-8"  );
        
        String viewerTemplate = ResourceLoader.loadStringResource( "com/vzome/core/exporters/github/viewerTemplate.md" );
        String instructionsTemplate = ResourceLoader.loadStringResource( "com/vzome/core/exporters/github/instructionsTemplate.md" );

        String viewerControls = "";
        String viewerParameters = "";
        String viewerScript = "";
        String componentTemplate = viewerTemplate;
        String postLayout = "vzome";
        String simpleLayout = "design";
        switch (style)
        {
        case "indexed": {
            viewerControls =
              "<div style='display:flex;'><div style='margin: auto;'>"
              +  "<vzome-viewer-previous label='prev step'></vzome-viewer-previous>"
              +  "<vzome-viewer-next label='next step'></vzome-viewer-next>"
              +"</div></div>";
            viewerParameters = "indexed='true'";
            break;
        }
        case "indexed (load-camera)": {
            viewerControls =
              "<div style='display:flex;'><div style='margin: auto;'>"
              +  "<vzome-viewer-previous load-camera='true' label='prev step'></vzome-viewer-previous>"
              +  "<vzome-viewer-next load-camera='true' label='next step'></vzome-viewer-next>"
              +"</div></div>";
            viewerParameters = "indexed='true'";
            break;
        }
        case "menu":
        case "menu (named)": {
            viewerParameters = "show-scenes='named'";
            break;
        }
        case "menu (all)": {
            viewerParameters = "show-scenes='all'";
            break;
        }
        case "javascript": {
            viewerParameters = "id='vzome-viewer' reactive='false'";
            viewerScript =
                "<script type='module'>\n"
              + "  const viewer = document.querySelector( '#vzome-viewer' );\n"
              + "  // viewer.scene = 'your scene';\n"
              + "  viewer.update();\n"
              + "</script>\n";
            break;
        }
        case "zometool": {
            componentTemplate = instructionsTemplate;
            postLayout = "zometool";
            simpleLayout = "zometool";
            break;
        }
        default:
        	break;
        }

        String siteUrl = "https://" + orgName + ".github.io/" + repoName;
                 // e.g. https://vorth.github.io/vzome-sharing
        String repoUrl = "https://github.com/" + orgName + "/" + repoName;
                 // e.g. https://github.com/vorth/vzome-sharing
        String gitUrl = repoUrl + "/tree/" + branchName + "/" + assetPath;
                 // e.g. https://github.com/vorth/vzome-sharing/tree/main/2021/11/29/08-01-41-sample-vZome-share/
        String rawUrl = "https://raw.githubusercontent.com/" + orgName + "/" + repoName + "/" + branchName + "/" + designPath;
                 // e.g. https://raw.githubusercontent.com/vorth/vzome-sharing/main/2021/11/29/08-01-41-sample-vZome-share/sample-vZome-share.vZome
        String postUrl = siteUrl + "/" + postPath;
                 // e.g. https://vorth.github.io/vzome-sharing/2021/11/29/sample-vZome-share-08-01-41.html
        String postSrcUrl = repoUrl + "/edit/" + branchName + "/" + postSrcPath;
                 // e.g. https://github.com/vorth/vzome-sharing/edit/main/_posts/2021-11-29-sample-vZome-share-08-01-41.md
        String indexSrcUrl = repoUrl + "/edit/" + branchName + "/" + assetPath + "index.md";
        // e.g. https://github.com/vorth/vzome-sharing/edit/main/_posts/2021-11-29-sample-vZome-share-08-01-41.md
                    
        String descriptionClean = description .replace( '\n', ' ' ) .replace( '\r', ' ' );
        
        String viewerComponent = componentTemplate
                .replace( "${siteUrl}", siteUrl )
                .replace( "${imagePath}", imagePath )
                .replace( "${designPath}", designPath )
                .replace( "${viewerControls}", viewerControls )
                .replace( "${viewerParameters}", viewerParameters );

        // Always generate a shareable page for the vZome user to use, not discoverable
        String indexTemplate = ResourceLoader.loadStringResource( "com/vzome/core/exporters/github/indexTemplate.md" );
        String indexMd = indexTemplate
                .replace( "${simpleLayout}", simpleLayout )
                .replace( "${viewerComponent}", viewerComponent )
                .replace( "${title}", title )
                .replace( "${description}", descriptionClean )
                .replace( "${siteUrl}", siteUrl )
                .replace( "${imagePath}", imagePath )
                .replace( "${assetsUrl}", gitUrl );
        if ( viewerScript != "" )
            indexMd = indexMd + viewerScript;

        this.handler .addEntry( assetPath + "index.md", indexMd, "utf-8" );

        // Generate a README for the vZome user to use
        String readmeTemplate = ResourceLoader.loadStringResource( "com/vzome/core/exporters/github/readmeTemplate.md" );
        String readmeMd = readmeTemplate
                .replace( "${viewerComponent}", viewerComponent )
                .replace( "${imageFile}", designName + ".png" )
                .replace( "${siteUrl}", siteUrl )
                .replace( "${assetPath}", assetPath )
                .replace( "${imagePath}", imagePath )
                .replace( "${viewerScript}", viewerScript )
                .replace( "${indexSrcUrl}", indexSrcUrl )
                .replace( "${rawUrl}", rawUrl );
        
        if ( blog )
        {
            // Add a section to the README 
            String githubReadmeBlogPrefixTemplate = ResourceLoader.loadStringResource( "com/vzome/core/exporters/github/readmeBlogPrefixTemplate.md" );
            String blogPostPrefix = githubReadmeBlogPrefixTemplate
                    .replace( "${postUrl}", postUrl )
                    .replace( "${postSrcUrl}", postSrcUrl );
            readmeMd = blogPostPrefix + readmeMd; // Prepend the extra bits about the blog post to the README

            /*
             * Generate a design-specific web page, assuming that GitHub Pages is
             * enabled for the repo.  The "front-matter" format is defined by
             * Jekyll, and these properties specifically are defined by the Jekyll SEO plugin.
             *    https://github.com/jekyll/jekyll-seo-tag/tree/master/docs
             * This guarantees good OpenGraph metadata for embedding in social media.
             * See also:
             *    https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll
             *    https://jekyllrb.com/
             */
            String postTemplate = ResourceLoader.loadStringResource( "com/vzome/core/exporters/github/postTemplate.md" );
            String postMd = postTemplate
                    .replace( "${viewerComponent}", viewerComponent )
                    .replace( "${postLayout}", postLayout )
                    .replace( "${title}", title )
                    .replace( "${description}", descriptionClean )
                    .replace( "${published}", ((Boolean)publish) .toString() )
                    .replace( "${siteUrl}", siteUrl )
                    .replace( "${postPath}", postPath )
                    .replace( "${imagePath}", imagePath )
                    .replace( "${assetsUrl}", gitUrl );
            if ( viewerScript != "" )
                postMd = postMd + viewerScript;
            this.handler .addEntry( postSrcPath, postMd, "utf-8"  );
        }
        this.handler .addEntry( assetPath + "README.md", readmeMd, "utf-8"  );
        return gitUrl;
    }
}
