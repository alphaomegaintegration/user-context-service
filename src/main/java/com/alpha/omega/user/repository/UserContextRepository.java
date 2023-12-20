package com.alpha.omega.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserContextRepository extends CrudRepository<UserContextEntity, String> {

	UserContextEntity findByUserId(String userId);
	List<UserContextEntity> findByContextId(String contextId);

	//@Query(value = "SELECT VALUE COUNT(1) from c where c.contextId = @contextId" )
	Long findCountByContextId(@Param("contextId") String contextId);

	//@Query(value = "SELECT VALUE COUNT(1) from c where c.userId = @userId" )
	Long findCountByUserId(@Param("userId") String userId);

	UserContextEntity findByUserIdAndContextId(String userId, String contextId);
	UserContextEntity findByUserIdAndContextIdAndRoleId(String userId, String contextId, String roleId);
	void deleteByUserIdAndContextIdAndRoleId(String userId, String contextId, String roleId);
	Long deleteByContextId(String contextId);

	//@Query(value = "SELECT DISTINCT c.userId from c")
	UserContextEntity getUsersWithDistinct();
}
