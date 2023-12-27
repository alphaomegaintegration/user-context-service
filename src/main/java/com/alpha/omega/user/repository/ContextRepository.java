package com.alpha.omega.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface ContextRepository extends CrudRepository<ContextEntity, String>,
		QueryByExampleExecutor<ContextEntity> {

	ContextEntity findByContextId(String contextId);
	ContextEntity deleteByContextId(String contextId);
}
