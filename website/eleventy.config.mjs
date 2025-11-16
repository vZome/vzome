export default async function (eleventyConfig) {
	// Output directory: _site

	eleventyConfig.addPassthroughCopy("assets");

  eleventyConfig.addGlobalData("layout", "page.njk");

	// eleventyConfig.addLayoutAlias("vzome", "vzome-sharing/_layouts/vzome.html");

  eleventyConfig.addLiquidFilter( "relative_url", value => value );
};