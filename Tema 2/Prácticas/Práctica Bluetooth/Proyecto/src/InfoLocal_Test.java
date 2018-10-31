import javax.bluetooth.BluetoothStateException;

import DescDispRemotos.DescubrirDispositivos;
import DispComBluetooth.InfoLocal;

public class InfoLocal_Test {

    public static void main(String[] args) {

        try {
            InfoLocal i = new InfoLocal();
            DescubrirDispositivos rd = new DescubrirDispositivos();

            System.out.println(i);
            System.out.println(rd.toString());

        } catch (BluetoothStateException e) {
            e.printStackTrace();
        }
    }
}
