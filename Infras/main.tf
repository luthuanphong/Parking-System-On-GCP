terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "6.8.0"
    }
  }
  backend "gcs" {
    bucket = "parking-system-tfstate"
    prefix = "terraform/state"
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
  zone    = var.zone
}

# VPC Network
resource "google_compute_network" "vpc" {
  name                    = "${var.project_name}-vpc"
  auto_create_subnetworks = false
}

# Subnet
resource "google_compute_subnetwork" "subnet" {
  name          = "${var.project_name}-subnet"
  ip_cidr_range = var.subnet_cidr
  network       = google_compute_network.vpc.id
  region        = var.region

  secondary_ip_range {
    range_name    = "services-range"
    ip_cidr_range = var.secondary_ip_range
  }
}

# Cloud SQL Instance with PostgreSQL
resource "google_sql_database_instance" "postgres" {
  name             = "${var.project_name}-db"
  database_version = var.postgres_version
  region           = var.region

  settings {
    tier = var.db_tier

    ip_configuration {
      ipv4_enabled = true
      authorized_networks {
        name  = "all"
        value = "0.0.0.0/0"
      }
    }

    backup_configuration {
      enabled = true
      backup_retention_settings {
        retained_backups = 7
        retention_unit   = "COUNT"
      }
      point_in_time_recovery_enabled = true
      start_time                     = "02:00" # 2 AM UTC
    }

    database_flags {
      name  = "max_connections"
      value = "100"
    }

    database_flags {
      name  = "postgresql.auto_explain.log_min_duration"
      value = "300000" # Log queries that take more than 300ms
    }

    insights_config {
      query_insights_enabled  = true
      query_string_length     = 1024
      record_application_tags = true
      record_client_address   = true
    }

    maintenance_window {
      day          = 7 # Sunday
      hour         = 2 # 2 AM
      update_track = "stable"
    }
  }

  deletion_protection = false
}

# Cloud SQL Database
resource "google_sql_database" "database" {
  name     = var.db_name
  instance = google_sql_database_instance.postgres.name
}

# Cloud SQL User
resource "google_sql_user" "postgres_user" {
  name     = var.db_user
  instance = google_sql_database_instance.postgres.name
  password = var.db_password
}

# Memorystore Redis Instance
resource "google_redis_instance" "cache" {
  name           = "${var.project_name}-redis"
  display_name   = "Parking System Cache"
  tier           = var.redis_tier
  memory_size_gb = var.redis_memory_size
  region         = var.region
  redis_version  = var.redis_version

  authorized_network = google_compute_network.vpc.id
  connect_mode       = "DIRECT_PEERING"

  redis_configs = {
    maxmemory-policy       = "allkeys-lru" # Eviction policy for when memory is full
    notify-keyspace-events = "Ex"          # Enable keyspace notifications for expired keys
    timeout                = "1800"        # Connection timeout in seconds
  }

  maintenance_policy {
    weekly_maintenance_window {
      day = "SUNDAY"
      start_time {
        hours   = 2 # 2 AM
        minutes = 0
        seconds = 0
        nanos   = 0
      }
    }
  }

  labels = {
    environment = var.environment
    application = "parking-system"
    service     = "cache"
  }

  depends_on = [google_compute_network.vpc]
}

# GKE Cluster
resource "google_container_cluster" "primary" {
  name     = "${var.project_name}-gke"
  location = var.zone

  network    = google_compute_network.vpc.name
  subnetwork = google_compute_subnetwork.subnet.name

  initial_node_count = 1

  node_config {
    machine_type = var.gke_machine_type
    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
  }
}

/*
# Service Account for Cloud Run
resource "google_service_account" "cloud_run_sa" {
  account_id   = "${var.project_name}-run-sa"
  display_name = "Service Account for Cloud Run"
}

# Cloud Run service
resource "google_cloud_run_service" "parking_service" {
  name     = "${var.project_name}-service"
  location = var.region

  template {
    spec {
      service_account_name = google_service_account.cloud_run_sa.email
      containers {
        image = var.container_image
        resources {
          limits = {
            cpu    = "1000m"
            memory = "512Mi"
          }
        }
        env {
          name  = "SPRING_PROFILES_ACTIVE"
          value = var.environment
        }
        env {
          name  = "DB_HOST"
          value = google_sql_database_instance.postgres.connection_name
        }
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  depends_on = [google_project_service.run_api]
}

# Allow unauthenticated access to Cloud Run service
resource "google_cloud_run_service_iam_member" "public_access" {
  service  = google_cloud_run_service.parking_service.name
  location = google_cloud_run_service.parking_service.location
  role     = "roles/run.invoker"
  member   = "allUsers"
}

# Enable Cloud Run API
resource "google_project_service" "run_api" {
  service = "run.googleapis.com"
  disable_on_destroy = false
}
*/

# Cloud KMS KeyRing
resource "google_kms_key_ring" "parking_keyring" {
  name     = "${var.project_name}-keyring"
  location = var.region
}

# Cloud KMS Encryption Key
resource "google_kms_crypto_key" "parking_key" {
  name     = "${var.project_name}-key"
  key_ring = google_kms_key_ring.parking_keyring.id
  purpose  = "ENCRYPT_DECRYPT"

  version_template {
    algorithm = "GOOGLE_SYMMETRIC_ENCRYPTION"
  }

  rotation_period = "7776000s" # 90 days

  lifecycle {
    prevent_destroy = true
  }
}

/*
# Grant Cloud Run SA access to KMS
resource "google_kms_crypto_key_iam_member" "crypto_key_user" {
  crypto_key_id = google_kms_crypto_key.parking_key.id
  role          = "roles/cloudkms.cryptoKeyEncrypterDecrypter"
  member        = "serviceAccount:${google_service_account.cloud_run_sa.email}"
}
*/

# Enable Cloud KMS API
resource "google_project_service" "kms_api" {
  service            = "cloudkms.googleapis.com"
  disable_on_destroy = false
}
