variable "project_id" {
  description = "The GCP Project ID"
  type        = string
}

variable "project_name" {
  description = "The name of the project, used as a prefix for resources"
  type        = string
  default     = "parking-system"
}

variable "region" {
  description = "The GCP region"
  type        = string
  default     = "us-central1"
}

variable "zone" {
  description = "The GCP region"
  type        = string
  default     = "us-central1"
}