package com.alpha.omega.user.repository;

import java.util.Collection;

public class RoleDto {

    private String roleId;
    private String roleName;
    private Collection<String> permissions;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = permissions;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoleDto{");
        sb.append("roleId='").append(roleId).append('\'');
        sb.append(", roleName='").append(roleName).append('\'');
        sb.append(", permissions=").append(permissions);
        sb.append('}');
        return sb.toString();
    }

    public static final class Builder {
        private String roleId;
        private String roleName;
        private Collection<String> permissions;

        private Builder() {
        }

        public static Builder aRoleDto() {
            return new Builder();
        }

        public Builder setRoleId(String roleId) {
            this.roleId = roleId;
            return this;
        }

        public Builder setRoleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder setPermissions(Collection<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public RoleDto build() {
            RoleDto roleDto = new RoleDto();
            roleDto.setRoleId(roleId);
            roleDto.setRoleName(roleName);
            roleDto.setPermissions(permissions);
            return roleDto;
        }
    }
}
