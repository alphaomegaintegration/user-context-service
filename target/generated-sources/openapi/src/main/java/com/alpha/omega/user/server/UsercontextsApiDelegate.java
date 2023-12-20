package com.alpha.omega.user.server;

import com.alpha.omega.user.model.UserContext;
import com.alpha.omega.user.model.UserContextBatchRequest;
import com.alpha.omega.user.model.UserContextPage;
import com.alpha.omega.user.model.UserContextPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.codec.multipart.Part;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

/**
 * A delegate to be called by the {@link UsercontextsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-20T06:26:02.527872-05:00[America/New_York]")
public interface UsercontextsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /usercontexts/user/{userId}/context/{contextId}/permissions/{permission} : gets userContext by userId and contextId
     *
     * @param userId userId (required)
     * @param contextId contextId (required)
     * @param permission permission (required)
     * @param cacheControl  (optional, default to no-cache)
     * @return OK (status code 200)
     * @see UsercontextsApi#addPermissionToUserContext
     */
    default Mono<ResponseEntity<UserContext>> addPermissionToUserContext(String userId,
        String contextId,
        String permission,
        String cacheControl,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * POST /usercontexts/user/{userId}/context/{contextId}/permissions : gets userContext by userId and contextId
     *
     * @param userId userId (required)
     * @param contextId contextId (required)
     * @param cacheControl  (optional, default to no-cache)
     * @param additionalPermissions additionalPermissions comma delimited (optional)
     * @return OK (status code 200)
     * @see UsercontextsApi#addPermissionsToUserContext
     */
    default Mono<ResponseEntity<UserContext>> addPermissionsToUserContext(String userId,
        String contextId,
        String cacheControl,
        String additionalPermissions,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * POST /usercontexts/user/{userId}/context/{contextId}/role/{roleId} : gets userContext by userId and contextId
     * User must have CREATE_USER_CONTEXTS permission
     *
     * @param userId userId (required)
     * @param contextId contextId (required)
     * @param roleId roleId (required)
     * @param cacheControl  (optional, default to no-cache)
     * @param additionalPermissions additionalPermissions comma delimited (optional)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#addRoleToUserContext
     */
    default Mono<ResponseEntity<UserContext>> addRoleToUserContext(String userId,
        String contextId,
        String roleId,
        String cacheControl,
        String additionalPermissions,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * POST /usercontexts : creates userContext
     * User must have CREATE_USER_CONTEXTS permission
     *
     * @param userContext  (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#createUserContext
     */
    default Mono<ResponseEntity<UserContext>> createUserContext(Mono<UserContext> userContext,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * POST /usercontexts/batch : creates userContext
     * User must have CREATE_USER_CONTEXTS permission
     *
     * @param userContextBatchRequest  (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#createUserContextBatch
     */
    default Mono<ResponseEntity<UserContextPage>> createUserContextBatch(Mono<UserContextBatchRequest> userContextBatchRequest,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"elapsed\" : \"elapsed\", \"total\" : 1, \"pageSize\" : 6, \"correlationId\" : \"correlationId\", \"page\" : 0, \"content\" : [ { \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }, { \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" } ] }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * DELETE /usercontexts/{usercontextId} : deletes userContext by usercontextId
     * User must have DELETE_USER_CONTEXTS permission
     *
     * @param usercontextId usercontextId (required)
     * @return deleted successfully (status code 204)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#deleteUserContextByUserContextId
     */
    default Mono<ResponseEntity<Void>> deleteUserContextByUserContextId(String usercontextId,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        return result.then(Mono.empty());

    }

    /**
     * GET /usercontexts : gets all userContexts
     * User must have LIST_USER_CONTEXTS permission
     *
     * @param page What page to grab (optional, default to 1)
     * @param pageSize Number of elements on page (optional, default to 25)
     * @param direction Sort order direction (optional, default to ASC)
     * @param cacheControl Http cache control header (optional)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#getAllUserContexts
     */
    default Mono<ResponseEntity<UserContextPage>> getAllUserContexts(Integer page,
        Integer pageSize,
        String direction,
        String cacheControl,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"elapsed\" : \"elapsed\", \"total\" : 1, \"pageSize\" : 6, \"correlationId\" : \"correlationId\", \"page\" : 0, \"content\" : [ { \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }, { \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" } ] }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * GET /usercontexts/context/{contextId} : gets userContext by userId and contextId
     * user must have LIST_USER_CONTEXTS permission
     *
     * @param contextId contextId (required)
     * @param cacheControl  (optional, default to no-cache)
     * @param page What page to grab (optional, default to 1)
     * @param pageSize Number of elements on page (optional, default to 25)
     * @param direction Sort order direction (optional, default to ASC)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#getUserContextByContextId
     */
    default Mono<ResponseEntity<UserContextPage>> getUserContextByContextId(String contextId,
        String cacheControl,
        Integer page,
        Integer pageSize,
        String direction,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"elapsed\" : \"elapsed\", \"total\" : 1, \"pageSize\" : 6, \"correlationId\" : \"correlationId\", \"page\" : 0, \"content\" : [ { \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }, { \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" } ] }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * GET /usercontexts/{usercontextId} : gets userContext by usercontextId
     * User must have LIST_USER_CONTEXTS permission
     *
     * @param usercontextId usercontextId (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#getUserContextByUserContextId
     */
    default Mono<ResponseEntity<UserContext>> getUserContextByUserContextId(String usercontextId,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * GET /usercontexts/user/{userId} : gets userContext by userId and contextId
     * User must have LIST_USER_CONTEXTS permission
     *
     * @param userId userId (required)
     * @param cacheControl  (optional, default to no-cache)
     * @param page What page to grab (optional, default to 1)
     * @param pageSize Number of elements on page (optional, default to 25)
     * @param direction Sort order direction (optional, default to ASC)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#getUserContextByUserId
     */
    default Mono<ResponseEntity<UserContextPage>> getUserContextByUserId(String userId,
        String cacheControl,
        Integer page,
        Integer pageSize,
        String direction,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"elapsed\" : \"elapsed\", \"total\" : 1, \"pageSize\" : 6, \"correlationId\" : \"correlationId\", \"page\" : 0, \"content\" : [ { \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }, { \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" } ] }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * GET /usercontexts/user/{userId}/context/{contextId} : gets userContext by userId and contextId
     * User must have LIST_USER_CONTEXTS permission
     *
     * @param userId userId (required)
     * @param contextId contextId (required)
     * @param allRoles Determines if all roles permissions are returned (optional, default to false)
     * @param roles comma separated list of roles (optional)
     * @param cacheControl  (optional)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#getUserContextByUserIdAndContextId
     */
    default Mono<ResponseEntity<UserContextPermissions>> getUserContextByUserIdAndContextId(String userId,
        String contextId,
        Boolean allRoles,
        String roles,
        String cacheControl,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"contextId\" : \"contextId\", \"userId\" : \"userId\", \"enabled\" : true }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * PUT /usercontexts/{usercontextId} : updates userContext via upsert
     * User must have UPDATE_USER_CONTEXTS permission
     *
     * @param usercontextId usercontextId (required)
     * @param userContext  (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UsercontextsApi#updateUserContext
     */
    default Mono<ResponseEntity<UserContext>> updateUserContext(String usercontextId,
        Mono<UserContext> userContext,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"roleId\" : \"roleId\", \"additionalRoles\" : [ \"additionalRoles\", \"additionalRoles\" ], \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"userId\" : \"userId\", \"additionalPermissions\" : [ \"additionalPermissions\", \"additionalPermissions\" ], \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

}
