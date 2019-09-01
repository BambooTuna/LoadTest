import uuid

from datetime import datetime
from locust import HttpLocust, TaskSet, task

class MetricsTaskSet(TaskSet):
    @task
    def index(self):
        self.client.get("/")

class MetricsLocust(HttpLocust):
    task_set = MetricsTaskSet