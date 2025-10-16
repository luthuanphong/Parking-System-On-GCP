module "parent" {
    source = "../"
    project_id         = var.project_id
    project_name       = var.project_name
    region             = var.region
    zone               = var.zone
}

resource "google_compute_network" "main" {
  name                    = module.parent.vpc
  auto_create_subnetworks = true
  project                 = var.project_id
}

resource "google_compute_global_address" "main" {
  name          = "${var.project_name}-vpc-address"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 16
  network       = google_compute_network.main.name
  project       = var.project_id
}

resource "google_service_networking_connection" "main" {
  network                 = google_compute_network.main.self_link
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.main.name]
  deletion_policy         = "ABANDON"
  depends_on              = [google_compute_network.main]
}

resource "google_vpc_access_connector" "main" {
  project        = var.project_id
  name           = "${var.project_name}-vpc-cx"
  ip_cidr_range  = "10.8.0.0/28"
  network        = google_compute_network.main.name
  region         = var.region
  max_throughput = 300
  min_throughput = 200
  depends_on     = [time_sleep.wait_before_destroying_network]
}

# The google_vpc_access_connector resource creates some firewalls that sometimes takes a while to destroy
# and causes errors when we try to destroy the VPC network (google_compute_network).
resource "time_sleep" "wait_before_destroying_network" {
  depends_on       = [google_compute_network.main]
  destroy_duration = "60s"
}
