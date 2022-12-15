package unsw.blackout;

import java.util.ArrayList;
import java.util.Arrays;

import unsw.utils.Angle;

public class StandardSatellite extends TransferSatellite {

    /**
     * Constructor of StandardSatellite
     * @param satelliteId
     * @param type
     * @param height
     * @param position
     */
    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        this.setDevicesSupported(new ArrayList<String>(
            Arrays.asList("LaptopDevice", "HandheldDevice",
            "RelaySatellite", "StandardSatellite", "TeleportingSatellite", "ElephantSatellite")
        ));
        this.setRange(150000);
        this.setLinearVelocity(2500);
        this.setBandwidthRestrictions(new BandwidthRestrictions(1, 1, 3, 80));
    }

    /**
     * Method to update position of standardSatellite
     */
    @Override
    public void updatePosition() {
        Angle angularDisplacement = Angle.fromRadians(this.calculateAngularVelocity());
        Angle newPosition = this.getPosition().subtract(angularDisplacement);
        if (newPosition.compareTo(new Angle()) == -1) {
            newPosition = newPosition.add(Angle.fromDegrees(360));
        }
        this.setPosition(newPosition);
    }
}
