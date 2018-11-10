package Utiles;

import javax.bluetooth.RemoteDevice;
import java.io.IOException;

public class ServicioSimple {

    private int ID;
    private RemoteDevice dispositivo;   // Dispositivo que ofrece el servicio.
    private String servicio;            // Nombre del servicio registrado.
    private String URL;                 // URL del servicio registrado.

    public ServicioSimple(String servicio, String URL){
        new ServicioSimple(null, servicio, URL);
    }

    public ServicioSimple(RemoteDevice dispositivo, String servicio, String URL){
        this.dispositivo = dispositivo;
        this.servicio = servicio;
        this.URL = URL;
    }

    public int getID() {
        return ID;
    }

    public RemoteDevice getDispositivo() {
        try {
            return dispositivo;

        }catch(NullPointerException e){
            System.err.println("Dispositivo no registrado");
            return null;
        }
    }

    public String getNombreDispositivo() {
        try {
            return dispositivo.getFriendlyName(false);

        } catch(IOException e){
            System.err.println("Fallo en el nombre, guardado como < desconocido >");
            return "< desconocido >";
        }
    }

    public String getDireccionDispositivo() {
        return dispositivo.getBluetoothAddress();
    }

    public String getServicio() {
        return servicio;
    }

    public String getURL() {
        return URL;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setDispositivo(RemoteDevice dispositivo) {
        this.dispositivo = dispositivo;
    }

    @Override
    public String toString(){

        return "Servicio [" + getID() + "]" + "\n" +
                "\tNombre: " + getServicio() + "\n" +
                "\t   URL: " + getURL() + "\n" +
                "\tDispositivo\n" +
                "\t  Direccion: " + getDireccionDispositivo() + "\n" +
                "\t     Nombre: " + getNombreDispositivo() + "\n";
    }
}