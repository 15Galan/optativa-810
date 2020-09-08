package DescServRemotos;

import DescDispRemotos.DescubrirDispositivos;
import Utiles.Filtro;
import Utiles.ServicioSimple;

import javax.bluetooth.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BuscarServicios {

    private static int SERVICE_NAME_ATTRID = 0x0100;    // Atributo para filtrar los servicios.

    public static List<ServicioSimple> servicios;     // Estas variables se
    private static Filtro filtro;               // usaran en el caso

    public static void main(String[] args) throws InterruptedException, IOException {
        final Object eventoServicios = new Object();            // Objeto usado para sincronizar la busqueda.

        DescubrirDispositivos.main(null);                   // Ejecutar el descubrimiento de dispositivos.

        List encontrados = DescubrirDispositivos.encontrados;   // Lista de dispositivos encontrados.

        // Crear un Array con los identificadores de servicios.
        UUID uuids[] = inicializarUUIDs();                  // UUIDs que tendran todos los servicios encontrados.
        int[] attrIDs = new int[] {SERVICE_NAME_ATTRID};    // Filtrar por atributo en 'ServicesRecord'.

        DiscoveryListener listener = new DiscoveryListener() {

            @Override   // Que hacer cuando se encuentra un dispositivo.
            public void deviceDiscovered(RemoteDevice dispositivo, DeviceClass tipo) {
            }

            @Override   // Que hacer cuando la busqueda de dispositivos finaliza.
            public void inquiryCompleted(int i) {
            }

            @Override   // Que hacer cuando se encuentra un servicio.
            public void servicesDiscovered(int id, ServiceRecord[] serviceRecords) {
                int cont = 0;

                for (ServiceRecord servicio : serviceRecords) {
                    DataElement elemento = servicio.getAttributeValue(SERVICE_NAME_ATTRID);

                    if (elemento != null) {
                        String nombre = elemento.getValue().toString().trim();
                        String URL = servicio.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

                        if (filtro != null) {
                            if (filtro.verificar(nombre, URL)) {
                                System.out.println("\tServicio: " + nombre);
                                System.out.println("\t     URL: " + URL + "\n");

                                servicios.add(new ServicioSimple(servicio.getHostDevice(), nombre, URL));
                            }

                        } else {
                            System.out.println("\tServicio: " + nombre);
                            System.out.println("\t     URL: " + URL + "\n");

                            cont++;
                        }
                    }
                }

                // Mostrar informacion.
                if (filtro != null && servicios.size() == 0) {      // No se encontro el servicio indicado en el filtro.
                    System.err.println("\tServicio no encontrado");

                } else if (filtro == null && cont == 0) {         // El dispositivo no posee servicios con los UUIDs indicados.
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
        filtrarServicio();

        for (Object encontrado : encontrados) {
            RemoteDevice dispositivo = (RemoteDevice) encontrado;   // Dispositivo actual.

            // Datos del dispositivo que se analiza.
            String nombre = dispositivo.getFriendlyName(false);
            String direccion = dispositivo.getBluetoothAddress();
            String servicio = "";
            String URL = "";

            if(filtro != null) {
                servicio = filtro.getNombre();
                URL = filtro.getDireccion();
            }

            synchronized (eventoServicios) {
                if(filtro != null && !servicio.equals("")) {
                    System.out.println("Buscando el servicio '" + servicio + "' en el dispositivo '" + nombre + "' (" + direccion + ")...");

                } else if(filtro != null && !URL.equals("")) {
                    System.out.println("Buscando el servicio '" + URL + "' en el dispositivo '" + nombre + "' (" + direccion + ")...");

                } else {
                    System.out.println("Buscando en el dispositivo '" + nombre + "' (" + direccion + ")...");
                }

                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, uuids, dispositivo, listener);

                // Busqueda bloqueante de servicios en el dispositivo actual.
                eventoServicios.wait();
            }
        }

        if(filtro != null) {
            System.out.println("...BUSQUEDA DEl SERVICIO FINALIZADA.\n");

        }else{
            System.out.println("...BUSQUEDA DE SERVICIOS FINALIZADA.\n");
        }
    }

    /**
     * Inicializa un array con los diferentes UUIDs que debe poseer un servicio
     * para ser detectado en la busqueda de servicios de un dispositivo.
     *
     * @return Un 'Array' de tipo {@code UUID[]} con los identificadores universales indicados.
     * @throws IOException
     */
    private static UUID[] inicializarUUIDs() throws IOException {
        // Creacion del array de
        UUID[] uuids;

        System.out.println("¿Filtrar por servicios de clase SerialPort (SPP)?    (si/no)");
        BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Respuesta: ");
        String texto = consola.readLine();

        if(texto.equalsIgnoreCase("si")) {
            uuids = new UUID[]{new UUID(0x1101)};   // Servicios de clase SerialPort Profile (SPP).

        }else if(texto.equalsIgnoreCase("no")){
            uuids = new UUID[]{new UUID(0x1002)};   // Servicios de clase PublicBrowseRoot (publicos).

        }else{
            System.err.println("Respuesta no reconocida, busqueda de SPPs desactivada por defecto");
            uuids = new UUID[]{new UUID(0x1002)};
        }

        System.out.println(/* Linea en blanco */);

        return uuids;
    }

    /**
     * Ejecuta una peticion de filtrado al usuario con el que se almacenara un servicio
     * en un objeto 'ServicioSimple' para poder manejarlo mas facilmente despues.
     */
    private static void filtrarServicio(){
        BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("¿Filtrar un servicio?    (si/no)");
        System.out.print("Respuesta: ");

        try {
            String texto = consola.readLine();

            if(texto.equalsIgnoreCase("si")){
                System.out.println("\nIntroduce el nombre o URL para encontrar.");
                System.out.print("\tNombre del servicio: ");
                String servicio = consola.readLine();
                System.out.print("\tURL del servicio: ");
                String url = consola.readLine();
                System.out.println("\nBUSCANDO SERVICIO...\n");

                filtro = new Filtro(servicio, url);
                servicios = new ArrayList<>();

            }else if (texto.equalsIgnoreCase("no")){
                System.out.println("\nBUSCANDO SERVICIOS...\n");

            }else{
                System.err.println("Respuesta no reconocida, busqueda por defecto activada.");
                System.out.println("\nBUSCANDO SERVICIOS...\n");
            }

        }catch(IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}
