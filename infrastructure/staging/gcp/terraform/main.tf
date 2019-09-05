variable "GOOGLE_PROJECT_ID" {}
variable "GOOGLE_COMPUTE_REGION" {}
variable "GOOGLE_COMPUTE_ZONE" {}
variable "GOOGLE_CLUSTER_NAME" {}

provider "google" {
  credentials = "${file("./account.json")}"
  region      = "${var.GOOGLE_COMPUTE_REGION}"
  project     = "${var.GOOGLE_PROJECT_ID}"
}

// Network
resource "google_compute_network" "default" {
  name                    = "${var.GOOGLE_PROJECT_ID}"
  auto_create_subnetworks = false
}

// Subnetwork
resource "google_compute_subnetwork" "default" {
  name                     = "${var.GOOGLE_PROJECT_ID}"
  ip_cidr_range            = "192.168.10.0/24"
  network                  = "${google_compute_network.default.self_link}"
  region                   = "${var.GOOGLE_COMPUTE_REGION}"
  private_ip_google_access = true
}

// Cluster
resource "google_container_cluster" "default" {
  name               = "${var.GOOGLE_CLUSTER_NAME}"
  zone               = "${var.GOOGLE_COMPUTE_ZONE}"
  initial_node_count = 21
  network            = "${google_compute_subnetwork.default.name}"
  subnetwork         = "${google_compute_subnetwork.default.name}"

  enable_legacy_abac = true

  master_auth {
    username = ""
    password = ""
  }

  provisioner "local-exec" {
    when    = "destroy"
    command = "sleep 90"
  }

  node_config {
    oauth_scopes = [
      "https://www.googleapis.com/auth/compute",
      "https://www.googleapis.com/auth/devstorage.read_only",
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
    ]
    preemptible  = true
    machine_type = "n1-standard-2"
    disk_size_gb = 10
    disk_type    = "pd-ssd"
  }
}

// Gatling Cluster
resource "google_container_cluster" "loadtest-server" {
  name               = "gatling-runner-cluster"
  zone               = "${var.GOOGLE_COMPUTE_ZONE}"
  initial_node_count = 4
  network            = "${google_compute_subnetwork.default.name}"
  subnetwork         = "${google_compute_subnetwork.default.name}"

  enable_legacy_abac = true

  master_auth {
    username = ""
    password = ""
  }

  provisioner "local-exec" {
    when    = "destroy"
    command = "sleep 90"
  }

  node_config {
    oauth_scopes = [
      "https://www.googleapis.com/auth/compute",
      "https://www.googleapis.com/auth/devstorage.read_only",
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
    ]
    preemptible  = true
    machine_type = "n1-standard-2"
    disk_size_gb = 10
    disk_type    = "pd-ssd"
  }
}
