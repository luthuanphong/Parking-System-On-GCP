Terraform Structure
    - main.tf
    - output.tf
    - variables.tf
    - terraform.tfvars
        - variable data not recommended as it's plain text file
        - Recommend to use Secret Manager (Google Secret Manager)
    - command: (refers)[./terraform_common_commands.md]

GCP Resource
- Cloud Storage Bucket
    - Purpose: Store terraform config on GCP
    - Region: Singapore
    - Storage type: (Default Class) Standard
    - No enable Hierachy namespace for this one (Q: what is it for ?)
    - No enable Anywhere Cache (Q: What benefit does it offer? Do we cost more ?)
    - Access control: Prevent public access, Access Control: Uniform/Fine-Grained
    - Data protection: 
        - Soft-delete policy with default retention policy
        - No Object Versioning, No Retention 
        - Encryption: Use Google-managed encryption key
- Google Secret Manager
- KMS
- MemoryStore
- Pub/Sub
- Cloud SQL