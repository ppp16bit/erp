This project is a RESTful API for managing business resources (ERP), developed in Java. It provides endpoints for managing customers, products, orders, and more.

## Prerequisites

- [Docker](https://www.docker.com/get-started) installed
- [Docker Compose](https://docs.docker.com/compose/install/) installed

## Running with Docker

Clone the repository:

   ```bash
   git clone https://github.com/ppp16bit/erp.git
   cd erp
   ```

Build the project:

   ```bash
   ./mvnw clean package
   ```

Build the Docker image:

   ```bash
   docker build -t erp-image .
   ```

Start the containers using Docker Compose:

   ```bash
   docker-compose up -d
   ```

   This will start the application on port `8080`.