module "parent" {
    source = "../"
    project_id         = var.project_id
    project_name       = var.project_name
    region             = var.region
    zone               = var.zone
}

resource "google_artifact_registry_repository" "parking_api_repo" {
  location      = var.region
  repository_id = "parking-api-repo"
  format        = "DOCKER"
  description   = "Parking API repository for container images"
}