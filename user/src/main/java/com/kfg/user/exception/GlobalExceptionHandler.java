package com.kfg.user.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final URI USER_NOT_FOUND =
            URI.create("urn:kfg:problem:user-not-found");
    private static final URI COUNTRY_NOT_ENABLED =
            URI.create("urn:kfg:problem:country-not-enabled");
    private static final URI ONBOARDING_APPLICATION_NOT_FOUND =
            URI.create("urn:kfg:problem:onboarding-application-not-found");
    private static final URI INVALID_ONBOARDING_STATE =
            URI.create("urn:kfg:problem:invalid-onboarding-state");
    private static final URI DUPLICATE_RESOURCE =
            URI.create("urn:kfg:problem:duplicate-resource");
    private static final URI UNSUPPORTED_KYC_TYPE =
            URI.create("urn:kfg:problem:unsupported-kyc-type");
    private static final URI VALIDATION_FAILED =
            URI.create("urn:kfg:problem:validation-failed");
    private static final URI MALFORMED_REQUEST =
            URI.create("urn:kfg:problem:malformed-request");
    private static final URI INTERNAL_SERVER_ERROR =
            URI.create("urn:kfg:problem:internal-server-error");

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(
            UserNotFoundException exception,
            WebRequest request
    ) {
        return createProblemDetail(
                HttpStatus.NOT_FOUND,
                USER_NOT_FOUND,
                "User Not Found",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(CountryNotEnabledException.class)
    public ProblemDetail handleCountryNotEnabled(
            CountryNotEnabledException exception,
            WebRequest request
    ) {
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                COUNTRY_NOT_ENABLED,
                "Country Not Enabled",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(OnboardingApplicationNotFoundException.class)
    public ProblemDetail handleOnboardingApplicationNotFound(
            OnboardingApplicationNotFoundException exception,
            WebRequest request
    ) {
        return createProblemDetail(
                HttpStatus.NOT_FOUND,
                ONBOARDING_APPLICATION_NOT_FOUND,
                "Onboarding Application Not Found",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidOnboardingStateException.class)
    public ProblemDetail handleInvalidOnboardingState(
            InvalidOnboardingStateException exception,
            WebRequest request
    ) {
        return createProblemDetail(
                HttpStatus.CONFLICT,
                INVALID_ONBOARDING_STATE,
                "Invalid Onboarding State",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(
            DuplicateResourceException exception,
            WebRequest request
    ) {
        return createProblemDetail(
                HttpStatus.CONFLICT,
                DUPLICATE_RESOURCE,
                "Resource Conflict",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(UnsupportedKycTypeException.class)
    public ProblemDetail handleUnsupportedKycType(
            UnsupportedKycTypeException exception,
            WebRequest request
    ) {
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                UNSUPPORTED_KYC_TYPE,
                "Unsupported KYC Type",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException exception,
            WebRequest request
    ) {
        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                VALIDATION_FAILED,
                "Validation Failed",
                "One or more request values are invalid.",
                request
        );

        List<ValidationError> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            WebRequest request
    ) {
        log.warn("Database constraint violation", exception);

        return createProblemDetail(
                HttpStatus.CONFLICT,
                DUPLICATE_RESOURCE,
                "Resource Conflict",
                "A resource with the supplied unique information already exists.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(
            Exception exception,
            WebRequest request
    ) {
        log.error("Unhandled exception while processing request", exception);

        return createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred while processing the request.",
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                VALIDATION_FAILED,
                "Validation Failed",
                "One or more request fields are invalid.",
                request
        );

        List<ValidationError> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toValidationError)
                .toList();

        problemDetail.setProperty("errors", errors);

        return handleExceptionInternal(
                exception,
                problemDetail,
                headers,
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException exception,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                MALFORMED_REQUEST,
                "Malformed Request",
                "The request body is missing, malformed, or contains an unsupported value.",
                request
        );

        return handleExceptionInternal(
                exception,
                problemDetail,
                headers,
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            @NonNull TypeMismatchException exception,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                MALFORMED_REQUEST,
                "Invalid Request Value",
                "A request value has an invalid format.",
                request
        );

        return handleExceptionInternal(
                exception,
                problemDetail,
                headers,
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    private ProblemDetail createProblemDetail(
            HttpStatus status,
            URI type,
            String title,
            String detail,
            WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                detail
        );
        problemDetail.setType(type);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", Instant.now());

        if (request instanceof ServletWebRequest servletWebRequest) {
            problemDetail.setInstance(
                    URI.create(servletWebRequest.getRequest().getRequestURI())
            );
        }

        return problemDetail;
    }

    private ValidationError toValidationError(FieldError fieldError) {
        String message = fieldError.getDefaultMessage() != null
                ? fieldError.getDefaultMessage()
                : "Invalid value";

        return new ValidationError(fieldError.getField(), message);
    }

    private record ValidationError(String field, String message) {
    }

}
