package unsw.blackout;

import java.util.ArrayList;
import java.util.Arrays;

import unsw.utils.Angle;

public class DesktopDevice extends Device {

    /**
     * Constructor for DesktopDevice
     * @param id
     * @param type
     * @param position
     */
    public DesktopDevice(String id, String type, Angle position) {
        super(id, type, position);
        this.setRange(200000);
        this.setDevicesSupported(new ArrayList<String>(
            Arrays.asList("RelaySatellite", "ElephantSatellite", "TeleportingSatellite")
        ));
    }
}
