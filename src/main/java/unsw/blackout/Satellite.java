package unsw.blackout;

import unsw.utils.Angle;

// import static unsw.utils.MathsHelper.getDistance;

// import java.util.ArrayList;
// import java.util.Iterator;

public abstract class Satellite extends Entity {
    private double linearVelocity;
    private int direction;

    /**
     * Constructor for Satellite
     * @param satelliteId
     * @param type
     * @param height
     * @param position
     */
    public Satellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        this.setNumFilesReceiving(0);
        this.setNumFilesSending(0);
    }


    /**
     * Getter for linearVelocity
     * @return double
     */
    public double getLinearVelocity() {
        return linearVelocity;
    }

    /**
     * Setter for linearVelocity
     * @param linearVelocity
     */
    public void setLinearVelocity(double linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    /**
     * Method to calculate the angular velocity of a satellite
     * @return double
     */
    public double calculateAngularVelocity() {
        return (this.linearVelocity / this.getHeight());
    }

    /**
     * Getter of direction
     * @return int
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Setter of direction
     * @param direction
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    public abstract void updatePosition();

}
