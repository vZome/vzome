# Plan: Porting `BigRationalImpl` Dual-Representation to `JavaBigRational`

## The Core Idea

`BigRationalImpl.java` uses a **dual representation**: it stores numerator/denominator as plain `long` values when they fit, falling back to `BigInteger` only when an operation overflows. Each arithmetic method tries fast `long` math first, checks for overflow, and promotes to `BigInteger` only when needed.

We'll do the same in JavaScript: use `Number` when values stay within safe integer range, promote to `BigInt` only when they don't.

## Key Difference: Safe Range

| | Java | JavaScript |
|---|---|---|
| Fast type | `long` (64-bit signed) | `Number` (53-bit safe integer) |
| Max value | 2^63 − 1 ≈ 9.2 × 10^18 | 2^53 − 1 = 9,007,199,254,740,991 |
| Slow type | `BigInteger` | `BigInt` |

The safe range in JS is ~1000× smaller than Java's. This means overflow to `BigInt` will happen somewhat more often, but for most vZome models (where coefficients are small integers from icosahedral/golden-ratio arithmetic), values should stay well within 2^53.

## Steps

### 1. Dual-representation constructor

Store `this.num` and `this.denom` as **either both `Number` or both `BigInt`**. Add a boolean flag `this.big` (analogous to Java's `isBig()`/`notBig()`).

```js
const MAX_SAFE = Number.MAX_SAFE_INTEGER; // 2^53 - 1

class JavaBigRational {
  constructor(num, denom) {
    // Normalize to Number if possible, BigInt otherwise
    // ... reduce/simplify ...
    // if both fit in safe integer range → store as Number, big=false
    // otherwise → store as BigInt, big=true
  }
}
```

The constructor must handle inputs that are `Number`, `BigInt`, or mixed. The Java class has multiple constructor overloads for `(long,long)`, `(BigInteger,BigInteger)`, `(BigInteger,long)`, etc. — in JS we can use type-checking in one constructor.

### 2. Number-safe GCD

Port the binary GCD algorithm from `BigRationalImpl.Gcd.calculateGcd` to work with JS `Number`. The current `gcd()` in `online/src/worker/fields/common.js` always uses `BigInt`. We need a `Number`-based version:

```js
function gcdNumber(a, b) {
  // Euclidean or binary GCD on regular JS Numbers
  a = Math.abs(a);
  b = Math.abs(b);
  while (b !== 0) { const t = b; b = a % t; a = t; }
  return a;
}
```

The Java code also has a **lookup table cache** for small GCD values (128×128 = 16KB). This is worth considering but may be less impactful in JS — benchmark first. The simple Euclidean GCD on Numbers should already be dramatically faster than BigInt GCD.

### 3. Number-safe `reduce()` (simplify)

Port the `reduce(long[])` pattern: given Number numerator and denominator, compute GCD, divide, ensure denominator is positive, and confirm the result still fits in safe integer range. If it doesn't fit, promote to BigInt and use the existing BigInt `simplify()`.

```js
function reduceNumber(num, denom) {
  // handle sign, compute gcdNumber, divide, check range
  // return { num, denom, big: false } or fall back to BigInt
}
```

### 4. Overflow-checked arithmetic helpers

Port `addAndCheck` and `multiplyAndCheck` from Java. In JavaScript, instead of bit-twiddling for overflow detection (which Java does on longs), we can use a simpler approach:

```js
function safeAdd(a, b) {
  const r = a + b;
  // Check if result is safe and not losing precision
  return (r >= -MAX_SAFE && r <= MAX_SAFE) ? r : null; // null = overflow
}

function safeMul(a, b) {
  const r = a * b;
  return (r >= -MAX_SAFE && r <= MAX_SAFE) ? r : null;
}
```

This is safe because JS `Number` arithmetic on integers within ±2^53 is exact; the result of adding/multiplying two safe integers may exceed the safe range, but the check catches it.

### 5. Port `plus()` with fast path

```js
plus(that) {
  if (this.isZero()) return that;
  if (that.isZero()) return this;
  if (!this.big && !that.big) {
    // Try Number arithmetic
    if (this.denom === that.denom) {
      const n = safeAdd(this.num, that.num);
      if (n !== null) return new JavaBigRational(n, this.denom); // Number path
    } else {
      const n1 = safeMul(this.num, that.denom);
      const n2 = safeMul(that.num, this.denom);
      const n = (n1 !== null && n2 !== null) ? safeAdd(n1, n2) : null;
      const d = safeMul(this.denom, that.denom);
      if (n !== null && d !== null) return new JavaBigRational(n, d); // Number path
    }
  }
  // Fallback: promote to BigInt
  const n = toBigInt(this.num) * toBigInt(that.denom) + toBigInt(that.num) * toBigInt(this.denom);
  const d = toBigInt(this.denom) * toBigInt(that.denom);
  return new JavaBigRational(n, d);
}
```

### 6. Port `times()` with fast path

Same pattern — try Number multiply, check overflow, fall back to BigInt.

### 7. Port `negate()`, `reciprocal()`, `timesInt()` with fast path

These are simpler since negate just flips sign (no overflow risk on Number since we constrain to `MAX_SAFE`), and reciprocal just swaps num/denom.

### 8. Cache computed properties

Following the Java pattern, cache `isZero`, `isOne`, `isWhole`, `signum` at construction time rather than computing them on each call. These are queried very frequently (every `plus`/`times` checks `isZero()` and `isOne()`).

### 9. Update callers in `common.js`

The `simplify3()` and `simplify4()` functions in `online/src/worker/fields/common.js` are used by the field arithmetic modules and also always use BigInt. These are **separate from `JavaBigRational`** — they work at the "trailing divisor" level where algebraic numbers are represented as arrays of BigInts. Optimizing those is a separate (and worthwhile) effort but should be a follow-up, not part of this initial port.

The `createBigRational`, `parseBigRational`, etc. methods in `JavaAlgebraicNumberFactory` currently do `BigInt(num)` unconditionally — these need updating to pass through `Number` values when possible.

### 10. Testing strategy

- **Correctness**: Add test cases comparing `JavaBigRational` results against known values, especially at boundary conditions (values near `MAX_SAFE_INTEGER`, mixed big/small operands, zero, one, negative).
- **Regression**: Load several `.vZome` files and verify the geometry matches (same vertex positions to exact precision). The `toTrailingDivisor()` output format should be identical before and after.
- **Performance**: Time the loading of a complex `.vZome` file (e.g., a large icosahedral model) before and after to measure the speedup.

## Risk Assessment

| Risk | Mitigation |
|------|------------|
| Subtle precision bugs from Number/BigInt boundary | Extensive boundary tests; `Number.isSafeInteger` assertions in debug mode |
| Breaking the `simplify()` / `gcd()` contract in `common.js` | Phase 1 only touches `JavaBigRational`; `common.js` functions are left unchanged initially |
| `getNumerator()` / `getDenominator()` return type changes | These currently return `BigInt`; callers in `common.js` field math expect `BigInt`. The accessors should always return `BigInt` for external consumption (convert on the way out if stored as Number) |
| Constructor receives mixed types | Type-check inputs (`typeof n === 'bigint'`) and normalize |

## Expected Impact

For typical vZome models, the vast majority of rational numbers have small numerators and denominators (single/double digit). These will stay entirely in the `Number` path, avoiding all `BigInt` overhead. The BigInt promotion path handles edge cases (large polygon fields, extreme coordinate values) correctly but rarely fires.

The Java experience shows this dual-representation strategy is very effective — it was a deliberate performance optimization by David Hall. The same wins should translate to JavaScript, where the `BigInt` vs `Number` performance gap is arguably even larger than Java's `BigInteger` vs `long` gap.
