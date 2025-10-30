package com.example.spas.controller;

import com.example.spas.exception.ForbiddenException;
import com.example.spas.exception.UnauthorizedException;
import com.example.spas.model.enums.Role;
import com.example.spas.model.User;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;

/**
 * A base class for controllers that require authentication.
 * Provides helper methods to check session and roles.
 */
public abstract class BaseController {

    /**
     * Gets the user from session.
     * Edge Case: Throws 401 Unauthorized if user is not logged in.
     */
    protected User getSessionUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new UnauthorizedException("You are not logged in. Please log in to continue.");
        }
        return user;
    }

    /**
     * Checks if the session user has a specific role.
     * Edge Case: Throws 401 if not logged in.
     * Edge Case: Throws 403 Forbidden if user has the wrong role.
     */
    protected User checkRole(HttpSession session, Role requiredRole) {
        User user = getSessionUser(session);
        if (user.getRole() != requiredRole) {
            throw new ForbiddenException("You do not have permission. Required role: " + requiredRole);
        }
        return user;
    }

//    /**
//     * Checks if the session user has one of the allowed roles.
//     * Edge Case: Throws 401 if not logged in.
//     * Edge Case: Throws 403 Forbidden if user's role is not in the list.
//     */
//    protected User checkRoles(HttpSession session, Role... allowedRoles) {
//        User user = getSessionUser(session);
//        List<Role> rolesList = Arrays.asList(allowedRoles);
//        
//        if (!rolesList.contains(user.getRole())) {
//            throw new ForbiddenException("You do not have permission for this action.");
//        }
//        return user;
//    }
}