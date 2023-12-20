package com.alpha.omega.user.client;

import com.alpha.omega.user.client.ApiClient;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.model.ContextPage;
import com.alpha.omega.user.model.Role;
import com.alpha.omega.user.model.RolePage;

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
public class ContextsApi {
    private ApiClient apiClient;

    public ContextsApi() {
        this(new ApiClient());
    }

    @Autowired
    public ContextsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * get roles by contextId
     * User must have UPDATE_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @param role The role parameter
     * @return Context
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec addAdditionalRolesByContextIdRequestCreation(String contextId, Role role) throws WebClientResponseException {
        Object postBody = role;
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling addAdditionalRolesByContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'role' is set
        if (role == null) {
            throw new WebClientResponseException("Missing the required parameter 'role' when calling addAdditionalRolesByContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("contextId", contextId);

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

        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return apiClient.invokeAPI("/contexts/{contextId}/roles", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * get roles by contextId
     * User must have UPDATE_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @param role The role parameter
     * @return Context
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Context> addAdditionalRolesByContextId(String contextId, Role role) throws WebClientResponseException {
        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return addAdditionalRolesByContextIdRequestCreation(contextId, role).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<Context>> addAdditionalRolesByContextIdWithHttpInfo(String contextId, Role role) throws WebClientResponseException {
        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return addAdditionalRolesByContextIdRequestCreation(contextId, role).toEntity(localVarReturnType);
    }
    /**
     * creates new context via upsert
     * User must have CREATE_CONTEXTS permission
     * <p><b>201</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param context The context parameter
     * @return Context
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec createContextRequestCreation(Context context) throws WebClientResponseException {
        Object postBody = context;
        // verify the required parameter 'context' is set
        if (context == null) {
            throw new WebClientResponseException("Missing the required parameter 'context' when calling createContext", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return apiClient.invokeAPI("/contexts", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * creates new context via upsert
     * User must have CREATE_CONTEXTS permission
     * <p><b>201</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param context The context parameter
     * @return Context
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Context> createContext(Context context) throws WebClientResponseException {
        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return createContextRequestCreation(context).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<Context>> createContextWithHttpInfo(Context context) throws WebClientResponseException {
        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return createContextRequestCreation(context).toEntity(localVarReturnType);
    }
    /**
     * deletes context by contextId
     * User must have DELETE_CONTEXTS permission
     * <p><b>204</b> - deleted successfully
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deleteContextByContextIdRequestCreation(String contextId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling deleteContextByContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("contextId", contextId);

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
        return apiClient.invokeAPI("/contexts/{contextId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * deletes context by contextId
     * User must have DELETE_CONTEXTS permission
     * <p><b>204</b> - deleted successfully
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> deleteContextByContextId(String contextId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteContextByContextIdRequestCreation(contextId).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<Void>> deleteContextByContextIdWithHttpInfo(String contextId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteContextByContextIdRequestCreation(contextId).toEntity(localVarReturnType);
    }
    /**
     * gets all contexts
     * User must have LIST_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param page What page to grab
     * @param pageSize Number of elements on page
     * @param direction Sort order direction
     * @return ContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getAllContextsRequestCreation(Integer page, Integer pageSize, String direction) throws WebClientResponseException {
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

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<ContextPage> localVarReturnType = new ParameterizedTypeReference<ContextPage>() {};
        return apiClient.invokeAPI("/contexts", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets all contexts
     * User must have LIST_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param page What page to grab
     * @param pageSize Number of elements on page
     * @param direction Sort order direction
     * @return ContextPage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ContextPage> getAllContexts(Integer page, Integer pageSize, String direction) throws WebClientResponseException {
        ParameterizedTypeReference<ContextPage> localVarReturnType = new ParameterizedTypeReference<ContextPage>() {};
        return getAllContextsRequestCreation(page, pageSize, direction).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<ContextPage>> getAllContextsWithHttpInfo(Integer page, Integer pageSize, String direction) throws WebClientResponseException {
        ParameterizedTypeReference<ContextPage> localVarReturnType = new ParameterizedTypeReference<ContextPage>() {};
        return getAllContextsRequestCreation(page, pageSize, direction).toEntity(localVarReturnType);
    }
    /**
     * gets context by contextId
     * User must have LIST_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @return Context
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getContextByContextIdRequestCreation(String contextId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling getContextByContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("contextId", contextId);

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

        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return apiClient.invokeAPI("/contexts/{contextId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets context by contextId
     * User must have LIST_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @return Context
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Context> getContextByContextId(String contextId) throws WebClientResponseException {
        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return getContextByContextIdRequestCreation(contextId).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<Context>> getContextByContextIdWithHttpInfo(String contextId) throws WebClientResponseException {
        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return getContextByContextIdRequestCreation(contextId).toEntity(localVarReturnType);
    }
    /**
     * get context by contextId and roleId
     * User must have LIST_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @param roleId roleId
     * @return Role
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getRoleByContextIdAndRoleIdRequestCreation(String contextId, String roleId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling getRoleByContextIdAndRoleId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'roleId' is set
        if (roleId == null) {
            throw new WebClientResponseException("Missing the required parameter 'roleId' when calling getRoleByContextIdAndRoleId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("contextId", contextId);
        pathParams.put("roleId", roleId);

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

        ParameterizedTypeReference<Role> localVarReturnType = new ParameterizedTypeReference<Role>() {};
        return apiClient.invokeAPI("/contexts/{contextId}/roles/{roleId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * get context by contextId and roleId
     * User must have LIST_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @param roleId roleId
     * @return Role
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Role> getRoleByContextIdAndRoleId(String contextId, String roleId) throws WebClientResponseException {
        ParameterizedTypeReference<Role> localVarReturnType = new ParameterizedTypeReference<Role>() {};
        return getRoleByContextIdAndRoleIdRequestCreation(contextId, roleId).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<Role>> getRoleByContextIdAndRoleIdWithHttpInfo(String contextId, String roleId) throws WebClientResponseException {
        ParameterizedTypeReference<Role> localVarReturnType = new ParameterizedTypeReference<Role>() {};
        return getRoleByContextIdAndRoleIdRequestCreation(contextId, roleId).toEntity(localVarReturnType);
    }
    /**
     * get roles by contextId
     * User must have LIST_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @return RolePage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getRolesByContextIdRequestCreation(String contextId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling getRolesByContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("contextId", contextId);

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

        ParameterizedTypeReference<RolePage> localVarReturnType = new ParameterizedTypeReference<RolePage>() {};
        return apiClient.invokeAPI("/contexts/{contextId}/roles", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * get roles by contextId
     * User must have LIST_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @return RolePage
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<RolePage> getRolesByContextId(String contextId) throws WebClientResponseException {
        ParameterizedTypeReference<RolePage> localVarReturnType = new ParameterizedTypeReference<RolePage>() {};
        return getRolesByContextIdRequestCreation(contextId).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<RolePage>> getRolesByContextIdWithHttpInfo(String contextId) throws WebClientResponseException {
        ParameterizedTypeReference<RolePage> localVarReturnType = new ParameterizedTypeReference<RolePage>() {};
        return getRolesByContextIdRequestCreation(contextId).toEntity(localVarReturnType);
    }
    /**
     * updates context by contextId
     * User must have UPDATE_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @param context The context parameter
     * @return Context
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec updateContextByContextIdRequestCreation(String contextId, Context context) throws WebClientResponseException {
        Object postBody = context;
        // verify the required parameter 'contextId' is set
        if (contextId == null) {
            throw new WebClientResponseException("Missing the required parameter 'contextId' when calling updateContextByContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'context' is set
        if (context == null) {
            throw new WebClientResponseException("Missing the required parameter 'context' when calling updateContextByContextId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("contextId", contextId);

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

        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return apiClient.invokeAPI("/contexts/{contextId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * updates context by contextId
     * User must have UPDATE_CONTEXTS permission
     * <p><b>200</b> - OK
     * <p><b>400</b> - Bad Request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param contextId contextId
     * @param context The context parameter
     * @return Context
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Context> updateContextByContextId(String contextId, Context context) throws WebClientResponseException {
        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return updateContextByContextIdRequestCreation(contextId, context).bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<Context>> updateContextByContextIdWithHttpInfo(String contextId, Context context) throws WebClientResponseException {
        ParameterizedTypeReference<Context> localVarReturnType = new ParameterizedTypeReference<Context>() {};
        return updateContextByContextIdRequestCreation(contextId, context).toEntity(localVarReturnType);
    }
}
