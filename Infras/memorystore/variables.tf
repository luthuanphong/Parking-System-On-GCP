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

variable "redis_tier" {
  description = "The service tier of the instance"
  type        = string
  default     = "BASIC" # Options: BASIC, STANDARD_HA
}

variable "redis_memory_size" {
  description = "Redis memory size in GB"
  type        = number
  default     = 1
}

variable "redis_version" {
  description = "The version of Redis software"
  type        = string
  default     = "REDIS_6_X"
}