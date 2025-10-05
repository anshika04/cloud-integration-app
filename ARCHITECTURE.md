# Architecture Documentation

## System Architecture Overview

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                Cloud Integration Platform                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐             │
│  │   Development   │    │      QA         │    │   Production    │             │
│  │   Environment   │    │   Environment   │    │   Environment   │             │
│  │                 │    │                 │    │                 │             │
│  │ Frontend: 3001  │    │ Frontend: 3002  │    │ Frontend: 3003  │             │
│  │ Backend:  8081  │    │ Backend:  8082  │    │ Backend:  8083  │             │
│  │ DB:       5432  │    │ DB:       5433  │    │ DB:       5434  │             │
│  │ Redis:    6379  │    │ Redis:    6380  │    │ Redis:    6381  │             │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘             │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Application Stack

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                Application Stack                                │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                            Frontend Layer                              │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐        │    │
│  │  │   Dashboard     │  │   Azure Mgmt    │  │   GCP Mgmt      │        │    │
│  │  │   Component     │  │   Component     │  │   Component     │        │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘        │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐        │    │
│  │  │   Splunk        │  │   Monitoring    │  │   Cloud         │        │    │
│  │  │   Component     │  │   Component     │  │   Service       │        │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘        │    │
│  │                                                                       │    │
│  │                    Angular 17 + Material Design                      │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                            Backend Layer                               │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐        │    │
│  │  │   REST          │  │   Security      │  │   Cloud         │        │    │
│  │  │   Controllers   │  │   Config        │  │   Integration   │        │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘        │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐        │    │
│  │  │   Azure         │  │   GCP           │  │   Splunk        │        │    │
│  │  │   Service       │  │   Service       │  │   Service       │        │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘        │    │
│  │                                                                       │    │
│  │                    Spring Boot 3.2 + Java 21                         │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                          Infrastructure Layer                         │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐        │    │
│  │  │   PostgreSQL    │  │   Redis         │  │   Docker        │        │    │
│  │  │   Database      │  │   Cache         │  │   Containers    │        │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘        │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Data Flow Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                Data Flow Architecture                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────┐    HTTP/REST     ┌─────────────┐    Database     ┌─────────────┐
│  │   Frontend  │ ────────────────► │   Backend   │ ──────────────► │ PostgreSQL │
│  │  (Angular)  │                  │ (Spring)    │                  │            │
│  └─────────────┘                  └─────────────┘                  └─────────────┘
│         │                                │                                │
│         │                                │ Cache                         │
│         │                                ▼                                │
│         │                         ┌─────────────┐                        │
│         │                         │   Redis     │                        │
│         │                         │            │                        │
│         │                         └─────────────┘                        │
│         │                                │                                │
│         │                                │ Logs                           │
│         │                                ▼                                │
│         │                         ┌─────────────┐                        │
│         │                         │   Splunk    │                        │
│         │                         │            │                        │
│         │                         └─────────────┘                        │
│         │                                                                 │
│         │ Cloud APIs                                                      │
│         ▼                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         Cloud Services                              │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   Azure     │  │    GCP      │  │   Azure     │  │    GCP      │ │   │
│  │  │ Key Vault   │  │   Storage   │  │ Blob Store  │  │   Pub/Sub   │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              Deployment Architecture                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                        Docker Compose Orchestration                    │    │
│  │                                                                       │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐        │    │
│  │  │   Development   │  │      QA         │  │   Production    │        │    │
│  │  │   Environment   │  │   Environment   │  │   Environment   │        │    │
│  │  │                 │  │                 │  │                 │        │    │
│  │  │ ┌─────────────┐ │  │ ┌─────────────┐ │  │ ┌─────────────┐ │        │    │
│  │  │ │  Frontend   │ │  │ │  Frontend   │ │  │ │  Frontend   │ │        │    │
│  │  │ │   Container │ │  │ │   Container │ │  │ │   Container │ │        │    │
│  │  │ └─────────────┘ │  │ └─────────────┘ │  │ └─────────────┘ │        │    │
│  │  │ ┌─────────────┐ │  │ ┌─────────────┐ │  │ ┌─────────────┐ │        │    │
│  │  │ │  Backend    │ │  │ │  Backend    │ │  │ │  Backend    │ │        │    │
│  │  │ │  Container  │ │  │ │  Container  │ │  │ │  Container  │ │        │    │
│  │  │ └─────────────┘ │  │ └─────────────┘ │  │ └─────────────┘ │        │    │
│  │  │ ┌─────────────┐ │  │ ┌─────────────┐ │  │ ┌─────────────┐ │        │    │
│  │  │ │ PostgreSQL  │ │  │ │ PostgreSQL  │ │  │ │ PostgreSQL  │ │        │    │
│  │  │ │  Container  │ │  │ │  Container  │ │  │ │  Container  │ │        │    │
│  │  │ └─────────────┘ │  │ └─────────────┘ │  │ └─────────────┘ │        │    │
│  │  │ ┌─────────────┐ │  │ ┌─────────────┐ │  │ ┌─────────────┐ │        │    │
│  │  │ │   Redis     │ │  │ │   Redis     │ │  │ │   Redis     │ │        │    │
│  │  │ │  Container  │ │  │ │  Container  │ │  │ │  Container  │ │        │    │
│  │  │ └─────────────┘ │  │ └─────────────┘ │  │ └─────────────┘ │        │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘        │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                         Port Mapping                                  │    │
│  │                                                                       │    │
│  │  Development:  Frontend:3001  Backend:8081  DB:5432  Redis:6379     │    │
│  │  QA:          Frontend:3002  Backend:8082  DB:5433  Redis:6380     │    │
│  │  Production:  Frontend:3003  Backend:8083  DB:5434  Redis:6381     │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Security Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              Security Architecture                             │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                        Authentication & Authorization                  │    │
│  │                                                                       │    │
│  │  Development/QA:                Production:                           │    │
│  │  ┌─────────────────┐           ┌─────────────────┐                    │    │
│  │  │   Anonymous     │           │   OAuth2        │                    │    │
│  │  │   Authentication│           │   Resource      │                    │    │
│  │  │                 │           │   Server        │                    │    │
│  │  │ • No Auth       │           │                 │                    │    │
│  │  │ • Open Access   │           │ • JWT Tokens    │                    │    │
│  │  │ • Debug Mode    │           │ • Bearer Auth   │                    │    │
│  │  └─────────────────┘           │ • Secure APIs   │                    │    │
│  │                                └─────────────────┘                    │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                           Security Headers                             │    │
│  │                                                                       │    │
│  │  • X-Frame-Options: DENY                                              │    │
│  │  • X-Content-Type-Options: nosniff                                    │    │
│  │  • X-XSS-Protection: 1; mode=block                                    │    │
│  │  • Referrer-Policy: strict-origin-when-cross-origin                   │    │
│  │  • Content-Security-Policy: default-src 'self'                        │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                         Network Security                               │    │
│  │                                                                       │    │
│  │  • Docker Networks: Isolated per environment                          │    │
│  │  • Internal Communication: Container-to-container                     │    │
│  │  • External Access: Port mapping only                                 │    │
│  │  • Database: No external access                                       │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Cloud Integration Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Cloud Integration Architecture                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                            Azure Integration                            │    │
│  │                                                                       │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │    │
│  │  │ Key Vault   │  │ Blob        │  │ Queue       │  │ Service     │    │    │
│  │  │ Secrets     │  │ Storage     │  │ Service     │  │ Principal   │    │    │
│  │  │ Management  │  │ File        │  │ Message     │  │ Auth        │    │    │
│  │  │             │  │ Storage     │  │ Processing  │  │             │    │    │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                            GCP Integration                             │    │
│  │                                                                       │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │    │
│  │  │ Secret      │  │ Cloud       │  │ Pub/Sub     │  │ Cloud       │    │    │
│  │  │ Manager     │  │ Storage     │  │ Messaging   │  │ Logging     │    │    │
│  │  │ Secrets     │  │ Object      │  │ Event       │  │ Centralized │    │    │
│  │  │ Storage     │  │ Storage     │  │ Streaming   │  │ Logging     │    │    │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                            Splunk Integration                          │    │
│  │                                                                       │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │    │
│  │  │ Log         │  │ Search      │  │ Monitoring  │  │ Alerting    │    │    │
│  │  │ Ingestion   │  │ Queries     │  │ Dashboards  │  │ Notifications│    │    │
│  │  │ Centralized │  │ Analytics   │  │ Real-time   │  │ Error       │    │    │
│  │  │ Logging     │  │ Reporting   │  │ Metrics     │  │ Detection   │    │    │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Deployment Flow

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              Deployment Flow                                   │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. Code Development                                                           │
│     ┌─────────────┐                                                            │
│     │   Developer │                                                            │
│     │   Commits   │                                                            │
│     │   Code      │                                                            │
│     └─────────────┘                                                            │
│           │                                                                    │
│           ▼                                                                    │
│  2. Environment Selection                                                      │
│     ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                        │
│     │ Development │  │     QA      │  │ Production  │                        │
│     │   Script    │  │   Script    │  │   Script    │                        │
│     └─────────────┘  └─────────────┘  └─────────────┘                        │
│           │                                                                    │
│           ▼                                                                    │
│  3. Docker Build Process                                                      │
│     ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                        │
│     │ Frontend    │  │  Backend    │  │ Database    │                        │
│     │ Build       │  │   Build     │  │   Setup     │                        │
│     └─────────────┘  └─────────────┘  └─────────────┘                        │
│           │                                                                    │
│           ▼                                                                    │
│  4. Container Orchestration                                                   │
│     ┌─────────────────────────────────────────────────────────────────────┐    │
│     │                    Docker Compose                                  │    │
│     │                                                                   │    │
│     │  • Start Containers                                               │    │
│     │  • Configure Networks                                             │    │
│     │  • Mount Volumes                                                  │    │
│     │  • Health Checks                                                  │    │
│     └─────────────────────────────────────────────────────────────────────┘    │
│           │                                                                    │
│           ▼                                                                    │
│  5. Service Verification                                                      │
│     ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                        │
│     │ Health      │  │ API         │  │ Frontend    │                        │
│     │ Checks      │  │ Tests       │  │ Tests       │                        │
│     └─────────────┘  └─────────────┘  └─────────────┘                        │
│           │                                                                    │
│           ▼                                                                    │
│  6. Application Ready                                                         │
│     ┌─────────────┐                                                            │
│     │   Services  │                                                            │
│     │   Running   │                                                            │
│     │   & Ready   │                                                            │
│     └─────────────┘                                                            │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Monitoring & Observability

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          Monitoring & Observability                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                            Application Monitoring                      │    │
│  │                                                                       │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │    │
│  │  │ Health      │  │ Metrics     │  │ Logs        │  │ Tracing     │    │    │
│  │  │ Checks      │  │ Collection  │  │ Aggregation │  │ Distributed │    │    │
│  │  │ Endpoints   │  │ Prometheus  │  │ Splunk      │  │ Tracing     │    │    │
│  │  │ Status      │  │ Metrics     │  │ Centralized │  │ Request     │    │    │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                            Infrastructure Monitoring                   │    │
│  │                                                                       │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │    │
│  │  │ Docker      │  │ Database    │  │ Redis       │  │ Network     │    │    │
│  │  │ Containers  │  │ Performance │  │ Cache       │  │ Traffic     │    │    │
│  │  │ Resource    │  │ Connection  │  │ Hit/Miss    │  │ Monitoring  │    │    │
│  │  │ Usage       │  │ Pool        │  │ Ratios      │  │ Bandwidth   │    │    │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                            Alerting & Notification                     │    │
│  │                                                                       │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │    │
│  │  │ Error       │  │ Performance │  │ Resource    │  │ Security    │    │    │
│  │  │ Detection   │  │ Degradation │  │ Exhaustion  │  │ Incidents   │    │    │
│  │  │ Critical    │  │ Slow        │  │ Memory      │  │ Unauthorized│    │    │
│  │  │ Exceptions  │  │ Response    │  │ CPU Usage   │  │ Access      │    │    │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Key Architectural Decisions

### 1. Multi-Environment Architecture
- **Isolation**: Each environment runs in complete isolation
- **Port Separation**: Different ports prevent conflicts
- **Configuration**: Environment-specific configurations
- **Data Separation**: Separate databases and caches

### 2. Container-First Approach
- **Docker**: All services containerized
- **Orchestration**: Docker Compose for local development
- **Scalability**: Ready for Kubernetes deployment
- **Consistency**: Same environment across dev/staging/prod

### 3. Microservices-Ready Design
- **Separation**: Frontend and backend are separate services
- **API-First**: RESTful APIs for communication
- **Stateless**: Backend services are stateless
- **Scalable**: Independent scaling of components

### 4. Security by Design
- **Environment-Specific**: Different security models per environment
- **Defense in Depth**: Multiple security layers
- **Network Isolation**: Docker networks for service isolation
- **Secrets Management**: Environment variables for sensitive data

### 5. Cloud-Native Integration
- **Multi-Cloud**: Support for Azure and GCP
- **Service Integration**: Native cloud service integration
- **Monitoring**: Comprehensive logging and monitoring
- **Observability**: Full application visibility

This architecture provides a robust, scalable, and maintainable foundation for cloud integration applications while supporting rapid development and deployment across multiple environments.
