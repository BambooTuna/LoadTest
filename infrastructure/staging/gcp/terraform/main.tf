variable "GOOGLE_PROJECT_ID" {}
variable "GOOGLE_COMPUTE_REGION" {}
variable "GOOGLE_COMPUTE_ZONE" {}
variable "GOOGLE_CLUSTER_NAME" {}

provider "google" {
  credentials = "${file("/root/account.json")}"
  region      = "${var.GOOGLE_COMPUTE_REGION}"
  project     = "${var.GOOGLE_PROJECT_ID}"
}

terraform {
  backend "gcs" {
    prefix = "terraform.tfstate"
  }
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
    machine_type = "g1-small"
  }
}

// Static IP
resource "google_compute_global_address" "ip_address" {
  name = "${var.GOOGLE_PROJECT_ID}-static-ip"
}

resource "google_storage_bucket" "terraform-state-store" {
  name          = "${var.GOOGLE_PROJECT_ID}_calendar"
  location      = "${var.GOOGLE_COMPUTE_REGION}"
  storage_class = "REGIONAL"

  versioning {
    enabled = true
  }

  lifecycle_rule {
    action {
      type = "Delete"
    }
    condition {
      num_newer_versions = 5
    }
  }
}

resource "google_storage_bucket_acl" "local-acl" {
  bucket = "${google_storage_bucket.terraform-state-store.name}"
  predefined_acl = "private"
}