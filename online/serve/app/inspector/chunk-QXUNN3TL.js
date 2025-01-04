var dr = "114";
var ve = () => {
  let e = import("/modules/vzome-worker-static.js").then((i) => {
      let u = new Blob([`import "${i.WORKER_ENTRY_FILE_URL}";`], {
          type: "text/javascript",
        }),
        c = new Worker(URL.createObjectURL(u), { type: "module" });
      return c.onmessage = o, c;
    }),
    r = (i) => {
      navigator.userAgent.indexOf("Firefox") > -1
        ? (console.log("The worker is not available in Firefox"),
          a("Module workers are not yet supported in Firefox.  Please try another browser."))
        : e.then((u) => {
          u.postMessage( { ...i, isInspector: true } );
        }).catch((u) => {
          console.log(u),
            console.log("The worker is not available"),
            a("The worker is not available.  Module workers are supported in newer versions of most browsers.  Please update your browser.");
        });
    },
    t = [],
    n = (i) => t.push(i),
    o = (i) => t.forEach((u) => u.onWorkerMessage(i.data)),
    a = (i) => t.forEach((u) => u.onWorkerError(i));
  return { sendToWorker: r, subscribe: n };
};
function A(e) {
  for (
    var r = arguments.length, t = Array(r > 1 ? r - 1 : 0), n = 1; n < r; n++
  ) t[n - 1] = arguments[n];
  if (!1) { var o, a; }
  throw Error(
    "[Immer] minified error nr: " + e + (t.length
      ? " " + t.map(function (i) {
        return "'" + i + "'";
      }).join(",")
      : "") + ". Find the full error at: https://bit.ly/3cXEKWf",
  );
}
function j(e) {
  return !!e && !!e[m];
}
function x(e) {
  return !!e && (function (r) {
    if (!r || typeof r != "object") return !1;
    var t = Object.getPrototypeOf(r);
    if (t === null) return !0;
    var n = Object.hasOwnProperty.call(t, "constructor") && t.constructor;
    return n === Object ||
      typeof n == "function" && Function.toString.call(n) === $e;
  }(e) || Array.isArray(e) || !!e[Ne] || !!e.constructor[Ne] || K(e) || q(e));
}
function M(e, r, t) {
  t === void 0 && (t = !1),
    I(e) === 0
      ? (t ? Object.keys : _)(e).forEach(function (n) {
        t && typeof n == "symbol" || r(n, e[n], e);
      })
      : e.forEach(function (n, o) {
        return r(o, n, e);
      });
}
function I(e) {
  var r = e[m];
  return r
    ? r.i > 3 ? r.i - 4 : r.i
    : Array.isArray(e)
    ? 1
    : K(e)
    ? 2
    : q(e)
    ? 3
    : 0;
}
function T(e, r) {
  return I(e) === 2 ? e.has(r) : Object.prototype.hasOwnProperty.call(e, r);
}
function Ue(e, r) {
  return I(e) === 2 ? e.get(r) : e[r];
}
function ye(e, r, t) {
  var n = I(e);
  n === 2 ? e.set(r, t) : n === 3 ? (e.delete(r), e.add(t)) : e[r] = t;
}
function me(e, r) {
  return e === r ? e !== 0 || 1 / e == 1 / r : e != e && r != r;
}
function K(e) {
  return Ye && e instanceof Map;
}
function q(e) {
  return Xe && e instanceof Set;
}
function P(e) {
  return e.o || e.t;
}
function G(e) {
  if (Array.isArray(e)) return Array.prototype.slice.call(e);
  var r = Pe(e);
  delete r[m];
  for (var t = _(r), n = 0; n < t.length; n++) {
    var o = t[n], a = r[o];
    a.writable === !1 && (a.writable = !0, a.configurable = !0),
      (a.get || a.set) &&
      (r[o] = {
        configurable: !0,
        writable: !0,
        enumerable: a.enumerable,
        value: e[o],
      });
  }
  return Object.create(Object.getPrototypeOf(e), r);
}
function Y(e, r) {
  return r === void 0 && (r = !1),
    X(e) || j(e) || !x(e) || (I(e) > 1 && (e.set =
      e.add =
      e.clear =
      e.delete =
        Be),
      Object.freeze(e),
      r && M(e, function (t, n) {
        return Y(n, !0);
      }, !0)),
    e;
}
function Be() {
  A(2);
}
function X(e) {
  return e == null || typeof e != "object" || Object.isFrozen(e);
}
function D(e) {
  var r = ne[e];
  return r || A(18, e), r;
}
function Ke(e, r) {
  ne[e] || (ne[e] = r);
}
function $() {
  return R;
}
function H(e, r) {
  r && (D("Patches"), e.u = [], e.s = [], e.v = r);
}
function z(e) {
  J(e), e.p.forEach(qe), e.p = null;
}
function J(e) {
  e === R && (R = e.l);
}
function ge(e) {
  return R = { p: [], l: R, h: e, m: !0, _: 0 };
}
function qe(e) {
  var r = e[m];
  r.i === 0 || r.i === 1 ? r.j() : r.O = !0;
}
function Q(e, r) {
  r._ = r.p.length;
  var t = r.p[0], n = e !== void 0 && e !== t;
  return r.h.g || D("ES5").S(r, e, n),
    n
      ? (t[m].P && (z(r), A(4)),
        x(e) && (e = W(r, e), r.l || F(r, e)),
        r.u && D("Patches").M(t[m].t, e, r.u, r.s))
      : e = W(r, t, []),
    z(r),
    r.u && r.v(r.u, r.s),
    e !== De ? e : void 0;
}
function W(e, r, t) {
  if (X(r)) return r;
  var n = r[m];
  if (!n) {
    return M(r, function (a, i) {
      return be(e, n, r, a, i, t);
    }, !0),
      r;
  }
  if (n.A !== e) return r;
  if (!n.P) return F(e, n.t, !0), n.t;
  if (!n.I) {
    n.I = !0, n.A._--;
    var o = n.i === 4 || n.i === 5 ? n.o = G(n.k) : n.o;
    M(n.i === 3 ? new Set(o) : o, function (a, i) {
      return be(e, n, o, a, i, t);
    }),
      F(e, o, !1),
      t && e.u && D("Patches").R(n, t, e.u, e.s);
  }
  return n.o;
}
function be(e, r, t, n, o, a) {
  if (j(o)) {
    var i = W(e, o, a && r && r.i !== 3 && !T(r.D, n) ? a.concat(n) : void 0);
    if (ye(t, n, i), !j(i)) return;
    e.m = !1;
  }
  if (x(o) && !X(o)) {
    if (!e.h.F && e._ < 1) return;
    W(e, o), r && r.A.l || F(e, o);
  }
}
function F(e, r, t) {
  t === void 0 && (t = !1), e.h.F && e.m && Y(r, t);
}
function Z(e, r) {
  var t = e[m];
  return (t ? P(t) : e)[r];
}
function we(e, r) {
  if (r in e) {
    for (var t = Object.getPrototypeOf(e); t;) {
      var n = Object.getOwnPropertyDescriptor(t, r);
      if (n) return n;
      t = Object.getPrototypeOf(t);
    }
  }
}
function k(e) {
  e.P || (e.P = !0, e.l && k(e.l));
}
function ee(e) {
  e.o || (e.o = G(e.t));
}
function re(e, r, t) {
  var n = K(r)
    ? D("MapSet").N(r, t)
    : q(r)
    ? D("MapSet").T(r, t)
    : e.g
    ? function (o, a) {
      var i = Array.isArray(o),
        u = {
          i: i ? 1 : 0,
          A: a ? a.A : $(),
          P: !1,
          I: !1,
          D: {},
          l: a,
          t: o,
          k: null,
          o: null,
          j: null,
          C: !1,
        },
        c = u,
        s = V;
      i && (c = [u], s = L);
      var f = Proxy.revocable(c, s), h = f.revoke, l = f.proxy;
      return u.k = l, u.j = h, l;
    }(r, t)
    : D("ES5").J(r, t);
  return (t ? t.A : $()).p.push(n), n;
}
function Ge(e) {
  return j(e) || A(22, e),
    function r(t) {
      if (!x(t)) return t;
      var n, o = t[m], a = I(t);
      if (o) {
        if (!o.P && (o.i < 4 || !D("ES5").K(o))) return o.t;
        o.I = !0, n = Ee(t, a), o.I = !1;
      } else n = Ee(t, a);
      return M(n, function (i, u) {
        o && Ue(o.t, i) === u || ye(n, i, r(u));
      }),
        a === 3 ? new Set(n) : n;
    }(e);
}
function Ee(e, r) {
  switch (r) {
    case 2:
      return new Map(e);
    case 3:
      return Array.from(e);
  }
  return G(e);
}
function Oe() {
  function e(i, u) {
    var c = a[i];
    return c
      ? c.enumerable = u
      : a[i] = c = {
        configurable: !0,
        enumerable: u,
        get: function () {
          var s = this[m];
          return V.get(s, i);
        },
        set: function (s) {
          var f = this[m];
          V.set(f, i, s);
        },
      },
      c;
  }
  function r(i) {
    for (var u = i.length - 1; u >= 0; u--) {
      var c = i[u][m];
      if (!c.P) {
        switch (c.i) {
          case 5:
            n(c) && k(c);
            break;
          case 4:
            t(c) && k(c);
        }
      }
    }
  }
  function t(i) {
    for (var u = i.t, c = i.k, s = _(c), f = s.length - 1; f >= 0; f--) {
      var h = s[f];
      if (h !== m) {
        var l = u[h];
        if (l === void 0 && !T(u, h)) return !0;
        var v = c[h], p = v && v[m];
        if (p ? p.t !== l : !me(v, l)) return !0;
      }
    }
    var d = !!u[m];
    return s.length !== _(u).length + (d ? 0 : 1);
  }
  function n(i) {
    var u = i.k;
    if (u.length !== i.t.length) return !0;
    var c = Object.getOwnPropertyDescriptor(u, u.length - 1);
    if (c && !c.get) return !0;
    for (var s = 0; s < u.length; s++) if (!u.hasOwnProperty(s)) return !0;
    return !1;
  }
  function o(i) {
    i.O && A(3, JSON.stringify(P(i)));
  }
  var a = {};
  Ke("ES5", {
    J: function (i, u) {
      var c = Array.isArray(i),
        s = function (h, l) {
          if (h) {
            for (var v = Array(l.length), p = 0; p < l.length; p++) {
              Object.defineProperty(v, "" + p, e(p, !0));
            }
            return v;
          }
          var d = Pe(l);
          delete d[m];
          for (var b = _(d), y = 0; y < b.length; y++) {
            var g = b[y];
            d[g] = e(g, h || !!d[g].enumerable);
          }
          return Object.create(Object.getPrototypeOf(l), d);
        }(c, i),
        f = {
          i: c ? 5 : 4,
          A: u ? u.A : $(),
          P: !1,
          I: !1,
          D: {},
          l: u,
          t: i,
          k: s,
          o: null,
          O: !1,
          C: !1,
        };
      return Object.defineProperty(s, m, { value: f, writable: !0 }), s;
    },
    S: function (i, u, c) {
      c ? j(u) && u[m].A === i && r(i.p) : (i.u && function s(f) {
        if (f && typeof f == "object") {
          var h = f[m];
          if (h) {
            var l = h.t, v = h.k, p = h.D, d = h.i;
            if (d === 4) {
              M(v, function (O) {
                O !== m && (l[O] !== void 0 || T(l, O)
                  ? p[O] || s(v[O])
                  : (p[O] = !0, k(h)));
              }),
                M(l, function (O) {
                  v[O] !== void 0 || T(v, O) || (p[O] = !1, k(h));
                });
            } else if (d === 5) {
              if (n(h) && (k(h), p.length = !0), v.length < l.length) {
                for (var b = v.length; b < l.length; b++) {
                  p[b] = !1;
                }
              } else for (var y = l.length; y < v.length; y++) p[y] = !0;
              for (var g = Math.min(v.length, l.length), w = 0; w < g; w++) {
                v.hasOwnProperty(w) || (p[w] = !0), p[w] === void 0 && s(v[w]);
              }
            }
          }
        }
      }(i.p[0]),
        r(i.p));
    },
    K: function (i) {
      return i.i === 4 ? t(i) : n(i);
    },
  });
}
var Se,
  R,
  te = typeof Symbol != "undefined" && typeof Symbol("x") == "symbol",
  Ye = typeof Map != "undefined",
  Xe = typeof Set != "undefined",
  Ae = typeof Proxy != "undefined" && Proxy.revocable !== void 0 &&
    typeof Reflect != "undefined",
  De = te ? Symbol.for("immer-nothing") : ((Se = {})["immer-nothing"] = !0, Se),
  Ne = te ? Symbol.for("immer-draftable") : "__$immer_draftable",
  m = te ? Symbol.for("immer-state") : "__$immer_state",
  vr = typeof Symbol != "undefined" && Symbol.iterator || "@@iterator";
var $e = "" + Object.prototype.constructor,
  _ = typeof Reflect != "undefined" && Reflect.ownKeys
    ? Reflect.ownKeys
    : Object.getOwnPropertySymbols !== void 0
    ? function (e) {
      return Object.getOwnPropertyNames(e).concat(
        Object.getOwnPropertySymbols(e),
      );
    }
    : Object.getOwnPropertyNames,
  Pe = Object.getOwnPropertyDescriptors || function (e) {
    var r = {};
    return _(e).forEach(function (t) {
      r[t] = Object.getOwnPropertyDescriptor(e, t);
    }),
      r;
  },
  ne = {},
  V = {
    get: function (e, r) {
      if (r === m) return e;
      var t = P(e);
      if (!T(t, r)) {
        return function (o, a, i) {
          var u, c = we(a, i);
          return c
            ? "value" in c
              ? c.value
              : (u = c.get) === null || u === void 0
              ? void 0
              : u.call(o.k)
            : void 0;
        }(e, t, r);
      }
      var n = t[r];
      return e.I || !x(n)
        ? n
        : n === Z(e.t, r)
        ? (ee(e), e.o[r] = re(e.A.h, n, e))
        : n;
    },
    has: function (e, r) {
      return r in P(e);
    },
    ownKeys: function (e) {
      return Reflect.ownKeys(P(e));
    },
    set: function (e, r, t) {
      var n = we(P(e), r);
      if (n == null ? void 0 : n.set) return n.set.call(e.k, t), !0;
      if (!e.P) {
        var o = Z(P(e), r), a = o == null ? void 0 : o[m];
        if (a && a.t === t) return e.o[r] = t, e.D[r] = !1, !0;
        if (me(t, o) && (t !== void 0 || T(e.t, r))) return !0;
        ee(e), k(e);
      }
      return e.o[r] === t && typeof t != "number" &&
          (t !== void 0 || r in e.o) || (e.o[r] = t, e.D[r] = !0, !0);
    },
    deleteProperty: function (e, r) {
      return Z(e.t, r) !== void 0 || r in e.t
        ? (e.D[r] = !1, ee(e), k(e))
        : delete e.D[r],
        e.o && delete e.o[r],
        !0;
    },
    getOwnPropertyDescriptor: function (e, r) {
      var t = P(e), n = Reflect.getOwnPropertyDescriptor(t, r);
      return n &&
        {
          writable: !0,
          configurable: e.i !== 1 || r !== "length",
          enumerable: n.enumerable,
          value: t[r],
        };
    },
    defineProperty: function () {
      A(11);
    },
    getPrototypeOf: function (e) {
      return Object.getPrototypeOf(e.t);
    },
    setPrototypeOf: function () {
      A(12);
    },
  },
  L = {};
M(V, function (e, r) {
  L[e] = function () {
    return arguments[0] = arguments[0][0], r.apply(this, arguments);
  };
}),
  L.deleteProperty = function (e, r) {
    return L.set.call(this, e, r, void 0);
  },
  L.set = function (e, r, t) {
    return V.set.call(this, e[0], r, t, e[0]);
  };
var He = function () {
    function e(t) {
      var n = this;
      this.g = Ae,
        this.F = !0,
        this.produce = function (o, a, i) {
          if (typeof o == "function" && typeof a != "function") {
            var u = a;
            a = o;
            var c = n;
            return function (d) {
              var b = this;
              d === void 0 && (d = u);
              for (
                var y = arguments.length, g = Array(y > 1 ? y - 1 : 0), w = 1;
                w < y;
                w++
              ) g[w - 1] = arguments[w];
              return c.produce(d, function (O) {
                var he;
                return (he = a).call.apply(he, [b, O].concat(g));
              });
            };
          }
          var s;
          if (
            typeof a != "function" && A(6),
              i !== void 0 && typeof i != "function" && A(7),
              x(o)
          ) {
            var f = ge(n), h = re(n, o, void 0), l = !0;
            try {
              s = a(h), l = !1;
            } finally {
              l ? z(f) : J(f);
            }
            return typeof Promise != "undefined" && s instanceof Promise
              ? s.then(function (d) {
                return H(f, i), Q(d, f);
              }, function (d) {
                throw z(f), d;
              })
              : (H(f, i), Q(s, f));
          }
          if (!o || typeof o != "object") {
            if (
              (s = a(o)) === void 0 && (s = o),
                s === De && (s = void 0),
                n.F && Y(s, !0),
                i
            ) {
              var v = [], p = [];
              D("Patches").M(o, s, v, p), i(v, p);
            }
            return s;
          }
          A(21, o);
        },
        this.produceWithPatches = function (o, a) {
          if (typeof o == "function") {
            return function (s) {
              for (
                var f = arguments.length, h = Array(f > 1 ? f - 1 : 0), l = 1;
                l < f;
                l++
              ) h[l - 1] = arguments[l];
              return n.produceWithPatches(s, function (v) {
                return o.apply(void 0, [v].concat(h));
              });
            };
          }
          var i,
            u,
            c = n.produce(o, a, function (s, f) {
              i = s, u = f;
            });
          return typeof Promise != "undefined" && c instanceof Promise
            ? c.then(function (s) {
              return [s, i, u];
            })
            : [c, i, u];
        },
        typeof (t == null ? void 0 : t.useProxies) == "boolean" &&
        this.setUseProxies(t.useProxies),
        typeof (t == null ? void 0 : t.autoFreeze) == "boolean" &&
        this.setAutoFreeze(t.autoFreeze);
    }
    var r = e.prototype;
    return r.createDraft = function (t) {
      x(t) || A(8), j(t) && (t = Ge(t));
      var n = ge(this), o = re(this, t, void 0);
      return o[m].C = !0, J(n), o;
    },
      r.finishDraft = function (t, n) {
        var o = t && t[m], a = o.A;
        return H(a, n), Q(void 0, a);
      },
      r.setAutoFreeze = function (t) {
        this.F = t;
      },
      r.setUseProxies = function (t) {
        t && !Ae && A(20), this.g = t;
      },
      r.applyPatches = function (t, n) {
        var o;
        for (o = n.length - 1; o >= 0; o--) {
          var a = n[o];
          if (a.path.length === 0 && a.op === "replace") {
            t = a.value;
            break;
          }
        }
        o > -1 && (n = n.slice(o + 1));
        var i = D("Patches").$;
        return j(t) ? i(t, n) : this.produce(t, function (u) {
          return i(u, n);
        });
      },
      e;
  }(),
  S = new He(),
  yr = S.produce,
  mr = S.produceWithPatches.bind(S),
  gr = S.setAutoFreeze.bind(S),
  br = S.setUseProxies.bind(S),
  wr = S.applyPatches.bind(S),
  Er = S.createDraft.bind(S),
  Or = S.finishDraft.bind(S);
function oe(e, r, t) {
  return r in e
    ? Object.defineProperty(e, r, {
      value: t,
      enumerable: !0,
      configurable: !0,
      writable: !0,
    })
    : e[r] = t,
    e;
}
function ke(e, r) {
  var t = Object.keys(e);
  if (Object.getOwnPropertySymbols) {
    var n = Object.getOwnPropertySymbols(e);
    r && (n = n.filter(function (o) {
      return Object.getOwnPropertyDescriptor(e, o).enumerable;
    })), t.push.apply(t, n);
  }
  return t;
}
function U(e) {
  for (var r = 1; r < arguments.length; r++) {
    var t = arguments[r] != null ? arguments[r] : {};
    r % 2
      ? ke(Object(t), !0).forEach(function (n) {
        oe(e, n, t[n]);
      })
      : Object.getOwnPropertyDescriptors
      ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(t))
      : ke(Object(t)).forEach(function (n) {
        Object.defineProperty(e, n, Object.getOwnPropertyDescriptor(t, n));
      });
  }
  return e;
}
function E(e) {
  return "Minified Redux error #" + e +
    "; visit https://redux.js.org/Errors?code=" + e +
    " for the full message or use the non-minified dev environment for full errors. ";
}
var je = function () {
    return typeof Symbol == "function" && Symbol.observable || "@@observable";
  }(),
  ae = function () {
    return Math.random().toString(36).substring(7).split("").join(".");
  },
  B = {
    INIT: "@@redux/INIT" + ae(),
    REPLACE: "@@redux/REPLACE" + ae(),
    PROBE_UNKNOWN_ACTION: function () {
      return "@@redux/PROBE_UNKNOWN_ACTION" + ae();
    },
  };
function Je(e) {
  if (typeof e != "object" || e === null) return !1;
  for (var r = e; Object.getPrototypeOf(r) !== null;) {
    r = Object.getPrototypeOf(r);
  }
  return Object.getPrototypeOf(e) === r;
}
function ie(e, r, t) {
  var n;
  if (
    typeof r == "function" && typeof t == "function" ||
    typeof t == "function" && typeof arguments[3] == "function"
  ) throw new Error(E(0));
  if (
    typeof r == "function" && typeof t == "undefined" && (t = r, r = void 0),
      typeof t != "undefined"
  ) {
    if (typeof t != "function") throw new Error(E(1));
    return t(ie)(e, r);
  }
  if (typeof e != "function") throw new Error(E(2));
  var o = e, a = r, i = [], u = i, c = !1;
  function s() {
    u === i && (u = i.slice());
  }
  function f() {
    if (c) throw new Error(E(3));
    return a;
  }
  function h(d) {
    if (typeof d != "function") throw new Error(E(4));
    if (c) throw new Error(E(5));
    var b = !0;
    return s(), u.push(d), function () {
      if (!!b) {
        if (c) throw new Error(E(6));
        b = !1, s();
        var g = u.indexOf(d);
        u.splice(g, 1), i = null;
      }
    };
  }
  function l(d) {
    if (!Je(d)) throw new Error(E(7));
    if (typeof d.type == "undefined") throw new Error(E(8));
    if (c) throw new Error(E(9));
    try {
      c = !0, a = o(a, d);
    } finally {
      c = !1;
    }
    for (var b = i = u, y = 0; y < b.length; y++) {
      var g = b[y];
      g();
    }
    return d;
  }
  function v(d) {
    if (typeof d != "function") throw new Error(E(10));
    o = d, l({ type: B.REPLACE });
  }
  function p() {
    var d, b = h;
    return d = {
      subscribe: function (g) {
        if (typeof g != "object" || g === null) throw new Error(E(11));
        function w() {
          g.next && g.next(f());
        }
        w();
        var O = b(w);
        return { unsubscribe: O };
      },
    },
      d[je] = function () {
        return this;
      },
      d;
  }
  return l({ type: B.INIT }),
    n = { dispatch: l, subscribe: h, getState: f, replaceReducer: v },
    n[je] = p,
    n;
}
function Qe(e) {
  Object.keys(e).forEach(function (r) {
    var t = e[r], n = t(void 0, { type: B.INIT });
    if (typeof n == "undefined") throw new Error(E(12));
    if (typeof t(void 0, { type: B.PROBE_UNKNOWN_ACTION() }) == "undefined") {
      throw new Error(E(13));
    }
  });
}
function xe(e) {
  for (var r = Object.keys(e), t = {}, n = 0; n < r.length; n++) {
    var o = r[n];
    typeof e[o] == "function" && (t[o] = e[o]);
  }
  var a = Object.keys(t), i, u;
  try {
    Qe(t);
  } catch (c) {
    u = c;
  }
  return function (s, f) {
    if (s === void 0 && (s = {}), u) throw u;
    if (!1) { var h; }
    for (var l = !1, v = {}, p = 0; p < a.length; p++) {
      var d = a[p], b = t[d], y = s[d], g = b(y, f);
      if (typeof g == "undefined") {
        var w = f && f.type;
        throw new Error(E(14));
      }
      v[d] = g, l = l || g !== y;
    }
    return l = l || a.length !== Object.keys(s).length, l ? v : s;
  };
}
function C() {
  for (var e = arguments.length, r = new Array(e), t = 0; t < e; t++) {
    r[t] = arguments[t];
  }
  return r.length === 0
    ? function (n) {
      return n;
    }
    : r.length === 1
    ? r[0]
    : r.reduce(function (n, o) {
      return function () {
        return n(o.apply(void 0, arguments));
      };
    });
}
function Me() {
  for (var e = arguments.length, r = new Array(e), t = 0; t < e; t++) {
    r[t] = arguments[t];
  }
  return function (n) {
    return function () {
      var o = n.apply(void 0, arguments),
        a = function () {
          throw new Error(E(15));
        },
        i = {
          getState: o.getState,
          dispatch: function () {
            return a.apply(void 0, arguments);
          },
        },
        u = r.map(function (c) {
          return c(i);
        });
      return a = C.apply(void 0, u)(o.dispatch),
        U(U({}, o), {}, { dispatch: a });
    };
  };
}
function Ie(e) {
  var r = function (n) {
    var o = n.dispatch, a = n.getState;
    return function (i) {
      return function (u) {
        return typeof u == "function" ? u(o, a, e) : i(u);
      };
    };
  };
  return r;
}
var Te = Ie();
Te.withExtraArgument = Ie;
var ue = Te;
var Ze = function () {
  var e = function (r, t) {
    return e = Object.setPrototypeOf ||
      { __proto__: [] } instanceof Array && function (n, o) {
          n.__proto__ = o;
        } ||
      function (n, o) {
        for (var a in o) {Object.prototype.hasOwnProperty.call(o, a) &&
            (n[a] = o[a]);}
      },
      e(r, t);
  };
  return function (r, t) {
    if (typeof t != "function" && t !== null) {
      throw new TypeError(
        "Class extends value " + String(t) + " is not a constructor or null",
      );
    }
    e(r, t);
    function n() {
      this.constructor = r;
    }
    r.prototype = t === null
      ? Object.create(t)
      : (n.prototype = t.prototype, new n());
  };
}();
var ce = function (e, r) {
    for (var t = 0, n = r.length, o = e.length; t < n; t++, o++) e[o] = r[t];
    return e;
  },
  er = Object.defineProperty;
var _e = Object.getOwnPropertySymbols,
  rr = Object.prototype.hasOwnProperty,
  tr = Object.prototype.propertyIsEnumerable,
  Ce = function (e, r, t) {
    return r in e
      ? er(e, r, { enumerable: !0, configurable: !0, writable: !0, value: t })
      : e[r] = t;
  },
  se = function (e, r) {
    for (var t in r || (r = {})) rr.call(r, t) && Ce(e, t, r[t]);
    if (_e) {
      for (var n = 0, o = _e(r); n < o.length; n++) {
        var t = o[n];
        tr.call(r, t) && Ce(e, t, r[t]);
      }
    }
    return e;
  };
var nr =
    typeof window != "undefined" && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
      ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
      : function () {
        if (arguments.length !== 0) {
          return typeof arguments[0] == "object"
            ? C
            : C.apply(null, arguments);
        }
      },
  Tr = typeof window != "undefined" && window.__REDUX_DEVTOOLS_EXTENSION__
    ? window.__REDUX_DEVTOOLS_EXTENSION__
    : function () {
      return function (e) {
        return e;
      };
    };
function or(e) {
  if (typeof e != "object" || e === null) return !1;
  var r = Object.getPrototypeOf(e);
  if (r === null) return !0;
  for (var t = r; Object.getPrototypeOf(t) !== null;) {
    t = Object.getPrototypeOf(t);
  }
  return r === t;
}
var ar = function (e) {
  Ze(r, e);
  function r() {
    for (var t = [], n = 0; n < arguments.length; n++) t[n] = arguments[n];
    var o = e.apply(this, t) || this;
    return Object.setPrototypeOf(o, r.prototype), o;
  }
  return Object.defineProperty(r, Symbol.species, {
    get: function () {
      return r;
    },
    enumerable: !1,
    configurable: !0,
  }),
    r.prototype.concat = function () {
      for (var t = [], n = 0; n < arguments.length; n++) t[n] = arguments[n];
      return e.prototype.concat.apply(this, t);
    },
    r.prototype.prepend = function () {
      for (var t = [], n = 0; n < arguments.length; n++) t[n] = arguments[n];
      return t.length === 1 && Array.isArray(t[0])
        ? new (r.bind.apply(r, ce([void 0], t[0].concat(this))))()
        : new (r.bind.apply(r, ce([void 0], t.concat(this))))();
    },
    r;
}(Array);
function ir(e) {
  return typeof e == "boolean";
}
function ur() {
  return function (r) {
    return cr(r);
  };
}
function cr(e) {
  e === void 0 && (e = {});
  var r = e.thunk,
    t = r === void 0 ? !0 : r,
    n = e.immutableCheck,
    o = n === void 0 ? !0 : n,
    a = e.serializableCheck,
    i = a === void 0 ? !0 : a,
    u = new ar();
  if (
    t && (ir(t) ? u.push(ue) : u.push(ue.withExtraArgument(t.extraArgument))),
      !1
  ) {
    if (o) { var c; }
    if (i) { var s; }
  }
  return u;
}
var fe = !0;
function Re(e) {
  var r = ur(),
    t = e || {},
    n = t.reducer,
    o = n === void 0 ? void 0 : n,
    a = t.middleware,
    i = a === void 0 ? r() : a,
    u = t.devTools,
    c = u === void 0 ? !0 : u,
    s = t.preloadedState,
    f = s === void 0 ? void 0 : s,
    h = t.enhancers,
    l = h === void 0 ? void 0 : h,
    v;
  if (typeof o == "function") v = o;
  else if (or(o)) v = xe(o);
  else {throw new Error(
      '"reducer" is a required argument, and must be a function or an object of functions that can be passed to combineReducers',
    );}
  var p = i;
  if (typeof p == "function" && (p = p(r), !fe && !Array.isArray(p))) {
    throw new Error(
      "when using a middleware builder function, an array of middleware must be returned",
    );
  }
  if (
    !fe && p.some(function (w) {
      return typeof w != "function";
    })
  ) {
    throw new Error(
      "each middleware provided to configureStore must be a function",
    );
  }
  var d = Me.apply(void 0, p), b = C;
  c && (b = nr(se({ trace: !fe }, typeof c == "object" && c)));
  var y = [d];
  Array.isArray(l) ? y = ce([d], l) : typeof l == "function" && (y = l(y));
  var g = b.apply(void 0, y);
  return ie(v, f, g);
}
function le(e, r) {
  function t() {
    for (var n = [], o = 0; o < arguments.length; o++) n[o] = arguments[o];
    if (r) {
      var a = r.apply(void 0, n);
      if (!a) throw new Error("prepareAction did not return an object");
      return se(
        se({ type: e, payload: a.payload }, "meta" in a && { meta: a.meta }),
        "error" in a && { error: a.error },
      );
    }
    return { type: e, payload: n[0] };
  }
  return t.toString = function () {
    return "" + e;
  },
    t.type = e,
    t.match = function (n) {
      return n.type === e;
    },
    t;
}
var Cr = function () {
    function e(r, t) {
      this.payload = r, this.meta = t;
    }
    return e;
  }(),
  Rr = function () {
    function e(r, t) {
      this.payload = r, this.meta = t;
    }
    return e;
  }();
var sr = "task",
  Ve = "listener",
  Le = "completed",
  de = "cancelled",
  Vr = "task-" + de,
  Lr = "task-" + Le,
  zr = Ve + "-" + de,
  Wr = Ve + "-" + Le,
  Fr = function () {
    function e(r) {
      this.code = r,
        this.name = "TaskAbortError",
        this.message = sr + " " + de + " (reason: " + r + ")";
    }
    return e;
  }();
var pe = "listenerMiddleware";
var Ur = le(pe + "/add"), Br = le(pe + "/removeAll"), Kr = le(pe + "/remove");
Oe();
var ze = {
  scene: {
    camera: {
      near: .271828,
      far: 217,
      width: 48,
      distance: 108,
      up: [0, 1, 0],
      lookAt: [0, 0, 0],
      lookDir: [0, 0, -1],
      perspective: !0,
    },
    lighting: {
      backgroundColor: "#BBDAED",
      ambientColor: "#555555",
      directionalLights: [{ direction: [1, -1, -.3], color: "#FDFDFD" }, {
        direction: [-1, 0, -.2],
        color: "#B5B5B5",
      }, { direction: [0, 0, -1], color: "#303030" }],
    },
  },
};
var We = (e) => ({ type: "PERSPECTIVE_SET", payload: e }),
  Jr = (e, r) => async (t) => {
    let n = (o) => new Promise((a) => setTimeout(a, 100));
    t(We(!0)), await n(10), r(), await n(10), t(We(e));
  },
  N = (e, r) => ({ type: e, payload: r, meta: "WORKER" }),
  Qr = (e) => N("SCENE_SELECTED", e);
var Zr = (e) => N("EDIT_SELECTED", { after: e }),
  et = (e, r = { preview: !1, debug: !1, showScenes: !1 }) =>
    N("URL_PROVIDED", { url: e, config: r }),
  rt = (e, r = !1) => N("FILE_PROVIDED", { file: e, debug: r }),
  tt = () => N("NEW_DESIGN_STARTED", { field: "golden" }),
  nt = (e = "", r, t) =>
    N("ACTION_TRIGGERED", { controllerPath: e, action: r, parameters: t }),
  ot = (e = "", r, t = r, n = !1) =>
    N("PROPERTY_REQUESTED", {
      controllerPath: e,
      propName: r,
      changeName: t,
      isList: n,
    }),
  at = (e, r, t, n, o) =>
    N("STRUT_CREATION_TRIGGERED", {
      id: e,
      plane: r,
      zone: t,
      index: n,
      orientation: o,
    }),
  it = (e, r) => N("JOIN_BALLS_TRIGGERED", { id1: e, id2: r }),
  fr = (e, r) => e + ":" + r,
  lr = (e = ze, r) => {
    switch (r.type) {
      case "ALERT_RAISED":
        return console.log(`Alert from the worker: ${r.payload}`),
          { ...e, problem: r.payload, waiting: !1 };
      case "ALERT_DISMISSED":
        return { ...e, problem: "" };
      case "FETCH_STARTED": {
        if (e.waiting) return e;
        let { url: t, preview: n } = r.payload;
        return { ...e, waiting: !0, editing: !n };
      }
      case "TEXT_FETCHED":
        return { ...e, source: r.payload };
      case "DESIGN_XML_SAVED":
        return { ...e, source: { ...e.source, changedText: r.payload } };
      case "DESIGN_XML_PARSED": {
        let t = r.payload,
          n = {},
          o = (a) =>
            a.children && a.children.map((i) => {
              n[i.id] = i.attributes, o(i);
            });
        return t && (o(t), t = Fe(t)),
          { ...e, waiting: !1, xmlTree: t, attributes: n };
      }
      case "SCENES_DISCOVERED":
        return { ...e, scenes: r.payload };
      case "SCENE_RENDERED": {
        let { scene: t, edit: n } = r.payload, o = t.camera || e.scene.camera;
        return {
          ...e,
          edit: n,
          scene: { ...e.scene, ...t, camera: o },
          waiting: !1,
        };
      }
      case "SHAPE_DEFINED": {
        let t = r.payload, n = { ...e.scene.shapes, [t.id]: t };
        return { ...e, scene: { ...e.scene, shapes: n }, waiting: !1 };
      }
      case "INSTANCE_ADDED": {
        let t = r.payload,
          n = e.scene.shapes[t.shapeId],
          o = {
            ...e.scene.shapes,
            [n.id]: { ...n, instances: [...n.instances, t] },
          };
        return {
          ...e,
          scene: { ...e.scene, shapes: o },
          waiting: !1,
          lastInstance: t,
        };
      }
      case "SELECTION_TOGGLED": {
        let { shapeId: t, id: n, selected: o } = r.payload,
          a = e.scene.shapes[t],
          i = a.instances.map((c) => c.id !== n ? c : { ...c, selected: o }),
          u = { ...e.scene.shapes, [t]: { ...a, instances: i } };
        return { ...e, scene: { ...e.scene, shapes: u }, waiting: !1 };
      }
      case "CAMERA_DEFINED": {
        let t = r.payload;
        return { ...e, scene: { ...e.scene, camera: t } };
      }
      case "PERSPECTIVE_SET": {
        let t = r.payload;
        return {
          ...e,
          scene: {
            ...e.scene,
            camera: { ...e.scene.camera, ...e.scene.trackball, perspective: t },
          },
        };
      }
      case "TRACKBALL_MOVED": {
        let t = { ...e.scene.camera, ...r.payload };
        return { ...e, scene: { ...e.scene, liveCamera: t } };
      }
      case "CONTROLLER_CREATED":
        return { ...e, controller: { isReady: !0 } };
      case "CONTROLLER_PROPERTY_CHANGED": {
        let { controllerPath: t, name: n, value: o } = r.payload;
        return { ...e, controller: { ...e.controller, [fr(t, n)]: o } };
      }
      default:
        return e;
    }
  },
  Fe = (e) => {
    if (e.children && e.children.length > 1) {
      let r = [], t = null;
      for (let n of e.children) {
        if (n.tagName === "BeginBlock") t = [];
        else if (n.tagName === "EndBlock") {
          let o = {
            tagName: "ChangeSelection",
            id: n.id,
            children: t,
            attributes: {},
          };
          r.push(o), t = null;
        } else t ? t.push(n) : r.push(Fe(n));
      }
      return e.children = r, e;
    } else return e;
  },
  ut = (e) => {
    let r = (o) => (a) => {
      let i = (f) => a({ type: "ALERT_RAISED", payload: f }),
        u = (f) => a(f),
        c = e || ve();
      return c.subscribe({ onWorkerMessage: u, onWorkerError: i }), (f) => {
        f.meta && f.meta === "WORKER" ? c.sendToWorker(f) : a(f);
      };
    };
    return Re({
      reducer: lr,
      preloadedState: ze,
      middleware: (o) => o({ immutableCheck: !1 }).concat(r),
      devTools: !0,
    });
  };
export {
  at as o,
  dr as c,
  et as j,
  it as p,
  Jr as g,
  nt as m,
  oe as a,
  ot as n,
  Qr as h,
  rt as k,
  tt as l,
  U as b,
  ut as q,
  ve as d,
  We as f,
  ze as e,
  Zr as i,
};
