output "database_instance_connection_name" {
  value       = google_sql_database_instance.postgres.connection_name
  description = "The connection name of the Cloud SQL instance"
}

output "redis_host" {
  value       = google_redis_instance.cache.host
  description = "The IP address of the Redis instance"
}

/*
output "gke_cluster_endpoint" {
  value       = google_container_cluster.primary.endpoint
  description = "The endpoint for the GKE cluster"
}
*/

output "vpc_network_name" {
  value       = google_compute_network.vpc.name
  description = "The name of the VPC network"
}

output "subnet_name" {
  value       = google_compute_subnetwork.subnet.name
  description = "The name of the subnet"
}

output "db_user" {
  value       = data.google_secret_manager_secret_version.db_user
  description = "The name of the subnet"
  sensitive = true
}

output "db_password" {
  value       = data.google_secret_manager_secret_version.db_password
  description = "The name of the subnet"
  sensitive = true
}