package unsw.blackout;

public class BandwidthRestrictions {
    private int receiveBandwidth;
    private int sendBandwidth;
    private int fileLimit;
    private int byteLimit;

    public BandwidthRestrictions(int receiveBandwidth, int sendBandwidth, int fileLimit, int byteLimit) {
        this.receiveBandwidth = receiveBandwidth;
        this.sendBandwidth = sendBandwidth;
        this.fileLimit = fileLimit;
        this.byteLimit = byteLimit;
    }

    /**
     * Getter for receiveBandwidth
     * @return int
     */
    public int getReceiveBandwidth() {
        return receiveBandwidth;
    }

    /**
     * Getter for sendBandwidth
     * @return int
     */
    public int getSendBandwidth() {
        return sendBandwidth;
    }

    /**
     * Getter for fileLimit
     * @return int
     */
    public int getFileLimit() {
        return fileLimit;
    }

    /**
     * Getter for byteLimit
     * @return int
     */
    public int getByteLimit() {
        return byteLimit;
    }

}
