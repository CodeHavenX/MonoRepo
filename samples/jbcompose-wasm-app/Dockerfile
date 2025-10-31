FROM nginx:alpine

COPY /build/dist/wasmJs/productionExecutable /usr/share/nginx/html

EXPOSE 80

# Start serving traffic
CMD ["nginx", "-g", "daemon off;"]