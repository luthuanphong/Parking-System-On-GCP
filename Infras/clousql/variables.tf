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

variable "db_tier" {
  description = "The machine type for Cloud SQL"
  type        = string
  default     = "db-f1-micro"
}

variable "disk_type" {
  description = "The machine type for Cloud SQL"
  type        = string
}

variable "disk_size" {
  description = "The machine type for Cloud SQL"
  type        = string
}

variable "db_name" {
  description = "The name of the database"
  type        = string
  default     = "parking_system"
}

variable "postgres_version" {
  description = "PostgreSQL version for Cloud SQL"
  type        = string
  default     = "POSTGRES_14"
}