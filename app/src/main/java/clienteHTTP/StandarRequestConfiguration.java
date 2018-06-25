package clienteHTTP;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map;

public class StandarRequestConfiguration extends RequestConfiguration {

    public StandarRequestConfiguration(String url, MethodType type, Hashtable<String, String> parameters) {
        super(url, type);

        try {
            this.parametros = getData(parameters);
        } catch (UnsupportedEncodingException ex) {
            parametros = "";
        }
    }


    private String getData(Hashtable<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        if(params != null) {
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }

        return result.toString();
    }

}
