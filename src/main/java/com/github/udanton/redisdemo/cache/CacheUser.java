package com.github.udanton.redisdemo.cache;

import java.io.Serializable;

import com.github.udanton.redisdemo.persistence.User;
import lombok.Data;

@Data
public class CacheUser implements Serializable {
    private Long expiration;
    private Long delta;
    private User user;
}
