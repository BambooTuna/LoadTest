FROM python:3.7.4-slim

ENV LOCUST_HOST="http://localhost:8080"
ENV LOCUST_FILE_PATH="main.py"

RUN apt-get update
RUN apt-get install -y vim less

RUN pip install --upgrade pip
RUN pip install --upgrade setuptools
RUN pip install locustio
RUN pip install pyzmq
# RUN pip install requests==2.3.0

ADD ./src /root/
WORKDIR /root

EXPOSE 8089

RUN echo $LOCUST_FILE_PATH
RUN echo $LOCUST_HOST
CMD locust -f ${LOCUST_FILE_PATH} -H ${LOCUST_HOST}