package RFCOMM;

import DescServRemotos.BuscarServicios;
import DispComBluetooth.InfoLocal;
import Utiles.ServicioSimple;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;

public class Cliente {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Mostrar informacion local.
        System.out.println("INICIANDO CLIENTE...");
        System.out.println(new InfoLocal());
        System.out.println("Filtra el dispositivo y el servicio de chat al que quieres conectarte.\n");

        // Iniciar busqueda de servicios.
        BuscarServicios.main(null);
        ServicioSimple servicio = BuscarServicios.servicioF;

        // Establecer la conexion asociandola al servicio indicado.
        StreamConnection conexion = null;

        try{
            conexion = (StreamConnection) Connector.open(servicio.getURL());

        }catch (NullPointerException e){
            System.err.println("Error en la conexion al servidor:");
            System.out.println("\t'Servicio de conexion incorrecto' o 'servicio no guardado/encontrado'");
        }

        if(conexion != null){
            RemoteDevice rd = RemoteDevice.getRemoteDevice(conexion);   // Dispositivo remoto (servidor) que se conecta.

            String servidor = rd.getFriendlyName(false);    // Nombre del dispositivo (servidor).
            System.out.println("\nConectado a '" + servidor + "' (" + rd.getBluetoothAddress() + ")");   // Datos del servidor.

            // Creacion de los flujos de lectura.
            BufferedReader in = new BufferedReader(new InputStreamReader(conexion.openInputStream()));  // Recibir mensaje.
            BufferedReader texto = new BufferedReader(new InputStreamReader(System.in));                // Leer de consola.

            // Creacion del flujo de salida.
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conexion.openOutputStream()));

            // Mensaje que se va a enviar.
            String mensaje;

            // Blucle para intercambiar mensajes con el servidor.
            do {
                System.out.print("Enviar: ");
                mensaje = texto.readLine();     // Leer mensaje de la consola.

                out.write(mensaje);             // Enviar el mensaje.
                out.newLine();                  // Escribir un salto de linea.
                out.flush();                    // Limpiar el buffer.

                System.out.print(servidor + ": " + in.readLine() + "\n");     // Recibir el mensaje.

            } while (!mensaje.equals("FIN"));

            // Cierre de los flujos y la conexion.
            in.close();         // Flujo (buffer) de entrada.
            out.close();        // Flujo (buffer) de salida.
            conexion.close();   // Conexion del cliente.

            System.out.println("\nConexion con '" + servidor + "' finalizada");
        }
    }
}
