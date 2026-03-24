Spring Boot scaffold for migrating Node.js backend.

Quick start:

1. Build:

```bash
mvn clean package
```

2. Run (set MongoDB url env):

```bash
set MONGODB_URL=mongodb://localhost:27017/doan
mvn spring-boot:run
```

API paths mirror the Node routes: `/api/car`, `/api/brandCar`, `/api/bookings`, `/api/payments`, `/api/comment`, `/api/user`, `/api/upload`.
