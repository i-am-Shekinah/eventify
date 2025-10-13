package com.codewithmike.eventify.security;

import com.codewithmike.eventify.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /**
     * Returns the currently authenticated User.
     * Assumes that the User object is stored in the Authentication principal.
     */
    public static User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        return null;
    }
}
