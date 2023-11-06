package com.alexandervov.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface Constants {

    SimpleGrantedAuthority ROLE_DEVELOPER = new SimpleGrantedAuthority("ROLE_DEVELOPER");
    String APP_NAME_DUPLICATION_ERROR_MESSAGE = "Upload error: Application name isn't unique";

    int QUANTITY_MAX_POPULAR_APPS = 5;

}
