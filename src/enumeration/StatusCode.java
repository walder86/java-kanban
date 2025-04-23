package enumeration;

public enum StatusCode {
    CODE_200(200),
    CODE_201(201),
    CODE_400(400),
    CODE_404(404),
    CODE_406(406),
    CODE_500(500),;
    private int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
