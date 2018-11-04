package RFCOMM;

import DescServRemotos.BuscarServicios;
import DispComBluetooth.InfoLocal;
import Utiles.ServicioBasico;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;

public class Cliente {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("INICIANDO CLIENTE...");
        System.out.println(new InfoLocal());
        System.out.println("Filtra el dispositivo y el servicio de chat al que quieres conectarte.\n");

        BuscarServicios.main(null);
        ServicioBasico servicio = BuscarServicios.servicioF;

        // Establecer la conexion asociandola al servicio indicado.
        StreamConnection conexion = (StreamConnection) Connector.open(servicio.getURL());
        RemoteDevice rd = RemoteDevice.getRemoteDevice(conexion);   // Dispositivo remoto (servidor) que se conecta.

        String servidor = "'" + rd.getFriendlyName(false) + "' (" + rd.getBluetoothAddress() +")";   // Datos del servidor.
        System.out.println("Conectado a " + servidor);

        // Creacion de los flujos de lectura.
        BufferedReader in = new BufferedReader(new InputStreamReader(conexion.openInputStream()));  // Recibir mensaje.
        BufferedReader texto = new BufferedReader(new InputStreamReader(System.in));                // Leer de consola.

        // Creacion del flujo de salida.
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conexion.openOutputStream()));
        String mensaje;

        // Blucle para intercambiar mensajes con el servidor.
        do {
            System.out.print("Mensaje para enviar: ");
            mensaje = texto.readLine();     // Leer mensaje de la consola.

            out.write(mensaje);             // Enviar el mensaje.
            out.newLine();                  // Forzar el retorno de carro.
            out.flush();                    // Limpiar el buffer.

            System.out.print("Mensaje recibido: " + in.readLine() + "\n");     // Recibir el mensaje.

        } while (!mensaje.equals("FIN"));

        // Cierre de los flujos y la conexion.
        in.close();         // Flujo (buffer) de entrada.
        out.close();        // Flujo (buffer) de salida.
        conexion.close();   // Conexion del cliente.
    }
}
