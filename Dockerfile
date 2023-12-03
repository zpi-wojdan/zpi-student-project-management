FROM nginx:latest
COPY ./zpi-frontend/certificates/proj01_zpi_wit_pwr_edu_pl_cert.cer /etc/nginx/ssl/proj01_zpi_wit_pwr_edu_pl_cert.cer
COPY ./zpi-frontend/certificates/proj01.zpi.wit.pwr.edu.pl.key /etc/nginx/ssl/proj01.zpi.wit.pwr.edu.pl.key
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN touch /var/run/nginx.pid
RUN chown -R nginx:nginx /var/run/nginx.pid /usr/share/nginx/html /var/cache/nginx /var/log/nginx /etc/nginx/conf.d
USER nginx
EXPOSE 443
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]