import {
  B as s,
  b as w,
  C as b,
  c as S,
  f as B,
  G as N,
  g,
  h as F,
  I as M,
  i as W,
  J as V,
  K as H,
  L as A,
  l as P,
  N as c,
  n as M1,
  O as $,
  P as v,
  p as _,
  Q as G,
  R as D,
  S as J,
  W as K,
  Y as Q,
} from "./chunk-K4NE5TDR.js";
import { a as u } from "./chunk-FFFGVZ2M.js";
import { c as U, j as q, k as E } from "./chunk-QXUNN3TL.js";
import { a as x, c as p } from "./chunk-5JBAU6D4.js";
var X = x((C) => {
  "use strict";
  var z1 = S(), g1 = w();
  Object.defineProperty(C, "__esModule", { value: !0 });
  C.default = void 0;
  var b1 = g1(u()),
    c1 = z1(_()),
    v1 = (0, c1.default)(
      b1.createElement("path", {
        d: "M20 6h-8l-1.41-1.41C10.21 4.21 9.7 4 9.17 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-1 12H5c-.55 0-1-.45-1-1V9c0-.55.45-1 1-1h14c.55 0 1 .45 1 1v8c0 .55-.45 1-1 1z",
      }),
      "FolderOpenRounded",
    );
  C.default = v1;
});
var o1 = x((y) => {
  "use strict";
  var I1 = S(), k1 = w();
  Object.defineProperty(y, "__esModule", { value: !0 });
  y.default = void 0;
  var x1 = k1(u()),
    w1 = I1(_()),
    S1 = (0, w1.default)(
      x1.createElement("path", {
        d: "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z",
      }),
      "Close",
    );
  y.default = S1;
});
var i1 = x((T) => {
  "use strict";
  var B1 = S(), q1 = w();
  Object.defineProperty(T, "__esModule", { value: !0 });
  T.default = void 0;
  var _1 = q1(u()),
    A1 = B1(_()),
    O1 = (0, A1.default)(
      _1.createElement("path", {
        d: "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 15c-.55 0-1-.45-1-1v-4c0-.55.45-1 1-1s1 .45 1 1v4c0 .55-.45 1-1 1zm1-8h-2V7h2v2z",
      }),
      "InfoRounded",
    );
  T.default = O1;
});
var i = p(u());
var R = p(X());
var n = p(u());
var Y = ({ show: o, setShow: L, openDesign: t }) => {
  let [a, f] = (0, n.useState)(""),
    d = () => {
      L(!1);
    };
  return n.default.createElement(
    c,
    {
      open: o,
      onClose: d,
      "aria-labelledby": "form-dialog-title",
      maxWidth: "lg",
      fullWidth: !0,
    },
    n.default.createElement(
      D,
      { id: "form-dialog-title" },
      "Load a Remote vZome Design",
    ),
    n.default.createElement(
      v,
      null,
      n.default.createElement(
        G,
        null,
        "The URL must be open to public access.",
      ),
      n.default.createElement(Q, {
        onChange: (m) => f(m.target.value),
        autoFocus: !0,
        margin: "dense",
        id: "name",
        label: "vZome design URL",
        type: "url",
        fullWidth: !0,
      }),
    ),
    n.default.createElement(
      $,
      null,
      n.default.createElement(A, { onClick: d, color: "secondary" }, "Cancel"),
      n.default.createElement(A, {
        onClick: () => {
          L(!1), t(a);
        },
        color: "primary",
      }, "Open"),
    ),
  );
};
var D1 = new URLSearchParams(window.location.search),
  C1 = !!D1.get("enableDropbox");
C1 && window.localStorage.setItem("vzome.enable.dropbox", !0);
var y1 = [
    {
      key: "vZomeLogo",
      label: "vZome Logo",
      description:
        "The vZome logo, one tetrahedral cell of the 4D 600-cell (cell-first projection)",
    },
    {
      key: "affineDodec",
      label: "Stretched Dodecahedron",
      description:
        "A regular dodecahedron stretched by a linear transformation",
    },
    {
      key: "120-cell",
      label: "Hyper-dodecahedron",
      description:
        "The 4D analogue of a dodecahedron, with 120 dodecahedral cells",
    },
    {
      key: "bluePlaneArches1",
      label: "Arched Rhombic Triacontahedron",
      description: "A sculpture built using just the blue planes",
    },
    { key: "C240", label: "C-240 Buckyball", description: "C-240 Buckyball" },
    {
      key: "orangePurpleChiral",
      label: "Orange and Purple Tangle",
      description: "A design by Brian Hall",
    },
    {
      key: "truncated5Cell",
      url: "https://www.vzome.com/models/2007/04-Apr/5cell/A4_3C.vZome",
      label: "Truncated 5-Cell",
      description: "Truncated 5-Cell",
    },
  ],
  T1 = (o, L = ".") =>
    new URL(`${L}/models/${o}.vZome`, window.location).toString(),
  l1 = (o) => {
    let { pathToRoot: L, forDebugger: t = !1 } = o,
      [a, f] = (0, i.useState)(null),
      [d, I] = (0, i.useState)(!1),
      O = ".vZome",
      m = (0, i.useRef)(),
      k = W(),
      a1 = window.Dropbox &&
        window.localStorage.getItem("vzome.enable.dropbox"),
      d1 = () => {
        f(null), m.current.click();
      },
      n1 = () => {
        f(null),
          window.Dropbox.choose({
            linkType: "direct",
            extensions: [".vzome"],
            success: (e) => {
              k(q(e[0].link, { preview: !1, debug: t }));
            },
          });
      },
      p1 = (e) => {
        let z = e.target.files && e.target.files[0];
        z && k(E(z, t)), m.current.value = null;
      },
      Z = (e) => {
        e && e.endsWith(".vZome") && k(q(e, { preview: !1, debug: t }));
      },
      h1 = (e) => {
        f(e.currentTarget);
      },
      u1 = (e) => {
        f(null);
        let { url: z, key: j } = e;
        Z(z || T1(j, L), j);
      },
      s1 = () => {
        f(null);
      },
      m1 = () => {
        f(null), I(!0);
      };
    return i.default.createElement(
      i.default.Fragment,
      null,
      i.default.createElement(
        b,
        { title: "Open a design", "aria-label": "open" },
        i.default.createElement(s, {
          color: "inherit",
          "aria-label": "open",
          onClick: h1,
        }, i.default.createElement(R.default, { fontSize: "large" })),
      ),
      i.default.createElement(
        N,
        { anchorEl: a, keepMounted: !0, open: Boolean(a), onClose: s1 },
        i.default.createElement(
          M,
          { onClick: d1 },
          "Local vZome file",
          i.default.createElement("input", {
            className: "FileInput",
            type: "file",
            ref: m,
            onChange: p1,
            accept: O,
          }),
        ),
        i.default.createElement(M, { onClick: m1 }, "Remote vZome URL"),
        a1 &&
          i.default.createElement(M, { onClick: n1 }, "Choose from Dropbox"),
        i.default.createElement(J, null),
        y1.map((e) =>
          i.default.createElement(M, {
            key: e.key,
            onClick: () => u1(e),
          }, e.label)
        ),
      ),
      i.default.createElement(Y, { show: d, setShow: I, openDesign: Z }),
    );
  };
var h = p(u());
var r = p(u());
var e1 = p(o1());
var f1 = p(i1());
var Z1 = (o) => ({
    root: { margin: 0, padding: o.spacing(2) },
    closeButton: {
      position: "absolute",
      right: o.spacing(1),
      top: o.spacing(1),
      color: o.palette.grey[500],
    },
  }),
  j1 = B(Z1)((o) => {
    let { children: L, classes: t, onClose: a, ...f } = o;
    return r.default.createElement(
      D,
      { disableTypography: !0, className: t.root, ...f },
      r.default.createElement(g, { variant: "h6" }, L),
      a
        ? r.default.createElement(s, {
          "aria-label": "close",
          className: t.closeButton,
          onClick: a,
        }, r.default.createElement(e1.default, null))
        : null,
    );
  }),
  F1 = B((o) => ({ root: { padding: o.spacing(2) } }))(v),
  r1 = ({ title: o, about: L }) => {
    let [t, a] = r.default.useState(!1),
      f = () => {
        a(!0);
      },
      d = () => {
        a(!1);
      };
    return r.default.createElement(
      r.default.Fragment,
      null,
      r.default.createElement(
        b,
        { title: `About ${o}`, "aria-label": "about" },
        r.default.createElement(s, {
          color: "inherit",
          "aria-label": "about",
          onClick: f,
        }, r.default.createElement(f1.default, { fontSize: "large" })),
      ),
      r.default.createElement(
        c,
        { onClose: d, "aria-labelledby": "customized-dialog-title", open: t },
        r.default.createElement(
          j1,
          { id: "customized-dialog-title", onClose: d },
          "About ",
          o,
          " (rev ",
          U,
          ")",
        ),
        r.default.createElement(F1, { dividers: !0 }, L),
      ),
    );
  };
var l = p(u()), L1 = p(M1());
var t1 = () =>
  l.default.createElement(
    s,
    {
      component: F,
      href: "https://vzome.com",
      target: "_blank",
      rel: "noopener",
    },
    l.default.createElement(
      L1.default,
      {
        fontSize: "large",
        stroke: "black",
        strokeLinejoin: "round",
        strokeWidth: "0.5",
        viewBox: "55 40 340.0 340.0",
      },
      l.default.createElement("path", {
        fill: "#3e3e3e",
        d: "M 269.02 277.8 L 272.46 283.83 L 287.98 296.29 L 286.3 291.64  z",
      }),
      l.default.createElement("path", {
        fill: "#e7e7e1",
        d: "M 278.1 234.58 L 267.43 243.11 L 275.89 238.56 L 287.57 229.43  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 337.14 259.25 L 331.34 244.39 L 328.71 237.77 L 334.6 252.83  z",
      }),
      l.default.createElement("path", {
        fill: "#7c7c7f",
        d: "M 312.8 292.04 L 323.06 288.12 L 301.1 296.41  z",
      }),
      l.default.createElement("path", {
        fill: "#c7c7c3",
        d: "M 299.53 226.34 L 287.57 229.43 L 301.91 228.56 L 312.64 225.73  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 337.14 259.25 L 334.6 252.83 L 330.46 274.92 L 333.45 279.09  z",
      }),
      l.default.createElement("path", {
        fill: "#231b33",
        d: "M 247.91 235.51 L 267.43 243.11 L 275.89 238.56 L 256.03 230.78  z",
      }),
      l.default.createElement("path", {
        fill: "#fbfbf5",
        d: "M 275.89 238.56 L 267.43 243.11 L 263.23 262.81 L 271.67 258.63  z",
      }),
      l.default.createElement("path", {
        fill: "#333333",
        d: "M 301.1 296.41 L 299.6 291.72 L 286.3 291.64 L 287.98 296.29  z",
      }),
      l.default.createElement("path", {
        fill: "#ac85fa",
        d: "M 258.98 217.29 L 275.89 238.56 L 287.57 229.43  z",
      }),
      l.default.createElement("path", {
        fill: "#b8b8b4",
        d: "M 269.02 277.8 L 278.45 273.52 L 271.67 258.63 L 263.23 262.81  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 301.91 228.56 L 318.16 240.84 L 328.71 237.77 L 312.64 225.73  z",
      }),
      l.default.createElement("path", {
        fill: "#828280",
        d: "M 269.02 277.8 L 286.3 291.64 L 278.45 273.52  z",
      }),
      l.default.createElement("path", {
        fill: "#d8d8dd",
        d: "M 319.88 284.05 L 323.06 288.12 L 333.45 279.09 L 330.46 274.92  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 301.91 228.56 L 287.57 229.43 L 275.89 238.56 L 290.21 237.76  z",
      }),
      l.default.createElement("path", {
        fill: "#78787a",
        d: "M 319.88 284.05 L 299.6 291.72 L 301.1 296.41 L 323.06 288.12  z",
      }),
      l.default.createElement("path", {
        fill: "#f9f9fe",
        d: "M 318.16 240.84 L 323.09 256.52 L 334.6 252.83 L 328.71 237.77  z",
      }),
      l.default.createElement("path", {
        fill: "#f3f3f9",
        d: "M 330.46 274.92 L 334.6 252.83 L 323.09 256.52  z",
      }),
      l.default.createElement("path", {
        fill: "#302546",
        d: "M 146.76 150.32 L 140.67 156.75 L 247.91 235.51 L 256.03 230.78  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 275.89 238.56 L 271.67 258.63 L 285.64 260.03 L 290.21 237.76  z",
      }),
      l.default.createElement("path", {
        fill: "#858583",
        d: "M 286.3 291.64 L 299.6 291.72 L 292.53 275.07 L 278.45 273.52  z",
      }),
      l.default.createElement("path", {
        fill: "#fffffd",
        d: "M 301.91 228.56 L 290.21 237.76 L 308.08 251.46 L 318.16 240.84  z",
      }),
      l.default.createElement("path", {
        fill: "#d1d1d5",
        d: "M 319.88 284.05 L 330.46 274.92 L 323.09 256.52 L 313.02 267.28  z",
      }),
      l.default.createElement("path", {
        fill: "#af87ff",
        d: "M 146.76 150.32 L 256.03 230.78 L 275.89 238.56 L 258.98 217.29 L 150.34 136.97 L 129.32 128.43  z",
      }),
      l.default.createElement("path", {
        fill: "#eeeee9",
        d: "M 278.45 273.52 L 292.53 275.07 L 285.64 260.03 L 271.67 258.63  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 308.08 251.46 L 290.21 237.76 L 285.64 260.03  z",
      }),
      l.default.createElement("path", {
        fill: "#838383",
        d: "M 319.88 284.05 L 313.02 267.28 L 292.53 275.07 L 299.6 291.72  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 318.16 240.84 L 308.08 251.46 L 313.02 267.28 L 323.09 256.52  z",
      }),
      l.default.createElement("path", {
        fill: "#434344",
        d: "M 110.72 143.96 L 94.46 145.51 L 83.09 147.02 L 97.95 145.6  z",
      }),
      l.default.createElement("path", {
        fill: "#efefea",
        d: "M 313.02 267.28 L 308.08 251.46 L 285.64 260.03 L 292.53 275.07  z",
      }),
      l.default.createElement("path", {
        fill: "#231b33",
        d: "M 146.76 150.32 L 116.8 136.54 L 110.72 143.96 L 140.67 156.75  z",
      }),
      l.default.createElement("path", {
        fill: "#b0b0b4",
        d: "M 123.58 135.31 L 129.32 128.43 L 116.8 136.54 L 110.72 143.96  z",
      }),
      l.default.createElement("path", {
        fill: "#637000",
        d: "M 252.8 271.39 L 266.84 272.95 L 285.64 260.03 L 271.67 258.63  z",
      }),
      l.default.createElement("path", {
        fill: "#67002d",
        d: "M 309.25 236.52 L 308.08 251.46 L 318.16 240.84  z",
      }),
      l.default.createElement("path", {
        fill: "#3e001a",
        d: "M 309.25 236.52 L 292.66 223.95 L 290.21 237.76 L 308.08 251.46  z",
      }),
      l.default.createElement("path", {
        fill: "#3e3e3e",
        d: "M 62.41 127.1 L 68.96 135.53 L 83.09 147.02 L 78.11 139.83  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 134.72 108.98 L 128.86 93.68 L 123.15 84.58 L 129.09 100.1  z",
      }),
      l.default.createElement("path", {
        fill: "#7c7c7f",
        d: "M 110.72 143.96 L 116.8 136.54 L 94.46 145.51  z",
      }),
      l.default.createElement("path", {
        fill: "#63002b",
        d: "M 309.25 236.52 L 318.16 240.84 L 319.12 227.83  z",
      }),
      l.default.createElement("path", {
        fill: "#647100",
        d: "M 273.8 288.3 L 292.53 275.07 L 285.64 260.03 L 266.84 272.95  z",
      }),
      l.default.createElement("path", {
        fill: "#af87ff",
        d: "M 146.76 150.32 L 129.32 128.43 L 116.8 136.54  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 134.72 108.98 L 129.09 100.1 L 123.02 121.74 L 129.32 128.43  z",
      }),
      l.default.createElement("path", {
        fill: "#fbfbf5",
        d: "M 67.08 84.51 L 62.52 92.36 L 56.57 111.66 L 61.04 104.16  z",
      }),
      l.default.createElement("path", {
        fill: "#333333",
        d: "M 94.46 145.51 L 89.61 138.22 L 78.11 139.83 L 83.09 147.02  z",
      }),
      l.default.createElement("path", {
        fill: "#b8b8b4",
        d: "M 62.41 127.1 L 67.47 119.18 L 61.04 104.16 L 56.57 111.66  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 93.53 73.69 L 108.32 84.9 L 123.15 84.58 L 108.48 73.58  z",
      }),
      l.default.createElement("path", {
        fill: "#828280",
        d: "M 62.41 127.1 L 78.11 139.83 L 67.47 119.18  z",
      }),
      l.default.createElement("path", {
        fill: "#d8d8dd",
        d: "M 110.24 129.91 L 116.8 136.54 L 129.32 128.43 L 123.02 121.74  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 93.53 73.69 L 80.55 76.02 L 67.08 84.51 L 80.02 82.25  z",
      }),
      l.default.createElement("path", {
        fill: "#78787a",
        d: "M 110.24 129.91 L 89.61 138.22 L 94.46 145.51 L 116.8 136.54  z",
      }),
      l.default.createElement("path", {
        fill: "#f9f9fe",
        d: "M 108.32 84.9 L 112.87 100.77 L 129.09 100.1 L 123.15 84.58  z",
      }),
      l.default.createElement("path", {
        fill: "#f3f3f9",
        d: "M 123.02 121.74 L 129.09 100.1 L 112.87 100.77  z",
      }),
      l.default.createElement("path", {
        fill: "#750032",
        d: "M 121.5 156.95 L 116.8 136.54 L 110.24 129.91  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 67.08 84.51 L 61.04 104.16 L 73.43 104.05 L 80.02 82.25  z",
      }),
      l.default.createElement("path", {
        fill: "#858583",
        d: "M 78.11 139.83 L 89.61 138.22 L 79.96 119.22 L 67.47 119.18  z",
      }),
      l.default.createElement("path", {
        fill: "#fffffd",
        d: "M 93.53 73.69 L 80.02 82.25 L 96.25 94.76 L 108.32 84.9  z",
      }),
      l.default.createElement("path", {
        fill: "#d1d1d5",
        d: "M 110.24 129.91 L 123.02 121.74 L 112.87 100.77 L 100.8 110.78  z",
      }),
      l.default.createElement("path", {
        fill: "#750032",
        d: "M 116.44 150.41 L 121.5 156.95 L 110.24 129.91  z",
      }),
      l.default.createElement("path", {
        fill: "#eeeee9",
        d: "M 67.47 119.18 L 79.96 119.22 L 73.43 104.05 L 61.04 104.16  z",
      }),
      l.default.createElement("path", {
        fill: "#332200",
        d: "M 132.25 105.95 L 123.02 121.74 L 145.32 116.11  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 96.25 94.76 L 80.02 82.25 L 73.43 104.05  z",
      }),
      l.default.createElement("path", {
        fill: "#750032",
        d: "M 116.44 150.41 L 110.24 129.91 L 89.61 138.22 L 95.59 158.76  z",
      }),
      l.default.createElement("path", {
        fill: "#7d5300",
        d: "M 132.25 105.95 L 112.87 100.77 L 123.02 121.74  z",
      }),
      l.default.createElement("path", {
        fill: "#838383",
        d: "M 110.24 129.91 L 100.8 110.78 L 79.96 119.22 L 89.61 138.22  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 108.32 84.9 L 96.25 94.76 L 100.8 110.78 L 112.87 100.77  z",
      }),
      l.default.createElement("path", {
        fill: "#f0a000",
        d: "M 132.25 105.95 L 143.38 94.58 L 112.87 100.77  z",
      }),
      l.default.createElement("path", {
        fill: "#efefea",
        d: "M 100.8 110.78 L 96.25 94.76 L 73.43 104.05 L 79.96 119.22  z",
      }),
      l.default.createElement("path", {
        fill: "#586300",
        d: "M 207.26 302.74 L 266.84 272.95 L 252.8 271.39 L 192.8 300.75  z",
      }),
      l.default.createElement("path", {
        fill: "#647100",
        d: "M 273.8 288.3 L 266.84 272.95 L 207.26 302.74 L 214.56 319.34  z",
      }),
      l.default.createElement("path", {
        fill: "#750032",
        d: "M 149.11 268.55 L 154.05 273.81 L 121.5 156.95 L 116.44 150.41  z",
      }),
      l.default.createElement("path", {
        fill: "#41001b",
        d: "M 309.25 236.52 L 324.33 145 L 305.62 131.97 L 292.66 223.95  z",
      }),
      l.default.createElement("path", {
        fill: "#63002b",
        d: "M 309.25 236.52 L 319.12 227.83 L 335.25 135.97 L 324.33 145  z",
      }),
      l.default.createElement("path", {
        fill: "#750032",
        d: "M 149.11 268.55 L 116.44 150.41 L 95.59 158.76 L 126.62 277.24  z",
      }),
      l.default.createElement("path", {
        fill: "#302000",
        d: "M 274.29 103.43 L 262.24 92 L 132.25 105.95 L 145.32 116.11  z",
      }),
      l.default.createElement("path", {
        fill: "#f0a000",
        d: "M 132.25 105.95 L 262.24 92 L 272.39 79.71 L 143.38 94.58  z",
      }),
      l.default.createElement("path", {
        fill: "#637000",
        d: "M 207.26 302.74 L 192.8 300.75 L 170.56 315.58 L 185.06 317.77  z",
      }),
      l.default.createElement("path", {
        fill: "#647100",
        d: "M 214.56 319.34 L 207.26 302.74 L 185.06 317.77 L 192.43 334.75  z",
      }),
      l.default.createElement("path", {
        fill: "#750032",
        d: "M 149.11 268.55 L 161.16 297.3 L 154.05 273.81  z",
      }),
      l.default.createElement("path", {
        fill: "#740032",
        d: "M 149.11 268.55 L 156.99 291.93 L 161.16 297.3  z",
      }),
      l.default.createElement("path", {
        fill: "#750032",
        d: "M 149.11 268.55 L 126.62 277.24 L 131.93 301.55 L 156.99 291.93  z",
      }),
      l.default.createElement("path", {
        fill: "#63002b",
        d: "M 324.33 145 L 335.25 135.97 L 325.57 127.82  z",
      }),
      l.default.createElement("path", {
        fill: "#370017",
        d: "M 324.33 145 L 325.57 127.82 L 306.43 114.69 L 305.62 131.97  z",
      }),
      l.default.createElement("path", {
        fill: "#e7e7e1",
        d: "M 131.93 301.55 L 117.86 312.32 L 124.41 308.54 L 139.43 297.14  z",
      }),
      l.default.createElement("path", {
        fill: "#5e0029",
        d: "M 335.25 135.97 L 339.08 117.79 L 325.57 127.82  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 200.56 331.58 L 193.79 313.85 L 188.34 307.13 L 195.22 325.15  z",
      }),
      l.default.createElement("path", {
        fill: "#9271da",
        d: "M 209.63 282.91 L 188.34 307.13 L 193.79 313.85  z",
      }),
      l.default.createElement("path", {
        fill: "#f0a000",
        d: "M 272.39 79.71 L 262.24 92 L 291.75 73.03  z",
      }),
      l.default.createElement("path", {
        fill: "#513600",
        d: "M 274.29 103.43 L 300.77 96.96 L 286.37 97.65  z",
      }),
      l.default.createElement("path", {
        fill: "#c7c7c3",
        d: "M 156.99 291.93 L 139.43 297.14 L 154.97 296.82 L 170.81 292.03  z",
      }),
      l.default.createElement("path", {
        fill: "#2b1d00",
        d: "M 274.29 103.43 L 286.37 97.65 L 262.24 92  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 200.56 331.58 L 195.22 325.15 L 188.9 352.23 L 194.96 355.84  z",
      }),
      l.default.createElement("path", {
        fill: "#a781ee",
        d: "M 170.81 292.03 L 188.34 307.13 L 199.43 286.03 L 181.6 271.03  z",
      }),
      l.default.createElement("path", {
        fill: "#fbfbf5",
        d: "M 124.41 308.54 L 117.86 312.32 L 111.54 336.38 L 117.99 333.14  z",
      }),
      l.default.createElement("path", {
        fill: "#333333",
        d: "M 155.44 377.01 L 150.98 372.88 L 137 371.75 L 141.65 375.86  z",
      }),
      l.default.createElement("path", {
        fill: "#f0a000",
        d: "M 286.37 97.65 L 291.75 73.03 L 262.24 92  z",
      }),
      l.default.createElement("path", {
        fill: "#b8b8b4",
        d: "M 118.29 354.29 L 125.58 351.19 L 117.99 333.14 L 111.54 336.38  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 154.97 296.82 L 172.69 312.3 L 188.34 307.13 L 170.81 292.03  z",
      }),
      l.default.createElement("path", {
        fill: "#828280",
        d: "M 118.29 354.29 L 137 371.75 L 125.58 351.19  z",
      }),
      l.default.createElement("path", {
        fill: "#d8d8dd",
        d: "M 174.86 363.91 L 181.23 367.34 L 194.96 355.84 L 188.9 352.23  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 154.97 296.82 L 139.43 297.14 L 124.41 308.54 L 139.89 308.33  z",
      }),
      l.default.createElement("path", {
        fill: "#78787a",
        d: "M 174.86 363.91 L 150.98 372.88 L 155.44 377.01 L 181.23 367.34  z",
      }),
      l.default.createElement("path", {
        fill: "#f9f9fe",
        d: "M 172.69 312.3 L 178.13 331.29 L 195.22 325.15 L 188.34 307.13  z",
      }),
      l.default.createElement("path", {
        fill: "#f3f3f9",
        d: "M 188.9 352.23 L 195.22 325.15 L 178.13 331.29  z",
      }),
      l.default.createElement("path", {
        fill: "#9472dd",
        d: "M 209.63 282.91 L 300.46 152.09 L 312.84 129.12 L 291.4 153.34 L 199.43 286.03 L 188.34 307.13  z",
      }),
      l.default.createElement("path", {
        fill: "#af87ff",
        d: "M 181.6 271.03 L 199.43 286.03 L 291.4 153.34 L 272.15 139.57  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 124.41 308.54 L 117.99 333.14 L 132.91 335.69 L 139.89 308.33  z",
      }),
      l.default.createElement("path", {
        fill: "#858583",
        d: "M 137 371.75 L 150.98 372.88 L 140.64 353.96 L 125.58 351.19  z",
      }),
      l.default.createElement("path", {
        fill: "#fffffd",
        d: "M 154.97 296.82 L 139.89 308.33 L 159.36 325.64 L 172.69 312.3  z",
      }),
      l.default.createElement("path", {
        fill: "#434344",
        d: "M 343.16 125.69 L 329.41 127.09 L 312.84 129.12 L 325.57 127.82  z",
      }),
      l.default.createElement("path", {
        fill: "#686867",
        d: "M 286.37 97.65 L 293.44 115.7 L 289.55 105.32 L 282.36 86.97  z",
      }),
      l.default.createElement("path", {
        fill: "#d1d1d5",
        d: "M 174.86 363.91 L 188.9 352.23 L 178.13 331.29 L 164.8 344.83  z",
      }),
      l.default.createElement("path", {
        fill: "#eeeee9",
        d: "M 125.58 351.19 L 140.64 353.96 L 132.91 335.69 L 117.99 333.14  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 159.36 325.64 L 139.89 308.33 L 132.91 335.69  z",
      }),
      l.default.createElement("path", {
        fill: "#838383",
        d: "M 174.86 363.91 L 164.8 344.83 L 140.64 353.96 L 150.98 372.88  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 172.69 312.3 L 159.36 325.64 L 164.8 344.83 L 178.13 331.29  z",
      }),
      l.default.createElement("path", {
        fill: "#3e3e3e",
        d: "M 289.55 105.32 L 293.44 115.7 L 312.84 129.12 L 311.23 120.22  z",
      }),
      l.default.createElement("path", {
        fill: "#efefea",
        d: "M 164.8 344.83 L 159.36 325.64 L 132.91 335.69 L 140.64 353.96  z",
      }),
      l.default.createElement("path", {
        fill: "#aa83f3",
        d: "M 272.15 139.57 L 291.4 153.34 L 311.23 120.22 L 289.55 105.32  z",
      }),
      l.default.createElement("path", {
        fill: "#7c7c7f",
        d: "M 343.16 125.69 L 356.69 116.35 L 329.41 127.09  z",
      }),
      l.default.createElement("path", {
        fill: "#9573de",
        d: "M 291.4 153.34 L 312.84 129.12 L 311.23 120.22  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 373.43 83.98 L 370.91 72.96 L 366.15 98.51 L 369.2 106.91  z",
      }),
      l.default.createElement("path", {
        fill: "#fbfbf5",
        d: "M 298.36 54.21 L 287.38 64.26 L 282.36 86.97 L 293.33 77.39  z",
      }),
      l.default.createElement("path", {
        fill: "#333333",
        d: "M 329.41 127.09 L 328.09 118.03 L 311.23 120.22 L 312.84 129.12  z",
      }),
      l.default.createElement("path", {
        fill: "#b8b8b4",
        d: "M 289.55 105.32 L 301.83 95.26 L 293.33 77.39 L 282.36 86.97  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 330.7 41.19 L 351.25 54.2 L 363.58 54.5 L 343.33 41.77  z",
      }),
      l.default.createElement("path", {
        fill: "#828280",
        d: "M 289.55 105.32 L 311.23 120.22 L 301.83 95.26  z",
      }),
      l.default.createElement("path", {
        fill: "#d8d8dd",
        d: "M 353.37 108.05 L 356.69 116.35 L 369.2 106.91 L 366.15 98.51  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 330.7 41.19 L 312.64 44.32 L 298.36 54.21 L 316.4 51.17  z",
      }),
      l.default.createElement("path", {
        fill: "#78787a",
        d: "M 353.37 108.05 L 328.09 118.03 L 329.41 127.09 L 356.69 116.35  z",
      }),
      l.default.createElement("path", {
        fill: "#f9f9fe",
        d: "M 351.25 54.2 L 357.49 73.1 L 370.91 72.96 L 363.58 54.5  z",
      }),
      l.default.createElement("path", {
        fill: "#f3f3f9",
        d: "M 366.15 98.51 L 370.91 72.96 L 357.49 73.1  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 298.36 54.21 L 293.33 77.39 L 310.98 76.93 L 316.4 51.17  z",
      }),
      l.default.createElement("path", {
        fill: "#858583",
        d: "M 311.23 120.22 L 328.09 118.03 L 319.66 95.01 L 301.83 95.26  z",
      }),
      l.default.createElement("path", {
        fill: "#fffffd",
        d: "M 330.7 41.19 L 316.4 51.17 L 339 65.73 L 351.25 54.2  z",
      }),
      l.default.createElement("path", {
        fill: "#d1d1d5",
        d: "M 353.37 108.05 L 366.15 98.51 L 357.49 73.1 L 345.25 84.84  z",
      }),
      l.default.createElement("path", {
        fill: "#eeeee9",
        d: "M 301.83 95.26 L 319.66 95.01 L 310.98 76.93 L 293.33 77.39  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 339 65.73 L 316.4 51.17 L 310.98 76.93  z",
      }),
      l.default.createElement("path", {
        fill: "#838383",
        d: "M 353.37 108.05 L 345.25 84.84 L 319.66 95.01 L 328.09 118.03  z",
      }),
      l.default.createElement("path", {
        fill: "#ffffff",
        d: "M 351.25 54.2 L 339 65.73 L 345.25 84.84 L 357.49 73.1  z",
      }),
      l.default.createElement("path", {
        fill: "#efefea",
        d: "M 345.25 84.84 L 339 65.73 L 310.98 76.93 L 319.66 95.01  z",
      }),
    ),
  );
var W1 = P((o) => ({
    root: { zIndex: o.zIndex.drawer + 1 },
    title: { marginLeft: o.spacing(2), flexGrow: 1 },
    open: { marginRight: o.spacing(5) },
  })
  ),
  I2 = (
    {
      oneDesign: o,
      pathToRoot: L = ".",
      forDebugger: t = !1,
      title: a,
      about: f,
    },
  ) => {
    let d = W1();
    return h.default.createElement(
      "div",
      { id: "appbar", className: d.root },
      h.default.createElement(
        V,
        { position: "static" },
        h.default.createElement(
          K,
          null,
          h.default.createElement(t1, null),
          h.default.createElement(
            g,
            { variant: "h5", className: d.title },
            "vZome ",
            h.default.createElement(H, {
              component: "span",
              fontStyle: "oblique",
            }, "Online"),
          ),
          !o &&
            h.default.createElement(l1, {
              pathToRoot: L,
              forDebugger: t,
              className: d.open,
            }),
          h.default.createElement(r1, { title: a, about: f }),
        ),
      ),
    );
  };
export { I2 as b, T1 as a };
