module "parent" {
  source       = "../"
  project_id   = var.project_id
  project_name = var.project_name
  region       = var.region
  zone         = var.zone
}

data "google_secret_manager_secret_version" "db_user" {
  project          = var.project_id
  secret  = "db_user"
  version = "1"
}

data "google_secret_manager_secret_version" "db_password" {
  project          = var.project_id
  secret  = "db_password"
  version = "1"
}

locals {
  db_user     = try(jsondecode(data.google_secret_manager_secret_version.db_user), data.google_secret_manager_secret_version.db_user).secret_data
  db_password = try(jsondecode(data.google_secret_manager_secret_version.db_password), data.google_secret_manager_secret_version.db_password).secret_data
}

# Cloud SQL Instance with PostgreSQL
resource "google_sql_database_instance" "postgres" {
  name             = "${var.project_name}-db"
  database_version = var.postgres_version
  region           = var.region
  project          = var.project_id
  settings {
    tier      = var.db_tier
    disk_size = var.disk_size
    disk_type = var.disk_type
    /*
    ip_configuration {
      ipv4_enabled = true
      ssl_mode = "ENCRYPTED_ONLY"
      private_network = "default"
      authorized_networks {
        name  = "all"
        value = "0.0.0.0/0"
      }
    }
    */

    ip_configuration {
      ipv4_enabled    = false
      ssl_mode        = "ENCRYPTED_ONLY"
      private_network = "projects/${var.project_id}/global/networks/${module.parent.vpc}"
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
  project          = var.project_id
}

# Cloud SQL User
resource "google_sql_user" "postgres_user" {
  name     = local.db_user
  instance = google_sql_database_instance.postgres.name
  password = local.db_password
  project          = var.project_id
}