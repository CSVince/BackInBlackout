package unsw.blackout;

import java.util.ArrayList;
import java.util.Arrays;

import unsw.utils.Angle;

public class LaptopDevice extends Device {

    /**
     * Constructor for LaptopDevice
     * @param id
     * @param type
     * @param position
     */
    public LaptopDevice(String id, String type, Angle position) {
        super(id, type, position);
        this.setRange(100000);
        this.setDevicesSupported(new ArrayList<String>(
            Arrays.asList("RelaySatellite", "StandardSatellite", "TeleportingSatellite", "ElephantSatellite")
        ));
    }
}
