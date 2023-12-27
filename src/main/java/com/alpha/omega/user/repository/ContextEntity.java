package com.alpha.omega.user.repository;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

import static com.alpha.omega.user.service.ServiceUtils.CONTEXT_KEY_PREFIX;
// TODO REDIS used contextEntity instead of context.
@RedisHash(CONTEXT_KEY_PREFIX)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class ContextEntity {
	@Id
	private String id;

	@NotNull
	@Indexed
	private String contextId;

	@NotNull
	private String contextName;
	private String description;
	private String name;
	private boolean enabled = Boolean.TRUE.booleanValue();
	@NotNull
	private Collection<String> permissions;
	@NotNull

	//@JsonIdentityReference(alwaysAsId = false)
	@Reference
	private Collection<RoleEntity> roles;
	private String transactionId;
	@CreatedBy
	//@NotNull
	private String createdBy;
	@LastModifiedBy
	//@NotNull
	private String lastModifiedBy;
	//@CreatedDate
	private Date createdDate;
	@LastModifiedDate
	private Date lastModifiedByDate;


}
