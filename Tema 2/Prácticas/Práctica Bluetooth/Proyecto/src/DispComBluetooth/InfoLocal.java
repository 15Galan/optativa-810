package DispComBluetooth;

import com.intel.bluetooth.BlueCoveLocalDeviceProperties;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.LocalDevice;

public class InfoLocal {

    private String nombre;
    private String direccion;
    private DeviceClass dispositivo;
    private String version;

    public InfoLocal() throws BluetoothStateException {
        LocalDevice ld = LocalDevice.getLocalDevice();

        nombre = ld.getFriendlyName();
        direccion = ld.getBluetoothAddress();
        dispositivo = ld.getDeviceClass();
        version = LocalDevice.getProperty(BlueCoveLocalDeviceProperties.LOCAL_DEVICE_PROPERTY_BLUECOVE_VERSION);
    }

    @Override
    public String toString(){

        return  "Nombre.......: " + nombre + "\n" +
                "Direccion....: " + direccion + "\n" +
                "Dispositivo..: " + dispositivo + "\n" +
                "Versión BC...: " + version + "\n";
    }

    public static void main(String[] args) throws BluetoothStateException {
        InfoLocal info = new InfoLocal();

        System.out.println(info);
    }
}
