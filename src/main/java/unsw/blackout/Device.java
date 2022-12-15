package unsw.blackout;

// import java.util.ArrayList;

import unsw.utils.Angle;

// import static unsw.utils.MathsHelper.getDistance;


public class Device extends Entity implements TransferEntityInterface {

    private BandwidthRestrictions bandwidthRestrictions;
    /**
     * Constructor for Device
     * @param deviceId
     * @param type
     * @param position
     */
    public Device(String deviceId, String type, Angle position) {
        super(deviceId, type, 69911, position);
        this.setBandwidthRestrictions(new BandwidthRestrictions(
            Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE
        ));
    }

    /**
     * Getter for bandwidthRestrictions
     * @return BandwidthRestrictions
     */
    public BandwidthRestrictions getBandwidthRestrictions() {
        return bandwidthRestrictions;
    }

    /**
     * Setter for bandwidthRestrictions
     * @param bandwidthRestrictions
     */
    public void setBandwidthRestrictions(BandwidthRestrictions bandwidthRestrictions) {
        this.bandwidthRestrictions = bandwidthRestrictions;
    }

    /**
     * Getter for receiveBandwidth of Device
     * @param restrictions
     * @return int
     */
    public int getReceiveBandwidth(BandwidthRestrictions restrictions) {
        return restrictions.getReceiveBandwidth();
    }

    /**
     * Getter for sendBandwidth of Device
     * @param restrictions
     * @return
     */
    public int getSendBandwidth(BandwidthRestrictions restrictions) {
        return restrictions.getSendBandwidth();
    }

    /**
     * Getter for fileLimit of Device
     * @param restrictions
     * @return
     */
    public int getFileLimit(BandwidthRestrictions restrictions) {
        return restrictions.getFileLimit();
    }

    /**
     * Getter for byteLimit of Device
     * @param restrictions
     * @return
     */
    public int getByteLimit(BandwidthRestrictions restrictions) {
        return restrictions.getByteLimit();
    }

    /**
     * Calculate device's receiving bandwidth
     * @param restrictions
     * @return Integer.MAX_VALUE
     */
    public int calculateReceiveBandwidth(BandwidthRestrictions restrictions) {
        return this.getReceiveBandwidth(restrictions);
    }

    /**
     * Calculate device's sending bandwidth
     * @param restrictions
     * @return Integer.MAX_VALUE
     */
    public int calculateSendBandwidth(BandwidthRestrictions restrictions) {
        return this.getSendBandwidth(restrictions);
    }

}
