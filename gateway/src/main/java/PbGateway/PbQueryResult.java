package PbGateway;

public class PbQueryResult {

    private long id;
    private String content;

    public PbQueryResult(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
