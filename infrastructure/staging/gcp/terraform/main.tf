variable "GOOGLE_PROJECT_ID" {}
variable "GOOGLE_COMPUTE_REGION" {}
variable "GOOGLE_COMPUTE_ZONE" {}
variable "GOOGLE_CLUSTER_NAME" {}

provider "google" {
  credentials = "${file("/root/account.json")}"
  region      = "${var.GOOGLE_COMPUTE_REGION}"
  project     = "${var.GOOGLE_PROJECT_ID}"
}

resource "google_project" "my_project" {
  name       = "loadtest"
  project_id = "${var.GOOGLE_PROJECT_ID}"
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
  initial_node_count = 2
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
    machine_type = "n1-standard-1"
  }
}

// Static IP
resource "google_compute_global_address" "ip_address" {
  name = "${var.GOOGLE_PROJECT_ID}-static-ip"
}