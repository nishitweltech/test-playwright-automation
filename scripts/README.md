# Optional pre-test scripts

Place reviewed setup scripts in this directory. The interactive execution CLI lists
and can run top-level `.sh`, `.bat`, `.cmd`, and `.ps1` files before Maven tests.

Keep scripts small and non-destructive. Do not store credentials in scripts; use
environment variables or your machine's secret manager instead.

No script is required. Select `0) Skip` in the CLI to run tests directly.
