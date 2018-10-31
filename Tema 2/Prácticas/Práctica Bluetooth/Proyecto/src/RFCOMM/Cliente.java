package RFCOMM;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;

public class Cliente {

    public static void main(String[] args) throws IOException, InterruptedException {
        // URL del servicio del servidor al que conectarse.
        // String url = "btspp://localhost:" + new UUID(0x1101).toString() + ";name=chat";
        // String url = "btspp://C83DD471B366:3;authenticate=false;encrypt=false;master=false";     // PABLO
        String url = "btspp://0B1000000001:1;authenticate=false;encrypt=false;master=false";        // PROFESOR

        // Establecer la conexion asociandola al servicio indicado.
        StreamConnection conexion = (StreamConnection) Connector.open(url);
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
