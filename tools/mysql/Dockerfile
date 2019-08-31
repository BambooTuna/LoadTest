FROM mysql:5.7.22

ADD ./table.sql /docker-entrypoint-initdb.d/
ADD ./my.cnf /etc/my.cnf

EXPOSE 3306