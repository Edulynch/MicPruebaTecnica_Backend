package com.blautech.pruebaTecnica.demo.api.roles.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleIdRequest {
    private Long roleId;

    public RoleIdRequest() {
    }

    public RoleIdRequest(Long roleId) {
        this.roleId = roleId;
    }

}
