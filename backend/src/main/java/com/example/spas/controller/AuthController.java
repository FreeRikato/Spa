package com.example.spas.controller;

import com.example.spas.dto.LoginRequest;
import com.example.spas.dto.RegistrationRequest;
import com.example.spas.dto.UserView;
import com.example.spas.model.User;
import com.example.spas.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Feature 1: Registration
     * Edge Case: @Valid catches bad input (e.g., empty email, short password).
     * Service logic catches "Email already in use".
     */
    @PostMapping("/register")
    public ResponseEntity<UserView> register(
        @Valid @RequestBody RegistrationRequest request
    ) {
        UserView newUser = userService.register(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    /**
     * Feature 1: Login
     * Edge Case: @Valid catches empty fields.
     * Service logic catches "User not found" or "Invalid password".
     */
    @PostMapping("/login")
    public ResponseEntity<UserView> login(
        @Valid @RequestBody LoginRequest request,
        HttpSession session
    ) {
        User user = userService.login(request);

        // --- THIS IS THE CORE SESSION LOGIC ---
        // We store the 'User' object directly in the session.
        session.setAttribute("loggedInUser", user);

        UserView userView = userService.mapToUserView(user);
        return ResponseEntity.ok(userView);
    }

    /**
     * Feature: Logout
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // Edge Case: Invalidating a session that doesn't exist is safe.
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully.");
    }

    /**
     * Feature: Get current user ("Who am I?")
     * This is for your Angular frontend to check if a session is active on page load.
     */
    @GetMapping("/me")
    public ResponseEntity<UserView> getCurrentUser(HttpSession session) {
        // We do NOT use BaseController.getSessionUser() because
        // we don't want to throw an error if the user is just not logged in.
        User user = (User) session.getAttribute("loggedInUser");

        // Edge Case: No user is logged in.
        if (user == null) {
            // 204 No Content is the correct HTTP status for "no data to return".
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(userService.mapToUserView(user));
    }
}
