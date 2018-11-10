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
        version = LocalDevice.getProperty(BlueCoveLocalDeviceProperties.LOCAL_DEVICE_RADIO_VERSION);
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public DeviceClass getDispositivo() {
        return dispositivo;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString(){

        return  "Nombre     : " + nombre + "\n" +
                "Direccion  : " + direccion + "\n" +
                "Tipo       : " + dispositivo + "\n" +
                "Ver. Radio : " + version;
    }

    public static void main(String[] args) throws BluetoothStateException {
        System.out.println(new InfoLocal());
    }
}
