# Java Scientific Calculator – Web App on Tomcat

A full-stack **Java Servlet + MySQL** scientific calculator, containerised with **Docker** and ready to deploy on **AWS EC2**.

> Originally a Java Swing desktop app — converted to a proper web application deployable on Apache Tomcat.

## Tech Stack

| Layer      | Technology                         |
|------------|------------------------------------|
| Frontend   | HTML5 / Vanilla CSS / JavaScript   |
| Backend    | Java 17 Servlet (Jakarta EE)       |
| Server     | Apache Tomcat 10.1                 |
| Database   | MySQL 8.0                          |
| Build      | Maven 3.9 (WAR packaging)          |
| Container  | Docker + Docker Compose            |
| Cloud      | AWS EC2 (Ubuntu 22.04)             |

## Project Structure

```
calculator/
├── Dockerfile              # Multi-stage: Maven build → Tomcat runtime
├── docker-compose.yml      # Orchestrates Tomcat + MySQL
├── init.sql                # Creates DB schema on first boot
├── pom.xml                 # Maven build (produces calculator.war)
└── src/
    └── main/
        ├── java/calculator/
        │   ├── Calculator.java         # Core arithmetic & scientific logic
        │   ├── DatabaseManager.java    # MySQL JDBC (reads env vars)
        │   ├── CalculatorServlet.java  # POST /api/calculate → JSON
        │   └── HistoryServlet.java     # GET  /api/history   → JSON
        └── webapp/
            ├── WEB-INF/web.xml         # Jakarta EE deployment descriptor
            └── index.html              # Dark-themed calculator UI
```

## API Endpoints

### `POST /api/calculate`
```json
// Request
{ "operand1": 45, "operand2": 0, "operator": "sin" }

// Response
{ "result": 0.7071067811865476, "error": null }
```

Supported operators: `+` `-` `*` `/` `%` `sin` `cos` `tan` `cosec` `sec` `cot` `log` `antilog`

### `GET /api/history`
Returns last 100 calculations as a JSON array.

---

## Quick Start (Docker locally)

```bash
git clone https://github.com/umar967/java-calculator.git
cd java-calculator
docker compose up --build
```

Open: **http://localhost:8080**

---

## AWS EC2 Deployment

See the full step-by-step guide → [DEPLOYMENT.md](DEPLOYMENT.md)

**TL;DR:**
1. Launch an Ubuntu 22.04 EC2 instance (t2.micro)
2. Open ports 22 and 8080 in the Security Group
3. SSH in, install Docker
4. Clone this repo and run `docker compose up --build -d`
5. Visit `http://<YOUR-EC2-PUBLIC-IP>:8080`
