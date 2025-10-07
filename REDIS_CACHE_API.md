# Redis Cache & Data Management API Documentation

This document describes the Redis cache integration and data management endpoints added to the Cloud Integration Application.

## Overview

The application now includes:
- **Reference ID Generation**: Unique identifier generation with various formats
- **Redis Cache Service**: High-performance data caching with TTL support
- **Data Entity Management**: CRUD operations for structured data
- **Custom Data Storage**: Flexible storage for any JSON data
- **Cache Management**: Statistics, monitoring, and maintenance operations

## API Endpoints

### Base URLs
- **Development**: `http://localhost:8081/api`
- **QA**: `http://localhost:8082/api`
- **Production**: `http://localhost:8083/api`

---

## 1. Reference ID Generation

### Generate Default Reference ID
```http
POST /cloud/generate-reference
POST /cache/generate-reference-id
```

**Parameters:**
- `prefix` (optional): Custom prefix for the reference ID

**Response:**
```json
{
  "success": true,
  "message": "Reference ID generated successfully",
  "data": "CLD-20241203143022-ABC123-0001",
  "referenceId": "CLD-20241203143022-ABC123-0001",
  "timestamp": "2024-12-03T14:30:22"
}
```

### Generate Type-Specific Reference ID
```http
GET /cache/generate-reference-id/{type}
```

**Supported Types:**
- `AZURE` - Azure operations
- `GCP` - Google Cloud operations
- `SPLUNK` - Splunk operations
- `USER` - User operations
- `DOCUMENT` - Document operations
- `TRANSACTION` - Transaction operations
- `LOG` - Logging operations
- `CACHE` - Cache operations
- `SYSTEM` - System operations

**Example:**
```http
GET /cache/generate-reference-id/AZURE
```

**Response:**
```json
{
  "success": true,
  "message": "Reference ID generated successfully",
  "data": "AZR-20241203143022-XYZ789-0002",
  "referenceId": "AZR-20241203143022-XYZ789-0002",
  "timestamp": "2024-12-03T14:30:22"
}
```

### Generate Custom Reference ID
```http
POST /cache/generate-custom-reference-id
```

**Request Body:**
```json
{
  "prefix": "CUSTOM",
  "includeTimestamp": true,
  "randomLength": 8,
  "includeSequence": true
}
```

---

## 2. Data Entity Management

### Create Data Entity
```http
POST /cache/data-entity
```

**Request Body:**
```json
{
  "name": "Sample Data Entity",
  "description": "This is a sample data entity",
  "category": "SAMPLE"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Data entity created successfully",
  "data": {
    "id": null,
    "referenceId": "CLD-20241203143022-ABC123-0001",
    "name": "Sample Data Entity",
    "description": "This is a sample data entity",
    "category": "SAMPLE",
    "status": "ACTIVE",
    "metadata": null,
    "createdAt": "2024-12-03T14:30:22",
    "updatedAt": "2024-12-03T14:30:22"
  },
  "referenceId": "CLD-20241203143022-ABC123-0001",
  "timestamp": "2024-12-03T14:30:22"
}
```

### Retrieve Data Entity
```http
GET /cache/data-entity/{referenceId}
```

**Response:**
```json
{
  "success": true,
  "message": "Data entity retrieved successfully",
  "data": {
    "id": null,
    "referenceId": "CLD-20241203143022-ABC123-0001",
    "name": "Sample Data Entity",
    "description": "This is a sample data entity",
    "category": "SAMPLE",
    "status": "ACTIVE",
    "metadata": null,
    "createdAt": "2024-12-03T14:30:22",
    "updatedAt": "2024-12-03T14:30:22"
  },
  "referenceId": "CLD-20241203143022-ABC123-0001",
  "timestamp": "2024-12-03T14:30:22"
}
```

### Update Data Entity
```http
PUT /cache/data-entity/{referenceId}
```

**Request Body:**
```json
{
  "name": "Updated Data Entity",
  "description": "Updated description",
  "category": "UPDATED",
  "status": "INACTIVE"
}
```

### Delete Data Entity
```http
DELETE /cache/data-entity/{referenceId}
```

### Get All Data Entities
```http
GET /cache/data-entities
GET /cache/data-entities?pattern=CLD-*
```

---

## 3. Custom Data Storage

### Store Custom Data
```http
POST /cache/store
POST /cloud/store-data
```

**Request Body:**
```json
{
  "prefix": "CUSTOM",
  "data": {
    "userId": 12345,
    "sessionId": "sess_abc123",
    "preferences": {
      "theme": "dark",
      "language": "en"
    }
  },
  "dataType": "USER_SESSION",
  "ttlSeconds": 3600
}
```

### Retrieve Custom Data
```http
GET /cache/retrieve/{referenceId}
GET /cloud/retrieve-data/{referenceId}
```

---

## 4. Cache Management

### Get Cache Statistics
```http
GET /cache/stats
GET /cloud/cache-stats
```

**Response:**
```json
{
  "success": true,
  "message": "Cache statistics retrieved successfully",
  "data": {
    "redis_version": "7.0.5",
    "used_memory": "2.5M",
    "connected_clients": "5",
    "total_commands_processed": "1250",
    "keyspace_hits": "890",
    "keyspace_misses": "45",
    "application_keys_count": 25
  },
  "timestamp": "2024-12-03T14:30:22"
}
```

### Clear All Cache
```http
DELETE /cache/clear
```

### Delete Specific Cache Entry
```http
DELETE /cache/delete/{referenceId}
```

### Check Cache Existence
```http
GET /cache/exists/{referenceId}
```

**Response:**
```json
{
  "success": true,
  "message": "Cache existence checked",
  "data": true,
  "referenceId": "CLD-20241203143022-ABC123-0001",
  "timestamp": "2024-12-03T14:30:22"
}
```

### Get Cache TTL
```http
GET /cache/ttl/{referenceId}
```

**Response:**
```json
{
  "success": true,
  "message": "Cache TTL retrieved",
  "data": 3245,
  "referenceId": "CLD-20241203143022-ABC123-0001",
  "timestamp": "2024-12-03T14:30:22"
}
```

### Set Cache TTL
```http
PUT /cache/ttl/{referenceId}?ttlSeconds=7200
```

---

## 5. Bulk Operations

### Bulk Create Data Entities
```http
POST /cache/bulk-create
```

**Request Body:**
```json
[
  {
    "name": "Entity 1",
    "description": "Description 1",
    "category": "BULK"
  },
  {
    "name": "Entity 2",
    "description": "Description 2",
    "category": "BULK"
  }
]
```

### Async Data Processing
```http
POST /cache/async-process/{referenceId}?operation=PROCESS
```

---

## 6. Utility Operations

### Validate Reference ID
```http
GET /cache/validate-reference-id/{referenceId}
```

### Extract Prefix from Reference ID
```http
GET /cache/extract-prefix/{referenceId}
```

### Get Generator Statistics
```http
GET /cache/generator-stats
```

**Response:**
```json
{
  "success": true,
  "message": "Generator statistics retrieved",
  "data": {
    "totalGenerated": 1250,
    "availablePrefixes": 10,
    "lastGenerated": "2024-12-03T14:30:22"
  },
  "timestamp": "2024-12-03T14:30:22"
}
```

---

## 7. Azure Integration with Cache

### Upload File with Cache Tracking
```http
POST /cloud/azure/upload-with-cache
```

**Request:** Multipart form data with file

**Response:**
```json
{
  "success": true,
  "message": "File uploaded to Azure with cache tracking",
  "data": "AZR-20241203143022-XYZ789-0002",
  "referenceId": "AZR-20241203143022-XYZ789-0002",
  "timestamp": "2024-12-03T14:30:22"
}
```

### Get Upload Status
```http
GET /cloud/azure/upload-status/{referenceId}
```

**Response:**
```json
{
  "success": true,
  "message": "Custom data retrieved successfully",
  "data": {
    "originalName": "document.pdf",
    "size": 1024000,
    "contentType": "application/pdf",
    "uploadTime": "2024-12-03T14:30:22",
    "status": "UPLOADED",
    "azureBlobName": "document.pdf"
  },
  "referenceId": "AZR-20241203143022-XYZ789-0002",
  "timestamp": "2024-12-03T14:30:22"
}
```

---

## Reference ID Format

Reference IDs follow this format: `PREFIX-TIMESTAMP-RANDOM-SEQUENCE`

- **PREFIX**: 2-10 characters (e.g., CLD, AZR, GCP)
- **TIMESTAMP**: yyyyMMddHHmmss (14 characters)
- **RANDOM**: 4-12 characters (alphanumeric)
- **SEQUENCE**: 3-4 digits (0001-9999)

**Examples:**
- `CLD-20241203143022-ABC123-0001`
- `AZR-20241203143022-XYZ789-0002`
- `GCP-20241203143022-DEF456-0003`

---

## Error Responses

All endpoints return consistent error responses:

```json
{
  "success": false,
  "message": null,
  "data": null,
  "referenceId": null,
  "error": "Error description",
  "errors": ["Error 1", "Error 2"],
  "timestamp": "2024-12-03T14:30:22",
  "statusCode": 400
}
```

**Common HTTP Status Codes:**
- `200` - Success
- `400` - Bad Request
- `404` - Not Found
- `500` - Internal Server Error

---

## Configuration

### Redis Configuration
The application uses different Redis databases for different environments:
- **Development**: Database 0
- **QA**: Database 1  
- **Production**: Database 2

### Environment Variables
```bash
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DATABASE=0
```

---

## Usage Examples

### 1. Store User Session Data
```bash
curl -X POST http://localhost:8081/api/cache/store \
  -H "Content-Type: application/json" \
  -d '{
    "prefix": "USR",
    "data": {
      "userId": 12345,
      "sessionId": "sess_abc123",
      "preferences": {"theme": "dark"}
    },
    "dataType": "USER_SESSION",
    "ttlSeconds": 3600
  }'
```

### 2. Retrieve and Check TTL
```bash
# Get data
curl http://localhost:8081/api/cache/retrieve/USR-20241203143022-ABC123-0001

# Check TTL
curl http://localhost:8081/api/cache/ttl/USR-20241203143022-ABC123-0001

# Check if exists
curl http://localhost:8081/api/cache/exists/USR-20241203143022-ABC123-0001
```

### 3. Generate and Store Data Entity
```bash
# Generate reference ID
REF_ID=$(curl -s http://localhost:8081/api/cache/generate-reference-id/USER | jq -r '.data')

# Create data entity
curl -X POST http://localhost:8081/api/cache/data-entity \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"User Profile\",
    \"description\": \"User profile data\",
    \"category\": \"USER\"
  }"
```

---

## Performance Considerations

1. **TTL Usage**: Always set appropriate TTL values to prevent memory bloat
2. **Key Patterns**: Use consistent key patterns for easier management
3. **Bulk Operations**: Use bulk endpoints for multiple operations
4. **Async Processing**: Use async endpoints for long-running operations
5. **Monitoring**: Regularly check cache statistics and performance

---

## Security Notes

1. **Data Validation**: All input data is validated before storage
2. **Access Control**: Cache access follows the same security rules as other endpoints
3. **TTL Management**: Sensitive data should have shorter TTL values
4. **Key Patterns**: Avoid storing sensitive information in reference IDs

This Redis cache integration provides a robust foundation for high-performance data management and caching in your Cloud Integration Application.
