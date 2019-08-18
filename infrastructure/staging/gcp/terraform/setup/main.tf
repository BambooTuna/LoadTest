variable "GOOGLE_PROJECT_ID" {}
variable "GOOGLE_COMPUTE_REGION" {}
variable "GOOGLE_COMPUTE_ZONE" {}
variable "GOOGLE_CLUSTER_NAME" {}

provider "google" {
  credentials = "${file(".gcloud-service-key.json")}"
  region      = "${var.GOOGLE_COMPUTE_REGION}"
  project     = "${var.GOOGLE_PROJECT_ID}"
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