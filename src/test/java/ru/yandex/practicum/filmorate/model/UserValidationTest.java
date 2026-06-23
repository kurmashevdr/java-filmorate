package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class UserValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldAcceptCorrectEmail() {
        User user = new User(null, "user@mail.ru", "login", "Name",
                LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectEmailWithoutAtSign() {
        User user = new User(null, "mail.ru", "login", "Name",
                LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString()
                .equals("email"));
    }

    @Test
    void shouldRejectEmptyEmail() {
        User user = new User(null, "", "login", "Name",
                LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString()
                .equals("email"));
    }
}
