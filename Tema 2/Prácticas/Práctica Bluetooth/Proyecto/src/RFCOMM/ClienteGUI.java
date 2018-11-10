package RFCOMM;

import java.awt.event.WindowEvent;
import java.io.*;

import DescServRemotos.BuscarServicios;
import DispComBluetooth.InfoLocal;
import Utiles.ServicioSimple;
import chatUI.ChatWindow;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import static RFCOMM.Cliente.seleccionar;

public class ClienteGUI {

    public static void main(String args[]) throws IOException, InterruptedException {
        // Mostrar informacion local.
        System.out.println("INICIANDO CLIENTE...");
        InfoLocal local = new InfoLocal();
        System.out.println(local);
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

        if(conexion != null) {
            RemoteDevice rd = RemoteDevice.getRemoteDevice(conexion);   // Dispositivo remoto (servidor) que se conecta.

            // Informacion del servidor.
            String servidor = rd.getFriendlyName(false);
            System.out.println("Conectado a '" + servidor + "' (" + rd.getBluetoothAddress() + ")");

            // Creacion de los flujos de lectura.
            BufferedReader in = new BufferedReader(new InputStreamReader(conexion.openInputStream()));  // Recibir el mensaje.

            // Creacion del flujo de salida.
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conexion.openOutputStream()));   // Enviar el mensaje.

            // Creacion de la ventana.
            final chatUI.ChatWindow _window;
            _window = new ChatWindow();
            _window.setVisible(true);

            // Registro de un ActionListener para sincronizar la ventana con las acciones del usuario.
            _window.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    // Envio del mensaje.
                    String mensaje = _window.getIn();   // Lectura de la entrada por la ventana.

                    try {
                        out.write(mensaje);     // Enviar el mensaje.
                        out.newLine();          // Escribir un salto de linea.
                        out.flush();            // Limpiar el buffer.

                    } catch (IOException e) {
                        System.err.println("Error en el envio del mensaje '" + e.getMessage() + "'");
                    }

                    _window.setOut(local.getNombre() + ":  " + mensaje);    // Escritura en la salida de la ventana.
                    System.out.print(local.getNombre() + ": " + mensaje + "\t");
                }
            });

            // Registro de un WindowListener para los eventos de ventana y cierre de conexiones.
            StreamConnection finalConexion = conexion;
            _window.addWindowListener(new java.awt.event.WindowListener() {
                public void windowOpened(WindowEvent e) {           // La ventana se abre (primera aparicion).
                    System.out.println("Ventana: ABIERTA");
                }

                public void windowClosing(WindowEvent e) {          // La ventana se cierra (empieza).
                    System.out.println("Ventana: cerrando... Finalizando conexiones");

                    try {
                        // Cierre de los flujos y la conexion.
                        in.close();         // Flujo (buffer) de entrada.
                        out.close();        // Flujo (buffer) de salida.
                        finalConexion.close();   // Conexion del cliente.

                    } catch (IOException o) {
                        System.err.println("Error sobre el cierre de conexiones '" + o.getMessage() + "'");
                    }
                }

                public void windowClosed(WindowEvent e) {           // La ventana se cierra (termina).
                    System.out.println("Ventana: CERRADA");
                }

                public void windowActivated(WindowEvent e) {        // La ventana pasa a primer plano (lista para usarse).
                    System.out.println("Ventana: activada");
                }

                public void windowDeactivated(WindowEvent e) {      // La ventana pasa a segundo plano.
                    System.out.println("Ventana: desactivada");
                }

                public void windowIconified(WindowEvent e) {        // La ventana se minimiza (pasa a ser un icono).
                    System.out.println("Ventana: minimizada");
                }

                public void windowDeiconified(WindowEvent e) {      // La ventana se abre (estando minimizada).
                    System.out.println("Ventana: normalizada");
                }
            });

            while (true) {
                // Recibir la informacion del servidor infinitamente.
                String respuesta = in.readLine();   // Mensaje recibido.
                _window.setOut(servidor + ":  " + respuesta);          // Escribir la respuesta en la ventana.
                System.out.print(servidor + ": " + respuesta + "\n");  // Informacion por la consola.
            }
        }
    }
}