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

## Design Review Notes

### Clarification: Step 4 overflow checks are precision guards, not storage guards

The `safeAdd` and `safeMul` helpers check whether the result exceeds `MAX_SAFE_INTEGER`, but there is no risk of `r` being "too large or too small" for the `Number` type to *store*. JS `Number` (IEEE 754 double) can represent values up to ~1.8 × 10^308. The issue is **precision**, not capacity: when two safe integers are multiplied and the mathematical result exceeds 2^53, the `Number` representation silently rounds to the nearest representable float. The range check detects when we've left the zone where integer arithmetic is exact, signaling that we should redo the operation with `BigInt`.

In short: `r` always gets a value — it's just potentially an *inexact* one. The existing comment in the code snippet ("Check if result is safe and not losing precision") is the correct framing.

### Analysis: Step 9 understates the scope of the `common.js` problem

Step 9 correctly identifies that `createBigRational`, `parseBigRational`, etc. coerce to `BigInt` at entry, and that updating those callers to pass `Number` when possible is worthwhile. However, this is a minor concern because the `JavaBigRational` constructor already **demotes back to Number** after GCD reduction (via `reduceBigInts`). The round-trip `Number → BigInt → Number` is wasteful but happens only at construction time.

The larger issue is that there are **two completely separate arithmetic systems** in the codebase, and the `JavaBigRational` optimization only benefits one of them:

1. **`JavaBigRational` arithmetic** — used by `JavaAlgebraicNumber.plus()`, which calls `JavaBigRational.plus()` on each coefficient. This benefits from the dual-representation optimization.

2. **Trailing-divisor BigInt arrays** — used by the field-specific code in `common.js` and field modules like `golden.js`. An algebraic number is represented as e.g. `[a0, a1, d]` (all BigInt), meaning $(a_0 + a_1\phi) / d$. Functions like `plus2`, `times` (in `golden.js`), `reciprocal`, and `simplify3`/`simplify4` operate entirely on raw BigInts and are **completely untouched** by the `JavaBigRational` optimization.

The two systems are bridged by `JavaAlgebraicNumber.toTrailingDivisor()`, which extracts BigInts from each `JavaBigRational` via `getNumerator()`/`getDenominator()` (these always return BigInt, re-promoting any Number values), then passes them to `createNumberFromPairs` in `common.js`.

Critically, `JavaAlgebraicNumber.times()` delegates to the field's `multiply` function, which calls the field-specific `times` (e.g., `golden.js`), which operates on trailing-divisor BigInt arrays via `simplify3`. **Multiplication — likely the most expensive algebraic operation — bypasses the `JavaBigRational` optimization entirely.**

This means the dual-representation work in `JavaBigRational` optimizes addition (and scalar operations like `negate`, `reciprocal`), but the `common.js` trailing-divisor path remains an independent hot path that needs its own Number/BigInt dual-representation treatment to get the full benefit. This should be a high-priority follow-up.

### Analysis: The two algebraic number classes serve completely separate fields

The two arithmetic systems are not two paths through the same code — they are used by **entirely different fields**, selected at setup time in `core.js`:

| Fields | AlgebraicNumber class | Multiplication path |
|--------|-----------------------|--------------------|
| golden, root2, root3, heptagon | `JsAlgebraicNumber` (core-java.js) | BigInt trailing-divisor arrays via field module `times()` → `simplify3()` in `common.js` |
| sqrtphi, snubCube, snubDodec, polygon, etc. | `JavaAlgebraicNumber` (jsweet2js.js) | `JavaBigRational.times()` + `JavaBigRational.plus()` via transpiled Java tensor multiply |

In `core.js`, `addNewField` creates a `JsAlgebraicField` wrapping a pure JS field delegate (e.g., `goldenField`). All number-creation methods produce `JsAlgebraicNumber` instances, whose `factors` are trailing-divisor BigInt arrays. `addLegacyField` creates a transpiled `AbstractAlgebraicField` subclass, passing a `JavaAlgebraicNumberFactory`, and all number-creation methods produce `JavaAlgebraicNumber` instances backed by `JavaBigRational[]`.

**The two classes never interoperate.** A field is entirely `JsAlgebraicField`-based or entirely `AbstractAlgebraicField`-based. They share only the `toTrailingDivisor()` serialization format.

Since the golden field is the default — and the vast majority of `.vZome` files use it — **the most common multiplication path is the BigInt trailing-divisor one, which gets zero benefit from the `JavaBigRational` dual-representation optimization.**

The `JavaBigRational` optimization covers only the less-common legacy fields (sqrtphi, snubCube, snubDodec, polygon, etc.).

### Optimizing the primary multiplication path (`common.js`)

To get the full performance benefit for golden, root2, root3, and heptagon fields, the trailing-divisor functions in `common.js` and field modules need their own Number/BigInt dual-representation treatment:

1. **Dual-representation trailing-divisor arrays.** Currently `[a0, a1, d]` is always all-BigInt. Instead, arrays could be all-Number when values fit in the safe integer range, promoted to all-BigInt when any value overflows — mirroring the `JavaBigRational` strategy.

2. **Number-path `simplify3` / `simplify4` / `gcd`.** These are the innermost hot functions. A Number variant would use `gcdNumber`, regular `%` and `/`, and overflow checks. If any intermediate result overflows, fall back to the BigInt path.

3. **Number-path field `times` / `reciprocal`.** E.g., golden `times`:
   ```js
   function times(a, b) {
     const [a0, a1, ad] = a, [b0, b1, bd] = b;
     return simplify3(a0*b0 + a1*b1, a0*b1 + a1*b0 + a1*b1, ad*bd);
   }
   ```
   The Number version would do the same arithmetic with `safeMul`/`safeAdd`, falling back to BigInt on any overflow.

4. **`plus2` / `minus2` / `plus3` / `minus3`** — same treatment.

5. **`createNumberFromPairs2` / `createNumberFromPairs3`** — these currently force `BigInt()` coercion on all inputs. They should pass Numbers through when possible.

6. **`parseInt` in `common.js`** — currently returns `BigInt(s)` unconditionally. Should return a plain `Number` when the parsed value fits in the safe range, since this is the entry point for most values parsed from `.vZome` XML.

This work is independent of and complementary to the `JavaBigRational` optimization. Together they cover both field families.

## Implementation: `common.js` Dual-Representation (completed)

The following changes implement the Number/BigInt dual-representation for the trailing-divisor arithmetic path used by golden, root2, root3, and heptagon fields.

### `common.js` changes

- Added `MAX_SAFE`, `isBig()`, `safeAdd`/`safeSub`/`safeMul`, `gcdNumber` — Number-safe arithmetic helpers with overflow detection (return `null` on overflow).
- Added `toBigInts3`/`toBigInts4` — promote a Number tuple to BigInt for fallback paths.
- Added `demote3`/`demote4` — convert BigInt results back to Number when all elements fit in safe range.
- `simplify3`/`simplify4` now dispatch to `simplifyNumber3`/`simplifyBigInt3` (and `4`-variants) based on `typeof` of the first element. BigInt results are demoted to Number when they fit.
- `plus2`/`minus2`/`negate2`/`plus3`/`minus3`/`negate3` try Number arithmetic first (with same-denominator fast path), fall back to BigInt on overflow.
- `createNumberFromPairs2`/`createNumberFromPairs3` accept Number inputs and try Number-path multiplication before falling back to BigInt.
- `createNumber2`/`createNumber3` accept Number inputs and route to the appropriate simplify path.
- `parseInt` returns `Number` when the parsed value is a safe integer, `BigInt` otherwise.
- `createField` uses Number constants for `zero` (e.g. `[0, 0, 1]`) and `one` (`[1, 0, 1]`), and Number defaults throughout `scalarmul`, `vectoradd`, `quatmul`, `quatconj`, `quatTransform`.
- `toString2`/`toString3` promote to BigInt at entry (formatting is not performance-critical) so their existing `=== 0n` comparisons continue to work.
- `bigRationalToString`/`bigRationalToMathML` use `==` instead of `===` for the denominator-is-one check, so they work with both Number and BigInt.

### Field module changes (`golden.js`, `root2.js`, `root3.js`, `heptagon.js`)

- `times` and `reciprocal` check `typeof` of the divisor element. If both inputs are Number, they perform Number arithmetic and verify all intermediates and results are within `MAX_SAFE_INTEGER`. On any overflow, they promote to BigInt and redo the operation.
- `embed` uses `Number()` conversion on array elements (works for both Number and BigInt).
- `golden.js`: quaternion constants, `goldenSequence`, `goldenRatio`, and `grsign` changed from BigInt literals to Number literals.

### Invariant

All trailing-divisor arrays are either **all Number** or **all BigInt**. The type is detected via `typeof` on the last element (the divisor). Values start as Number and stay Number for typical vZome models (small coefficients). BigInt promotion happens automatically when intermediates exceed $2^{53}$, and demotion back to Number occurs after GCD simplification when results fit again.
