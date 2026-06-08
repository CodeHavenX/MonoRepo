// Enable SPA fallback so that refreshing on any deep-linked path (e.g. /home/property/123)
// serves index.html instead of returning a 404 from the dev server.
config.devServer = config.devServer || {};
config.devServer.historyApiFallback = true;
