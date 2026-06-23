FROM nginx:alpine

COPY build/dist/wasmJs/productionExecutable /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

# Start serving traffic
CMD ["nginx", "-g", "daemon off;"]