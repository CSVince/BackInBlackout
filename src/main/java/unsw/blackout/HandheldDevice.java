package unsw.blackout;

import java.util.ArrayList;
import java.util.Arrays;

import unsw.utils.Angle;

public class HandheldDevice extends Device {

    /**
     * Constructor for HandheldDevice
     * @param id
     * @param type
     * @param position
     */
    public HandheldDevice(String id, String type, Angle position) {
        super(id, type, position);
        this.setRange(50000);
        this.setDevicesSupported(new ArrayList<String>(
            Arrays.asList("RelaySatellite", "StandardSatellite", "TeleportingSatellite")
        ));
    }
}
