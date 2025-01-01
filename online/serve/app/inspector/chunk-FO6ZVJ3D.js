import { a as Q } from "./chunk-5JBAU6D4.js";
var U = Q((V, M) => {
  var C = function (s) {
    "use strict";
    var R = Object.prototype,
      p = R.hasOwnProperty,
      v,
      G = typeof Symbol == "function" ? Symbol : {},
      m = G.iterator || "@@iterator",
      B = G.asyncIterator || "@@asyncIterator",
      _ = G.toStringTag || "@@toStringTag";
    function h(r, t, e) {
      return Object.defineProperty(r, t, {
        value: e,
        enumerable: !0,
        configurable: !0,
        writable: !0,
      }),
        r[t];
    }
    try {
      h({}, "");
    } catch {
      h = function (t, e, o) {
        return t[e] = o;
      };
    }
    function Y(r, t, e, o) {
      var n = t && t.prototype instanceof k ? t : k,
        i = Object.create(n.prototype),
        a = new I(o || []);
      return i._invoke = J(r, e, a), i;
    }
    s.wrap = Y;
    function O(r, t, e) {
      try {
        return { type: "normal", arg: r.call(t, e) };
      } catch (o) {
        return { type: "throw", arg: o };
      }
    }
    var q = "suspendedStart",
      H = "suspendedYield",
      D = "executing",
      L = "completed",
      l = {};
    function k() {}
    function b() {}
    function y() {}
    var T = {};
    h(T, m, function () {
      return this;
    });
    var N = Object.getPrototypeOf, S = N && N(N(P([])));
    S && S !== R && p.call(S, m) && (T = S);
    var g = y.prototype = k.prototype = Object.create(T);
    b.prototype = y,
      h(g, "constructor", y),
      h(y, "constructor", b),
      b.displayName = h(y, _, "GeneratorFunction");
    function W(r) {
      ["next", "throw", "return"].forEach(function (t) {
        h(r, t, function (e) {
          return this._invoke(t, e);
        });
      });
    }
    s.isGeneratorFunction = function (r) {
      var t = typeof r == "function" && r.constructor;
      return t
        ? t === b || (t.displayName || t.name) === "GeneratorFunction"
        : !1;
    },
      s.mark = function (r) {
        return Object.setPrototypeOf
          ? Object.setPrototypeOf(r, y)
          : (r.__proto__ = y, h(r, _, "GeneratorFunction")),
          r.prototype = Object.create(g),
          r;
      },
      s.awrap = function (r) {
        return { __await: r };
      };
    function E(r, t) {
      function e(i, a, u, c) {
        var f = O(r[i], r, a);
        if (f.type === "throw") c(f.arg);
        else {
          var A = f.arg, w = A.value;
          return w && typeof w == "object" && p.call(w, "__await")
            ? t.resolve(w.__await).then(function (d) {
              e("next", d, u, c);
            }, function (d) {
              e("throw", d, u, c);
            })
            : t.resolve(w).then(function (d) {
              A.value = d, u(A);
            }, function (d) {
              return e("throw", d, u, c);
            });
        }
      }
      var o;
      function n(i, a) {
        function u() {
          return new t(function (c, f) {
            e(i, a, c, f);
          });
        }
        return o = o ? o.then(u, u) : u();
      }
      this._invoke = n;
    }
    W(E.prototype),
      h(E.prototype, B, function () {
        return this;
      }),
      s.AsyncIterator = E,
      s.async = function (r, t, e, o, n) {
        n === void 0 && (n = Promise);
        var i = new E(Y(r, t, e, o), n);
        return s.isGeneratorFunction(t) ? i : i.next().then(function (a) {
          return a.done ? a.value : i.next();
        });
      };
    function J(r, t, e) {
      var o = q;
      return function (i, a) {
        if (o === D) throw new Error("Generator is already running");
        if (o === L) {
          if (i === "throw") throw a;
          return z();
        }
        for (e.method = i, e.arg = a;;) {
          var u = e.delegate;
          if (u) {
            var c = $(u, e);
            if (c) {
              if (c === l) continue;
              return c;
            }
          }
          if (e.method === "next") e.sent = e._sent = e.arg;
          else if (e.method === "throw") {
            if (o === q) throw o = L, e.arg;
            e.dispatchException(e.arg);
          } else e.method === "return" && e.abrupt("return", e.arg);
          o = D;
          var f = O(r, t, e);
          if (f.type === "normal") {
            if (o = e.done ? L : H, f.arg === l) continue;
            return { value: f.arg, done: e.done };
          } else {f.type === "throw" &&
              (o = L, e.method = "throw", e.arg = f.arg);}
        }
      };
    }
    function $(r, t) {
      var e = r.iterator[t.method];
      if (e === v) {
        if (t.delegate = null, t.method === "throw") {
          if (
            r.iterator.return &&
            (t.method = "return", t.arg = v, $(r, t), t.method === "throw")
          ) return l;
          t.method = "throw",
            t.arg = new TypeError(
              "The iterator does not provide a 'throw' method",
            );
        }
        return l;
      }
      var o = O(e, r.iterator, t.arg);
      if (o.type === "throw") {
        return t.method = "throw", t.arg = o.arg, t.delegate = null, l;
      }
      var n = o.arg;
      if (!n) {
        return t.method = "throw",
          t.arg = new TypeError("iterator result is not an object"),
          t.delegate = null,
          l;
      }
      if (n.done) {
        t[r.resultName] = n.value,
          t.next = r.nextLoc,
          t.method !== "return" && (t.method = "next", t.arg = v);
      } else return n;
      return t.delegate = null, l;
    }
    W(g),
      h(g, _, "Generator"),
      h(g, m, function () {
        return this;
      }),
      h(g, "toString", function () {
        return "[object Generator]";
      });
    function K(r) {
      var t = { tryLoc: r[0] };
      1 in r && (t.catchLoc = r[1]),
        2 in r && (t.finallyLoc = r[2], t.afterLoc = r[3]),
        this.tryEntries.push(t);
    }
    function j(r) {
      var t = r.completion || {};
      t.type = "normal", delete t.arg, r.completion = t;
    }
    function I(r) {
      this.tryEntries = [{ tryLoc: "root" }],
        r.forEach(K, this),
        this.reset(!0);
    }
    s.keys = function (r) {
      var t = [];
      for (var e in r) t.push(e);
      return t.reverse(), function o() {
        for (; t.length;) {
          var n = t.pop();
          if (n in r) return o.value = n, o.done = !1, o;
        }
        return o.done = !0, o;
      };
    };
    function P(r) {
      if (r) {
        var t = r[m];
        if (t) return t.call(r);
        if (typeof r.next == "function") return r;
        if (!isNaN(r.length)) {
          var e = -1,
            o = function n() {
              for (; ++e < r.length;) {
                if (p.call(r, e)) return n.value = r[e], n.done = !1, n;
              }
              return n.value = v, n.done = !0, n;
            };
          return o.next = o;
        }
      }
      return { next: z };
    }
    s.values = P;
    function z() {
      return { value: v, done: !0 };
    }
    return I.prototype = {
      constructor: I,
      reset: function (r) {
        if (
          this.prev = 0,
            this.next = 0,
            this.sent = this._sent = v,
            this.done = !1,
            this.delegate = null,
            this.method = "next",
            this.arg = v,
            this.tryEntries.forEach(j),
            !r
        ) {
          for (var t in this) {
            t.charAt(0) === "t" && p.call(this, t) && !isNaN(+t.slice(1)) &&
              (this[t] = v);
          }
        }
      },
      stop: function () {
        this.done = !0;
        var r = this.tryEntries[0], t = r.completion;
        if (t.type === "throw") throw t.arg;
        return this.rval;
      },
      dispatchException: function (r) {
        if (this.done) throw r;
        var t = this;
        function e(c, f) {
          return i.type = "throw",
            i.arg = r,
            t.next = c,
            f && (t.method = "next", t.arg = v),
            !!f;
        }
        for (var o = this.tryEntries.length - 1; o >= 0; --o) {
          var n = this.tryEntries[o], i = n.completion;
          if (n.tryLoc === "root") return e("end");
          if (n.tryLoc <= this.prev) {
            var a = p.call(n, "catchLoc"), u = p.call(n, "finallyLoc");
            if (a && u) {
              if (this.prev < n.catchLoc) return e(n.catchLoc, !0);
              if (this.prev < n.finallyLoc) return e(n.finallyLoc);
            } else if (a) {
              if (this.prev < n.catchLoc) return e(n.catchLoc, !0);
            } else if (u) {
              if (this.prev < n.finallyLoc) return e(n.finallyLoc);
            } else throw new Error("try statement without catch or finally");
          }
        }
      },
      abrupt: function (r, t) {
        for (var e = this.tryEntries.length - 1; e >= 0; --e) {
          var o = this.tryEntries[e];
          if (
            o.tryLoc <= this.prev && p.call(o, "finallyLoc") &&
            this.prev < o.finallyLoc
          ) {
            var n = o;
            break;
          }
        }
        n && (r === "break" || r === "continue") && n.tryLoc <= t &&
          t <= n.finallyLoc && (n = null);
        var i = n ? n.completion : {};
        return i.type = r,
          i.arg = t,
          n
            ? (this.method = "next", this.next = n.finallyLoc, l)
            : this.complete(i);
      },
      complete: function (r, t) {
        if (r.type === "throw") throw r.arg;
        return r.type === "break" || r.type === "continue"
          ? this.next = r.arg
          : r.type === "return"
          ? (this.rval = this.arg = r.arg,
            this.method = "return",
            this.next = "end")
          : r.type === "normal" && t && (this.next = t),
          l;
      },
      finish: function (r) {
        for (var t = this.tryEntries.length - 1; t >= 0; --t) {
          var e = this.tryEntries[t];
          if (e.finallyLoc === r) {
            return this.complete(e.completion, e.afterLoc), j(e), l;
          }
        }
      },
      catch: function (r) {
        for (var t = this.tryEntries.length - 1; t >= 0; --t) {
          var e = this.tryEntries[t];
          if (e.tryLoc === r) {
            var o = e.completion;
            if (o.type === "throw") {
              var n = o.arg;
              j(e);
            }
            return n;
          }
        }
        throw new Error("illegal catch attempt");
      },
      delegateYield: function (r, t, e) {
        return this.delegate = { iterator: P(r), resultName: t, nextLoc: e },
          this.method === "next" && (this.arg = v),
          l;
      },
    },
      s;
  }(typeof M == "object" ? M.exports : {});
  try {
    regeneratorRuntime = C;
  } catch {
    typeof globalThis == "object"
      ? globalThis.regeneratorRuntime = C
      : Function("r", "regeneratorRuntime = r")(C);
  }
});
export { U as a };
