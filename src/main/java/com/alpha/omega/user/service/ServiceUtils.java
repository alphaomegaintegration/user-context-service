package com.alpha.omega.user.service;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.model.Role;
import com.alpha.omega.user.model.UserContext;
import com.alpha.omega.user.repository.ContextEntity;
import com.alpha.omega.user.repository.RoleDto;
import com.alpha.omega.user.repository.RoleEntity;
import com.alpha.omega.user.repository.UserContextEntity;
import com.alpha.omega.user.validator.ServiceError;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alpha.omega.user.utils.Constants.*;

public class ServiceUtils {

    static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

    public final static int ONE = 1;
    public final static DateTimeFormatter SERVICE_DATETIME_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneId.of("UTC"));

    public final static String ADMIN = "ADMIN";
    public final static String ADMIN_LOWERCASE = ADMIN.toLowerCase();
    public final static Set<String> adminPerms = Collections.singleton(ADMIN);
    final static String UNDERSCORE = "_";
    final static String DASH = "-";

    public final static String CORRELATION_ID = "correlationId";


    public final static String migrationContextId(String contextId){
        return contextId.toLowerCase();
    }

    public static String calculateRoleMigrationName(String serviceName) {
        String result = serviceName.toUpperCase().replace(DASH,UNDERSCORE);
        return new StringBuilder(result).append(UNDERSCORE).append(ADMIN).toString();
    }
    public static String calculateRoleMigrationId(String serviceName) {
        String result = serviceName.toLowerCase().replace(UNDERSCORE,DASH);
        return new StringBuilder(result).append(DASH).append(ADMIN_LOWERCASE).toString();
    }

    public final static RoleDto migrationRoleDto(String serviceName){
        String rolelName = calculateRoleMigrationName(serviceName);
        String roleId = calculateRoleMigrationId(serviceName);

        return RoleDto.newBuilder()
                .setRoleId(roleId)
                .setRoleName(rolelName)
                .setPermissions(Collections.singleton(rolelName))
                .build();
    }

    public final static Function<RoleEntity, Role> roleEntityToRole = (roleEntity) -> {

        Role role = new Role();
        role.setRoleId(roleEntity.getRoleId());
        role.setRoleName(roleEntity.getRoleName());
        role.setPermissions(new HashSet<>(roleEntity.getPermissions()));
        return role;
    };


    public final static Function<Role, RoleEntity> roleToRoleEntity = (role) -> {

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(role.getRoleId());
        roleEntity.setRoleName(role.getRoleName());
        roleEntity.setPermissions(new ArrayList<>(role.getPermissions()));
        return roleEntity;
    };

    public final static Function<RoleDto, Role> roleDtoToRole = (roleDto) -> {

        Role role = new Role();
        role.setRoleId(roleDto.getRoleId());
        role.setRoleName(roleDto.getRoleName());
        role.setPermissions(new HashSet<>(roleDto.getPermissions()));
        return role;
    };

    public final static Function<Role, RoleDto> roleToRoleDto = (role) -> {

        RoleDto roleDto = new RoleDto();
        roleDto.setRoleId(role.getRoleId());
        roleDto.setRoleName(role.getRoleName());
        roleDto.setPermissions(new ArrayList<>(role.getPermissions()));
        return roleDto;
    };

    public final static Function<ContextEntity, Context> convertContextEntityToContext = (contextEntity) -> {
        logger.debug("Got context in convertContextToDto => {}", contextEntity);
        Context context = new Context();
        BeanUtils.copyProperties(contextEntity, context);
        context.setPermissions(new HashSet<>(contextEntity.getPermissions()));
        context.setRoles(contextEntity.getRoles().stream()
                        .filter(Objects::nonNull)
                .map(roleEntityToRole)
                .collect(Collectors.toList()));
        return context;
    };

    public static String formatValidationErrors(List<ServiceError> errors, String entity) {

        return new StringBuilder("Object: ").append(entity)
                .append(" Errors: ").append(errors).toString();

    }

    public static Function<Long,String> calculateElapsedMessage() {
            return (val) -> {
                float fVal = new BigDecimal(val).divide(ONE_THOUSAND).floatValue();
                String message = String.format(ELAPSED_FORMAT_STR, fVal);
                return message;
            };
    }

    public static final Jackson2JsonRedisSerializer JACKSON_2_JSON_REDIS_SERIALIZER_MAP = new Jackson2JsonRedisSerializer(Map.class);

    final static ParameterizedTypeReference<Map<String, Object>> MAP_OBJECT_TYPE_REFERENCE = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    public final static String CONTEXT_KEY_PREFIX = "contextEntity";
    public final static String ALL_CONTEXT_KEY_PREFIX = CONTEXT_KEY_PREFIX+COLON+STAR;


    public final static String calculateContextKey(String contextId) {
        return new StringBuilder(CONTEXT_KEY_PREFIX)
                .append(COLON)
                .append(contextId).toString();
    }

    public final static String calculateUserContextKey(String userId, String contextId, String roleId){
        return new StringBuilder(USER_CONTEXT_KEY_PREFIX).append(COLON)
                .append("user").append(COLON).append(userId).append(COLON)
                .append("context").append(COLON).append(contextId).append(COLON)
                .append("role").append(COLON).append(roleId).toString();
    }

    public final static String calculateUserContextKey(UserContext ctx){
        return calculateUserContextKey(ctx.getUserId(), ctx.getContextId(), ctx.getRoleId());
    }


    public final static String calculateUserContextKey(UserContextEntity ctx){
        return calculateUserContextKey(ctx.getUserId(), ctx.getContextId(), ctx.getRoleId());
    }


    public final static String USER_CONTEXT_KEY_PREFIX = "userContextEntity";
    public final static String ROLE_KEY_PREFIX = "roleEntity";
    public final static String CLIENT_REGISTRATION_KEY_PREFIX = "clientRegistrationEntity";
    public final static String ALL_USER_CONTEXT_KEY_PREFIX = USER_CONTEXT_KEY_PREFIX+COLON+STAR;
    public final static String BASIC_PREFIX = "Basic ";

    public static final BigDecimal ONE_THOUSAND = new BigDecimal(1000);
    public static final String ELAPSED_FORMAT_STR = "Seconds %.3f";
    public static final String CONTEXT_NOT_FOUND_FOR_ID = "Context not found for contextId %s";
    public static final String USER_CONTEXT_NOT_FOUND = "UserContext not found ";
    public static final String USER_CONTEXT_NOT_FOUND_FOR_USER_ID_AND_CONTEXT_ID = USER_CONTEXT_NOT_FOUND +"for userId %s and contextId %s";
    public static final String USER_CONTEXT_NOT_FOUND_FOR_CONTEXT_ID =  USER_CONTEXT_NOT_FOUND +"for contextId %s";
    public static final String USER_CONTEXT_NOT_FOUND_FOR_USER_ID = USER_CONTEXT_NOT_FOUND +"for userId %s";
    public static final String USER_CONTEXT_NOT_FOUND_FOR_USER_CONTEXT_ID = USER_CONTEXT_NOT_FOUND +"for userContextId %s";
    public static final Mono<ContextEntity> getEmptyMonoContextEntity(){
        return Mono.just(new ContextEntity());
    }

    public final static Optional<String> extractFileAsStringFromResource(Resource resource){
        Optional<String> value = Optional.empty();
        try {
            String fileStr = IOUtils.toString(resource.getInputStream(), Charset.defaultCharset());
            value = Optional.of(fileStr);
        } catch (Exception e) {
            logger.warn("Could not extractFileFromResource {} because {}",resource,e.getMessage());

        }
        return value;
    }

    public static BiFunction<Map<String,Object>, ObjectMapper, JsonNode> convertToJsonNode(){
        return (map,objectMapper) -> {
            return objectMapper.convertValue(map, JsonNode.class);
        };
    }
}
