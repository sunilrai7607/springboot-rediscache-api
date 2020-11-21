package com.sbr.rest.api.redis.redisapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserProfile implements Serializable {

    private static final long serialVersionUID = -5301061441364381749L;

    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
}
