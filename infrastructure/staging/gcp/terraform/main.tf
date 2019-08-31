variable "GOOGLE_PROJECT_ID" {}
variable "GOOGLE_COMPUTE_REGION" {}
variable "GOOGLE_COMPUTE_ZONE" {}
variable "GOOGLE_CLUSTER_NAME" {}

provider "google" {
  credentials = "${file("./account.json")}"
  region      = "${var.GOOGLE_COMPUTE_REGION}"
  project     = "${var.GOOGLE_PROJECT_ID}"
}