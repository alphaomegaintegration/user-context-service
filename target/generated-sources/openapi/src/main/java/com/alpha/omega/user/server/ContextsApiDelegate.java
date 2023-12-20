package com.alpha.omega.user.server;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.model.ContextPage;
import com.alpha.omega.user.model.Role;
import com.alpha.omega.user.model.RolePage;
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
 * A delegate to be called by the {@link ContextsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-20T06:26:02.527872-05:00[America/New_York]")
public interface ContextsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /contexts/{contextId}/roles : get roles by contextId
     * User must have UPDATE_CONTEXTS permission
     *
     * @param contextId contextId (required)
     * @param role  (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see ContextsApi#addAdditionalRolesByContextId
     */
    default Mono<ResponseEntity<Context>> addAdditionalRolesByContextId(String contextId,
        Mono<Role> role,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"contextName\" : \"contextName\", \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roles\" : [ { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" }, { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" } ], \"description\" : \"description\", \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * POST /contexts : creates new context via upsert
     * User must have CREATE_CONTEXTS permission
     *
     * @param context  (required)
     * @return OK (status code 201)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see ContextsApi#createContext
     */
    default Mono<ResponseEntity<Context>> createContext(Mono<Context> context,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"contextName\" : \"contextName\", \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roles\" : [ { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" }, { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" } ], \"description\" : \"description\", \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * DELETE /contexts/{contextId} : deletes context by contextId
     * User must have DELETE_CONTEXTS permission
     *
     * @param contextId contextId (required)
     * @return deleted successfully (status code 204)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see ContextsApi#deleteContextByContextId
     */
    default Mono<ResponseEntity<Void>> deleteContextByContextId(String contextId,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        return result.then(Mono.empty());

    }

    /**
     * GET /contexts : gets all contexts
     * User must have LIST_CONTEXTS permission
     *
     * @param page What page to grab (optional, default to 1)
     * @param pageSize Number of elements on page (optional, default to 25)
     * @param direction Sort order direction (optional, default to ASC)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see ContextsApi#getAllContexts
     */
    default Mono<ResponseEntity<ContextPage>> getAllContexts(Integer page,
        Integer pageSize,
        String direction,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"elapsed\" : \"elapsed\", \"total\" : 1, \"pageSize\" : 6, \"correlationId\" : \"correlationId\", \"page\" : 0, \"content\" : [ { \"contextName\" : \"contextName\", \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roles\" : [ { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" }, { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" } ], \"description\" : \"description\", \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"enabled\" : true, \"transactionId\" : \"transactionId\" }, { \"contextName\" : \"contextName\", \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roles\" : [ { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" }, { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" } ], \"description\" : \"description\", \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"enabled\" : true, \"transactionId\" : \"transactionId\" } ] }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * GET /contexts/{contextId} : gets context by contextId
     * User must have LIST_CONTEXTS permission
     *
     * @param contextId contextId (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see ContextsApi#getContextByContextId
     */
    default Mono<ResponseEntity<Context>> getContextByContextId(String contextId,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"contextName\" : \"contextName\", \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roles\" : [ { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" }, { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" } ], \"description\" : \"description\", \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * GET /contexts/{contextId}/roles/{roleId} : get context by contextId and roleId
     * User must have LIST_CONTEXTS permission
     *
     * @param contextId contextId (required)
     * @param roleId roleId (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see ContextsApi#getRoleByContextIdAndRoleId
     */
    default Mono<ResponseEntity<Role>> getRoleByContextIdAndRoleId(String contextId,
        String roleId,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * GET /contexts/{contextId}/roles : get roles by contextId
     * User must have LIST_CONTEXTS permission
     *
     * @param contextId contextId (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see ContextsApi#getRolesByContextId
     */
    default Mono<ResponseEntity<RolePage>> getRolesByContextId(String contextId,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"elapsed\" : \"elapsed\", \"total\" : 1, \"pageSize\" : 6, \"correlationId\" : \"correlationId\", \"page\" : 0, \"content\" : [ { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" }, { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" } ] }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

    /**
     * PUT /contexts/{contextId} : updates context by contextId
     * User must have UPDATE_CONTEXTS permission
     *
     * @param contextId contextId (required)
     * @param context  (required)
     * @return OK (status code 200)
     *         or Bad Request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see ContextsApi#updateContextByContextId
     */
    default Mono<ResponseEntity<Context>> updateContextByContextId(String contextId,
        Mono<Context> context,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"contextName\" : \"contextName\", \"modifiedTime\" : \"modifiedTime\", \"createdBy\" : \"createdBy\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roles\" : [ { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" }, { \"roleId\" : \"roleId\", \"permissions\" : [ \"permissions\", \"permissions\" ], \"roleName\" : \"roleName\" } ], \"description\" : \"description\", \"createdTime\" : \"createdTime\", \"contextId\" : \"contextId\", \"modifiedBy\" : \"modifiedBy\", \"id\" : \"id\", \"enabled\" : true, \"transactionId\" : \"transactionId\" }";
                result = ApiUtil.getExampleResponse(exchange, mediaType, exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

}
