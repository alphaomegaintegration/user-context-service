# UsersApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createUsers**](UsersApi.md#createUsers) | **POST** /users/batch | gets userContext by userId and contextId |



## createUsers

> UserLoad createUsers()

gets userContext by userId and contextId

### Example

```java
// Import classes:
import com.alpha.omega.user.client.ApiClient;
import com.alpha.omega.user.client.ApiException;
import com.alpha.omega.user.client.Configuration;
import com.alpha.omega.user.client.models.*;
import com.alpha.omega.user.client.UsersApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        UsersApi apiInstance = new UsersApi(defaultClient);
        try {
            UserLoad result = apiInstance.createUsers();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersApi#createUsers");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**UserLoad**](UserLoad.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  * Cache-Control - Cache-Control header provides important info on how long a response may be considered fresh <br>  |

