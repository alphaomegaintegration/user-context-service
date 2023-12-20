package com.alpha.omega.user.client;

import com.alpha.omega.user.client.ApiClient;

import com.alpha.omega.user.model.UserContext;
import com.alpha.omega.user.model.UserContextBatchRequest;
import com.alpha.omega.user.model.UserContextPage;
import com.alpha.omega.user.model.UserContextPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-12-20T06:26:02.767674-05:00[America/New_York]")
public class UserContextsApi {
    private ApiClient apiClient;

    public UserContextsApi() {
        this(new ApiClient());
    }

    @Autowired
    public UserContextsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * gets userContext by userId and contextId
     * 
     * <p><b>200</b> - OK
     * @param userId userId
     * @param contextId contextId
     * @param permission permission
     * @param cacheControl The cacheControl parameter
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec addPermissionToUserContextRequestCreation(String userId, String contextId, String permission, String cacheControl) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new WebClientResponseException("Missing the required parameter 'userId' when calling addPermissionToUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling addPermissionToUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'permission' is set
        if (permission == null) {
            throw new WebClientResponseException("Missing the required parameter 'permission' when calling addPermissionToUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("userId", userId);
        pathParams.put("contextId", contextId);
        pathParams.put("permission", permission);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (cacheControl != null)
        headerParams.add("Cache-Control", apiClient.parameterToString(cacheControl));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return apiClient.invokeAPI("/usercontexts/user/{userId}/context/{contextId}/permissions/{permission}", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets userContext by userId and contextId
     * 
     * <p><b>200</b> - OK
     * @param userId userId
     * @param contextId contextId
     * @param permission permission
     * @param cacheControl The cacheControl parameter
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContext> addPermissionToUserContext(String userId, String contextId, String permission, String cacheControl) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return addPermissionToUserContextRequestCreation(userId, contextId, permission, cacheControl).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContext>> addPermissionToUserContextWithHttpInfo(String userId, String contextId, String permission, String cacheControl) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return addPermissionToUserContextRequestCreation(userId, contextId, permission, cacheControl).toEntity(localVarReturnType);
    }
    /**
     * gets userContext by userId and contextId
     * 
     * <p><b>200</b> - OK
     * @param userId userId
     * @param contextId contextId
     * @param cacheControl The cacheControl parameter
     * @param additionalPermissions additionalPermissions comma delimited
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec addPermissionsToUserContextRequestCreation(String userId, String contextId, String cacheControl, String additionalPermissions) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new WebClientResponseException("Missing the required parameter 'userId' when calling addPermissionsToUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling addPermissionsToUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("userId", userId);
        pathParams.put("contextId", contextId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "additionalPermissions", additionalPermissions));

        if (cacheControl != null)
        headerParams.add("Cache-Control", apiClient.parameterToString(cacheControl));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return apiClient.invokeAPI("/usercontexts/user/{userId}/context/{contextId}/permissions", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets userContext by userId and contextId
     * 
     * <p><b>200</b> - OK
     * @param userId userId
     * @param contextId contextId
     * @param cacheControl The cacheControl parameter
     * @param additionalPermissions additionalPermissions comma delimited
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContext> addPermissionsToUserContext(String userId, String contextId, String cacheControl, String additionalPermissions) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return addPermissionsToUserContextRequestCreation(userId, contextId, cacheControl, additionalPermissions).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContext>> addPermissionsToUserContextWithHttpInfo(String userId, String contextId, String cacheControl, String additionalPermissions) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return addPermissionsToUserContextRequestCreation(userId, contextId, cacheControl, additionalPermissions).toEntity(localVarReturnType);
    }
    /**
     * gets userContext by userId and contextId
     * User must have CREATE_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userId userId
     * @param contextId contextId
     * @param roleId roleId
     * @param cacheControl The cacheControl parameter
     * @param additionalPermissions additionalPermissions comma delimited
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec addRoleToUserContextRequestCreation(String userId, String contextId, String roleId, String cacheControl, String additionalPermissions) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new WebClientResponseException("Missing the required parameter 'userId' when calling addRoleToUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling addRoleToUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'roleId' is set
        if (roleId == null) {
            throw new WebClientResponseException("Missing the required parameter 'roleId' when calling addRoleToUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("userId", userId);
        pathParams.put("contextId", contextId);
        pathParams.put("roleId", roleId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "additionalPermissions", additionalPermissions));

        if (cacheControl != null)
        headerParams.add("Cache-Control", apiClient.parameterToString(cacheControl));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return apiClient.invokeAPI("/usercontexts/user/{userId}/context/{contextId}/role/{roleId}", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets userContext by userId and contextId
     * User must have CREATE_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userId userId
     * @param contextId contextId
     * @param roleId roleId
     * @param cacheControl The cacheControl parameter
     * @param additionalPermissions additionalPermissions comma delimited
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContext> addRoleToUserContext(String userId, String contextId, String roleId, String cacheControl, String additionalPermissions) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return addRoleToUserContextRequestCreation(userId, contextId, roleId, cacheControl, additionalPermissions).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContext>> addRoleToUserContextWithHttpInfo(String userId, String contextId, String roleId, String cacheControl, String additionalPermissions) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return addRoleToUserContextRequestCreation(userId, contextId, roleId, cacheControl, additionalPermissions).toEntity(localVarReturnType);
    }
    /**
     * creates userContext
     * User must have CREATE_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userContext The userContext parameter
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec createUserContextRequestCreation(UserContext userContext) throws WebClientResponseException {
        Object postBody = userContext;
        // verify the required parameter 'userContext' is set
        if (userContext == null) {
            throw new WebClientResponseException("Missing the required parameter 'userContext' when calling createUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return apiClient.invokeAPI("/usercontexts", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * creates userContext
     * User must have CREATE_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userContext The userContext parameter
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContext> createUserContext(UserContext userContext) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return createUserContextRequestCreation(userContext).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContext>> createUserContextWithHttpInfo(UserContext userContext) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return createUserContextRequestCreation(userContext).toEntity(localVarReturnType);
    }
    /**
     * creates userContext
     * User must have CREATE_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userContextBatchRequest The userContextBatchRequest parameter
     * @return UserContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec createUserContextBatchRequestCreation(UserContextBatchRequest userContextBatchRequest) throws WebClientResponseException {
        Object postBody = userContextBatchRequest;
        // verify the required parameter 'userContextBatchRequest' is set
        if (userContextBatchRequest == null) {
            throw new WebClientResponseException("Missing the required parameter 'userContextBatchRequest' when calling createUserContextBatch", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return apiClient.invokeAPI("/usercontexts/batch", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * creates userContext
     * User must have CREATE_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userContextBatchRequest The userContextBatchRequest parameter
     * @return UserContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContextPage> createUserContextBatch(UserContextBatchRequest userContextBatchRequest) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return createUserContextBatchRequestCreation(userContextBatchRequest).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContextPage>> createUserContextBatchWithHttpInfo(UserContextBatchRequest userContextBatchRequest) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return createUserContextBatchRequestCreation(userContextBatchRequest).toEntity(localVarReturnType);
    }
    /**
     * deletes userContext by usercontextId
     * User must have DELETE_USER_CONTEXTS permission
     * <p><b>204</b> - deleted successfully
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param usercontextId usercontextId
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deleteUserContextByUserContextIdRequestCreation(String usercontextId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'usercontextId' is set
        if (usercontextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'usercontextId' when calling deleteUserContextByUserContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("usercontextId", usercontextId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/usercontexts/{usercontextId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * deletes userContext by usercontextId
     * User must have DELETE_USER_CONTEXTS permission
     * <p><b>204</b> - deleted successfully
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param usercontextId usercontextId
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> deleteUserContextByUserContextId(String usercontextId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteUserContextByUserContextIdRequestCreation(usercontextId).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<Void>> deleteUserContextByUserContextIdWithHttpInfo(String usercontextId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteUserContextByUserContextIdRequestCreation(usercontextId).toEntity(localVarReturnType);
    }
    /**
     * gets all userContexts
     * User must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param page What page to grab
     * @param pageSize Number of elements on page
     * @param direction Sort order direction
     * @param cacheControl Http cache control header
     * @return UserContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getAllUserContextsRequestCreation(Integer page, Integer pageSize, String direction, String cacheControl) throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "pageSize", pageSize));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "direction", direction));

        if (cacheControl != null)
        headerParams.add("Cache-Control", apiClient.parameterToString(cacheControl));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return apiClient.invokeAPI("/usercontexts", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets all userContexts
     * User must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param page What page to grab
     * @param pageSize Number of elements on page
     * @param direction Sort order direction
     * @param cacheControl Http cache control header
     * @return UserContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContextPage> getAllUserContexts(Integer page, Integer pageSize, String direction, String cacheControl) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return getAllUserContextsRequestCreation(page, pageSize, direction, cacheControl).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContextPage>> getAllUserContextsWithHttpInfo(Integer page, Integer pageSize, String direction, String cacheControl) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return getAllUserContextsRequestCreation(page, pageSize, direction, cacheControl).toEntity(localVarReturnType);
    }
    /**
     * gets userContext by userId and contextId
     * user must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @param cacheControl The cacheControl parameter
     * @param page What page to grab
     * @param pageSize Number of elements on page
     * @param direction Sort order direction
     * @return UserContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getUserContextByContextIdRequestCreation(String contextId, String cacheControl, Integer page, Integer pageSize, String direction) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling getUserContextByContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("contextId", contextId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "pageSize", pageSize));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "direction", direction));

        if (cacheControl != null)
        headerParams.add("Cache-Control", apiClient.parameterToString(cacheControl));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return apiClient.invokeAPI("/usercontexts/context/{contextId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets userContext by userId and contextId
     * user must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @param cacheControl The cacheControl parameter
     * @param page What page to grab
     * @param pageSize Number of elements on page
     * @param direction Sort order direction
     * @return UserContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContextPage> getUserContextByContextId(String contextId, String cacheControl, Integer page, Integer pageSize, String direction) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return getUserContextByContextIdRequestCreation(contextId, cacheControl, page, pageSize, direction).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContextPage>> getUserContextByContextIdWithHttpInfo(String contextId, String cacheControl, Integer page, Integer pageSize, String direction) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return getUserContextByContextIdRequestCreation(contextId, cacheControl, page, pageSize, direction).toEntity(localVarReturnType);
    }
    /**
     * gets userContext by usercontextId
     * User must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param usercontextId usercontextId
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getUserContextByUserContextIdRequestCreation(String usercontextId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'usercontextId' is set
        if (usercontextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'usercontextId' when calling getUserContextByUserContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("usercontextId", usercontextId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return apiClient.invokeAPI("/usercontexts/{usercontextId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets userContext by usercontextId
     * User must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param usercontextId usercontextId
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContext> getUserContextByUserContextId(String usercontextId) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return getUserContextByUserContextIdRequestCreation(usercontextId).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContext>> getUserContextByUserContextIdWithHttpInfo(String usercontextId) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return getUserContextByUserContextIdRequestCreation(usercontextId).toEntity(localVarReturnType);
    }
    /**
     * gets userContext by userId and contextId
     * User must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userId userId
     * @param cacheControl The cacheControl parameter
     * @param page What page to grab
     * @param pageSize Number of elements on page
     * @param direction Sort order direction
     * @return UserContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getUserContextByUserIdRequestCreation(String userId, String cacheControl, Integer page, Integer pageSize, String direction) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new WebClientResponseException("Missing the required parameter 'userId' when calling getUserContextByUserId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("userId", userId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "pageSize", pageSize));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "direction", direction));

        if (cacheControl != null)
        headerParams.add("Cache-Control", apiClient.parameterToString(cacheControl));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return apiClient.invokeAPI("/usercontexts/user/{userId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets userContext by userId and contextId
     * User must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userId userId
     * @param cacheControl The cacheControl parameter
     * @param page What page to grab
     * @param pageSize Number of elements on page
     * @param direction Sort order direction
     * @return UserContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContextPage> getUserContextByUserId(String userId, String cacheControl, Integer page, Integer pageSize, String direction) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return getUserContextByUserIdRequestCreation(userId, cacheControl, page, pageSize, direction).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContextPage>> getUserContextByUserIdWithHttpInfo(String userId, String cacheControl, Integer page, Integer pageSize, String direction) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPage> localVarReturnType = new ParameterizedTypeReference<UserContextPage>() {};
        return getUserContextByUserIdRequestCreation(userId, cacheControl, page, pageSize, direction).toEntity(localVarReturnType);
    }
    /**
     * gets userContext by userId and contextId
     * User must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userId userId
     * @param contextId contextId
     * @param allRoles Determines if all roles permissions are returned
     * @param roles comma separated list of roles
     * @param cacheControl The cacheControl parameter
     * @return UserContextPermissions
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getUserContextByUserIdAndContextIdRequestCreation(String userId, String contextId, Boolean allRoles, String roles, String cacheControl) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new WebClientResponseException("Missing the required parameter 'userId' when calling getUserContextByUserIdAndContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling getUserContextByUserIdAndContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("userId", userId);
        pathParams.put("contextId", contextId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "allRoles", allRoles));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "roles", roles));

        if (cacheControl != null)
        headerParams.add("Cache-Control", apiClient.parameterToString(cacheControl));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContextPermissions> localVarReturnType = new ParameterizedTypeReference<UserContextPermissions>() {};
        return apiClient.invokeAPI("/usercontexts/user/{userId}/context/{contextId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets userContext by userId and contextId
     * User must have LIST_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param userId userId
     * @param contextId contextId
     * @param allRoles Determines if all roles permissions are returned
     * @param roles comma separated list of roles
     * @param cacheControl The cacheControl parameter
     * @return UserContextPermissions
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContextPermissions> getUserContextByUserIdAndContextId(String userId, String contextId, Boolean allRoles, String roles, String cacheControl) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPermissions> localVarReturnType = new ParameterizedTypeReference<UserContextPermissions>() {};
        return getUserContextByUserIdAndContextIdRequestCreation(userId, contextId, allRoles, roles, cacheControl).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContextPermissions>> getUserContextByUserIdAndContextIdWithHttpInfo(String userId, String contextId, Boolean allRoles, String roles, String cacheControl) throws WebClientResponseException {
        ParameterizedTypeReference<UserContextPermissions> localVarReturnType = new ParameterizedTypeReference<UserContextPermissions>() {};
        return getUserContextByUserIdAndContextIdRequestCreation(userId, contextId, allRoles, roles, cacheControl).toEntity(localVarReturnType);
    }
    /**
     * updates userContext via upsert
     * User must have UPDATE_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param usercontextId usercontextId
     * @param userContext The userContext parameter
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec updateUserContextRequestCreation(String usercontextId, UserContext userContext) throws WebClientResponseException {
        Object postBody = userContext;
        // verify the required parameter 'usercontextId' is set
        if (usercontextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'usercontextId' when calling updateUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'userContext' is set
        if (userContext == null) {
            throw new WebClientResponseException("Missing the required parameter 'userContext' when calling updateUserContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("usercontextId", usercontextId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return apiClient.invokeAPI("/usercontexts/{usercontextId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * updates userContext via upsert
     * User must have UPDATE_USER_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param usercontextId usercontextId
     * @param userContext The userContext parameter
     * @return UserContext
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserContext> updateUserContext(String usercontextId, UserContext userContext) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return updateUserContextRequestCreation(usercontextId, userContext).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserContext>> updateUserContextWithHttpInfo(String usercontextId, UserContext userContext) throws WebClientResponseException {
        ParameterizedTypeReference<UserContext> localVarReturnType = new ParameterizedTypeReference<UserContext>() {};
        return updateUserContextRequestCreation(usercontextId, userContext).toEntity(localVarReturnType);
    }
}
