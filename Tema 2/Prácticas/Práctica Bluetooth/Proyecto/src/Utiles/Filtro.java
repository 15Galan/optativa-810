package Utiles;

public class Filtro {

    private String nombre;      // Nombre del dispositivo BT o del servicio.
    private String direccion;   // Direccion del dispositivo BT o del servicio (URL).

    public Filtro(String nombre, String direccion){
        this.nombre = nombre;
        this.direccion = direccion;
    }

    /**
     * Comprueba si un elemento debe ser filtrado en funcion de la cantidad
     * de argumentos que se introdujeron en el filtro (nombre, direccion o ambos).
     *
     * @param nombre Nombre del dispositivo Bluetooth/servicio para filtrar.
     * @param direccion Direccion Bluetooth/servicio (URL) para filtrar.
     * @return 'true' si la(s) condicion(es) se cumple(n); 'false' en caso contrario.
     */
    public boolean verificar(String nombre, String direccion){
        boolean filtrar = false;    // Resultado final de la comprobacion.

        // Casos posibles.
        if(!this.nombre.equals("") && !this.direccion.equals("")) {      // Se introducen ambos datos, debe filtrarse si coinciden ambos.
            filtrar = this.nombre.equalsIgnoreCase(nombre) && this.direccion.equalsIgnoreCase(direccion);

        }else if(!this.nombre.equals("")){              // Solo se introduce el nombre del dispositivo BT / servicio.
            filtrar = this.nombre.equalsIgnoreCase(nombre);

        }else if(!this.direccion.equals("")){           // Solo se introduce la direccion del dispositivo BT / URL de servicio.
            filtrar = this.direccion.equalsIgnoreCase(direccion);
        }

        return filtrar;
    }
}
