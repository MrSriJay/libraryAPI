# Library API

## Setup
1. Install Java 17, Maven, MySQL, Docker, and kubectl.
2. Create MySQL database: `librarydb` (e.g., `CREATE DATABASE librarydb;`).
3. Run locally: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
4. Tables are automatically created on startup (`spring.jpa.hibernate.ddl-auto=create`). Sample data (5 borrowers, 10 books) is inserted via `src/main/resources/data.sql`. Alternatively, use `schema.sql` with `spring.jpa.hibernate.ddl-auto=none` and `spring.sql.init.mode=always` for manual schema control.

## API Documentation
- **Swagger UI**: Access at `http://<service-ip>:80/swagger-ui.html` (Kubernetes) or `http://localhost:8080/swagger-ui.html` (local/Docker).
- **OpenAPI JSON**: Access at `/v3/api-docs`.

## Endpoints
- **POST /api/borrowers**
    - Body: `{"name": "John Doe", "email": "john@example.com"}`
    - Response: 201, `{"id": 1, "name": "John Doe", "email": "john@example.com"}`
- **POST /api/books**
    - Body: `{"isbn": "1234567890", "title": "Book Title", "author": "Author Name"}`
    - Response: 201, `{"id": 1, "isbn": "1234567890", "title": "Book Title", "author": "Author Name", "borrowedById": null}`
- **GET /api/books**
    - Response: 200, array of BookResponse
- **POST /api/borrowers/{borrowerId}/borrow/{bookId}**
    - No body
    - Response: 200 OK (or 400 if already borrowed)
- **POST /api/borrowers/{borrowerId}/return/{bookId}**
    - No body
    - Response: 200 OK (or 400 if not borrowed by you)

## Sample Data
- 5 borrowers and 10 books (with some shared ISBNs) are inserted on startup via `data.sql`.
- Check via `GET /api/books` or MySQL queries (e.g., `SELECT * FROM Borrower; SELECT * FROM Book;`).

## Tests
- Run unit tests: `mvn test`
- Tests cover `BorrowerService`, `BookService`, `BorrowerController`, and `BookController` (in `src/test/java/com/libraryApi/library/`).
- For coverage, add JaCoCo plugin to `pom.xml` and run `mvn jacoco:report`.

## Docker Setup
1. **Build the Docker Image**:
   ```bash
   docker build -t library-api:1.0.0 .
   ```
2. **Run MySQL Container** (if not using local MySQL):
   ```bash
   docker run -d --name mysql-library -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=librarydb mysql:8.0
   ```
3. **Run Application Container**:
    - If MySQL is on `localhost:3306` (host machine):
      ```bash
      docker run -d --name library-api -p 8080:8080 --network host -e DB_URL=jdbc:mysql://localhost:3306/librarydb -e DB_USERNAME=root -e DB_PASSWORD=root library-api:1.0.0
      ```
    - If using MySQL container:
      ```bash
      docker network create library-network
      docker network connect library-network mysql-library
      docker run -d --name library-api -p 8080:8080 --network library-network -e DB_URL=jdbc:mysql://mysql-library:3306/librarydb -e DB_USERNAME=root -e DB_PASSWORD=root library-api:1.0.0
      ```
4. **Access the API**:
    - API: `http://localhost:8080`
    - Swagger UI: `http://localhost:8080/swagger-ui.html`
5. **Stop Containers**:
   ```bash
   docker stop library-api mysql-library
   docker rm library-api mysql-library
   docker network rm library-network
   ```

## Kubernetes Setup
1. **Build and Load Docker Image**:
    - Build: `docker build -t library-api:1.0.0 .`
    - For Minikube:
      ```bash
      minikube image load library-api:1.0.0
      ```
    - For cloud clusters, push to a registry (e.g., Docker Hub):
      ```bash
      docker tag library-api:1.0.0 yourusername/library-api:1.0.0
      docker push yourusername/library-api:1.0.0
      ```
      Update `image` in `k8s-deployment.yaml` to `yourusername/library-api:1.0.0`.
2. **Set Up MySQL**:
    - If using external MySQL (e.g., local or cloud):
        - Update `DB_URL` in `k8s-deployment.yaml` (e.g., `jdbc:mysql://host.docker.internal:3306/librarydb` for local MySQL on Docker Desktop).
        - Remove MySQL `Deployment` and `Service` from `k8s-deployment.yaml`.
    - If using MySQL in Kubernetes, keep the MySQL `Deployment` and `Service`.
3. **Apply Kubernetes Configuration**:
   ```bash
   kubectl apply -f k8s-deployment.yaml
   ```
4. **Access the API**:
    - Get the service IP (for LoadBalancer):
      ```bash
      kubectl get svc library-api-service
      ```
      Use the `EXTERNAL-IP` (e.g., `http://<external-ip>:80/swagger-ui.html`).
    - For Minikube (NodePort):
      ```bash
      minikube service library-api-service --url
      ```
5. **Clean Up**:
   ```bash
   kubectl delete -f k8s-deployment.yaml
   ```

## Deployment
- **Docker**: See Docker Setup section.
- **Kubernetes**: See Kubernetes Setup section.