# UserContextsApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addPermissionToUserContext**](UserContextsApi.md#addPermissionToUserContext) | **POST** /usercontexts/user/{userId}/context/{contextId}/permissions/{permission} | gets userContext by userId and contextId |
| [**addPermissionsToUserContext**](UserContextsApi.md#addPermissionsToUserContext) | **POST** /usercontexts/user/{userId}/context/{contextId}/permissions | gets userContext by userId and contextId |
| [**addRoleToUserContext**](UserContextsApi.md#addRoleToUserContext) | **POST** /usercontexts/user/{userId}/context/{contextId}/role/{roleId} | gets userContext by userId and contextId |
| [**createUserContext**](UserContextsApi.md#createUserContext) | **POST** /usercontexts | creates userContext |
| [**createUserContextBatch**](UserContextsApi.md#createUserContextBatch) | **POST** /usercontexts/batch | creates userContext |
| [**deleteUserContextByUserContextId**](UserContextsApi.md#deleteUserContextByUserContextId) | **DELETE** /usercontexts/{usercontextId} | deletes userContext by usercontextId |
| [**getAllUserContexts**](UserContextsApi.md#getAllUserContexts) | **GET** /usercontexts | gets all userContexts |
| [**getUserContextByContextId**](UserContextsApi.md#getUserContextByContextId) | **GET** /usercontexts/context/{contextId} | gets userContext by userId and contextId |
| [**getUserContextByUserContextId**](UserContextsApi.md#getUserContextByUserContextId) | **GET** /usercontexts/{usercontextId} | gets userContext by usercontextId |
| [**getUserContextByUserId**](UserContextsApi.md#getUserContextByUserId) | **GET** /usercontexts/user/{userId} | gets userContext by userId and contextId |
| [**getUserContextByUserIdAndContextId**](UserContextsApi.md#getUserContextByUserIdAndContextId) | **GET** /usercontexts/user/{userId}/context/{contextId} | gets userContext by userId and contextId |
| [**updateUserContext**](UserContextsApi.md#updateUserContext) | **PUT** /usercontexts/{usercontextId} | updates userContext via upsert |



## addPermissionToUserContext

> UserContext addPermissionToUserContext(userId, contextId, permission, cacheControl)

gets userContext by userId and contextId

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        String userId = "userId_example"; // String | userId
        String contextId = "contextId_example"; // String | contextId
        String permission = "permission_example"; // String | permission
        String cacheControl = "no-cache"; // String | 
        try {
            UserContext result = apiInstance.addPermissionToUserContext(userId, contextId, permission, cacheControl);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#addPermissionToUserContext");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **String**| userId | |
| **contextId** | **String**| contextId | |
| **permission** | **String**| permission | |
| **cacheControl** | **String**|  | [optional] [default to no-cache] |

### Return type

[**UserContext**](UserContext.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |


## addPermissionsToUserContext

> UserContext addPermissionsToUserContext(userId, contextId, cacheControl, additionalPermissions)

gets userContext by userId and contextId

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        String userId = "userId_example"; // String | userId
        String contextId = "contextId_example"; // String | contextId
        String cacheControl = "no-cache"; // String | 
        String additionalPermissions = "additionalPermissions_example"; // String | additionalPermissions comma delimited
        try {
            UserContext result = apiInstance.addPermissionsToUserContext(userId, contextId, cacheControl, additionalPermissions);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#addPermissionsToUserContext");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **String**| userId | |
| **contextId** | **String**| contextId | |
| **cacheControl** | **String**|  | [optional] [default to no-cache] |
| **additionalPermissions** | **String**| additionalPermissions comma delimited | [optional] |

### Return type

[**UserContext**](UserContext.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |


## addRoleToUserContext

> UserContext addRoleToUserContext(userId, contextId, roleId, cacheControl, additionalPermissions)

gets userContext by userId and contextId

User must have CREATE_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        String userId = "userId_example"; // String | userId
        String contextId = "contextId_example"; // String | contextId
        String roleId = "roleId_example"; // String | roleId
        String cacheControl = "no-cache"; // String | 
        String additionalPermissions = "additionalPermissions_example"; // String | additionalPermissions comma delimited
        try {
            UserContext result = apiInstance.addRoleToUserContext(userId, contextId, roleId, cacheControl, additionalPermissions);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#addRoleToUserContext");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **String**| userId | |
| **contextId** | **String**| contextId | |
| **roleId** | **String**| roleId | |
| **cacheControl** | **String**|  | [optional] [default to no-cache] |
| **additionalPermissions** | **String**| additionalPermissions comma delimited | [optional] |

### Return type

[**UserContext**](UserContext.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## createUserContext

> UserContext createUserContext(userContext)

creates userContext

User must have CREATE_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        UserContext userContext = new UserContext(); // UserContext | 
        try {
            UserContext result = apiInstance.createUserContext(userContext);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#createUserContext");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userContext** | [**UserContext**](UserContext.md)|  | |

### Return type

[**UserContext**](UserContext.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## createUserContextBatch

> UserContextPage createUserContextBatch(userContextBatchRequest)

creates userContext

User must have CREATE_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        UserContextBatchRequest userContextBatchRequest = new UserContextBatchRequest(); // UserContextBatchRequest | 
        try {
            UserContextPage result = apiInstance.createUserContextBatch(userContextBatchRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#createUserContextBatch");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userContextBatchRequest** | [**UserContextBatchRequest**](UserContextBatchRequest.md)|  | |

### Return type

[**UserContextPage**](UserContextPage.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## deleteUserContextByUserContextId

> deleteUserContextByUserContextId(usercontextId)

deletes userContext by usercontextId

User must have DELETE_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        String usercontextId = "usercontextId_example"; // String | usercontextId
        try {
            apiInstance.deleteUserContextByUserContextId(usercontextId);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#deleteUserContextByUserContextId");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **usercontextId** | **String**| usercontextId | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | deleted successfully |  -  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## getAllUserContexts

> UserContextPage getAllUserContexts(page, pageSize, direction, cacheControl)

gets all userContexts

User must have LIST_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        Integer page = 1; // Integer | What page to grab
        Integer pageSize = 25; // Integer | Number of elements on page
        String direction = "ASC"; // String | Sort order direction
        String cacheControl = "no-cache"; // String | Http cache control header
        try {
            UserContextPage result = apiInstance.getAllUserContexts(page, pageSize, direction, cacheControl);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#getAllUserContexts");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **page** | **Integer**| What page to grab | [optional] [default to 1] |
| **pageSize** | **Integer**| Number of elements on page | [optional] [default to 25] |
| **direction** | **String**| Sort order direction | [optional] [default to ASC] [enum: ASC, DESC] |
| **cacheControl** | **String**| Http cache control header | [optional] [enum: no-cache, must-revalidate] |

### Return type

[**UserContextPage**](UserContextPage.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## getUserContextByContextId

> UserContextPage getUserContextByContextId(contextId, cacheControl, page, pageSize, direction)

gets userContext by userId and contextId

user must have LIST_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        String contextId = "contextId_example"; // String | contextId
        String cacheControl = "no-cache"; // String | 
        Integer page = 1; // Integer | What page to grab
        Integer pageSize = 25; // Integer | Number of elements on page
        String direction = "ASC"; // String | Sort order direction
        try {
            UserContextPage result = apiInstance.getUserContextByContextId(contextId, cacheControl, page, pageSize, direction);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#getUserContextByContextId");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **contextId** | **String**| contextId | |
| **cacheControl** | **String**|  | [optional] [default to no-cache] |
| **page** | **Integer**| What page to grab | [optional] [default to 1] |
| **pageSize** | **Integer**| Number of elements on page | [optional] [default to 25] |
| **direction** | **String**| Sort order direction | [optional] [default to ASC] [enum: ASC, DESC] |

### Return type

[**UserContextPage**](UserContextPage.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## getUserContextByUserContextId

> UserContext getUserContextByUserContextId(usercontextId)

gets userContext by usercontextId

User must have LIST_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        String usercontextId = "usercontextId_example"; // String | usercontextId
        try {
            UserContext result = apiInstance.getUserContextByUserContextId(usercontextId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#getUserContextByUserContextId");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **usercontextId** | **String**| usercontextId | |

### Return type

[**UserContext**](UserContext.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## getUserContextByUserId

> UserContextPage getUserContextByUserId(userId, cacheControl, page, pageSize, direction)

gets userContext by userId and contextId

User must have LIST_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        String userId = "userId_example"; // String | userId
        String cacheControl = "no-cache"; // String | 
        Integer page = 1; // Integer | What page to grab
        Integer pageSize = 25; // Integer | Number of elements on page
        String direction = "ASC"; // String | Sort order direction
        try {
            UserContextPage result = apiInstance.getUserContextByUserId(userId, cacheControl, page, pageSize, direction);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#getUserContextByUserId");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **String**| userId | |
| **cacheControl** | **String**|  | [optional] [default to no-cache] |
| **page** | **Integer**| What page to grab | [optional] [default to 1] |
| **pageSize** | **Integer**| Number of elements on page | [optional] [default to 25] |
| **direction** | **String**| Sort order direction | [optional] [default to ASC] [enum: ASC, DESC] |

### Return type

[**UserContextPage**](UserContextPage.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## getUserContextByUserIdAndContextId

> UserContextPermissions getUserContextByUserIdAndContextId(userId, contextId, allRoles, roles, cacheControl)

gets userContext by userId and contextId

User must have LIST_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        String userId = "userId_example"; // String | userId
        String contextId = "contextId_example"; // String | contextId
        Boolean allRoles = false; // Boolean | Determines if all roles permissions are returned
        String roles = "roles_example"; // String | comma separated list of roles
        String cacheControl = "cacheControl_example"; // String | 
        try {
            UserContextPermissions result = apiInstance.getUserContextByUserIdAndContextId(userId, contextId, allRoles, roles, cacheControl);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#getUserContextByUserIdAndContextId");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **String**| userId | |
| **contextId** | **String**| contextId | |
| **allRoles** | **Boolean**| Determines if all roles permissions are returned | [optional] [default to false] |
| **roles** | **String**| comma separated list of roles | [optional] |
| **cacheControl** | **String**|  | [optional] |

### Return type

[**UserContextPermissions**](UserContextPermissions.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## updateUserContext

> UserContext updateUserContext(usercontextId, userContext)

updates userContext via upsert

User must have UPDATE_USER_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UserContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UserContextsApi apiInstance = new UserContextsApi(defaultClient);
        String usercontextId = "usercontextId_example"; // String | usercontextId
        UserContext userContext = new UserContext(); // UserContext | 
        try {
            UserContext result = apiInstance.updateUserContext(usercontextId, userContext);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UserContextsApi#updateUserContext");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **usercontextId** | **String**| usercontextId | |
| **userContext** | [**UserContext**](UserContext.md)|  | |

### Return type

[**UserContext**](UserContext.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |

