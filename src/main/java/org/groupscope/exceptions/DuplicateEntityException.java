package org.groupscope.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEntityException extends RuntimeException {

        public DuplicateEntityException() {
            super("Entity already exists");
        }

        public DuplicateEntityException(Class<?> entityClass, Long id) {
            super(String.format("Entity %s with id %d already exists", entityClass.getSimpleName(), id));
        }

        public DuplicateEntityException(Class<?> entityClass, String id) {
            super(String.format("Entity %s with id = '%s' already exists", entityClass.getSimpleName(), id));
        }

        public DuplicateEntityException(String message) {
            super(message);
        }

        public DuplicateEntityException(String message, Throwable cause) {
            super(message, cause);
        }
}
