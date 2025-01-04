import { a as U, b as F } from "./chunk-HX64Q3ZC.js";
import { a as ne } from "./chunk-FO6ZVJ3D.js";
import {
  $ as O,
  aa as P,
  b as Z,
  c as G,
  f as C,
  g as a,
  h as p,
  i as h,
  j as y,
  k as s,
  l as E,
  o as W,
  p as A,
  s as B,
  t as N,
  u as V,
  v as j,
  w as M,
  x as z,
  y as _,
  Z as v,
  z as H,
} from "./chunk-K4NE5TDR.js";
import { a as b, b as ae } from "./chunk-FFFGVZ2M.js";
import { i as $ } from "./chunk-QXUNN3TL.js";
import { a as L, c as d } from "./chunk-5JBAU6D4.js";
var J = L((T) => {
  "use strict";
  var se = G(), de = Z();
  Object.defineProperty(T, "__esModule", { value: !0 });
  T.default = void 0;
  var pe = de(b()),
    me = se(A()),
    ce = (0, me.default)(
      pe.createElement("path", {
        d: "M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z",
      }),
      "ExpandMore",
    );
  T.default = ce;
});
var K = L((w) => {
  "use strict";
  var ue = G(), ge = Z();
  Object.defineProperty(w, "__esModule", { value: !0 });
  w.default = void 0;
  var fe = ge(b()),
    be = ue(A()),
    he = (0, be.default)(
      fe.createElement("path", {
        d: "M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z",
      }),
      "ChevronRight",
    );
  w.default = he;
});
var Ue = d(ne()), t = d(b()), oe = d(ae());
var m = d(b());
var e = d(b());
var Q = d(J()), X = d(K());
var x = C((r) => ({
    head: {
      backgroundColor: r.palette.common.black,
      color: r.palette.common.white,
    },
    body: { fontSize: 14 },
  })
  )(j),
  ye = C((r) => ({
    root: { "&:nth-of-type(odd)": { backgroundColor: r.palette.action.hover } },
  }))(_),
  ve = E((r) => ({
    root: {
      color: r.palette.text.secondary,
      "&:hover > $content": { backgroundColor: r.palette.action.hover },
      "&:focus > $content": {
        backgroundColor: `var(--tree-view-bg-color, ${r.palette.grey[400]})`,
        color: "var(--tree-view-color)",
      },
      "&$selected > $content": {
        backgroundColor: "#ab003c",
        color: r.palette.common.white,
      },
      "&:focus > $content $label, &:hover > $content $label, &$selected > $content $label":
        { backgroundColor: "transparent" },
    },
    content: {
      color: r.palette.text.secondary,
      borderTopRightRadius: r.spacing(2),
      borderBottomRightRadius: r.spacing(2),
      paddingRight: r.spacing(1),
      fontWeight: r.typography.fontWeightMedium,
      "$expanded > &": { fontWeight: r.typography.fontWeightRegular },
    },
    expanded: {},
    selected: {},
    label: { fontWeight: "inherit", color: "inherit" },
    labelRoot: {
      display: "flex",
      alignItems: "center",
      padding: r.spacing(.5, 0),
    },
    labelIcon: { marginRight: r.spacing(1) },
    labelText: { fontWeight: "inherit", flexGrow: 1 },
  })),
  Y = (r) => {
    let i = ve(),
      { labelText: u, labelInfo: l, color: n, bgColor: g, ...S } = r;
    return e.default.createElement(B, {
      label: e.default.createElement(
        "div",
        { className: i.labelRoot },
        e.default.createElement(
          a,
          { variant: "body2", className: i.labelText },
          u,
        ),
        e.default.createElement(a, { variant: "caption", color: "inherit" }, l),
      ),
      style: { "--tree-view-color": n, "--tree-view-bg-color": g },
      classes: {
        root: i.root,
        content: i.content,
        expanded: i.expanded,
        selected: i.selected,
        group: i.group,
        label: i.label,
      },
      ...S,
    });
  },
  R = (r) => {
    let i = h(),
      u = y((o) => o.xmlTree),
      l = y((o) => o.attributes),
      n = y((o) => o.edit),
      [g, S] = e.default.useState([]),
      ie = (o, c) => {
        S(c);
      },
      le = (o, c) => {
        i($(c));
      },
      D = (o) => {
        if (
          typeof o == "string" || o.tagName === "Boolean" ||
          o.tagName === "polygonVertex"
        ) return null;
        let c = o.children.length > 0 ? o.children : null,
          f = c && c.filter((I) => I.tagName !== "effects").map((I) => D(I));
        return f && f.length === 1 && f[0] === null && (f = null),
          e.default.createElement(Y, {
            key: o.id,
            nodeId: o.id,
            labelText: o.tagName,
          }, f);
      };
    return u
      ? e.default.createElement(
        s,
        {
          container: !0,
          direction: "column",
          style: { display: "table", height: "100%" },
        },
        e.default.createElement(
          s,
          {
            item: !0,
            id: "debugger-source",
            style: { display: "table-row", height: "100%" },
          },
          e.default.createElement(
            "div",
            { style: { width: "100%", height: "100%", position: "relative" } },
            e.default.createElement(
              W,
              {
                style: {
                  overflow: "auto",
                  position: "absolute",
                  top: 0,
                  bottom: 0,
                  left: 0,
                  right: 0,
                },
                expanded: g,
                selected: n,
                onNodeToggle: ie,
                onNodeSelect: le,
                defaultCollapseIcon: e.default.createElement(Q.default, null),
                defaultExpanded: [":"],
                defaultExpandIcon: e.default.createElement(X.default, null),
              },
              e.default.createElement(Y, {
                key: "--START--",
                nodeId: "--START--",
                labelText: "--START--",
              }),
              u.children.map((o) => D(o)),
            ),
          ),
        ),
        l &&
          e.default.createElement(
            s,
            { item: !0, style: { display: "table-row" } },
            e.default.createElement(
              M,
              { component: H },
              e.default.createElement(
                N,
                { size: "small", "aria-label": "command attributes" },
                e.default.createElement(
                  z,
                  null,
                  e.default.createElement(
                    _,
                    null,
                    e.default.createElement(x, null, "Name"),
                    e.default.createElement(x, null, "Value"),
                  ),
                ),
                e.default.createElement(
                  V,
                  null,
                  n && l[n] && Object.keys(l[n]).map((o) =>
                    e.default.createElement(
                      ye,
                      { key: o },
                      e.default.createElement(x, {
                        component: "th",
                        scope: "row",
                      }, o),
                      e.default.createElement(x, null, l[n][o]),
                    )
                  ),
                ),
              ),
            ),
          ),
      )
      : null;
  };
var ee = (r) => {
  let i = h(),
    u = (g) => {
      i({ type: "ACTION_TRIGGERED", payload: g });
    },
    l = 3,
    n = 12 - l;
  return m.default.createElement(
    "div",
    { style: { flex: "1", height: "100%" } },
    m.default.createElement(
      s,
      {
        id: "editor-main",
        container: !0,
        spacing: 0,
        style: { height: "100%" },
      },
      m.default.createElement(
        s,
        { id: "editor-drawer", item: !0, xs: l },
        m.default.createElement(R, null),
      ),
      m.default.createElement(
        s,
        { id: "editor-canvas", item: !0, xs: n },
        m.default.createElement(v, { config: { useSpinner: !0 } }),
      ),
    ),
  );
};
var re = new URLSearchParams(window.location.search),
  q = re.get("url"),
  k = !!q,
  Te = q && new URL(q, window.location).toString(),
  te = !k && re.get("debug") === "true",
  we = () => (P(Te || U("vZomeLogo"), { preview: k, debug: te }),
    t.default.createElement(
      t.default.Fragment,
      null,
      t.default.createElement(F, {
        oneDesign: k,
        forDebugger: te,
        title: "vZome Online",
        about: t.default.createElement(
          t.default.Fragment,
          null,
          t.default.createElement(
            a,
            { gutterBottom: !0 },
            "vZome Online is the world's first in-browser modeling tool for ",
            t.default.createElement(p, {
              target: "_blank",
              href: "https://zometool.com",
              rel: "noopener",
            }, "Zometool"),
            "... or it will be soon.",
          ),
          t.default.createElement(
            a,
            { gutterBottom: !0 },
            "Right now, you can load and view existing vZome designs, created using the ",
            t.default.createElement(p, {
              target: "_blank",
              rel: "noopener",
              href: "https://vzome.com/home/index/vzome-7/",
            }, "vZome desktop app"),
            ". The left-side drawer shows the complete edit history for the design, so you can explore how it was created. Click on the folder icon try out some of the built-in designs, or load one of your own!",
          ),
          t.default.createElement(
            a,
            { gutterBottom: !0 },
            "At the moment, you cannot modify designs or create new designs.  I'm working to complete those features (and all the other features of desktop vZome) as soon as possible. If you want to stay informed about my progress, follow vZome on ",
            t.default.createElement(p, {
              target: "_blank",
              rel: "noopener",
              href: "https://www.facebook.com/vZome",
            }, "Facebook"),
            " or ",
            t.default.createElement(p, {
              target: "_blank",
              rel: "noopener",
              href: "https://twitter.com/vZome",
            }, "Twitter"),
            ", or join the ",
            t.default.createElement(p, {
              target: "_blank",
              rel: "noopener",
              href: "https://discord.gg/vhyFsNAFPS",
            }, "Discord server"),
            ".",
          ),
          t.default.createElement(
            a,
            { gutterBottom: !0 },
            "If you have a vZome design that does not load here, ",
            t.default.createElement(p, {
              target: "_blank",
              rel: "noopener",
              href: "mailto:info@vzome.com",
            }, "send me the vZome file"),
            ", and I can prioritize the necessary fixes.",
          ),
        ),
      }),
      k
        ? t.default.createElement(v, { config: { useSpinner: !0 } })
        : t.default.createElement(ee, null),
    )),
  xe = () =>
    t.default.createElement(O, null, t.default.createElement(we, null));
(0, oe.render)(
  t.default.createElement(xe, null),
  document.getElementById("root"),
);
