# E-Commerce REST API

A simple, robust e-commerce REST API built with Java 21, Spring Boot, MySQL, and Elasticsearch. It fetches dummy product data on startup and provides high-performance querying and full-text search capabilities.

## 🚀 How to Run the App Locally

This project is fully dockerized. You do not need to install MySQL, Elasticsearch, or Maven on your local machine to run it—just Docker.

1. Ensure **Docker** and **Docker Compose** are running on your machine.
2. Open a terminal in the root directory of this project (where `docker-compose.yml` is located).
3. Run the following command:
   ```bash
   docker compose up --build