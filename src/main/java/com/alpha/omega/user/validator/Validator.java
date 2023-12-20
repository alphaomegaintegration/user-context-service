package com.alpha.omega.user.validator;


import java.util.List;

public interface Validator<T> {

	public List<ServiceError> validate(T t);
}
