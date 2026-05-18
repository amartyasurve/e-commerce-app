# E-Commerce REST API

A simple, robust e-commerce REST API built with Java 21, Spring Boot, MySQL, and Elasticsearch. It fetches dummy product data on startup and provides high-performance querying and full-text search capabilities.


# Example Endpoints to Test:
1. All Products: GET http://localhost:8080/products

2. Categories: GET http://localhost:8080/categories

3. Filter by Category: GET http://localhost:8080/products?category=laptops

4. Full-text Search (Elasticsearch): GET http://localhost:8080/products?query=apple

5. Single Product: GET http://localhost:8080/products/1


## 🧠 Design Choices & Thought Process
1. SOLID Principles: * Single Responsibility: Separated database entities (Product) from Elasticsearch documents (ProductDocument) and API DTOs (ProductDto). Each layer (Controller, Service, Repository) has one strict job.

2. Dependency Inversion: Controllers depend on ProductService interfaces rather than implementations, allowing for easier testing and future modifications.

3. CQRS-lite Pattern: Used MySQL as the primary Source of Truth (SoT) for exact matches and data storage, while leveraging Elasticsearch specifically for full-text search queries via the ?query= parameter.

4. Automated Data Ingestion: Utilized Spring's @PostConstruct to gracefully seed the databases on app startup. It features a check (count == 0) to prevent data duplication upon container restarts.

## ⚠️ Trade-offs & Known Limitations
1. Security: Elasticsearch security (xpack.security.enabled) is set to false in docker-compose.yml for ease of local development and out-of-the-box running. In a production environment, this must be secured with TLS and basic authentication.

2. Schema Design: For simplicity and mapping directly to the DummyJSON structure, Category is stored as a string within the Product table rather than a normalized relational mapping (e.g., a separate Category table with a @ManyToOne relationship). For a massive application, normalizing this would be beneficial.

3. Pagination: The current API returns all products at once. For production APIs, returning 100+ items should be paginated using Spring Data's Pageable.

## 🚀 How to Run the App Locally

This project is fully dockerized. You do not need to install MySQL, Elasticsearch, or Maven on your local machine to run it—just Docker.

1. Ensure **Docker** and **Docker Compose** are running on your machine.
2. Open a terminal in the root directory of this project (where `docker-compose.yml` is located).
3. Run the following command:
   ```bash
   docker compose up --build