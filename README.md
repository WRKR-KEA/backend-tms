# backend-tms

## 프로젝트 개요
### 본 프로젝트는 사내 업무 관리 시스템의 백엔드 서버를 담당하고 있습니다.
- **프로젝트 명**: backend-tms
- **프로젝트 기간**: 2025.01.06. ~ 2025.02.18.
- **프로젝트 목적**: 사내 업무 관리 시스템의 백엔드 서버 구축
- **프로젝트 버전**: 0.1.0
- **주요 기능**: 
  - 사용자 관리
  - 업무(티켓) 관리
  - 프로젝트 관리
  - 일정 관리
  - 소통 기능
  - 알림 기능

## 프로젝트 구조
### 1. 패키지 구조
```plaintext
├─domains
│  └─[domain]
│     ├─application
│     │  ├─dto
│     │  │  ├─request
│     │  │  └─response
│     │  ├─mapper
│     │  └─usecase
│     ├─domain
│     │  ├─model
│     │  └─service
│     ├─exception
│     ├─persistence
│     │  ├─entity
│     │  ├─mapper
│     │  └─repository
│     └─presentation 
├─global
│  ├─exception
│  ├─response
│  │  └─code   
│  └─config
└─utils
```
### 2. 패키지 구조 설명
- **1️⃣ domains (하위 개별 도메인 관련 aggregate)**
    > **[도메인명]**
    >
    > ex). `user`,  `ticket` …
  - **application**
      - **dto**: 계층 간 데이터 전송을 위한 객체들 (request / response 구분)
      - **mapper**: 객체 간의 변환 로직 (ex. DTO ↔ Entity)
      - **usecase**: 하나의 행위에 대한 비즈니스 로직을 통합, service 계층에 의존
        (다른 usecase 참조는 가급적 지양)
  - **domain**
      - **model**: 순수 도메인 모델
      - **service**: repository 계층에 의존하며, Save / Get / Delete / Update 4가지의 비즈니스 로직을 제공
  - **exception**: 도메인에서 발생할 수 있는 커스텀 예외 정의
  - **persistence**: 도메인의 데이터 영속성 계층에 초점, 데이터베이스 및 영속 계층 관련 구현체를 포함
      - **entity**: 데이터베이스와 직접적으로 매핑되는 JPA 엔티티 클래스
          - 영속성 계층에서 사용하는 도메인 객체로, 데이터베이스 테이블과 1:1 매핑
          - 순수 도메인 모델과는 분리되며, DB 관련 필드 및 설정 포함
      - **mapper**: 영속성 개체(entity)와 도메인 모델(model) 간의 변환 로직을 제공
          - 데이터베이스와의 상호작용 후 반환된 entity를 도메인 모델로 변환하거나, 도메인 모델을 entity로 변환
          - MapStruct 및 Builder 사용
      - **repository**: 데이터베이스와의 상호작용을 처리하는 Repository 구현체와 관련된 코드를 포함
          - 쿼리를 작성하거나 JPA 커스텀 메서드를 추가해 비즈니스 요구사항에 맞는 데이터 접근을 처리
  - **presentation**: REST API 또는 사용자 인터페이스 담당, usecase를 호출하여 응답 반환 (ex. Controller)
  - **(선택적) infra**: 해당 도메인 내에서만 사용하는 외부 시스템과의 통합 기능 (ex. DB 이외의 외부 시스템 접근)


- **2️⃣ global**
  - **exception:** 서비스 내 커스텀 에러 표준화
  - **response:** 서비스 내 공통 응답 표준화
  - **config**: configuration class 정의
  - **jwt**: jwt 관련 기능


- **3️⃣ infrastructure**
  - 외부 계층 관련 기능
    eg. 외부 DB, 외부 API 서버 등


- **4️⃣ utils**
  - helper, parser 등의 부가 기능


## 팀 협업 전략
### 1. 브랜치 전략
- **🚀 main**: 실 서버 배포가 이루어지는 브랜치입니다.
  - `main` 브랜치에는 배포 가능한 상태만을 반영합니다.
  - `main` 브랜치에서는 실제 소스 수정 및 개발을 진행하지 않습니다.
  - `main` 브랜치의 소스가 변경이 될 때, `tag`를 생성하게 됩니다.

  
- **♻️ release**: 배포 전 최종 검증 및 QA가 이루어지는 브랜치입니다.
    - `release` 브랜치는 배포 전 안정성을 검증하고 최종테스트를 진행합니다.
    - `release` 브랜치는 `develop` 브랜치에서 생성합니다.
    - 테스트 혹은 QA과정에서 발생된 문제를 `release` 브랜치에서 수정하며, 수정된 내용은 반드시 `develop` 브랜치에 병합합니다.
    - `release` 브랜치의 모든 검증이 완료되면, `main` 브랜치에 병합합니다.

  
- **✅ develop**: 배포 및 빌드가 이루어지는 브랜치입니다.
    - `develop` 브랜치는 `feature` 브랜치에서 개발이 완료된 기능이 올라오는 브랜치입니다.
    - `develop` 브랜치에서 기능이 배포가 가능 할 정도로 완성되면 `release` 브랜치로 병합합니다.

  
- **🚑 hotfix**: 개발이 완료된 프로젝트의 긴급한 수정사항이나 버그 수정 시 사용하는 브랜치입니다.
  - `hotfix` 브랜치는 수정 완료 시, 병합과 동시에 삭제합니다.


- **✨ feature**: 기능 개발을 위한 브랜치입니다.
  - `feature` 브랜치는 개인별 작업이 이루어지는 소스들의 병합이 진행됩니다.
  - `feature` 브랜치는 `main` 브랜치로 직접 접근이 불가합니다.
  - `feature` 브랜치는 작업하고자 하는 티켓을 브랜치 명으로 생성합니다.
  - 개발 완료 시, `develop` 브랜치로 병합과 동시에 삭제합니다.
  
<img src="https://private-user-images.githubusercontent.com/44363238/405489078-724ac54f-caa9-455a-b018-e699cefc2313.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3Mzc1MjUxNDEsIm5iZiI6MTczNzUyNDg0MSwicGF0aCI6Ii80NDM2MzIzOC80MDU0ODkwNzgtNzI0YWM1NGYtY2FhOS00NTVhLWIwMTgtZTY5OWNlZmMyMzEzLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTAxMjIlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwMTIyVDA1NDcyMVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTdkZDcxYjE0N2UyYzU3NmI3NjZlODcyNmE5YjEyN2ZhOWFlYzBjNzI2ZTg3MmQ5YWMwMDc4YTA5ZmNjM2Q3NjImWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.OpAcdIvkIqh4nAdmnsNxGqgqqLVrR5TlPVF3OQgYF2g" width="500" >

### 2. 커밋 전략
- **a. 커밋 메시지 작성의 중요성**
    - 팀원과의 원활한 소통을 위해
    - 편리한 history 추적을 위해
    - 이슈(issue) 관리를 위해
  

- **b. 커밋 메시지 양식**
```
WRKR-<티켓번호> :Emoji: <type>: <subject>

<body>
- <Scope>
	- discription

<footer>
# 이슈 번호
```

```
WRKR-1 :sparkles: Feature: 게시글 작성 API 추가

<body>
- PostController.java
	- 게시글 생성 api 작성
- PostService.java
	- 게시글 생성 로직 작성
- UserRepository.java
	- 작성자 검색용 method 작성

<footer>
- 해결: #123
- 관련: #321
- 참고: #321
```

- **c. 커밋 메시지 타입**

  | Type      | Emoji               | Description                                      |
  |-----------|---------------------|--------------------------------------------------|
  | Feature   | ✨ (sparkles)       | 새로운 기능 추가                                 |
  | Fix       | 🐛 (bug)           | 버그 수정                                        |
  | Docs      | 📝 (memo)          | 문서 수정                                        |
  | Style     | 🎨 (art)           | 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우 |
  | Refactor  | ♻️ (recycle)       | 코드 리팩토링                                    |
  | Test      | ✅ (white_check_mark) | 테스트 코드, 리팩토링 테스트 코드 추가            |
  | Chore     | 🔧 (wrench)        | 빌드 업무 수정, 패키지 매니저 수정               |
  | Wip       | 🚧 (construction)  | 완료되지 않은 작업 임시 커밋 (가능하다면 지양합시다!) |
  | Rename    | 🚚 (truck)         | 파일 or 폴더명 수정하거나 옮기는 경우            |

- **d. 커밋 항목 설명**
    - **Header**: 작업 중인 브랜치의 티켓 번호
    - **Emoji**: 커밋의 타입을 이모지로 표현
    - **Type**: 커밋의 타입
    - **Subject**: 커밋의 간단한 한줄 요약 내용
    - **Body**: 커밋에 대한 상세 내용 (선택 사항)
    - **Footer**: 커밋과 관련된 이슈 번호 (선택 사항)


- **e. 커밋 항목 상세 설명 및 예시**
    - **subject**: 커밋의 간단한 한줄 요약 내용
      - 50자를 넘기지 않고, 마침표 및 특수기호를 사용하지 않습니다.
      - 영문으로 시작하는 경우 동사(원형)를 가장 앞에 두고 첫 글자는 대문자로 작성합니다. (과거시제 사용 금지)
      - 제목은 개조식으로 작성합니다.
        <br><br>

          **Bad Example**

          ```
          :bug: Fix: 버그 수정했습니다.
          ```
      
          **Good Example**

          ```
          WRKR-1 :bug: Fix: 로그인 API refresh token 오류 수정
          ```
    - **Body** (선택사항): 해당 커밋에 대한 상세 내용을 작성합니다.
      - 선택 사항임으로 모든 커밋에 작성할 필요는 없습니다.
      - 한 줄에 72자를 넘기지 않습니다.
      - 어떻게(how)보다 무엇(what)과 왜(why)를 설명합니다.
      - 작업 설명 뿐만이 아니라 커밋의 이유를 작성할 때에도 사용합니다.
        <br><br>

        **Example of Body**

        ```
        :sparkles: Feature: 게시글 작성 API 추가

        <body>
        - PostController.java
            - 게시글 생성 api 작성
        - PostService.java
            - 게시글 생성 로직 작성
        - UserRepository.java
            - 작성자 검색용 method 작성
        ```

    - **Footer** (선택사항): 커밋 메세지의 맺음말입니다.
      - 선택 사항이므로 모든 커밋에 작성할 필요는 없습니다.
      - 이슈를 추적하기 위한 ID를 추가할 때 사용합니다.
          - 해결 - 해결한 이슈 ID
          - 관련 - 해당 커밋에 관련된 이슈 ID
          - 참고 - 참고할만한 이슈 ID

        <br>**Example of Footer**
        ```
        <footer>
         - 해결: #123
         - 관련: #321
         - 참고: #222
        ```

## 개발 환경 설정
### 1. 프로젝트 클론:
```bash
   git clone https://github.com/WRKR-KEA/backend-tms.git
```
### 2. 종속성 설치:
```bash
   ./gradlew clean build
```
### 3. 프로젝트 실행:
```bash
   ./gradlew bootRun
```
### 4. 환경 변수 설정:
- env.properties 파일을 생성하여 아래와 같이 환경 변수를 설정합니다.
```env.properties
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
PK_CRYPTO_ALGORITHM=AES
PK_CRYPTO_SECRET=GREATTEAM4WRKR123
```
### 5. commitizen 설정(선택사항):
- 커밋 메시지를 통일하기 위해 commitizen을 사용합니다.
- npm intall 명령어를 통해 package.json을 실행시켜 commitizen을 설치합니다.
- 이후 설치된 commitizen을 실행하기 위해 cz 명령어로 커밋 메시지를 작성합니다.

```bash
  npm install
  cz
```

## 5. **전체 컨벤션 정리 (Convention)**
- 코드 스타일, 리뷰 정책, 테스트 작성 규칙 등 협업 시 지켜야 할 기준을 명확히 합니다.

### 코딩 컨벤션
- **코딩 스타일**: Google Java Style Guide
- **테스트 작성 규칙**:
- 새 기능 추가 시 단위 테스트 포함
- 테스트 커버리지 80% 이상 유지 목표

### GitHub 컨벤션
- **리뷰 정책**:
  - 모든 PR(Pull Request)은 최소 1명 이상의 승인 필요
  - 주요 변경 사항은 Slack에서 공유
- **커밋 메시지 작성 규칙**:
  - 상기 문서 참조

### 명명 컨벤션
- **명명 규칙**
  1. 변수, 메서드, 클래스 등에는 일관된 명명 규칙을 적용해야한다.
  2. 폴더는 lowercase로 작성하고, 파일명은 PascalCase로 작성한다.
  2. 명확하고 의미 있는 이름은 코드를 읽기 쉽게 만들며 거기에 규칙성이 더해지면 팀 전체의 생산성에 도움을 줄 수 있다.
  3. 위와 같은 이유로 되도록이면 변수, 메서드, 클래스의 명칭은 의미를 알 수 없는 축약어를 사용하지 않고 풀어서 작성하도록 한다.



- **Layer 별 method**
  >   Layer에서 사용되는 메서드 명명 규칙이며 예제는 “User” 로 작성합니다.
  > 
  >   reference: [파트너플랫폼 스쿼드 코드 컨벤션](https://oliveyoung.tech/blog/2023-12-05/partner-platform-code-convention/)


- | Method | Controller and Service | Repository |
  | --- | --- | --- |
  | Data Read | getUser | findByUser, countByUser, existsByUser |
  | Data Insert | createUser | insertUser |
  | Data Delete | removeUser | deleteUser |
  | Data Modify | modifyUser | updateUser |
  | Event 발행 | UserEvent | - |

- **Controller ~ Service Layer의 메서드 명칭**과 **Repository Layer의 메서드 명칭**을 **다르게 작성**하여, 비즈니스 로직이 동작하는 메서드인지, 저장소에 Accesss 하기위한 기능을 제공하는 메서드인지 구별할 수 있도록 합니다.
 
- 기능을 명확히 설명할 수 있다면 get, create, remove를 사용하지 않아도 됨.
  - ex) getUser, createUser, removeUser -> findUser, saveUser, deleteUser

### 테스트 컨벤션
#### DisplayName Annotation

> Junit 5에서 나온 DisplayName annotation 활용
>
- 명사의 나열보다 문장형으로 작성

  > A이면 B이다. 또는
  >
  >
  > A이면 B가 아니고 C다.
  >
  - "~테스트" 지양하기
  - 음료 1개 추가 테스트 : 테스트의 대한 설명이 명확하게 끝나지 않음
  - 음료를 1개 추가할 수 있다. : 설명이 명확하게 끝남
- 테스트 행위에 대한 결과까지 기술하기
  - 음료를 추가할 수 있다.
  - 음료를 추가하면 `주문 목록에 담긴다.`
- 도메인 용어를 사용하기

  > 메서드 자체의 관점보다 도메인 정책 관점으로
  >
  - 특정 시간 이전에 주문을 생성하면 실패한다.
  - `영업 시작 시간` 이전에는 주문을 생성할 수 없다.

#### BDD 스타일 테스트

- BDD란?
  - TDD에서 파생된 개발 방법
  - 함수 단위의 테스트에 집중하기 보다, 시나리오에 기반한 `테스트케이스(TC)` 자체에 집중하여 테스트를 진행
  - 개발자가 아닌 사람이 봐도 이해할 수 있을 정도의 추상화 수준(레벨)을 권장
  - 테스트 자체가 문서의 역할을 할 수 있도록 해야 함을 의미
- Given / When / Then
  - Given : 어떤 환경에서
    - 시나리오 진행에 필요한 모든 준비 과정 (객체, 값, 상태 등)
  - When : 어떤 행동을 진행했을 때
    - 시나리오 행동 진행
  - Then : 어떤 상태 변화가 일어난다
    - 시나리오 진행에 대한 결과 명시, 검증