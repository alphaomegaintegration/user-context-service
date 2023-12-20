package com.alpha.omega.user.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;


@JsonInclude(JsonInclude.Include.NON_NULL)
@RedisHash("user:context")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class UserContextEntity {
	@Id
	private String id;

	@Indexed
	private String userId;
	@Indexed
	private String contextId;
	@Indexed
	private String roleId;
	private Collection<String> additionalPermissions;
	private boolean enabled;
	private String transactionId;
	@CreatedBy
	private String createdBy;
	@LastModifiedBy
	private String lastModifiedBy;
	@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date lastModifiedByDate;
	private Collection<String> additionalRoles;

	public UserContextEntity addRole(String roleId){
		if (additionalRoles == null){
			additionalRoles = new HashSet<>();
		}
		additionalRoles.add(roleId);
		return this;
	}

	public UserContextEntity auditModify(String auditUser, Date modifyDate){
		lastModifiedBy = auditUser;
		lastModifiedByDate = modifyDate;
		return this;
	}

}
