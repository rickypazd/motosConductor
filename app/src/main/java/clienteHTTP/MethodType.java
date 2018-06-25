package clienteHTTP;

public enum MethodType {

    GET("GET"),
    POST("POST");

    private final String texto;

    MethodType(final String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }

}
