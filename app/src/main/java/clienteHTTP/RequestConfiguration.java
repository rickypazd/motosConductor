package clienteHTTP;

public abstract class RequestConfiguration {

    private MethodType type;
    protected String parametros;
    private String requestUrl;
    protected String contentType;

    public RequestConfiguration(String url, MethodType type){
        this.requestUrl = url;
        this.type = type;
        this.contentType = "application/x-www-form-urlencoded";
    }

    public MethodType getType() {
        return type;
    }

    public String getParametros() {
        return parametros;
    }

    public String getRequestUrl() {
        return type == MethodType.POST ? requestUrl :
                (requestUrl.endsWith("?") ? requestUrl : requestUrl + "?") + parametros;
    }

}
