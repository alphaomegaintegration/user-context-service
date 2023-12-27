package com.alpha.omega.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<RoleEntity, String>, QueryByExampleExecutor<RoleEntity> {

	Optional<RoleEntity> findByContextId(String contextId);
	List<RoleEntity> findByContextIdIn(List<String> ids);
	Integer deleteByContextId(String contextId);
}
