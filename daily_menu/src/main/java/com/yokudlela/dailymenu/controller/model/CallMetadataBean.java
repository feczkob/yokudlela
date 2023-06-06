package com.yokudlela.dailymenu.controller.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.Dependent;

@Getter
@Setter
@NoArgsConstructor
@Dependent
public class CallMetadataBean {

    @NonNull
    private String correlationId = UUID.randomUUID().toString();

    private String userId;
    private String userName;
    private String userEmail;
    private Set<String> roles;

}
