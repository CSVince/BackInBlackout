package unsw.blackout;

public interface TransferEntityInterface {

    public BandwidthRestrictions getBandwidthRestrictions();

    public void setBandwidthRestrictions(BandwidthRestrictions bandwidthRestrictions);

    public int getReceiveBandwidth(BandwidthRestrictions restrictions);

    public int getSendBandwidth(BandwidthRestrictions restrictions);

    public int getFileLimit(BandwidthRestrictions restrictions);

    public int getByteLimit(BandwidthRestrictions restrictions);

    public int calculateSendBandwidth(BandwidthRestrictions restrictions);

    public int calculateReceiveBandwidth(BandwidthRestrictions restrictions);
}
