package com.alpha.omega.user.service;

public class UserContextRequest {
    String userId;
    String contextId;
    Boolean allRoles;
    String roles;
    String cacheControl;

    public String getUserId() {
        return userId;
    }

    public String getContextId() {
        return contextId;
    }

    public Boolean getAllRoles() {
        return allRoles;
    }

    public String getRoles() {
        return roles;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserContextRequest{");
        sb.append("userId='").append(userId).append('\'');
        sb.append(", contextId='").append(contextId).append('\'');
        sb.append(", allRoles=").append(allRoles);
        sb.append(", roles='").append(roles).append('\'');
        sb.append(", cacheControl='").append(cacheControl).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        String userId;
        String contextId;
        Boolean allRoles = Boolean.FALSE;
        String roles;
        String cacheControl;

        private Builder() {
        }

        public static Builder anUserContextRequest() {
            return new Builder();
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setContextId(String contextId) {
            this.contextId = contextId;
            return this;
        }

        public Builder setAllRoles(Boolean allRoles) {
            this.allRoles = allRoles;
            return this;
        }

        public Builder setRoles(String roles) {
            this.roles = roles;
            return this;
        }

        public Builder setCacheControl(String cacheControl) {
            this.cacheControl = cacheControl;
            return this;
        }

        public UserContextRequest build() {
            UserContextRequest userContextRequest = new UserContextRequest();
            userContextRequest.allRoles = this.allRoles;
            userContextRequest.roles = this.roles;
            userContextRequest.contextId = this.contextId;
            userContextRequest.userId = this.userId;
            userContextRequest.cacheControl = this.cacheControl;
            return userContextRequest;
        }
    }
}
