# LLM Knowledge Capture Plan

This document tracks the work needed to make the vZome codebase maximally accessible to LLMs (and human contributors). The goal is to turn implicit knowledge — in Scott's head, in Git history, in Discord threads — into explicit, in-repo prose that any LLM tool can consume.

## Background

The `.github/copilot-instructions.md` file provides a strong foundation. These tasks fill the remaining gaps, roughly ordered by impact.

---

## High Priority

### 1. Annotate the Action Names

The 211 action names in `developer-docs/vZome-action-names.txt` are the API surface of the entire editor, but they have no descriptions. Adding even a one-line description to each entry would let any LLM reason about what vZome can do.

**Source file:** `developer-docs/vZome-action-names.txt`

### 2. Brain-Dump Interview: Symmetry / Fields / Orbits Mental Model

The interaction between symmetry perspectives, field applications, and orbit systems exists primarily in Scott's memory. Use an LLM-assisted interview (have it ask probing questions, answer in natural language, then commit the structured output).

**Output:** `developer-docs/knowledge/symmetry-fields-orbits.md`

### 3. Finish Online Architecture Doc

`online/developer-docs/architecture.md` ends with "More content to follow." Since online is the active development focus, completing this has outsized impact for any LLM or contributor working on the web app.

**Source file:** `online/developer-docs/architecture.md`

---

## Medium Priority

### 4. Brain-Dump Interview: Pre-2014 History

Origins, early Java3D version, design philosophy evolution, the Zometool company relationship. This context is irreplaceable and exists only in Scott's memory.

**Output:** `developer-docs/knowledge/pre-2014-history.md`

### 5. Decision Records for Rejected Alternatives

LLMs frequently suggest approaches that were already tried and rejected (e.g., the client-server architecture in `server/`). A short decisions doc saves enormous back-and-forth.

**Output:** `developer-docs/decisions.md`

### 6. Git History Narrative

Feed the full commit history to an LLM and generate a feature timeline narrative. The 4,000+ commits contain the story of the project's evolution; they just need synthesis.

**How:** `git log --all --format='%ai %s'` → LLM prompt → commit the result.

**Output:** `developer-docs/knowledge/git-history-timeline.md`

---

## Lower Priority (Still Valuable)

### 7. Discord / Email Archive Export

Export vZome-related Discord channels (using DiscordChatExporter) and relevant email threads. These conversations often contain *why* decisions were made.

**Output:** `developer-docs/archives/` (or a private repo if they contain personal info)

### 8. Document Why Each Algebraic Field Was Added

Each field in `com.vzome.core.algebra` was motivated by a specific geometric exploration. Capturing the "why" prevents future contributors from treating them as arbitrary.

**Output:** Add a section to `copilot-instructions.md` or a standalone `developer-docs/knowledge/algebraic-fields.md`

### 9. Document the Community / User Base

Who uses vZome and for what? This context helps LLMs give better UX advice and prioritize features.

**Output:** `developer-docs/knowledge/community.md`

### 10. Complete `rendering-state.md`

Currently only ~20 lines on `.shapes.json` design. Fleshing this out helps with any rendering or export work.

**Source file:** `developer-docs/rendering-state.md`

---

## Non-Goals

- **Fine-tuning a model** — Too expensive and fragile for a project of this scale. The models improve faster than you can retrain.
- **Building a custom RAG system** — High maintenance for marginal gain over good in-repo prose.
- **Binding to a single model** — Keep everything model-agnostic by writing prose in the repo. Use NotebookLM or similar as a supplementary query layer, not the source of truth.
