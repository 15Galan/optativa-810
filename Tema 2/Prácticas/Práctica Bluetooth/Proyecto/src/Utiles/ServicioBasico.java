package Utiles;

public class ServicioBasico {

    private String nombre;      // Nombre del servicio registrado.
    private String URL;         // URL del servicio registrado.

    public ServicioBasico(String nombre, String URL){
        this.nombre = nombre;
        this.URL = URL;
    }

    public String getNombre() {
        return nombre;
    }

    public String getURL() {
        return URL;
    }
}
