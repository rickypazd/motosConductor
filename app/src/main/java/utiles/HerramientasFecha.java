package utiles;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HerramientasFecha {

    public static int obtenerEdad(String fechaStr) {
        DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar fechaActual = Calendar.getInstance();

        try {
            Calendar nacimiento = Calendar.getInstance();
            nacimiento.setTime(formatoFecha.parse(fechaStr));

            int dia = fechaActual.get(Calendar.DAY_OF_MONTH) - nacimiento.get(Calendar.DAY_OF_MONTH);
            int mes = fechaActual.get(Calendar.MONTH) - nacimiento.get(Calendar.MONTH);
            int anio = fechaActual.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);

            if (mes < 0 || (mes == 0 && dia < 0)) {
                anio--;
            }

            return anio;
        } catch (ParseException ex) {
            return 0;
        }
    }

}
