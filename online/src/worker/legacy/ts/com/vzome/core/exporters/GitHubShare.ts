/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class GitHubShare {
        /*private*/ designName: string;

        /*private*/ date: string;

        /*private*/ time: string;

        /*private*/ xml: string;

        /*private*/ png: string;

        /*private*/ shapesJson: string;

        /*private*/ handler: GitHubShare.EntryHandler;

        public constructor(fileName: string, date: string, time: string, xml: string, png: string, shapesJson: string) {
            if (this.designName === undefined) { this.designName = null; }
            if (this.date === undefined) { this.date = null; }
            if (this.time === undefined) { this.time = null; }
            if (this.xml === undefined) { this.xml = null; }
            if (this.png === undefined) { this.png = null; }
            if (this.shapesJson === undefined) { this.shapesJson = null; }
            if (this.handler === undefined) { this.handler = null; }
            this.date = date;
            this.time = time;
            this.xml = xml;
            this.png = png;
            this.shapesJson = shapesJson;
            this.designName = /* replaceAll */fileName.trim().replace(new RegExp(" ", 'g'),"-");
            const index: number = this.designName.toLowerCase().lastIndexOf(".vZome".toLowerCase());
            if (index > 0)this.designName = this.designName.substring(0, index);
            while((/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(this.designName, "-"))) {this.designName = this.designName.substring(1)};
            while((/* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(this.designName, "-"))) {this.designName = this.designName.substring(0, this.designName.lastIndexOf('-'))};
        }

        public getDesignName(): string {
            return this.designName;
        }

        public setEntryHandler(handler: GitHubShare.EntryHandler) {
            this.handler = handler;
        }

        public generateContent(orgName: string, repoName: string, branchName: string, title: string, description: string, blog: boolean, publish: boolean, style: string): string {
            const dateFolder: string = /* replace */this.date.split('-').join('/');
            const postSrcPath: string = "_posts/" + this.date + "-" + this.designName + "-" + this.time + ".md";
            const postPath: string = dateFolder + "/" + this.designName + "-" + this.time + ".html";
            const assetPath: string = dateFolder + "/" + this.time + "-" + this.designName + "/";
            const designPath: string = assetPath + this.designName + ".vZome";
            const imagePath: string = assetPath + this.designName + ".png";
            this.handler.addEntry(designPath, this.xml, "utf-8");
            this.handler.addEntry(imagePath, this.png, "base64");
            this.handler.addEntry(assetPath + this.designName + ".shapes.json", this.shapesJson, "utf-8");
            const viewerTemplate: string = com.vzome.xml.ResourceLoader.loadStringResource("com/vzome/core/exporters/github/viewerTemplate.md");
            const instructionsTemplate: string = com.vzome.xml.ResourceLoader.loadStringResource("com/vzome/core/exporters/github/instructionsTemplate.md");
            let viewerControls: string = "";
            let viewerParameters: string = "";
            let viewerScript: string = "";
            let componentTemplate: string = viewerTemplate;
            let postLayout: string = "vzome";
            let simpleLayout: string = "design";
            switch((style)) {
            case "indexed":
                {
                    viewerControls = "<div style=\'display:flex;\'><div style=\'margin: auto;\'><vzome-viewer-previous label=\'prev step\'></vzome-viewer-previous><vzome-viewer-next label=\'next step\'></vzome-viewer-next></div></div>";
                    viewerParameters = "indexed=\'true\'";
                    break;
                };
            case "indexed (load-camera)":
                {
                    viewerControls = "<div style=\'display:flex;\'><div style=\'margin: auto;\'><vzome-viewer-previous load-camera=\'true\' label=\'prev step\'></vzome-viewer-previous><vzome-viewer-next load-camera=\'true\' label=\'next step\'></vzome-viewer-next></div></div>";
                    viewerParameters = "indexed=\'true\'";
                    break;
                };
            case "menu":
            case "menu (named)":
                {
                    viewerParameters = "show-scenes=\'named\'";
                    break;
                };
            case "menu (all)":
                {
                    viewerParameters = "show-scenes=\'all\'";
                    break;
                };
            case "javascript":
                {
                    viewerParameters = "id=\'vzome-viewer\' reactive=\'false\'";
                    viewerScript = "<script type=\'module\'>\n  const viewer = document.querySelector( \'#vzome-viewer\' );\n  // viewer.scene = \'your scene\';\n  viewer.update();\n</script>\n";
                    break;
                };
            case "zometool":
                {
                    componentTemplate = instructionsTemplate;
                    postLayout = "zometool";
                    simpleLayout = "zometool";
                    break;
                };
            default:
                break;
            }
            const siteUrl: string = "https://" + orgName + ".github.io/" + repoName;
            const repoUrl: string = "https://github.com/" + orgName + "/" + repoName;
            const gitUrl: string = repoUrl + "/tree/" + branchName + "/" + assetPath;
            const rawUrl: string = "https://raw.githubusercontent.com/" + orgName + "/" + repoName + "/" + branchName + "/" + designPath;
            const postUrl: string = siteUrl + "/" + postPath;
            const postSrcUrl: string = repoUrl + "/edit/" + branchName + "/" + postSrcPath;
            const indexSrcUrl: string = repoUrl + "/edit/" + branchName + "/" + assetPath + "index.md";
            const descriptionClean: string = /* replace *//* replace */description.split('\n').join(' ').split('\r').join(' ');
            const viewerComponent: string = /* replace *//* replace *//* replace *//* replace *//* replace */componentTemplate.split("${siteUrl}").join(siteUrl).split("${imagePath}").join(imagePath).split("${designPath}").join(designPath).split("${viewerControls}").join(viewerControls).split("${viewerParameters}").join(viewerParameters);
            const indexTemplate: string = com.vzome.xml.ResourceLoader.loadStringResource("com/vzome/core/exporters/github/indexTemplate.md");
            let indexMd: string = /* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace */indexTemplate.split("${simpleLayout}").join(simpleLayout).split("${viewerComponent}").join(viewerComponent).split("${title}").join(title).split("${description}").join(descriptionClean).split("${siteUrl}").join(siteUrl).split("${imagePath}").join(imagePath).split("${assetsUrl}").join(gitUrl);
            if (viewerScript !== "")indexMd = indexMd + viewerScript;
            this.handler.addEntry(assetPath + "index.md", indexMd, "utf-8");
            const readmeTemplate: string = com.vzome.xml.ResourceLoader.loadStringResource("com/vzome/core/exporters/github/readmeTemplate.md");
            let readmeMd: string = /* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace */readmeTemplate.split("${viewerComponent}").join(viewerComponent).split("${imageFile}").join(this.designName + ".png").split("${siteUrl}").join(siteUrl).split("${assetPath}").join(assetPath).split("${imagePath}").join(imagePath).split("${viewerScript}").join(viewerScript).split("${indexSrcUrl}").join(indexSrcUrl).split("${rawUrl}").join(rawUrl);
            if (blog){
                const githubReadmeBlogPrefixTemplate: string = com.vzome.xml.ResourceLoader.loadStringResource("com/vzome/core/exporters/github/readmeBlogPrefixTemplate.md");
                const blogPostPrefix: string = /* replace *//* replace */githubReadmeBlogPrefixTemplate.split("${postUrl}").join(postUrl).split("${postSrcUrl}").join(postSrcUrl);
                readmeMd = blogPostPrefix + readmeMd;
                const postTemplate: string = com.vzome.xml.ResourceLoader.loadStringResource("com/vzome/core/exporters/github/postTemplate.md");
                let postMd: string = /* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace */postTemplate.split("${viewerComponent}").join(viewerComponent).split("${postLayout}").join(postLayout).split("${title}").join(title).split("${description}").join(descriptionClean).split("${published}").join((<boolean>publish).toString()).split("${siteUrl}").join(siteUrl).split("${postPath}").join(postPath).split("${imagePath}").join(imagePath).split("${assetsUrl}").join(gitUrl);
                if (viewerScript !== "")postMd = postMd + viewerScript;
                this.handler.addEntry(postSrcPath, postMd, "utf-8");
            }
            this.handler.addEntry(assetPath + "README.md", readmeMd, "utf-8");
            return gitUrl;
        }
    }
    GitHubShare["__class"] = "com.vzome.core.exporters.GitHubShare";


    export namespace GitHubShare {

        export interface EntryHandler {
            addEntry(path: string, content: string, encoding: string);
        }
    }

}

