import java.awt.event.WindowEvent;;
import chatUI.ChatWindow;

public class EjemploChatWindow {

    public static void main(String args[]) {

    	/* En este punto deberíamos buscar el servicio (en caso de que estemos en el cliente)
    	 * y conectarnos a la URL o activar la escucha de peticiones (en el caso del servidor).

         * A continuación, obtenemos el inputStream y outputStream y los usamos desde la ventana.

         * Invocamos la ventana (que se ejecuta como un thread en segundo plano)
         * y definimos la acción de enviar lo que insertemos por teclado.
         */

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                final chatUI.ChatWindow _window;
                _window = new ChatWindow();
                _window.setVisible(true);
                _window.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {

                        // MODIFICAR EL CÓDIGO PARA EL ENVÍO AQUÍ.
                        String s = _window.getIn();     // Metodo que lee de la entrada de la ventana.
                        _window.setOut(s);              // Metodo que escribe en la salida de la ventana.
                    }
                });

                // Líneas obligatorias: hay que registrar un listener para los eventos de
                // ventana y sobre el de 'windowClosing', realizar el cierre de conexiones.
                _window.addWindowListener(new java.awt.event.WindowListener() {
                    public void windowClosing(WindowEvent e) {
                        System.out.println("Ventana: cerrando... Finalizando conexiones");
                    }

                    public void windowClosed(WindowEvent e) {
                        System.out.println("Ventana: CERRADA");
                    }

                    public void windowDeactivated(WindowEvent e) {
                        System.out.println("Ventana: desactivada");
                    }

                    public void windowOpened(WindowEvent e) {
                        System.out.println("Ventana: ABIERTA");
                    }

                    public void windowIconified(WindowEvent e) {
                        System.out.println("Ventana: minimizada");
                    }

                    public void windowDeiconified(WindowEvent e) {
                        System.out.println("Ventana: normalizada");
                    }

                    public void windowActivated(WindowEvent e) {
                        System.out.println("Ventana: activada");
                    }
                });
            }
        });

        while (true) {
             // En este punto, una vez iniciada la ventana, nos ponemos
             // en bucle a recibir la información del otro extremo.

             // MODIFICAR EL CÓDIGO PARA LA RECEPCIÓN AQUÍ.
        }
    }
}