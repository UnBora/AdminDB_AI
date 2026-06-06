package com.platform.modules.user.validator;

import com.platform.modules.user.dto.request.UserCreateRequest;
import com.platform.modules.user.dto.request.UserUpdateRequest;
import com.platform.modules.user.repository.UserRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._-]{3,100}$"
    );

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 255;

    /**
     * Validate user creation request
     */
    public ValidationResult validateUserCreate(UserCreateRequest request) {
        List<ValidationError> errors = new ArrayList<>();

        if (request == null) {
            errors.add(new ValidationError("general", "User request cannot be null"));
            return new ValidationResult(false, errors);
        }

        validateUsername(request.getUsername(), errors, null);
        validateEmail(request.getEmail(), errors, null);
        validatePassword(request.getPassword(), errors);
        validateFirstName(request.getFirstName(), errors);
        validateLastName(request.getLastName(), errors);

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validate user update request
     */
    public ValidationResult validateUserUpdate(UserUpdateRequest request, UUID userId) {
        List<ValidationError> errors = new ArrayList<>();

        if (request == null) {
            errors.add(new ValidationError("general", "User request cannot be null"));
            return new ValidationResult(false, errors);
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            validateUsername(request.getUsername(), errors, userId);
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            validateEmail(request.getEmail(), errors, userId);
        }

        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            validateFirstName(request.getFirstName(), errors);
        }

        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            validateLastName(request.getLastName(), errors);
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validate username format and uniqueness
     */
    private void validateUsername(String username, List<ValidationError> errors, UUID excludeUserId) {
        if (username == null || username.isEmpty()) {
            errors.add(new ValidationError("username", "Username is required"));
            return;
        }

        if (username.length() < 3 || username.length() > 100) {
            errors.add(new ValidationError("username", "Username must be between 3 and 100 characters"));
            return;
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            errors.add(new ValidationError("username", "Username must contain only letters, numbers, dots, hyphens, and underscores"));
            return;
        }

        if (userRepository.findByUsername(username).isPresent()) {
            errors.add(new ValidationError("username", "Username already exists"));
        }
    }

    /**
     * Validate email format and uniqueness
     */
    private void validateEmail(String email, List<ValidationError> errors, UUID excludeUserId) {
        if (email == null || email.isEmpty()) {
            errors.add(new ValidationError("email", "Email is required"));
            return;
        }

        if (email.length() > 255) {
            errors.add(new ValidationError("email", "Email must not exceed 255 characters"));
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add(new ValidationError("email", "Email format is invalid"));
            return;
        }

        if (userRepository.findByEmail(email).isPresent()) {
            errors.add(new ValidationError("email", "Email already exists"));
        }
    }

    /**
     * Validate password strength
     */
    private void validatePassword(String password, List<ValidationError> errors) {
        if (password == null || password.isEmpty()) {
            errors.add(new ValidationError("password", "Password is required"));
            return;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.add(new ValidationError("password", "Password must be at least " + MIN_PASSWORD_LENGTH + " characters"));
            return;
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            errors.add(new ValidationError("password", "Password must not exceed " + MAX_PASSWORD_LENGTH + " characters"));
            return;
        }

        if (!hasPasswordStrength(password)) {
            errors.add(new ValidationError("password", "Password must contain uppercase, lowercase, numbers, and special characters"));
        }
    }

    /**
     * Check password strength (contains uppercase, lowercase, number, and special character)
     */
    private boolean hasPasswordStrength(String password) {
        return password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*\\d.*") &&
               password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};:'\",.<>?/].*");
    }

    /**
     * Validate first name
     */
    private void validateFirstName(String firstName, List<ValidationError> errors) {
        if (firstName != null && firstName.length() > 100) {
            errors.add(new ValidationError("firstName", "First name must not exceed 100 characters"));
        }
    }

    /**
     * Validate last name
     */
    private void validateLastName(String lastName, List<ValidationError> errors) {
        if (lastName != null && lastName.length() > 100) {
            errors.add(new ValidationError("lastName", "Last name must not exceed 100 characters"));
        }
    }

    /**
     * Validation result holder
     */
    @Data
    @Builder
    public static class ValidationResult {
        private boolean valid;
        private List<ValidationError> errors;

        public boolean hasErrors() {
            return errors != null && !errors.isEmpty();
        }

        public boolean isValid() {
            return valid && !hasErrors();
        }
    }

    /**
     * Validation error holder
     */
    @Data
    @Builder
    public static class ValidationError {
        private String fieldName;
        private String message;

        public ValidationError(String fieldName, String message) {
            this.fieldName = fieldName;
            this.message = message;
        }
    }
}
