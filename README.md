# MoviesWeb Backend

Backend của hệ thống quản lý phim **MoviesWeb** được xây dựng trên **Spring Boot 3.5.6** với **PostgreSQL** làm cơ sở dữ liệu. Hỗ trợ quản lý phim, thể loại, quốc gia, phiên bản, máy chủ, người dùng, phân quyền và xác thực bằng JWT. Backend cũng hỗ trợ import dữ liệu phim hàng loạt qua CSV và cache dữ liệu để tối ưu hiệu năng.

---

## Công nghệ chính

- **Framework:** [Spring Boot 3.5.6](https://spring.io/projects/spring-boot)
- **Cơ sở dữ liệu:** [PostgreSQL](https://www.postgresql.org/)
- **Xác thực & Phân quyền:** Spring Security + JWT ([jjwt](https://github.com/jwtk/jjwt))
- **ORM:** Spring Data JPA
- **Validation:** Spring Boot Starter Validation
- **Caching:** Spring Cache + [Caffeine](https://github.com/ben-manes/caffeine)
- **CSV Import:** [OpenCSV](http://opencsv.sourceforge.net/)
- **Tiện ích:** [Lombok](https://projectlombok.org/) để giảm boilerplate
- **ID Generator:** [jnanoid](https://github.com/aventrix/jnanoid)
- **Testing:** Spring Boot Starter Test, Spring Security Test
- **DevTools:** Spring Boot DevTools cho hot reload khi phát triển
- **Docker:** Hỗ trợ chạy backend trong container Docker, có thể build và chạy với multi-stage Dockerfile.

---

## Tính năng chính

- Tìm kiếm phim theo bộ lọc, xem danh sách phim, chi tiết phim
- CRUD:
    - Phim, thể loại, quốc gia, phiên bản, máy chủ
- Upload dữ liệu phim hàng loạt bằng file CSV

_Kết quả trả về được phân trang với dữ liệu lớn_

---

## Cấu trúc dự án

- src/main/java/com/movieweb/
  - comment/ # Chứa utils, xử lý phản hồi,..
  - config/  # Các file config (security, JWT, cache, CORS,...)
  - modules/ # Các module theo entity, phân tầng
    - movie/
      - dto/ # Movie DTO
      - entity/ # Movie entity
      - repository/ # Movie repository
      - service/ # Business logic
      - controller/ # REST API
      - init/ # Init dữ liệu mặc định
    - ...
- src/main/resources/
  - application.properties # Cấu hình môi trường
- pom.xml



---
## Cấu hình môi trường

File `application.properties` mẫu:

```properties
spring.application.name=movieweb
spring.mvc.servlet.path=/api/v1

# Admin default
admin.username=admin
admin.password=123456

# JWT configuration
jwt.secret=your_jwt_secret
jwt.expiration=millisecond 

# PostgreSQL Production
spring.datasource.url=jdbc:postgresql://<host>:<port>/<database>?sslmode=require&channelBinding=require
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate / JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# HikariCP
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# Logging
logging.level.org.hibernate.SQL=ERROR
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=ERROR
```

Lưu ý:
- Cập nhật các thông số database cho phù hợp với môi trường phát triển hoặc production.
- JWT secret cần bảo mật.
- Có thể dùng spring.jpa.hibernate.ddl-auto=update cho dev, validate cho production.

## Cài đặt & chạy dự án
### 1. Cài đặt dependencies
```bash
mvn clean install
    # hoặc
./mvnw clean install
```
### 2. Chạy backend

```bash
mvn spring-boot:run
    # hoặc
./mvnw spring-boot:run
```
_Backend mặc định chạy tại http://localhost:8080/api/v1_

### Chạy bằng Docker
#### 1. Build Docker image

```bash
docker build -t movieweb-backend:latest .  
```

#### 2. Chạy container
```bash
docker run -p 8080:8080 movieweb-backend:latest
```

#### 3. Dừng container
```bash
docker stop movieweb-backend
docker rm movieweb-backend
```

- movieweb-backend:latest là tên image + tag.
- --name movieweb-backend là tên container.

_Backend sẽ chạy tại http://localhost:8080/api/v1_


## Lưu ý 
- **Cấu hình mặc định:**  
  - File `application.properties` đã chứa cấu hình database, JWT secret, và tài khoản admin mặc định (`admin / 123456`). Nếu muốn thay đổi, có thể sửa trực tiếp trong file hoặc override bằng **biến môi trường** khi chạy Docker hoặc chạy trực tiếp bằng Java.
- **Init khởi tạo dữ liệu:**
  - Các module có thư mục `init` chứa logic và dữ liệu để khởi tạo khi lần đầu kết nối database.
  - Có thể bật/tắt init bằng cách **bỏ comment hoặc comment.
  - Init dữ liệu chỉ chạy một lần khi database được kết nối và dữ liệu tương ứng trống.
- **Cache:**  
  - Backend sử dụng Spring Cache + Caffeine để cache dữ liệu, điều chỉnh thời hạn cache phù hợp (nếu muốn tại config)

- **Port:**
  - Backend chạy mặc định trên port `8080`.
  - Nếu port bị chiếm dụng, có thể chỉnh `server.port` trong `application.properties` hoặc khi chạy Docker map port khác.

- **Security / JWT:**
  - JWT secret trong `application.properties` là giá trị demo, **khuyến cáo thay bằng secret an toàn** cho môi trường production.
  - Tất cả API bảo vệ bằng Spring Security, mặc định yêu cầu đăng nhập, có phân quyền.
  - Sử dụng các anotation PublicEndpoint cho endpoint không cần bảo vệ và RoleRequired("ROLE_NAME") cho các endpoint yêu cầu role cụ thể.

- **Import CSV:**
  - Cần đảm bảo file CSV đúng định dạng và mapping chính xác với entity tương ứng.

## Trần Huỳnh Chương