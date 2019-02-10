# Loan manager Demo
Standard maven/spring boot configuration
mvn clean install to build the project
mvn spring-boot:run to run the project
All external property configuration placed in application.properties file. 

Happy reviewing!

### Endpoints

| Method | Url | Decription |
| ------ | --- | ---------- |
| GET    |/actuator/info  | info / heartbeat - provided by boot |
| GET    |/actuator/health| application health - provided by boot |
| GET    |/v2/api-docs    | swagger json |
| GET    |/swagger-ui.html| swagger html |
| GET    |/v1/loan/{id}| get loan by id |
| GET    |/v1/loans    | get N loans with an offset|
| POST   |/v1/loan     | add loan|
| PUT    |/v1/loan/extend     | extend loan|
