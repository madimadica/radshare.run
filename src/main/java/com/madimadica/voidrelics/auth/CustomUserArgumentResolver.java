package com.madimadica.voidrelics.auth;

import com.madimadica.voidrelics.exceptions.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

/**
 * Custom resolver to resolve {@link CustomUser} or {@link Optional<CustomUser>}.
 * When using a standalone {@link CustomUser} the API will throw if the user is not authenticated. Otherwise,
 * when using {@link Optional<CustomUser>} the API will return an empty optional if the user is not authenticated.
 */
public class CustomUserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Allow "CustomUser" (required) or "Optional<CustomUser>" optional
        return CustomUser.class.equals(parameter.getParameterType())
                || Optional.class.equals(parameter.getParameterType()) &&
                ((ParameterizedType) parameter.getGenericParameterType())
                        .getActualTypeArguments()[0].equals(CustomUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        var auth = (Authentication) webRequest.getUserPrincipal();
        boolean isOptional = parameter.getParameterType().equals(Optional.class);
        if (auth == null || auth.getPrincipal() == null) {
            // un-authenticated request
            if (isOptional) {
                LOGGER.debug("Resolved empty Optional<CustomUser>");
                return Optional.<CustomUser>empty();
            } else {
                LOGGER.debug("Failed to resolve CustomUser");
                throw new ApiError(401, "Unauthorized: Not logged in");
            }
        }
        var userDetails = (CustomUser) auth.getPrincipal();
        if (isOptional) {
            LOGGER.debug("Resolved Optional<CustomUser> {}", userDetails);
            return Optional.of((CustomUser) userDetails);
        } else {
            LOGGER.debug("Resolved CustomUser {}", userDetails);
            return (CustomUser) userDetails;
        }
    }
}
