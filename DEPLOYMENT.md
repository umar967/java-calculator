# 🚀 AWS EC2 Deployment Guide
## Java Calculator → Docker → Tomcat on Ubuntu

---

## Prerequisites

- An **AWS account** (free tier works — t2.micro)
- Your **GitHub repo** is live at: https://github.com/umar967/java-calculator
- A laptop with an SSH client (Windows: PowerShell / PuTTY / Windows Terminal)

---

## PART 1 — Launch an EC2 Instance

### Step 1 · Open EC2 Dashboard
1. Log in to the [AWS Console](https://console.aws.amazon.com)
2. Search for **EC2** → click **Launch Instance**

### Step 2 · Configure the Instance

| Setting | Value |
|---|---|
| **Name** | `calculator-server` |
| **AMI** | `Ubuntu Server 22.04 LTS (HVM)` |
| **Instance type** | `t2.micro` (free tier eligible) |
| **Key pair** | Create new → name it `calc-key` → download `.pem` file → **SAVE IT** |
| **Network** | Default VPC is fine |

### Step 3 · Configure Security Group (Firewall Rules)

In the "Network settings" section, click **Edit** and add these **Inbound Rules**:

| Type | Protocol | Port | Source | Purpose |
|---|---|---|---|---|
| SSH | TCP | 22 | My IP | Your SSH access |
| Custom TCP | TCP | 8080 | 0.0.0.0/0 | Tomcat web access |

> [!IMPORTANT]
> Port 8080 must be open to `0.0.0.0/0` so anyone can reach the calculator in a browser.

### Step 4 · Launch
Click **Launch Instance** → wait ~1 minute → click your instance → copy the **Public IPv4 address**.

---

## PART 2 — Connect to EC2 via SSH

### On Windows (PowerShell)

```powershell
# Move your key to a good location first
Move-Item "$env:USERPROFILE\Downloads\calc-key.pem" "$env:USERPROFILE\.ssh\calc-key.pem"

# Fix permissions (required or SSH will reject the key)
icacls "$env:USERPROFILE\.ssh\calc-key.pem" /inheritance:r /grant:r "${env:USERNAME}:R"

# SSH into the server  (replace with YOUR EC2 public IP)
ssh -i "$env:USERPROFILE\.ssh\calc-key.pem" ubuntu@<YOUR-EC2-PUBLIC-IP>
```

> [!TIP]
> Accept the fingerprint prompt by typing `yes` and pressing Enter. You're now inside the Ubuntu server.

---

## PART 3 — Install Docker on the Server

Run these commands **inside your EC2 SSH session** (copy-paste each block):

```bash
# 1. Update package list
sudo apt update -y

# 2. Install Docker Engine
sudo apt install -y docker.io

# 3. Install Docker Compose plugin
sudo apt install -y docker-compose-plugin

# 4. Start Docker and enable it on boot
sudo systemctl start docker
sudo systemctl enable docker

# 5. Add ubuntu user to docker group (no sudo needed for docker)
sudo usermod -aG docker ubuntu

# 6. Apply the group change (log out and back in, or run:)
newgrp docker

# Verify everything works
docker --version
docker compose version
```

Expected output: `Docker version 24.x.x` and `Docker Compose version v2.x.x`

---

## PART 4 — Clone the Repo & Deploy

```bash
# 1. Install git (usually pre-installed, but just in case)
sudo apt install -y git

# 2. Clone your GitHub repository
git clone https://github.com/umar967/java-calculator.git

# 3. Enter the project directory
cd java-calculator

# 4. Build and start both containers in the background (-d = detached)
docker compose up --build -d
```

> [!NOTE]
> The **first build takes 3–5 minutes** — Maven downloads dependencies and compiles the WAR file. Subsequent starts are instant (Docker caches layers).

### Watch the build logs (optional)

```bash
docker compose logs -f
```

Press `Ctrl+C` to stop watching logs (containers keep running).

---

## PART 5 — Verify the Deployment

### Check containers are running

```bash
docker compose ps
```

Expected output:
```
NAME               IMAGE     STATUS
calculator-db      mysql:8.0 Up (healthy)
calculator-app     ...       Up
```

### Open in browser

```
http://<YOUR-EC2-PUBLIC-IP>:8080
```

You should see the **dark-themed scientific calculator** UI.

### Test the API directly

```bash
curl -X POST http://localhost:8080/api/calculate \
     -H "Content-Type: application/json" \
     -d '{"operand1": 10, "operand2": 5, "operator": "+"}'
```

Expected: `{"result":15.0,"error":null}`

---

## PART 6 — Understanding What's Running

```
┌─────────────────────────────────────────────────────┐
│                   EC2 Ubuntu VM                     │
│                                                     │
│   ┌───────────────────┐   ┌────────────────────┐   │
│   │  calculator-app   │   │  calculator-db     │   │
│   │  (Tomcat 10.1)    │──▶│  (MySQL 8.0)       │   │
│   │  Port: 8080       │   │  Port: 3306        │   │
│   │                   │   │  (internal only)   │   │
│   └───────────────────┘   └────────────────────┘   │
│            │                                        │
└────────────│────────────────────────────────────────┘
             │ Public
             ▼
     Browser :8080
```

- **calculator-app** — Tomcat serves your WAR. Handles HTTP at port 8080.
- **calculator-db** — MySQL stores calculation history. Only reachable by the app container (not from the internet).
- Both talk to each other on an **internal Docker network** (hostname `db`).

---

## PART 7 — Useful Commands

### Stop the app
```bash
docker compose down
```

### Restart after a reboot
```bash
cd ~/java-calculator
docker compose up -d
```

### Pull latest code from GitHub and redeploy
```bash
cd ~/java-calculator
git pull
docker compose up --build -d
```

### View Tomcat logs
```bash
docker compose logs app -f
```

### View MySQL logs
```bash
docker compose logs db -f
```

### Connect to MySQL inside Docker (inspect data)
```bash
docker exec -it calculator-db mysql -u root -p12345 calculator
```
Then inside MySQL:
```sql
SELECT * FROM history ORDER BY created_at DESC LIMIT 10;
```

---

## Troubleshooting

| Symptom | Fix |
|---|---|
| Browser shows "Connection refused" | Check Security Group has port 8080 open |
| `docker compose` not found | Use `docker-compose` (with hyphen) on older installs |
| Build fails with "no space left" | Run `docker system prune -f` to free disk |
| DB connection error in app | Wait 30s more — MySQL takes time to initialise |
| SSH "Permission denied" | Run the `icacls` fix-permissions command again |

---

## What You Learned

```
GitHub Repo
    │
    │  git clone (on EC2)
    ▼
docker compose up --build
    │
    ├─► Stage 1: Maven compiles Java → calculator.war
    │
    └─► Stage 2: WAR copied into Tomcat → Tomcat starts → App live!
              │
              └─► MySQL container starts → init.sql creates schema
```

1. **Dockerfile** — tells Docker how to build the image in two stages
2. **docker-compose.yml** — tells Docker how to run multiple containers together
3. **Tomcat** — Java web server that loads and runs the `.war` file
4. **Jakarta Servlet** — Java class that handles HTTP requests from the browser
5. **init.sql** — SQL script auto-executed by MySQL on first boot
