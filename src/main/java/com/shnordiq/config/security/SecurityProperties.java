package com.shnordiq.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.security")
public class SecurityProperties {

    private Swagger swagger;
    private Jwt jwt;
    private Login login;
    private Oauth2 oauth2;

    @Getter
    @Setter
    public static class Swagger {

        private String swaggerUri;
        private String apiDocsUri;
    }

    @Getter
    @Setter
    public static class Jwt {

        private String secretKey;
        private int expirationTime;
        private String keyCryptoAlgorithm;
        private String jwtAlgorithm;
    }

    @Getter
    @Setter
    public static class Login {

        private String loginPage;
        private String logoutPage;
    }

    @Getter
    @Setter
    public static class Oauth2 {

        private String loginPage;
        private String defaultSuccessPage;
        private String failureLoginPage;
    }
}
