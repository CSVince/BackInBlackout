package unsw.blackout;

public class File {

    private String filename;
    private String currentContents;
    private int size;
    private int bytesTransmitted;
    private String finalContents;
    private Entity receivedFrom;
    private Entity sendingTo;

    /**
     * Constructor for File
     * @param filename
     * @param currentContents
     * @param bytesTransmitted
     * @param finalContents
     * @param receivedFrom
     * @param sendingTo
     */
    public File(String filename, String currentContents, int bytesTransmitted, String finalContents,
        Entity receivedFrom, Entity sendingTo) {
        this.filename = filename;
        this.currentContents = currentContents;
        this.size = finalContents.length();
        this.bytesTransmitted = bytesTransmitted;
        this.finalContents = finalContents;
        this.receivedFrom = receivedFrom;
        this.sendingTo = sendingTo;
    }

    /**
     * Getter for filename
     * @return String
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Getter for current contents of file
     * @return String
     */
    public String getCurrentContents() {
        return currentContents;
    }

    /**
     * Getter for size
     * @return int
     */
    public int getSize() {
        return size;
    }

    /**
     * Setter for size
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Getter for number of bytes transmitted
     * @return int
     */
    public int getBytesTransmitted() {
        return bytesTransmitted;
    }

    /**
     * Setter for current file contents
     * @param currentContents
     */
    public void setCurrentContents(String currentContents) {
        this.currentContents = currentContents;
    }

    /**
     * Setter for current number of bytes transmitted
     * @param bytesTransmitted
     */
    public void setBytesTransmitted(int bytesTransmitted) {
        this.bytesTransmitted = bytesTransmitted;
    }

    /**
     * Getter for final file contents
     * @return
     */
    public String getFinalContents() {
        return finalContents;
    }

    /**
     * Setter for final file contents
     * @param finalContents
     */
    public void setFinalContents(String finalContents) {
        this.finalContents = finalContents;
    }

    /**
     * Getter for receivedFrom (The entity which sent the file)
     * @return Entity
     */
    public Entity getReceivedFrom() {
        return receivedFrom;
    }

    /**
     * Getter for sendingTo (The entity which is receiving the file)
     * @return Entity
     */
    public Entity getSendingTo() {
        return sendingTo;
    }

    /**
     * Method to remove the t's from a file and update its values accordingly
     * @param receiver
     * @param sender
     */
    public void removeAllTs(Entity receiver, Entity sender) {
        String newContent = this.getFinalContents();
        newContent = newContent.replace("t", "");
        this.setCurrentContents(newContent);
        this.setFinalContents(newContent);
        this.setBytesTransmitted(newContent.length());
        this.setSize(newContent.length());
        receiver.updateNumFilesReceivingSending(sender);
    }

    /**
     * Method to remove the remaining t's from a file to be downloaded
     * @param receiver
     * @param sender
     */
    public void removeRemainingTs(Entity receiver, Entity sender) {
        String currentContent = this.getCurrentContents();
        String remainingContent = this.getFinalContents().substring(currentContent.length());
        String newContent = currentContent + remainingContent.replace("t", "");
        this.setCurrentContents(newContent);
        this.setFinalContents(newContent);
        this.setBytesTransmitted(newContent.length());
        this.setSize(newContent.length());
        receiver.updateNumFilesReceivingSending(sender);
    }

    /**
     * Function to execute the teleporting satellite behaviour if necessary
     * @param receiver
     * @param sender
     * @return true if sender/receiver teleported. else false.
     */
    public boolean entitiesTeleported(Entity receiver, Entity sender) {
        if (receiver instanceof TeleportingSatellite && sender instanceof Device) {
            TeleportingSatellite receiverTeleport = (TeleportingSatellite) receiver;
            if (receiverTeleport.getTeleported()) {
                receiver.removeFile(this);
                File senderFile = sender.findFile(this.getFilename());
                senderFile.removeAllTs(receiver, sender);
                return true;
            }
        } else if (sender instanceof TeleportingSatellite || receiver instanceof TeleportingSatellite) {
            if (sender instanceof TeleportingSatellite) {
                TeleportingSatellite senderTeleport = (TeleportingSatellite) sender;
                if (senderTeleport.getTeleported()) {
                    this.removeRemainingTs(receiver, sender);
                    return true;
                }
            }
            if (receiver instanceof TeleportingSatellite) {
                TeleportingSatellite receiverTeleport = (TeleportingSatellite) receiver;
                if (receiverTeleport.getTeleported()) {
                    this.removeRemainingTs(receiver, sender);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method to update a single file
     */
    public void update() {
        Entity sender = this.getReceivedFrom();
        Entity receiver = this.getSendingTo();
        TransferEntityInterface transferableSender = (TransferEntityInterface) sender;
        TransferEntityInterface transferableReceiver = (TransferEntityInterface) receiver;
        int receivingBandwidth = Integer.MAX_VALUE;
        int sendingBandwidth = Integer.MAX_VALUE;

        if (this.entitiesTeleported(receiver, sender)) {
            return;
        }

        if (!receiver.inRangeOfSender(sender)) {
            if (receiver instanceof ElephantSatellite) {
                receivingBandwidth = 0;
            } else {
                receiver.removeFile(this);
                return;
            }
        } else {
            receivingBandwidth = transferableReceiver.calculateReceiveBandwidth(
                transferableReceiver.getBandwidthRestrictions());
            sendingBandwidth = transferableSender.calculateSendBandwidth(
                transferableSender.getBandwidthRestrictions());
        }

        this.setBytesTransmitted(Math.min(this.getBytesTransmitted() + Math.min(receivingBandwidth, sendingBandwidth),
            this.getSize()));
        this.setCurrentContents(this.getFinalContents().substring(0, this.getBytesTransmitted()));

        if (this.getBytesTransmitted() == this.getSize()) {
            receiver.updateNumFilesReceivingSending(sender);
        }
    }
}
