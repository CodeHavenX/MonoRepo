# Output Format

List all findings sorted by priority (P0 first, then P1, P2, P3). Within the same priority, group by file.

For each finding:

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
```

If no violations are found, say so explicitly.
