FROM node:lts-slim AS builder
WORKDIR /app
COPY package*.json .
RUN npm install
COPY . .
ENV REACT_APP_BACKEND_HOST=https://proj01.zpi.wit.pwr.edu.pl/api/
ENV REACT_APP_GOOGLE_CLIENT_ID=333365127566-llqb3rl4kcvvcnurr7cih7s126iu0e5v.apps.googleusercontent.com
RUN npm run build

FROM nginx:latest
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/build /usr/share/nginx/html
RUN touch /var/run/nginx.pid
RUN chown -R nginx:nginx /var/run/nginx.pid /usr/share/nginx/html /var/cache/nginx /var/log/nginx /etc/nginx/conf.d
USER nginx
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]