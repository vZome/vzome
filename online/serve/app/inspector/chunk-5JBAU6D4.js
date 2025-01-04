var g = Object.create;
var e = Object.defineProperty;
var h = Object.getOwnPropertyDescriptor;
var i = Object.getOwnPropertyNames;
var j = Object.getPrototypeOf, k = Object.prototype.hasOwnProperty;
var f = (a) => e(a, "__esModule", { value: !0 });
var m = (a, b) => () => (b || a((b = { exports: {} }).exports, b), b.exports),
  n = (a, b) => {
    f(a);
    for (var c in b) e(a, c, { get: b[c], enumerable: !0 });
  },
  l = (a, b, c) => {
    if (b && typeof b == "object" || typeof b == "function") {
      for (let d of i(b)) {
        !k.call(a, d) && d !== "default" && e(a, d, {
          get: () => b[d],
          enumerable: !(c = h(b, d)) || c.enumerable,
        });
      }
    }
    return a;
  },
  o = (a) =>
    l(
      f(e(
        a != null ? g(j(a)) : {},
        "default",
        a && a.__esModule && "default" in a
          ? { get: () => a.default, enumerable: !0 }
          : { value: a, enumerable: !0 },
      )),
      a,
    );
export { m as a, n as b, o as c };
