# Generic Rules

These rules apply to every Kotlin file regardless of category.

**G1 — KDoc on public API** (P1)
Every `public` class and every `public` function must have a KDoc comment (`/** ... */`).
Exceptions: functions annotated `@Preview`; functions annotated `@Test`; the default `companion object` inside a class.
Remediation: add a one-line KDoc describing the purpose.

**G2 — NO RULE** (P4)
This is a placeholder since a rule has been removed. Ignore this rule.

**G3 — No wildcard imports** (P2)
`import com.example.*` is forbidden.
Remediation: replace with explicit individual imports.

**G4 — No detekt baseline suppression** (P0)
Never add a violation to the detekt baseline. If a violation cannot be fixed, stop and ask the developer.
Remediation: fix the violation directly.

**G5 — Annotations on their own line** (P3)
Annotations must not appear on the same line as the declaration they annotate.
Remediation: move the annotation to its own line above the declaration.
