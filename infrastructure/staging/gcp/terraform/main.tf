variable "project" {
    default = "loadtest-12"
}

variable "region" {
    default = "asia-northeast1"
}

variable "zone" {
    default = "asia-northeast1-a"
}

variable "repo_name" {
    default = "github_bambootuna_loadtest"
}

variable "docker_image_name" {
    default = "loadtest"
}

provider "google" {
    credentials = "${file(".account.json")}"
    region = "${var.region}"
    project = "${var.project}"
}

// Network
resource "google_compute_network" "default" {
    name                    = "${var.project}"
    auto_create_subnetworks = false
}

// Subnetwork
resource "google_compute_subnetwork" "default" {
    name                     = "${var.project}"
    ip_cidr_range            = "192.168.10.0/24"
    network                  = "${google_compute_network.default.self_link}"
    region                   = "${var.region}"
    private_ip_google_access = true
}

// Cluster
resource "google_container_cluster" "default" {
    name               = "${var.project}"
    zone               = "${var.zone}"
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
    name = "${var.project}-static-ip"
}

// Cloud Build
resource "google_cloudbuild_trigger" "default" {
    trigger_template {
        branch_name = "develop"
        repo_name   = "${var.repo_name}"
    }

    substitutions = {
        _REGION = "${var.region}"
        _ZONE = "${var.zone}"
        _IMAGE_MANE = "${var.docker_image_name}"
    }

    filename = "../cloudbuild.yml"
}