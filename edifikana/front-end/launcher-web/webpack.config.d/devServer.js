// Enable SPA fallback so that refreshing on any deep-linked path (e.g. /home/property/123)
// serves index.html instead of returning a 404 from the dev server.
config.devServer = config.devServer || {};
config.devServer.historyApiFallback = true;

// Pin the dev server (and its auto-opened browser tab) to 127.0.0.1 so it always
// matches webapp.url / allowed.host in the back-end config. Browser storage
// (localStorage) is origin-scoped, so opening on a different host than usual
// silently loses settings like the Supabase API URL/key override.
config.devServer.host = '127.0.0.1';
