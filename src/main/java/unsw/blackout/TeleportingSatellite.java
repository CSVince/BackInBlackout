package unsw.blackout;

import java.util.ArrayList;
// import java.util.Iterator;
import java.util.Arrays;

import unsw.utils.Angle;

import static unsw.utils.MathsHelper.CLOCKWISE;
import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;

public class TeleportingSatellite extends TransferSatellite {

    private boolean teleported;

    /**
     * Constructor for TeleportingSatellite
     * @param satelliteId
     * @param type
     * @param height
     * @param position
     */
    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        this.setDevicesSupported(new ArrayList<String>(
            Arrays.asList("DesktopDevice", "LaptopDevice", "HandheldDevice",
            "RelaySatellite", "StandardSatellite", "TeleportingSatellite")
        ));
        this.setRange(200000);
        this.setLinearVelocity(1000);
        this.setBandwidthRestrictions(new BandwidthRestrictions(10, 15, 200, 200));
        this.teleported = false;
        this.setDirection(ANTI_CLOCKWISE);
    }

    /**
     * Getter for teleported boolean
     * @return boolean
     */
    public boolean getTeleported() {
        return teleported;
    }

    /**
     * Setter of teleported
     * @param teleported
     */
    public void setTeleported(boolean teleported) {
        this.teleported = teleported;
    }

    /**
     * Method to update position of teleportingSatellite
     */
    @Override
    public void updatePosition() {
        Angle angularDisplacement = Angle.fromRadians(this.calculateAngularVelocity());
        Angle newPosition;
        Angle oldPosition = this.getPosition();
        if (this.getDirection() == CLOCKWISE) {
            newPosition = this.getPosition().subtract(angularDisplacement);
            if (newPosition.compareTo(Angle.fromDegrees(0)) == -1) {
                newPosition = newPosition.add(Angle.fromDegrees(360));
            }
            if (newPosition.compareTo(Angle.fromDegrees(180)) == -1
            && oldPosition.compareTo(Angle.fromDegrees(180)) == 1) {
                newPosition = new Angle();
                this.setDirection(ANTI_CLOCKWISE);
                this.setTeleported(true);
            }
        } else {
            newPosition = this.getPosition().add(angularDisplacement);
            if (newPosition.compareTo(Angle.fromDegrees(360)) == 1) {
                newPosition = newPosition.subtract(Angle.fromDegrees(360));
            }
            if (newPosition.compareTo(Angle.fromDegrees(180)) == 1
            && oldPosition.compareTo(Angle.fromDegrees(180)) == -1) {
                newPosition = new Angle();
                this.setDirection(CLOCKWISE);
                this.setTeleported(true);
            }
        }
        this.setPosition(newPosition);
    }
}
