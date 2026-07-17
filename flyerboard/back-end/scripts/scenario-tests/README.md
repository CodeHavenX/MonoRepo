# Flyerboard API scenario tests

Black-box client-simulation tests for the Flyerboard back-end. Each script drives the running
HTTP API exactly the way a real client would (mint a real Supabase-issued token, make HTTP calls,
check status codes and response bodies) rather than calling Kotlin code directly. This
complements the Kotlin unit/integration test suites (`src/test`, `src/integTest`) by exercising
the whole stack — routing, auth, controllers, services, datastores, Postgres — from the outside.

## Prerequisites

1. Local Supabase must be running for this project: `supabase start` from `flyerboard/back-end/`.
2. The back-end server must be running locally against that instance, on port 9292:

   ```bash
   export FLYERBOARD_SUPABASE_URL="http://127.0.0.1:54321"
   # Well-known local Supabase CLI demo service_role key (see scripts/supabase_get_access_token.sh)
   export FLYERBOARD_SUPABASE_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZS1kZW1vIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImV4cCI6MTk4MzgxMjk5Nn0.EGIM96RAZx35lJzdJsyH-qQwv8Hdp7fsn3W0YpN81IU"
   export PORT=9292
   ./gradlew :flyerboard:back-end:run --quiet
   ```

3. `jq`, `curl`, and `docker` (for the local Supabase Postgres container) must be on `PATH`.

## Running

Run everything and get a pass/fail summary:

```bash
./run_all.sh
```

Run a single scenario (useful while iterating):

```bash
./16_pagination_edge_cases.sh
```

Every script is self-contained: it mints its own throwaway users (unique emails per run, keyed
off `$$`), creates whatever flyers/profiles it needs, and asserts on the resulting HTTP status
codes and JSON bodies. Nothing needs to be cleaned up between runs — each run uses fresh emails
and flyer titles.

## What's covered

- `01`-`15`: core flows — signup, auth boundaries, the full flyer lifecycle (create → moderate →
  approve/reject → edit), ownership checks, validation and not-found handling.
- `16`-`21`: pagination, search-query, and flyer-creation input edge cases (blank/oversized
  fields, sanitization, malformed/past `expires_at`, unknown JSON fields).
- `22`-`27`: flyer-update edge cases (no-op updates, re-editing already-pending flyers, editing
  archived flyers, `expires_at` clearing, sanitization, repeated upload-URL requests).
- `28`-`32`: moderation edge cases (idempotent approve/reject, claw-back transitions, moderating
  archived flyers, reason-field sanitization, action-string case sensitivity).
- `33`-`36`: auth edge cases (malformed `Authorization` headers, an authenticated user who never
  completed signup, role-escalation probes, the approved→archived lifecycle transition).
- `37`-`42`: transport-layer robustness (missing/wrong `Content-Type`, duplicate JSON keys,
  oversized bodies, path-param fuzzing, unsupported HTTP methods).

## Adding a new scenario

Copy the numbering convention (`NN_short_description.sh`), `source lib.sh`, and end with a call
to `summary` (its exit code reflects pass/fail, and `run_all.sh` parses its stdout). Prefer the
`ok`/`bad`/`assert_eq`/`assert_in` helpers in `lib.sh` over ad hoc `echo` so `run_all.sh` can
tally results consistently.
