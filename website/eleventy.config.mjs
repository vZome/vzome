export default async function (eleventyConfig) {
	// Output directory: _site

	eleventyConfig.addPassthroughCopy("assets");
	eleventyConfig.addPassthroughCopy("media");

  eleventyConfig.addGlobalData("layout", "page");
  eleventyConfig.addGlobalData("page-type", "page");

	// eleventyConfig.addLayoutAlias("vzome", "vzome-sharing/_layouts/vzome.html");

  eleventyConfig.addLiquidFilter( "relative_url", value => value );

  eleventyConfig.addCollection( "geomPublished", (collectionsApi) => {
		return collectionsApi .getFilteredByTag("posts") .filter( item => item.data.published !== false );
  });

  eleventyConfig.addCollection( "newsPublished", (collectionsApi) => {
		return collectionsApi .getFilteredByTag("news") .filter( item => item.data.published !== false );
  });

};