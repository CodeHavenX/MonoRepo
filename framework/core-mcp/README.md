# Core MCP Module

Exposes `framework:network-api` operations as MCP (Model Context Protocol) tools on top of a Ktor server, reusing
the same `ContextRetriever`/`ClientContext` auth model as `framework:core-ktor`'s REST route handlers.
