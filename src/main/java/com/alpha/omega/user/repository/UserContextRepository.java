package com.alpha.omega.user.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserContextRepository extends CrudRepository<UserContextEntity, String> {

	List<UserContextEntity> findByUserId(String userId);
	List<UserContextEntity> findByContextId(String contextId);

	//@Query(value = "SELECT VALUE COUNT(1) from c where c.contextId = @contextId" )
	Long findCountByContextId(@Param("contextId") String contextId);

	//@Query(value = "SELECT VALUE COUNT(1) from c where c.userId = @userId" )
	Long findCountByUserId(@Param("userId") String userId);

	Optional<UserContextEntity> findByUserIdAndContextId(String userId, String contextId);
	Optional<UserContextEntity> findByUserIdAndContextIdAndRoleId(String userId, String contextId, String roleId);
	void deleteByUserIdAndContextIdAndRoleId(String userId, String contextId, String roleId);
	Long deleteByContextId(String contextId);

	//@Query(value = "SELECT DISTINCT c.userId from c")
	UserContextEntity getUsersWithDistinct();
}
