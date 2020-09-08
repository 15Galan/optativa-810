package chatBluetooth;

import java.io.*;
import java.util.Scanner;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class Servidor {
    private static String mensaje = "";

    public void startServer() throws IOException {
        String url = "btspp://localhost:" + new UUID(0x1101).toString() + ";name=chat";
        StreamConnectionNotifier service = (StreamConnectionNotifier) Connector.open(url);

        LocalDevice ld = LocalDevice.getLocalDevice();
        System.out.println("Datos del servidor: " + ld.getBluetoothAddress() + " - " + ld.getFriendlyName());
        System.out.println("\nServidor Iniciado. Esperando clientes...");

        StreamConnection con = (StreamConnection) service.acceptAndOpen();
        RemoteDevice dev = RemoteDevice.getRemoteDevice(con);
        System.out.println("Dirección del dispositivo remoto: " + dev.getBluetoothAddress());
        System.out.println("Nombre del dispositivo remoto: " + dev.getFriendlyName(true));

        InputStream is = con.openInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        OutputStream os	= con.openOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

        Scanner sc = new Scanner(System.in);
        String response = "";

        while (!mensaje.equals("FIN.")) {
            System.out.println("Esperano respuesta del cliente...");
            mensaje = br.readLine();

            System.out.println(dev.getBluetoothAddress() + " - " + dev.getFriendlyName(true) + ": " + mensaje);
            System.out.print(ld.getFriendlyName() + ": ");
            response = sc.nextLine();

            bw.write(response);
            bw.newLine();
            bw.flush();
        }

        System.out.println("Dispositivo " + dev.getBluetoothAddress() + " - " + dev.getFriendlyName(true) + " desconectado correctamente");

        br.close();
        bw.close();
        sc.close();

        mensaje = "";
        con.close();
    }

    public static void main(String args[]) throws IOException {
        Servidor servidor = new Servidor();
        servidor.startServer();
    }
}