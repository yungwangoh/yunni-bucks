## 프로젝트 구조

### Main
```bash
.main
├── java
│   └── sejong
│       └── coffee
│           └── yun
│               ├── config
│               │   ├── auditing
│               │   ├── querydsl
│               │   ├── redis
│               │   └── web
│               ├── controller
│               │   ├── advise
│               │   ├── ocr
│               │   └── pay
│               ├── custom
│               │   └── annotation
│               ├── domain
│               │   ├── delivery
│               │   ├── discount
│               │   │   ├── condition
│               │   │   ├── policy
│               │   │   └── type
│               │   ├── exception
│               │   ├── order
│               │   │   └── menu
│               │   ├── pay
│               │   └── user
│               ├── dto
│               │   ├── card
│               │   ├── cart
│               │   ├── delivery
│               │   ├── error
│               │   ├── menu
│               │   ├── ocr
│               │   ├── order
│               │   ├── pay
│               │   ├── review
│               │   │   └── menu
│               │   ├── thumbnail
│               │   └── user
│               ├── infra
│               │   └── port
│               ├── interceptor
│               ├── jwt
│               ├── mapper
│               ├── message
│               ├── repository
│               │   ├── card
│               │   │   ├── impl
│               │   │   └── jpa
│               │   ├── cart
│               │   │   ├── fake
│               │   │   ├── impl
│               │   │   └── jpa
│               │   ├── cartitem
│               │   │   ├── impl
│               │   │   └── jpa
│               │   ├── delivery
│               │   │   ├── impl
│               │   │   └── jpa
│               │   ├── menu
│               │   │   ├── impl
│               │   │   └── jpa
│               │   ├── order
│               │   │   ├── impl
│               │   │   └── jpa
│               │   ├── pay
│               │   │   ├── impl
│               │   │   └── jpa
│               │   ├── redis
│               │   ├── review
│               │   │   └── menu
│               │   │       ├── impl
│               │   │       └── jpa
│               │   ├── thumbnail
│               │   │   ├── impl
│               │   │   └── jpa
│               │   └── user
│               │       ├── impl
│               │       └── jpa
│               ├── resolver
│               ├── service
│               └── util
│                   ├── jwt
│                   ├── parse
│                   ├── password
│                   └── regex
└── resources
```

### Test
```bash
.test
├── java
│   └── sejong
│       └── coffee
│           └── yun
│               ├── config
│               ├── controller
│               │   ├── mock
│               │   └── pay
│               ├── domain
│               │   ├── card
│               │   ├── delivery
│               │   ├── discount
│               │   │   ├── condition
│               │   │   └── policy
│               │   ├── order
│               │   │   └── menu
│               │   ├── pay
│               │   └── user
│               ├── integration
│               │   ├── card
│               │   ├── delivery
│               │   ├── menu
│               │   ├── ocr
│               │   ├── order
│               │   ├── pay
│               │   └── user
│               ├── jwt
│               ├── mapper
│               ├── mock
│               │   └── repository
│               ├── repository
│               │   ├── card
│               │   ├── delivery
│               │   │   └── fake
│               │   ├── menu
│               │   ├── order
│               │   │   └── fake
│               │   ├── pay
│               │   ├── redis
│               │   └── user
│               │       ├── fake
│               │       └── impl
│               ├── service
│               │   ├── card
│               │   ├── externalApi
│               │   ├── fake
│               │   ├── mock
│               │   └── pay
│               └── util
│                   └── password
└── resources
    └── sql
```
