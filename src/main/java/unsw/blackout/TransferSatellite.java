package unsw.blackout;

import unsw.utils.Angle;

public abstract class TransferSatellite extends Satellite implements TransferEntityInterface {

    private BandwidthRestrictions bandwidthRestrictions;

    /**
     * Constructor for AbstractSatellite
     * @param satelliteId
     * @param type
     * @param height
     * @param position
     */
    public TransferSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
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
     * Getter for receiveBandwidth of satellite
     * @param restrictions
     * @return int
     */
    public int getReceiveBandwidth(BandwidthRestrictions restrictions) {
        return restrictions.getReceiveBandwidth();
    }

    /**
     * Getter for sendBandwidth of satellite
     * @param restrictions
     * @return int
     */
    public int getSendBandwidth(BandwidthRestrictions restrictions) {
        return restrictions.getSendBandwidth();
    }

    /**
     * Getter for fileLimit of satellite
     * @param restrictions
     * @return int
     */
    public int getFileLimit(BandwidthRestrictions restrictions) {
        return restrictions.getFileLimit();
    }

    /**
     * Getter for byteLimit of satellite
     * @param restrictions
     * @return int
     */
    public int getByteLimit(BandwidthRestrictions restrictions) {
        return restrictions.getByteLimit();
    }

    /**
     * Calculate satellite's receiving bandwidth
     * @param restrictions
     * @return int
     */
    public int calculateReceiveBandwidth(BandwidthRestrictions restrictions) {
        return this.getReceiveBandwidth(restrictions) / this.getNumFilesReceiving();
    }

    /**
     * Calculate satellite's sending bandwidth
     * @param restrictions
     * @return int
     */
    public int calculateSendBandwidth(BandwidthRestrictions restrictions) {
        return this.getSendBandwidth(restrictions) / this.getNumFilesSending();
    }
}
