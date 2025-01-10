package manager.exception;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(final String message) {
        super(message);
    }

    public ManagerSaveException(final String message, Exception e) {
        super(message, e);
    }

    public ManagerSaveException(Exception e) {
        super(e);
    }
}
