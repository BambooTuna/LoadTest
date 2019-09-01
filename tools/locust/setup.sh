LOCUST_FILE_PATH="main.py" \
LOCUST_HOST=http://localhost:8080 \
docker build . -t locust && \
docker run -p 8089:8089 -it locust