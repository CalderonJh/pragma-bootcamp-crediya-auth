package com.co.crediya.auth.usecase.exception;

public class PermissionException extends RuntimeException {
  public PermissionException(String message) {
    super(message);
  }
}
