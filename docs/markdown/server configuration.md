# 콘서트 예약 서비스 패키지 구조 설명 및 기술 스택

## 패키지 구조 설명
```angular2html
com.hhp.ConcertReservation
├── application
│   ├── controller
│   └── dto
├── domain
│   ├── entity
│   ├── facade
│   └── service
└── infra
├── entity
└── repository
ConcertReservationApplication
```

## 기술 스택
### 기본구현
-  Java Spring Framework
- Spring Web (Spring MVC)
- Spring Data JPA
- H2 Database

---
### 고도화
- Kafka
- Redis
