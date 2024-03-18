
import { parse } from "path";
import { readFile } from "fs/promises";
import { transformAsync } from "@babel/core";
import solid from "babel-preset-solid";
import ts from "@babel/preset-typescript";


export function solidPlugin(options = {}) {
  return {
    name: "esbuild:solid",

    setup(build) {
      build.onLoad({ filter: /\.(t|j)sx$/ }, async (args) => {
        const source = await readFile(args.path, { encoding: "utf-8" });

        const { name, ext } = parse(args.path);
        const filename = name + ext;

        const regex = new RegExp( 'import React' );
        if ( regex.test( source ) )
          return undefined; // fall through to the built-in JSX processor

        const { code } = await transformAsync(source, {
          presets: [[solid, options?.solid || {}], [ts, options?.typescript || {}]],
          filename,
          sourceMaps: "inline",
        });

        return { contents: code, loader: "js" };
      });
    },
  };
}