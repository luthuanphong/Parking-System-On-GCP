
# 🧱 Terraform Common Commands

Here’s a **comprehensive list of common Terraform commands**, organized by category — from setup to debugging — with expert-level explanations and typical use cases.

---

## 🧱 1. Initialization & Setup

### `terraform init`
> Initializes a Terraform working directory.

- Downloads required **providers**, **modules**, and **plugins**.  
- Sets up the **backend** for state storage.  
- Must be run **once per workspace** (or after modifying providers/modules).

```bash
terraform init
terraform init -upgrade     # Upgrade all providers/modules to latest allowed versions
terraform init -reconfigure # Reinitialize backend configuration
```

---

## ⚙️ 2. Validation & Formatting

### `terraform validate`
> Checks that your configuration syntax is valid and internally consistent.

```bash
terraform validate
```

### `terraform fmt`
> Formats `.tf` files according to canonical style.

```bash
terraform fmt
terraform fmt -recursive    # Format all subdirectories
```

---

## 🔍 3. Planning Changes

### `terraform plan`
> Creates an **execution plan** showing what will be added, changed, or destroyed — without applying it.

- Detects drift between configuration and current state.  
- Safe way to preview what Terraform *will do*.

```bash
terraform plan
terraform plan -out=plan.out   # Save plan to file
terraform plan -var="region=us-west-2"   # Pass variable inline
terraform show plan.out        # View saved plan
```

---

## 🚀 4. Applying & Destroying

### `terraform apply`
> Executes the plan and applies changes to real infrastructure.

```bash
terraform apply
terraform apply plan.out
terraform apply -auto-approve   # Skip interactive approval
```

### `terraform destroy`
> Destroys all managed infrastructure.

```bash
terraform destroy
terraform destroy -target=aws_instance.web
```

---

## 📦 5. State Management (Advanced)

### `terraform state list`
> Lists all resources in the current state file.

```bash
terraform state list
```

### `terraform state show <resource>`
> Shows detailed attributes of a resource from state.

```bash
terraform state show aws_instance.web
```

### `terraform state mv <source> <destination>`
> Moves items within the state (useful when refactoring).

```bash
terraform state mv aws_instance.old aws_instance.new
```

### `terraform state rm <resource>`
> Removes a resource from the state (does **not** destroy it).

```bash
terraform state rm aws_instance.orphan
```

---

## 🧩 6. Output & Data Extraction

### `terraform output`
> Displays output values defined in `output.tf`.

```bash
terraform output
terraform output instance_public_ip
terraform output -json | jq '.instance_public_ip.value'
```

---

## 🌎 7. Workspace Management

> Workspaces allow multiple state files for the same configuration (e.g., dev, stage, prod).

```bash
terraform workspace list
terraform workspace new staging
terraform workspace select prod
terraform workspace show
```

---

## 🧰 8. Importing Existing Infrastructure

### `terraform import`
> Brings existing resources under Terraform management.

```bash
terraform import aws_instance.web i-0c1234567890abcd
```

- Updates the **state** but not your configuration.
- You must still manually write matching `.tf` resource blocks.

---

## 🧪 9. Debugging & Inspection

### `terraform show`
> Shows the current state or saved plan in human-readable or JSON format.

```bash
terraform show
terraform show -json
```

### `terraform providers`
> Lists providers used and their versions.

```bash
terraform providers
terraform providers lock   # Generate dependency lock file
```

### `TF_LOG`
> Environment variable for verbose debugging logs.

```bash
export TF_LOG=DEBUG
terraform apply
```

---

## 🧼 10. Clean-up & Maintenance

### `terraform refresh`
> Syncs state file with real-world infrastructure (deprecated — replaced by `terraform apply -refresh-only`).

```bash
terraform apply -refresh-only
```

### `terraform uninstall`
> Removes downloaded providers/modules (rarely used manually).

---

## 💡 Expert Tip

A common **Terraform workflow** looks like this:

```bash
terraform init
terraform fmt
terraform validate
terraform plan -out=tfplan
terraform apply tfplan
terraform output
```

---
