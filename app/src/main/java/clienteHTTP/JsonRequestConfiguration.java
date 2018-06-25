package clienteHTTP;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonRequestConfiguration extends RequestConfiguration {


    public JsonRequestConfiguration(String url, MethodType type, JSONObject object) {
        super(url, type);
        this.parametros = object.toString();
        this.contentType = "application/json";
    }

    public JsonRequestConfiguration(String url, MethodType type, JSONArray array) {
        super(url, type);
        this.parametros = array.toString();
        this.contentType = "application/json";
    }

}
