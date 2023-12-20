package com.alpha.omega.user.validator;

import com.alpha.omega.user.model.UserContext;
import com.alpha.omega.user.repository.ContextEntity;
import com.alpha.omega.user.repository.RoleEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserContextValidator implements Validator<UserContext>{

	IdValidator idValidator = new IdValidator();

	@Override
	public List<ServiceError> validate(UserContext userContext) {
		List<ServiceError> errors = new ArrayList<>();
		if (userContext == null){
			errors.add(ServiceError.builder().message("UserContext cannot be null").build());
			return errors;
		}

		if (StringUtils.isBlank(userContext.getContextId())){
			errors.add(ServiceError.builder().message("ContextId cannot be blank or null").property("contextId").build());
		} else {
			errors.addAll(idValidator.validate(userContext.getContextId()));
		}



		if (StringUtils.isBlank(userContext.getUserId())){
			errors.add(ServiceError.builder().message("UserId cannot be blank or null").property("userId").build());
		}


		if (StringUtils.isBlank(userContext.getRoleId())){
			errors.add(ServiceError.builder().message("RoleId cannot be blank or null").property("roleId").build());
		} else {
			errors.addAll(idValidator.validate(userContext.getRoleId()));
		}



		return errors;
	}

	 public final static boolean additionalPermissionsInContextPermissions(UserContext uc, ContextEntity ctx) {
		boolean inContext = Boolean.TRUE.booleanValue();
		if (uc != null &&  !uc.getAdditionalPermissions().isEmpty()){
			Set<String> contextPermissions = new HashSet<>(ctx.getPermissions());
			inContext = contextPermissions.containsAll(uc.getAdditionalPermissions());
		}

		return inContext;
	}

	public final static boolean additionalRolesInContextRoles(UserContext uc, ContextEntity ctx){
		boolean hasRole = Boolean.TRUE.booleanValue();

		if (uc != null &&  !uc.getAdditionalRoles().isEmpty()){
			Set<String> contextRoleIds = ctx.getRoles().stream().map(RoleEntity::getRoleId).collect(Collectors.toSet());
			hasRole = contextRoleIds.containsAll(uc.getAdditionalRoles());
		}
		return hasRole;
	}

	public final static boolean ucRoleInContextRoles(UserContext uc, ContextEntity ctx) {
		Boolean foundRole = ctx.getRoles().stream()
				.map(RoleEntity::getRoleId)
				.filter(rid -> rid.equals(uc.getRoleId()))
				.map(rid -> Boolean.TRUE)
				.findFirst()
				.orElse(Boolean.FALSE);

		return foundRole.booleanValue();
	}
}
