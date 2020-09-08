package RFCOMM;

import DescServRemotos.BuscarServicios;
import DispComBluetooth.InfoLocal;
import Utiles.ServicioSimple;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.util.Iterator;
import java.util.List;

public class Cliente {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Mostrar informacion local.
        System.out.println("INICIANDO CLIENTE...");
        System.out.println(new InfoLocal());
        System.out.println("\nFiltra el dispositivo y el servicio de chat al que quieres conectarte.\n");

        // Iniciar la busqueda de servicios.
        BuscarServicios.main(null);
        ServicioSimple servicio = seleccionar(BuscarServicios.servicios);

        // Establecer la conexion asociandola al servicio indicado.
        StreamConnection conexion = null;

        try{
            conexion = (StreamConnection) Connector.open(servicio.getURL());

        }catch (NullPointerException e){
            System.err.println("Error en la conexion al servidor");
        }

        if(conexion != null){
            RemoteDevice rd = RemoteDevice.getRemoteDevice(conexion);   // Dispositivo remoto (servidor) que se conecta.

            // Informacion del servidor.
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

    protected static ServicioSimple seleccionar(List<ServicioSimple> lista) throws IOException {
        ServicioSimple seleccionado = null;

        try{
            if (lista.size() == 0) {
                System.err.println("No se ha almacenado ningun servicio");

            } else if (lista.size() == 1) {
                seleccionado = lista.iterator().next();
                seleccionado.setID(1);

                System.out.println("Se ha encontrado un solo servicio en la lista:");
                System.out.println("\n" + seleccionado + "\nConectando...\n");

            } else {
                // Mostrar servicios filtrados.
                System.out.println();
                int i = 0;
                for (ServicioSimple servicio : lista) {
                    servicio.setID(++i);
                    System.out.println(servicio + "\n");
                }

                // Seleccionar el servicio.
                if (lista.size() > 0) {
                    // Peticion al usuario.
                    BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("¿A que servicio quieres conectarte?    (ID) = [1," + lista.size() + "]");
                    int respuesta;

                    boolean seguir;      // Variable de control (primer uso).

                    do {
                        System.out.print("Respuesta: ");
                        respuesta = Integer.parseInt(consola.readLine());

                        seguir = !(0 < respuesta && respuesta <= lista.size());

                        if (seguir) {
                            System.err.println("Respuesta no valida, debe ser un respuesta entre 0 y " + lista.size());
                        }

                    } while (seguir);

                    // Variables de control.
                    Iterator it = lista.iterator();
                    seguir = true;

                    while (seguir && it.hasNext()) {
                        seleccionado = (ServicioSimple) it.next();

                        if (seleccionado.getID() == respuesta) {
                            seguir = false;
                        }
                    }

                    if (seguir) {
                        System.err.println("Algo ha fallado en la seleccion del servicio");
                        seleccionado = null;
                    }
                }
            }

        }catch (NullPointerException e){
            System.err.println("No se activo el filtro, la lista de servicios esta vacia");
        }

        return seleccionado;
    }
}
