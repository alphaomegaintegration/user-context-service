package com.alpha.omega.user.validator;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.repository.RoleEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ContextValidator implements Validator<Context>{

	RoleValidator roleValidator = new RoleValidator();
	IdValidator idValidator = new IdValidator();

	public List<ServiceError> validate(Context context){
		List<ServiceError> errors = new ArrayList<>();
		if (context == null){
			errors.add(ServiceError.builder().message("Context cannot be null").build());
			return errors;
		}

		if (StringUtils.isBlank(context.getContextId())){
			errors.add(ServiceError.builder().message("ContextId cannot be blank or null").property("contextId").build());
		} else {
			errors.addAll(idValidator.validate(context.getContextId()));
		}

		if (StringUtils.isBlank(context.getContextName())){
			errors.add(ServiceError.builder().message("ContextName cannot be blank or null").property("contextName").build());
		}

		if (StringUtils.isBlank(context.getDescription())){
			errors.add(ServiceError.builder().message("Description cannot be blank or null").property("description").build());
		}

		if (context.getPermissions() == null || context.getPermissions().isEmpty()){
			errors.add(ServiceError.builder().message("Permissions cannot be empty or null").property("permissions").build());
		}

		if (context.getRoles() == null || context.getRoles().isEmpty()){
			errors.add(ServiceError.builder().message("Roles cannot be empty or null").property("roles").build());
		}

		Set<String> roleIds = context.getRoles().stream().map(role -> role.getRoleId()).collect(Collectors.toSet());
		if (roleIds.size() < context.getRoles().size()){
			errors.add(ServiceError.builder().message("Roles cannot contain duplicate roleIds").property("roles").build());
		}

		List<ServiceError> validRolePermissions =  checkValidRolePermissions(context);
		errors.addAll(validRolePermissions);

		List<ServiceError> roleErrors = context.getRoles().stream()
				.flatMap(rl -> roleValidator.validate(rl).stream())
				.collect(Collectors.toList());
		errors.addAll(roleErrors);

		return errors;
	}

	public static List<ServiceError> checkValidRolePermissions(Context context){
	List<ServiceError> validRolePermissions =  context.getRoles().stream()
			.filter(rDto ->  !context.getPermissions().containsAll(rDto.getPermissions()))
			.map(rDto -> ServiceError.builder().message("Role permissions must be in permissions set for roleId "+rDto.getRoleId())
					.property("roles")
					.build())
			.collect(Collectors.toList());
	return validRolePermissions;
	}

}
