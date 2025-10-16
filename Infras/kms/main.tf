module "parent" {
    source = "../"
    project_id         = var.project_id
    project_name       = var.project_name
    region             = var.region
    zone               = var.zone
}

# Cloud KMS KeyRing
resource "google_kms_key_ring" "parking_keyring" {
  name     = "${var.project_name}-keyring-2"
  project = var.project_id
  location = var.region
}

# Cloud KMS Encryption Key
resource "google_kms_crypto_key" "parking_key" {
  name     = "${var.project_name}-key-2"
  key_ring = google_kms_key_ring.parking_keyring.id
  purpose  = "ENCRYPT_DECRYPT"

  version_template {
    algorithm = "GOOGLE_SYMMETRIC_ENCRYPTION"
  }

  rotation_period = "7776000s" # 90 days

  lifecycle {
    prevent_destroy = false
  }
}