package unsw.blackout;

import java.util.ArrayList;
import java.util.Iterator;

import unsw.utils.Angle;

import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

public class Entity {
    private String id;
    private String type;
    private Angle position;
    private double height;
    private ArrayList<String> devicesSupported;
    private ArrayList<File> files;
    private int range;
    private int numFilesSending;
    private int numFilesReceiving;

    /**
     * Constructor for Entity
     * @param id
     * @param type
     * @param height
     * @param position
     */
    public Entity(String id, String type, double height, Angle position) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.height = height;
        this.setFiles(new ArrayList<File>());
    }

    /**
     * Getter for Id
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for type
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for position
     * @return Angle
     */
    public Angle getPosition() {
        return position;
    }

    /**
     * Getter for height
     * @return double
     */
    public double getHeight() {
        return height;
    }

    /**
     * Setter for position
     * @param position
     */
    public void setPosition(Angle position) {
        this.position = position;
    }

    /**
     * Setter for devicesSupported
     * @param devicesSupported
     */
    public void setDevicesSupported(ArrayList<String> devicesSupported) {
        this.devicesSupported = devicesSupported;
    }

    /**
     * Getter for devicesSupported
     * @return ArrayList<String>
     */
    public ArrayList<String> getDevicesSupported() {
        return devicesSupported;
    }

    /**
     * Setter for range
     * @param range
     */
    public void setRange(int range) {
        this.range = range;
    }

    /**
     * Getter for range
     * @return int
     */
    public int getRange() {
        return this.range;
    }

    /**
     * Getter for files
     * @return ArrayList<File>
     */
    public ArrayList<File> getFiles() {
        return files;
    }

    /**
     * Setter for files
     * @param files
     */
    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    /**
     * Find file
     * @param sender
     * @return
     */
    public File findFile(String fileName) {
        ArrayList<File> files = this.getFiles();
        for (File file: files) {
            if (file.getFilename().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    /**
     * Method to determine whether this entity is in range of another sender
     * @param sender
     * @return
     */
    public boolean inRangeOfSender(Entity sender) {
        // Device is sending to a satellite
        if (sender instanceof Device) {
            return (getDistance(this.getHeight(), this.getPosition(), sender.getPosition()) < sender.getRange()
            && isVisible(this.getHeight(), this.getPosition(), sender.getPosition()));
        // Satellite is sending to a device
        } else if (sender instanceof Satellite && this instanceof Device) {
            return (getDistance(sender.getHeight(), sender.getPosition(), this.getPosition()) < sender.getRange()
            && isVisible(sender.getHeight(), sender.getPosition(), this.getPosition()));
        // Satellite is sending to a satellite
        } else {
            return (getDistance(this.getHeight(), this.getPosition(), sender.getHeight(), sender.getPosition())
            < sender.getRange()
            && (isVisible(this.getHeight(), this.getPosition(), sender.getHeight(), sender.getPosition())));
        }
    }

    /**
     * Getter for number of files being sent from this entity
     * @return int
     */
    public int getNumFilesSending() {
        return numFilesSending;
    }

    /**
     * Setter for number of files being sent from this entity
     * @param int
     */
    public void setNumFilesSending(int numFilesSending) {
        this.numFilesSending = numFilesSending;
    }

    /**
     * Getter for number of files being received by this entity
     * @return int
     */
    public int getNumFilesReceiving() {
        return numFilesReceiving;
    }

    /**
     * Setter for number of files being received by this entity
     * @param int
     */
    public void setNumFilesReceiving(int numFilesReceiving) {
        this.numFilesReceiving = numFilesReceiving;
    }

    /**
     * Method to update the number of files sent/received of a receiver and a
     * sender once a file is done downloading
     * @param sender
     */
    public void updateNumFilesReceivingSending(Entity sender) {
        this.setNumFilesReceiving(this.getNumFilesReceiving() - 1);
        sender.setNumFilesSending(sender.getNumFilesSending() - 1);
    }

    /**
     * Method to remove a file from an entity
     * @param fileToBeRemoved
     */
    public void removeFile(File fileToBeRemoved) {
        ArrayList<File> files = this.getFiles();
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (fileToBeRemoved == file) {
                this.updateNumFilesReceivingSending(file.getReceivedFrom());
                iterator.remove();
            }
        }
        this.setFiles(files);
    }

    /**
     * Method to update the files after one tick
     */
    public void updateFiles() {
        ArrayList<File> files = this.getFiles();
        Iterator<File> iterator = files.iterator();
        ArrayList<File> undownloadedFiles = new ArrayList<>();
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (file.getBytesTransmitted() != file.getSize()) {
                undownloadedFiles.add(file);
            }
        }
        for (File file: undownloadedFiles) {
            file.update();
        }
        this.setFiles(files);
    }

}
