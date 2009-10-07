package bubu.pathfinder.exception;

public class CannotFindPathException extends Exception {

    public CannotFindPathException(Throwable cause) {
        super(cause);
    }

    public CannotFindPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotFindPathException(String message) {
        super(message);
    }

    public CannotFindPathException() {
    }

}
