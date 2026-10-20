package com.example.shop.web;

import io.github.ilyaslabs.moduleswitch.ModuleException;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Turns a module error into an HTTP response for the public API. Works the same whether the
 * module that threw it is local or remote.
 */
@RestControllerAdvice
class ModuleExceptionHandler {

    @ExceptionHandler
    ProblemDetail handle(ModuleException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(ex.status()), ex.getMessage());
        problem.setTitle(ex.getClass().getSimpleName());
        if (ex.code() != null) {
            problem.setProperty("code", ex.code());
        }
        if (!ex.details().isEmpty()) {
            problem.setProperty("details", ex.details());
        }
        return problem;
    }
}
