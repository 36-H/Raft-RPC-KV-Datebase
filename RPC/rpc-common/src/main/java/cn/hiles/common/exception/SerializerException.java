package cn.hiles.common.exception;

/**
 * @author Helios
 * Time：2024-03-12 16:58
 */
public class SerializerException extends RuntimeException {
    private static final long serialVersionUID = 1256845134L;

    public SerializerException(final String message) {
        super(message);
    }

    public SerializerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SerializerException(final Throwable cause) {
        super(cause);
    }
}
