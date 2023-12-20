package com.alpha.omega.user.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdValidator implements Validator<String>{

	/*
	https://regex101.com/
	 */
	public static final String ID_REGEX = "^([a-zA-z]+[a-zA-Z0-9]+(-*[a-zA-z0-9]+)*)$";
	public static final String REMOVABLE[] = {"_"};

	Pattern pattern = Pattern.compile(ID_REGEX);

	@Override
	public List<ServiceError> validate(String strId) {
		List<ServiceError> errors = new ArrayList<>();

		if (StringUtils.isBlank(strId)){
			errors.add(ServiceError.builder().message("Id cannot be blank or null").property("id").build());
		}

		Matcher matcher = pattern.matcher(strId);
		if (!matcher.matches() || StringUtils.containsAny(strId, REMOVABLE)){
			errors.add(ServiceError.builder().name(strId).message("Id has the wrong format").property("id").build());
		}

		return errors;
	}
}
