package com.alpha.omega.user.client;

import com.alpha.omega.user.client.ApiClient;

import com.alpha.omega.user.batch.UserLoad;

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
public class UsersApi {
    private ApiClient apiClient;

    public UsersApi() {
        this(new ApiClient());
    }

    @Autowired
    public UsersApi(ApiClient apiClient) {
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
     * @return UserLoad
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec createUsersRequestCreation() throws WebClientResponseException {
        Object postBody = null;
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
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserLoad> localVarReturnType = new ParameterizedTypeReference<UserLoad>() {};
        return apiClient.invokeAPI("/users/batch", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * gets userContext by userId and contextId
     * 
     * <p><b>200</b> - OK
     * @return UserLoad
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UserLoad> createUsers() throws WebClientResponseException {
        ParameterizedTypeReference<UserLoad> localVarReturnType = new ParameterizedTypeReference<UserLoad>() {};
        return createUsersRequestCreation().bodyToMono(localVarReturnType);
    }

    public Mono<ResponseEntity<UserLoad>> createUsersWithHttpInfo() throws WebClientResponseException {
        ParameterizedTypeReference<UserLoad> localVarReturnType = new ParameterizedTypeReference<UserLoad>() {};
        return createUsersRequestCreation().toEntity(localVarReturnType);
    }
}
