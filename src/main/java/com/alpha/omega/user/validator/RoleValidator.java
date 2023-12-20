package com.alpha.omega.user.validator;

import com.alpha.omega.user.model.Role;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RoleValidator  implements Validator<Role>{

	IdValidator idValidator = new IdValidator();

	public List<ServiceError> validate(Role role){
		List<ServiceError> errors = new ArrayList<>();
		if (role == null){
			errors.add(ServiceError.builder().message("Role cannot be null").build());
			return errors;
		}

		if (StringUtils.isBlank(role.getRoleId())){
			errors.add(ServiceError.builder().message("RoleId cannot be blank or null").property("roleId").build());
		} else {
			errors.addAll(idValidator.validate(role.getRoleId()));
		}

		if (StringUtils.isBlank(role.getRoleName())){
			errors.add(ServiceError.builder().message("RoleName cannot be blank or null").property("roleName").build());
		}

		if (role.getPermissions() == null || role.getPermissions().isEmpty()){
			errors.add(ServiceError.builder().message("Permissions cannot be empty or null").property("permissions").build());
		}
		return errors;

	}
}
