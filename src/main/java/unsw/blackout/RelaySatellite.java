package unsw.blackout;

import java.util.ArrayList;
import java.util.Arrays;

import unsw.utils.Angle;

import static unsw.utils.MathsHelper.CLOCKWISE;
import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;

public class RelaySatellite extends Satellite {

    /**
     * Constructor for RelaySatellite
     * @param satelliteId
     * @param type
     * @param height
     * @param position
     */
    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        this.setDevicesSupported(new ArrayList<String>(
            Arrays.asList("DesktopDevice", "LaptopDevice", "HandheldDevice",
            "RelaySatellite", "StandardSatellite", "TeleportingSatellite", "ElephantSatellite")
        ));
        this.setRange(300000);
        this.setLinearVelocity(1500);
        this.setDirection(CLOCKWISE);
    }

    /**
     * Method to update the position of a relay satellite
     */
    @Override
    public void updatePosition() {
        if (this.getPosition().compareTo(Angle.fromDegrees(140)) == -1
        || this.getPosition().compareTo(Angle.fromDegrees(345)) != -1) {
            this.setDirection(ANTI_CLOCKWISE);
            this.setPosition(this.getPosition().add(Angle.fromRadians(this.calculateAngularVelocity())));
        } else if (this.getPosition().compareTo(Angle.fromDegrees(345)) == -1
        && this.getPosition().compareTo(Angle.fromDegrees(190)) == 1) {
            this.setDirection(CLOCKWISE);
            this.setPosition(
                this.getPosition().subtract(Angle.fromRadians(this.calculateAngularVelocity()))
            );
        } else if (this.getDirection() == CLOCKWISE) {
            this.setPosition(
                this.getPosition().subtract(Angle.fromRadians(this.calculateAngularVelocity()))
            );
        } else {
            this.setPosition(this.getPosition().add(Angle.fromRadians(this.calculateAngularVelocity())));
        }
    }
}
