package com.alpha.omega.user.repository;

import java.util.Collection;
import java.util.Date;

public final class UserContextEntityBuilder {
	private String id;
	private String userId;
	private String contextId;
	private String roleId;
	private Collection<String> additionalPermissions;
	private boolean enabled;
	private String transactionId;
	private String createdBy;
	private String lastModifiedBy;
	private Date createdDate;
	private Date lastModifiedByDate;
	private Collection<String> additionalRoles;

	private UserContextEntityBuilder() {
	}

	public static UserContextEntityBuilder newBuilder() {
		return new UserContextEntityBuilder();
	}

	public UserContextEntityBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public UserContextEntityBuilder setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public UserContextEntityBuilder setContextId(String contextId) {
		this.contextId = contextId;
		return this;
	}

	public UserContextEntityBuilder setRoleId(String roleId) {
		this.roleId = roleId;
		return this;
	}

	public UserContextEntityBuilder setAdditionalPermissions(Collection<String> additionalPermissions) {
		this.additionalPermissions = additionalPermissions;
		return this;
	}

	public UserContextEntityBuilder setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public UserContextEntityBuilder setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	public UserContextEntityBuilder setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public UserContextEntityBuilder setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
		return this;
	}

	public UserContextEntityBuilder setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public UserContextEntityBuilder setLastModifiedByDate(Date lastModifiedByDate) {
		this.lastModifiedByDate = lastModifiedByDate;
		return this;
	}

	public UserContextEntityBuilder setAdditionalRoles(Collection<String> additionalRoles) {
		this.additionalRoles = additionalRoles;
		return this;
	}

	public UserContextEntity build() {
		UserContextEntity userContextEntity = new UserContextEntity();
		userContextEntity.setId(id);
		userContextEntity.setUserId(userId);
		userContextEntity.setContextId(contextId);
		userContextEntity.setRoleId(roleId);
		userContextEntity.setAdditionalPermissions(additionalPermissions);
		userContextEntity.setEnabled(enabled);
		userContextEntity.setTransactionId(transactionId);
		userContextEntity.setCreatedBy(createdBy);
		userContextEntity.setLastModifiedBy(lastModifiedBy);
		userContextEntity.setCreatedDate(createdDate);
		userContextEntity.setLastModifiedByDate(lastModifiedByDate);
		userContextEntity.setAdditionalRoles(additionalRoles);
		return userContextEntity;
	}
}
