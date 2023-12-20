package com.alpha.omega.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<RoleEntity, String>, QueryByExampleExecutor<RoleEntity> {

	Optional<RoleEntity> findByContextId(String contextId);
	Integer deleteByContextId(String contextId);
}
