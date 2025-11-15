export default async function (eleventyConfig) {
	// Output directory: _site

	eleventyConfig.addPassthroughCopy("assets");

  eleventyConfig.addGlobalData("layout", "page.njk");

};