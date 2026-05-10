# Scope Resolution

Resolve the user-provided scope to a list of Kotlin files using the table below. If no scope was provided, ask the user for one.

| Scope | Command |
|-------|---------|
| `changes` | `git diff main...HEAD --name-only --diff-filter=d` + `git diff --name-only` — take the union of both |
| `module <path>` | `find <path> -name "*.kt" -type f` |
| `feature <name>` | `find . -type d -name "<name>" \| grep -E "features\|feature"` — pick the best match, then `find <dir> -name "*.kt"` |
| `pr <number>` | `gh pr diff <number> --name-only` |

Filter to **only `.kt` files**. If the resulting list is empty, report that and stop.
