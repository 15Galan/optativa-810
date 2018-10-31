package DescServRemotos;

import DescDispRemotos.DescubrirDispositivos;
import javax.bluetooth.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class BuscarServicios {

    private static int SERVICE_NAME_ATTRID = 0x0100;    // Atributo para filtrar

    private static boolean filtro;      // Estas variables se usaran
    private static String servicio;     // en caso en el que el usuario
    private static String url;          // decida buscar un solo servicio.

    public static void main(String[] args) throws InterruptedException, IOException {
        final Object eventoServicios = new Object();            // Objeto usado para sincronizar la busqueda.

        DescubrirDispositivos.main(null);                   // Ejecutar el descubrimiento de dispositivos.

        List encontrados = DescubrirDispositivos.encontrados;   // Lista de dispositivos encontrados.

        // Crear un Array con los identificadores de servicios.
        UUID uuids[] = {new UUID(0x1002)};              // Encontrar todos los servicios (1002).
        int[] attrIDs = new int[] {SERVICE_NAME_ATTRID};          // Filtrar por atributo en ServicesRecord.

        DiscoveryListener listener = new DiscoveryListener() {

            @Override   // Que hacer cuando se encuentra un dispositivo.
            public void deviceDiscovered(RemoteDevice dispositivo, DeviceClass tipo) {
            }

            @Override   // Que hacer cuando la búsqueda de dispositivos finaliza.
            public void inquiryCompleted(int i) {
            }

            @Override   // Que hacer cuando se encuentra un servicio.
            public void servicesDiscovered(int id, ServiceRecord[] serviceRecords) {
                int servicios = 0;

                for (int i = 0; i < serviceRecords.length; i++) {
                    DataElement elemento = serviceRecords[i].getAttributeValue(SERVICE_NAME_ATTRID);

                    if (elemento != null) {

                        String elementoNombre = elemento.getValue().toString();
                        String elementoURL = serviceRecords[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

                        if (filtro) {
                            if ((servicio.equalsIgnoreCase(elementoNombre) || url.equalsIgnoreCase(elementoURL))) {
                                System.out.println("\tServicio[" + (i+1) + "]: " + elementoNombre);
                                System.out.println("\tURL: " + elementoURL);

                                servicios++;
                            }

                        } else {
                            System.out.println("\tServicio[" + (i + 1) + "]: " + elementoNombre);
                            System.out.println("\tURL: " + elementoURL);

                            System.out.println(/* Linea en blanco */);

                            servicios++;
                        }
                    }
                }

                // Mostrar mensaje de error en caso de no encontrar el/los servicio/s.
                if (filtro && servicios == 0) {
                    System.err.println("\tServicio no encontrado");

                } else if (servicios == 0) {
                    System.err.println("\nNo se encontraron servicios\n");
                }
            }

            @Override   // Que hacer cuando la busqueda de servicios finaliza.
            public void serviceSearchCompleted(int i, int i1) {
                // System.out.println("Busqueda de servicios finalizada." + "\n");
                System.out.println(/* Linea en blanco */);

                synchronized (eventoServicios){
                    eventoServicios.notifyAll();
                }
            }
        };

        // Busqueda de servicios.
        filtro = filtrarServicio();

        for (Object encontrado : encontrados) {
            RemoteDevice dispositivo = (RemoteDevice) encontrado;   // Dispositivo actual.

            // Datos del dispositivo que se analiza.
            String nombre = dispositivo.getFriendlyName(false);
            String direccion = dispositivo.getBluetoothAddress();

            synchronized (eventoServicios) {
                if(filtro && servicio != null) {
                    System.out.println("Buscando el servicio '" + servicio + "' en el dispositivo '" + nombre + "' (" + direccion + ")...");

                } else if(filtro && url != null) {
                    System.out.println("Buscando el servicio '" + url + "' en el dispositivo '" + nombre + "' (" + direccion + ")...");

                } else {
                    System.out.println("Buscando en el dispositivo '" + nombre + "' (" + direccion + ")...");
                }

                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, uuids, dispositivo, listener);

                eventoServicios.wait();
            }
        }

        if(filtro) {
            System.out.println("...BUSQUEDA DEl SERVICIO FINALIZADA.");

        }else{
            System.out.println("...BUSQUEDA DE SERVICIOS FINALIZADA.");
        }
    }

    private static boolean filtrarServicio(){
        boolean filtro = false;

        BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
        String texto;

        System.out.println("¿Filtrar un servicio?    (si/no)");
        System.out.print("Respuesta: ");

        try {
            texto = consola.readLine();

            if(texto.equalsIgnoreCase("si")){
                System.out.println("\nIntroduce el nombre o URL para encontrar.");

                System.out.print("  Nombre del servicio: ");
                servicio = consola.readLine();

                System.out.print("  URL del servicio: ");
                url = consola.readLine();

                System.out.println("\nBUSCANDO SERVICIO...\n");

                filtro = true;

            }else if (texto.equalsIgnoreCase("no")){
                System.out.println("\nBUSCANDO SERVICIOS...\n");

            }else{
                System.err.println("Respuesta no reconocida, busqueda por defecto activada.");
                System.out.println("\nBUSCANDO SERVICIOS...\n");
            }

        }catch(IOException e){
            System.err.println("Error: " + e.getMessage());
        }

        return filtro;
    }
}
