FROM node:lts-alpine3.18

COPY package*.json .

RUN npm install

COPY . .

EXPOSE 3000

RUN npm run build