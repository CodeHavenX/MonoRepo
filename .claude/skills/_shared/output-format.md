# Output Format

List all the findings from our own rules sorted by priority (P0 first, then P1, P2, P3). Within the same priority, group by file.

Then list the failures from the Build Tools(compiler, linter or static analyzer). This output does not need to be modified.
If there are no failures from the Build Tools Report this section must be omitted.

For each finding from our rules:

```
[P<N>] <RuleID> — <File>:<line-or-section>
Problem : <one sentence describing the violation>
Remediation: <one or two sentences on how to fix it>
```

End with a summary table:

```
Summary
-------
P0 (Critical): N findings
P1 (High)    : N findings
P2 (Medium)  : N findings
P3 (Low)     : N findings
Total        : N findings

Build Tools Report
<Paste the output of the errors found>
```

If no violations are found, say so explicitly.

## Next Step

End with an explicit pointer to what comes next in the pipeline:

- **P0 or P1 findings exist** — tell the user to fix them and re-run this review before moving
  on. Do not suggest `create-pr` yet.
- **Only P2/P3 findings, or none at all** — tell the user they can proceed to `create-pr` to
  open the pull request.
