package br.com.basis.jenkins.rancher.exception;

public class RancherRuntimeException extends RuntimeException {

    public RancherRuntimeException(String message) {
        super(message);
    }

    public RancherRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
