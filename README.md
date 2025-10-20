# Parking System on GCP

A comprehensive parking reservation system built with Spring Boot backend and Angular frontend, deployed on Google Cloud Platform using Terraform for infrastructure as code.

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular UI    │───▶│   Cloud Run     │───▶│   Cloud SQL     │
│   (Frontend)    │    │   (Backend)     │    │  (PostgreSQL)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │  Memorystore    │
                       │    (Redis)      │
                       └─────────────────┘
```

## 📁 Project Structure

```
├── BE/                     # Backend (Spring Boot)
│   ├── src/main/java/
│   ├── Dockerfile
│   └── pom.xml
├── FE/                     # Frontend (Angular)
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── DB/                     # Database schemas
│   └── schemas.sql
├── Infras/                 # Infrastructure (Terraform)
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── terraform.tfvars
└── README.md
```

## 🚀 Quick Start

### Prerequisites
- Node.js 18+
- Java 17+
- Docker
- Terraform
- GCP Account with billing enabled

### 1. Infrastructure Setup
```bash
cd Infras
terraform init
terraform plan
terraform apply
```

### 2. Backend Setup
```bash
cd BE
./mvnw clean package
docker build -t parking-backend .
```

### 3. Frontend Setup
```bash
cd FE
npm install
npm run build
docker build -t parking-frontend .
```

## 🛠️ Terraform Infrastructure

### File Structure
- **`main.tf`** - Main infrastructure configuration
- **`variables.tf`** - Variable definitions
- **`outputs.tf`** - Output values
- **`terraform.tfvars`** - Variable values (⚠️ **Not recommended for sensitive data**)

> **Security Note:** Use Google Secret Manager for sensitive variables instead of plain text files.

### Common Commands
For detailed Terraform commands, refer to [.terraform_common_commands.md](.terraform_common_commands.md)

## ☁️ GCP Resources

### 🔐 Identity & Access Management (IAM)
- **Purpose**: Define access permissions for service accounts
- **Components**:
  - **Custom Roles**: Basic/Predefined/Custom types
  - **Role Launch Stage**: GA (Alpha/Beta/GA/Disabled)
  - **Service Accounts**: Created via Terraform
  - **Role Bindings**: Link custom roles to service accounts

### 🪣 Cloud Storage Bucket
- **Purpose**: Store Terraform state files
- **Configuration**:
  - **Region**: Singapore (`asia-southeast1`)
  - **Storage Class**: Standard
  - **Hierarchy Namespace**: Disabled
  - **Anywhere Cache**: Disabled
  - **Access Control**: Uniform (Prevent public access)
  - **Data Protection**:
    - ✅ Soft-delete policy with default retention
    - ❌ Object Versioning
    - ❌ Retention policies
  - **Encryption**: Google-managed encryption keys

### 🔒 Google Secret Manager
- **Purpose**: Securely store sensitive data
- **Usage**: Integrated with Terraform for provisioning resources like:
  - Cloud SQL credentials
  - Cloud Run environment variables
  - API keys and tokens

### 🗝️ Cloud KMS (Key Management Service)
- **Purpose**: Cryptographic key management and encryption services
- **Features**:
  - Key rotation policies
  - Hardware Security Module (HSM) support
  - Integration with other GCP services

### 🐳 Google Container Registry (GCR)
- **Purpose**: Store and manage Docker container images
- **Integration**: Used for Cloud Run deployments

### ⚡ Memorystore (Redis)
- **Purpose**: In-memory data store for caching
- **Configuration**:
  - **Network**: Private VPC network
  - **Tier**: Basic/Standard HA
  - **Memory**: Configurable (1GB default)
  - **Version**: Redis 6.x

### 🗄️ Cloud SQL
- **Purpose**: Managed PostgreSQL database
- **Configuration**:
  - **Provider**: PostgreSQL 14
  - **Machine Type**: `db-custom-2-8192`
    - **CPU**: 2 vCPUs
    - **Memory**: 8GB RAM
  - **Storage**:
    - **Type**: SSD (default)
    - **Size**: 10GB (default, auto-expandable)
  - **Database Flags**:
    - `log_connections`: Enable connection logging
    - `log_min_error_statement`: Log error statements
  - **Security**:
    - SSL/TLS enforcement
    - Private IP configuration
    - Automated backups

### 📨 Pub/Sub
- **Purpose**: Asynchronous messaging service
- **Use Cases**:
  - Event-driven architecture
  - Decoupling services
  - Real-time notifications

### 🏃 Cloud Run
- **Purpose**: Host containerized backend application
- **Features**:
  - **Serverless**: Automatic scaling to zero
  - **Container-based**: Supports any language/framework
  - **Underlying Technology**: Knative on Kubernetes
  - **Traffic Management**: Blue/green deployments
  - **Security**: Service-to-service authentication

### Solutions description
- Version 1.0
- Version 2.0


## 🔧 Configuration

### Environment Variables
```bash
# Backend Configuration
SPRING_PROFILES_ACTIVE=prod
DB_HOST=<cloud-sql-connection-name>
REDIS_HOST=<memorystore-ip>
GCP_PROJECT_ID=<your-project-id>

# Frontend Configuration
API_BASE_URL=<backend-cloud-run-url>
```

### Security Best Practices
- ✅ Use Secret Manager for sensitive data
- ✅ Enable IAM roles with least privilege
- ✅ Use private networks for databases
- ✅ Enable SSL/TLS for all connections
- ✅ Implement proper authentication and authorization

## Service Account
# For deployment
- Name:
- Roles:
# For cloudrun
- Name:
- Roles:

## 📊 Monitoring & Observability
- **Cloud Logging**: Application and infrastructure logs
- **Cloud Monitoring**: Metrics and alerting
- **Error Reporting**: Application error tracking
- **Cloud Trace**: Distributed tracing

## 🚀 Deployment Pipeline
1. **Infrastructure**: Deploy via Terraform
2. **Database**: Apply schema migrations
3. **Backend**: Deploy to Cloud Run
4. **Frontend**: Deploy to Cloud Run or Cloud Storage + CDN

## 🤝 Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📄 License
This project is licensed under the MIT License.

## 📞 Support
For questions and support, please open an issue in the GitHub repository.

