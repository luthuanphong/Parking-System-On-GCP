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
  description = "The GCP zone"
  type        = string
  default     = "us-central1-c"
}

variable "subnet_cidr" {
  description = "CIDR range for the subnet"
  type        = string
  default     = "10.0.0.0/24"
}

variable "secondary_ip_range" {
  description = "CIDR range for the secondary IP range"
  type        = string
  default     = "10.1.0.0/24"
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

/*
variable "db_user" {
  description = "PostgreSQL user name"
  type        = string
  default     = "postgres"
  sensitive   = true
}

variable "db_password" {
  description = "PostgreSQL user password"
  type        = string
  sensitive   = true
}
*/

variable "kms_keyring_name" {
  description = "Name of the Cloud KMS KeyRing"
  type        = string
  default     = "parking-keyring"
}

variable "kms_key_name" {
  description = "Name of the Cloud KMS Key"
  type        = string
  default     = "parking-key"
}

variable "gke_machine_type" {
  description = "The machine type for GKE nodes"
  type        = string
  default     = "e2-medium"
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

variable "service_account_role" {
    description = "The role to be assigned to the service account (e.g., roles/secretmanager.secretAccessor)"
    type        = string
}

variable "environment" {
  description = "Environment name for resource labeling"
  type        = string
  default     = "poc"
}