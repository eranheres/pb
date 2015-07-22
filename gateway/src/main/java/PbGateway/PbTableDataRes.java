package PbGateway;

public class PbTableDataRes {

    private final long id;
    private final String status;
    private final String message;

    public PbTableDataRes(long id, String status, String message) {
        this.id = id;
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
