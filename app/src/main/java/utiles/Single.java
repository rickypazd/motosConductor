package utiles;

public class Single {
    private static int tiempo;

    public static int getTiempo(){
        if(tiempo<=0){
            tiempo=0;
        }
        return tiempo;
    }
    public static void settiempo(int t){
        tiempo=t;
    }
}
