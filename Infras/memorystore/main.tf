module "parent" {
    source = "../"
    project_id         = var.project_id
    project_name       = var.project_name
    region             = var.region
    zone               = var.zone
}

resource "google_redis_instance" "cache" {
  name           = "${var.project_name}-redis"
  project = var.project_id
  display_name   = "Parking System Cache"
  tier           = var.redis_tier
  memory_size_gb = var.redis_memory_size
  region         = var.region
  redis_version  = var.redis_version

  authorized_network = module.parent.vpc
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
}