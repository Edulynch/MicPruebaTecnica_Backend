package com.blautech.pruebaTecnica.demo.api.roles.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleUpdateRequest {
    private String role;

    public RoleUpdateRequest() {
    }

    public RoleUpdateRequest(String role) {
        this.role = role;
    }

}
