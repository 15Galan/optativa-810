package RFCOMM;

import java.io.*;
import java.util.Scanner;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class Servidor {

    public static void main(String args[]) throws IOException {
        // URL del servicio del servidor.
        String url = "btspp://localhost:" + new UUID(0x1101).toString() + ";name=chat";

        // Creacion de un servicio cuya URL es la indicada (equivalente a ServerSocket, pero Bluetooth).
        StreamConnectionNotifier servicio = (StreamConnectionNotifier) Connector.open(url);

        // Obtener informacion del dispositivo local.
        LocalDevice ld = LocalDevice.getLocalDevice();
        String servidor = ld.getFriendlyName() + ":" + ld.getBluetoothAddress();    // Datos del servidor.

        // Mostrar informacion.
        System.out.println("\nRFCOMM '" + servidor + "' iniciado.");
        System.out.println("Esperando clientes...");

        // Establecer la conexion asociandola al servicio creado.
        StreamConnection conexion = servicio.acceptAndOpen();       // El servicio espera y acepta peticiones.
        RemoteDevice rd = RemoteDevice.getRemoteDevice(conexion);   // Dispositivo remoto (cliente) que se conecta.

        String cliente = rd.getFriendlyName(true) + ":" + rd.getBluetoothAddress();     // Datos del cliente.

        System.out.println("Nuevo cliente: '" + cliente + "'.");

        // Creacion de los flujos (buffers) de escritura y de lectura.
        BufferedReader in = new BufferedReader(new InputStreamReader(conexion.openInputStream()));      // Lectura.
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conexion.openOutputStream()));   // Escritura.

        // Creacion de las variables para los mensajes.
        Scanner sc = new Scanner(System.in);    // Scanner para la lectura por consola (teclado).
        String respuesta;                       // Mensaje recibido del cliente.
        String mensaje;                         // Mensaje enviado al cliente.

        // Blucle para enviar y recibir mensajes del cliente.
        do {
            System.out.println("Esperando respuesta del cliente...");

            mensaje = in.readLine();        // Leer mensaje del cliente.

            System.out.println(servidor + " -> " + mensaje);    // Informacion que indica a que cliente se responde.
            System.out.print(ld.getFriendlyName() + ": ");      // Respuesta del servidor.

            respuesta = sc.nextLine();                          // Lectura del mensaje por la consola.
            out.write(respuesta);                               // Enviar la respuesta del servidor (buffer de salida).
            out.newLine();
            out.flush();                                        // Limpiar buffer.

        }while (!mensaje.equals("FIN"));   // El servidor se apaga cuando el cliente envia "FIN".

        // Cierre de los flujos y de la conexion.
        in.close();             // Flujo (buffer) de entrada de mensajes.
        out.close();            // Flujo (buffer) de salida de mensajes.
        sc.close();             // Scanner para lectura por consola.
        conexion.close();       // Conexion del servidor.

        System.out.println("Dispositivo '" + cliente + "' desconectado correctamente");
    }
}