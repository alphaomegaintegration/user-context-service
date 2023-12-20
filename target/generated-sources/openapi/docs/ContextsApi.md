# ContextsApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addAdditionalRolesByContextId**](ContextsApi.md#addAdditionalRolesByContextId) | **POST** /contexts/{contextId}/roles | get roles by contextId |
| [**createContext**](ContextsApi.md#createContext) | **POST** /contexts | creates new context via upsert |
| [**deleteContextByContextId**](ContextsApi.md#deleteContextByContextId) | **DELETE** /contexts/{contextId} | deletes context by contextId |
| [**getAllContexts**](ContextsApi.md#getAllContexts) | **GET** /contexts | gets all contexts |
| [**getContextByContextId**](ContextsApi.md#getContextByContextId) | **GET** /contexts/{contextId} | gets context by contextId |
| [**getRoleByContextIdAndRoleId**](ContextsApi.md#getRoleByContextIdAndRoleId) | **GET** /contexts/{contextId}/roles/{roleId} | get context by contextId and roleId |
| [**getRolesByContextId**](ContextsApi.md#getRolesByContextId) | **GET** /contexts/{contextId}/roles | get roles by contextId |
| [**updateContextByContextId**](ContextsApi.md#updateContextByContextId) | **PUT** /contexts/{contextId} | updates context by contextId |



## addAdditionalRolesByContextId

> Context addAdditionalRolesByContextId(contextId, role)

get roles by contextId

User must have UPDATE_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.ContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        ContextsApi apiInstance = new ContextsApi(defaultClient);
        String contextId = "contextId_example"; // String | contextId
        Role role = new Role(); // Role | 
        try {
            Context result = apiInstance.addAdditionalRolesByContextId(contextId, role);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ContextsApi#addAdditionalRolesByContextId");
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
| **role** | [**Role**](Role.md)|  | |

### Return type

[**Context**](Context.md)

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


## createContext

> Context createContext(context)

creates new context via upsert

User must have CREATE_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.ContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        ContextsApi apiInstance = new ContextsApi(defaultClient);
        Context context = new Context(); // Context | 
        try {
            Context result = apiInstance.createContext(context);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ContextsApi#createContext");
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
| **context** | [**Context**](Context.md)|  | |

### Return type

[**Context**](Context.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |
| **400** | Bad Request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |


## deleteContextByContextId

> deleteContextByContextId(contextId)

deletes context by contextId

User must have DELETE_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.ContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        ContextsApi apiInstance = new ContextsApi(defaultClient);
        String contextId = "contextId_example"; // String | contextId
        try {
            apiInstance.deleteContextByContextId(contextId);
        } catch (ApiException e) {
            System.err.println("Exception when calling ContextsApi#deleteContextByContextId");
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


## getAllContexts

> ContextPage getAllContexts(page, pageSize, direction)

gets all contexts

User must have LIST_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.ContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        ContextsApi apiInstance = new ContextsApi(defaultClient);
        Integer page = 1; // Integer | What page to grab
        Integer pageSize = 25; // Integer | Number of elements on page
        String direction = "ASC"; // String | Sort order direction
        try {
            ContextPage result = apiInstance.getAllContexts(page, pageSize, direction);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ContextsApi#getAllContexts");
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

### Return type

[**ContextPage**](ContextPage.md)

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


## getContextByContextId

> Context getContextByContextId(contextId)

gets context by contextId

User must have LIST_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.ContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        ContextsApi apiInstance = new ContextsApi(defaultClient);
        String contextId = "contextId_example"; // String | contextId
        try {
            Context result = apiInstance.getContextByContextId(contextId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ContextsApi#getContextByContextId");
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

### Return type

[**Context**](Context.md)

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


## getRoleByContextIdAndRoleId

> Role getRoleByContextIdAndRoleId(contextId, roleId)

get context by contextId and roleId

User must have LIST_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.ContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        ContextsApi apiInstance = new ContextsApi(defaultClient);
        String contextId = "contextId_example"; // String | contextId
        String roleId = "roleId_example"; // String | roleId
        try {
            Role result = apiInstance.getRoleByContextIdAndRoleId(contextId, roleId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ContextsApi#getRoleByContextIdAndRoleId");
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
| **roleId** | **String**| roleId | |

### Return type

[**Role**](Role.md)

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


## getRolesByContextId

> RolePage getRolesByContextId(contextId)

get roles by contextId

User must have LIST_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.ContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        ContextsApi apiInstance = new ContextsApi(defaultClient);
        String contextId = "contextId_example"; // String | contextId
        try {
            RolePage result = apiInstance.getRolesByContextId(contextId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ContextsApi#getRolesByContextId");
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

### Return type

[**RolePage**](RolePage.md)

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


## updateContextByContextId

> Context updateContextByContextId(contextId, context)

updates context by contextId

User must have UPDATE_CONTEXTS permission

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.ContextsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        ContextsApi apiInstance = new ContextsApi(defaultClient);
        String contextId = "contextId_example"; // String | contextId
        Context context = new Context(); // Context | 
        try {
            Context result = apiInstance.updateContextByContextId(contextId, context);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ContextsApi#updateContextByContextId");
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
| **context** | [**Context**](Context.md)|  | |

### Return type

[**Context**](Context.md)

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

