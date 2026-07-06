# API Endpoints Documentation

## Base Information

**Base URL:** `https://jsonplaceholder.typicode.com/`  
**Protocol:** HTTPS  
**Data Format:** JSON  
**Authentication:** None (Public API)  

---

## Endpoints

### 1. Get Users (Login Service)

**Endpoint:** `/users`  
**Method:** `GET`  
**Description:** Retrieve list of all users from the system  
**Authentication:** Not required  

#### Request

```http
GET /users HTTP/1.1
Host: jsonplaceholder.typicode.com
Accept: application/json
```

#### Response

**Status Code:** `200 OK`

```json
[
  {
    "id": 1,
    "name": "Leanne Graham",
    "username": "Bret",
    "email": "Sincere@april.biz",
    "address": {
      "street": "Kulas Light",
      "suite": "Apt. 556",
      "city": "Gwenborough",
      "zipcode": "92998-3874",
      "geo": {
        "lat": "-37.3159",
        "lng": "81.1496"
      }
    },
    "phone": "1-770-736-8031 x56442",
    "website": "hildegard.org",
    "company": {
      "name": "Romaguera-Crona",
      "catchPhrase": "Multi-layered client-server neural-net",
      "bs": "harness real-time e-markets"
    }
  },
  {
    "id": 2,
    "name": "Ervin Howell",
    "username": "Antonette",
    "email": "Shanna@melissa.tv",
    ...
  }
]
```

#### Response Model (Kotlin)

```kotlin
@Serializable
data class UserResponse(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: Address? = null,
    val phone: String? = null,
    val website: String? = null,
    val company: Company? = null
)

@Serializable
data class Address(
    val street: String?,
    val suite: String?,
    val city: String?,
    val zipcode: String?,
    val geo: Geo?
)

@Serializable
data class Geo(
    val lat: String?,
    val lng: String?
)

@Serializable
data class Company(
    val name: String?,
    val catchPhrase: String?,
    val bs: String?
)
```

#### Retrofit Interface

```kotlin
interface LoginApi {
    @GET("users")
    suspend fun getUsers(): List<UserResponse>
}
```

#### Usage in Repository

```kotlin
class LoginRepositoryImpl(private val loginApi: LoginApi) : LoginRepository {
    override suspend fun login(user: String, password: String): Result<LoginModel> {
        return try {
            val response = loginApi.getUsers()
            val firstUser = response.firstOrNull()
            
            if (firstUser != null) {
                Result.success(
                    LoginModel(user = firstUser)
                )
            } else {
                Result.failure(Exception("No users found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### Error Handling

| Status Code | Description | Handling |
|-----------|-------------|----------|
| 200 | Success | Process response list |
| 404 | Not Found | Show error toast to user |
| 500 | Server Error | Show generic error message |
| Network Error | Connection failed | Retry mechanism recommended |

#### Example Error Response

```json
{
  "error": "Internal Server Error",
  "message": "Something went wrong",
  "status": 500
}
```

---

## Network Configuration

### HTTP Client Configuration (Retrofit)

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    })
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

### Request/Response Logging (Debug Only)

When `BuildConfig.DEBUG = true`, all HTTP transactions are logged with:
- Request headers and body
- Response headers and body
- Duration of request

---

## Rate Limiting

**JSONPlaceholder** is a free public API with no strict rate limiting, but it's recommended to:
- Implement reasonable request delays
- Cache responses where possible
- Handle connection timeouts gracefully

---

## CORS & Security

- **CORS:** Enabled for public requests
- **HTTPS:** Required
- **Authentication:** N/A
- **Token:** Not required

---

## API Response Times

| Operation | Expected Time | Timeout |
|-----------|--------------|---------|
| Get Users | 100-500ms | 30s |

---

## Sample Integration

### Making a Request

```kotlin
lifecycleScope.launch {
    try {
        val users = loginApi.getUsers()
        Log.d("API", "Users fetched: ${users.size}")
        
        users.forEach { user ->
            Log.d("API", "User: ${user.name} - ${user.email}")
        }
    } catch (e: Exception) {
        Log.e("API", "Error fetching users", e)
    }
}
```

---

**Last Updated:** July 6, 2026

