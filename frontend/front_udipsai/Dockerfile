# First stage: Node.js image to build the Angular app
FROM node:16-alpine as build-stage
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build -- --output-path=./dist/frontend-udipsai

# Second stage: Nginx image to serve the app
FROM nginx:1.25.4-alpine
COPY --from=build-stage /app/dist/frontend-udipsai/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

