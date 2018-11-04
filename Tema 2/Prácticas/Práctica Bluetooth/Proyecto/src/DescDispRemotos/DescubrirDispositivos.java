package DescDispRemotos;

import Utiles.Filtro;

import javax.bluetooth.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DescubrirDispositivos {

    public static final List<RemoteDevice> encontrados = new ArrayList<>(); // Lista para almacenar los dispositivos.

    private static Filtro filtro;       // Objeto que manejara las comprobaciones para filtrar un dispositivo.

    public static void main(String[] args) throws BluetoothStateException, InterruptedException {
        final Object eventoDispositivos = new Object();         // Objeto usado para sincronizar la busqueda.

        DiscoveryListener listener = new DiscoveryListener() {  // Objeto para poder descubrir los dispositivos.

            @Override   // Que hacer cuando se encuentra un dispositivo.
            public void deviceDiscovered(RemoteDevice dispositivo, DeviceClass tipo) {
                String dirDispositivo = null;           // Variables locales para
                String nomDispositivo = null;           // usarlas en el metodo
                DeviceClass tipDispositivo = null;      // siendo mas eficiente.

                try {
                    dirDispositivo = dispositivo.getBluetoothAddress();
                    nomDispositivo = dispositivo.getFriendlyName(false);
                    tipDispositivo = tipo;

                } catch (IOException e) {
                    System.err.println("\tNo puede obtenerse el nombre de '" + dispositivo + "'");
                }

                if (filtro != null) {
                    if (filtro.verificar(nomDispositivo, dirDispositivo)) {
                        System.out.println("Dispositivo: " + dirDispositivo);
                        System.out.println("\t Nombre: " + nomDispositivo);
                        System.out.println("\t   Tipo: " + tipDispositivo);

                        encontrados.add(dispositivo);       // Añadirlo a la lista de dispositivos encontrados.

                        System.out.println(/* Linea en blanco */);
                    }

                } else {
                    System.out.println("Dispositivo: " + dirDispositivo);
                    System.out.println("\t Nombre: " + nomDispositivo);
                    System.out.println("\t   Tipo: " + tipDispositivo);

                    encontrados.add(dispositivo);       // Añadirlo a la lista de dispositivos encontrados.

                    System.out.println(/* Linea en blanco */);
                }
            }

            @Override   // Que hacer cuando la busqueda de dispositivos finaliza.
            public void inquiryCompleted(int i) {
                if(encontrados.size() == 0){
                    System.err.println("Dispositivo no encontrado.\n\n");
                }

                System.out.println("...BUSQUEDA DE DISPOSITIVOS FINALIZADA: ");
                System.out.println("Se ha(n) ecnontrado " + encontrados.size() + " dispositivo(s)\n\n");

                synchronized (eventoDispositivos){   // Sincronizar el objeto de evento.
                    eventoDispositivos.notifyAll();  // Notificar a todas las hebras.
                }
            }

            @Override
            public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {
            }

            @Override
            public void serviceSearchCompleted(int i, int i1) {
            }
        };

        // Sincronizar el objeto de evento.
        synchronized (eventoDispositivos){
            DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();

            filtrarDispositivo();       // Activar el filtro para buscar un solo dispositivo.

            discoveryAgent.startInquiry(DiscoveryAgent.GIAC, listener);     // Empezar la busqueda de dispositivos.

            eventoDispositivos.wait();      // Esperar hasta que se encuentren los dispositivos.
        }
    }

    private static void filtrarDispositivo(){
        BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("¿Filtrar un dispositivo?    (si/no)");
        System.out.print("Respuesta: ");

        try {
            String texto = consola.readLine();

            if(texto.equalsIgnoreCase("si")){
                System.out.println("\nIntroduce un nombre y/o direccion Bluetooth para filtrar.");
                System.out.print("\tNombre del dispositivo: ");
                String nombre = consola.readLine();
                System.out.print("\tDireccion Bluetooth: ");
                String direccion = consola.readLine();
                System.out.println("\nBUSCANDO DISPOSITIVO...\n");

                filtro = new Filtro(nombre, direccion);             // Filtro para el nombre y/o la direccion Bluetooth.

            }else if (texto.equalsIgnoreCase("no")){
                System.out.println("\nBUSCANDO DISPOSITIVOS...\n");

            }else{
                System.err.println("Respuesta no reconocida, busqueda por defecto activada.");
                System.out.println("\nBUSCANDO DISPOSITIVOS...\n");
            }

        }catch(IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}
