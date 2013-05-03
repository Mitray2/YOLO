package modelDTO;

/**
 * Simple response to respond to client
 */
public class SimpleResp {
    private int status;
    private String message;
    private Long id;

    public SimpleResp(int status) {
        this.status = status;
    }

    public SimpleResp(int status, Long id) {
        this.status = status;
        this.id = id;
    }

    public SimpleResp(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public SimpleResp(int status, String message, Long id) {
        this.status = status;
        this.message = message;
        this.id = id;
    }



    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Long getId() {
        return id;
    }
}
