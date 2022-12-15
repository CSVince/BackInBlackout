package unsw.blackout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import unsw.utils.Angle;

public class ElephantSatellite extends TransferSatellite {

    /**
     * Constructor for ElephantSatellite
     * @param satelliteId
     * @param type
     * @param height
     * @param position
     */
    public ElephantSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        this.setDevicesSupported(new ArrayList<String>(
            Arrays.asList("LaptopDevice", "DesktopDevice",
            "RelaySatellite", "StandardSatellite", "ElephantSatellite")
        ));
        this.setRange(400000);
        this.setLinearVelocity(2500);
        this.setBandwidthRestrictions(new BandwidthRestrictions(20, 20, Integer.MAX_VALUE, 90));
    }

    /**
     * Returns an arraylist of all transient files
     * @return ArrayList<File>
     */
    public ArrayList<File> getTransientFiles() {
        ArrayList<File> transFiles = new ArrayList<File>();
        for (File file: this.getFiles()) {
            if (file.getBytesTransmitted() != file.getSize() && !this.inRangeOfSender(file.getReceivedFrom())) {
                transFiles.add(file);
            }
        }
        return transFiles;
    }

    @Override
    public int calculateReceiveBandwidth(BandwidthRestrictions restrictions) {
        int numNonTransFilesReceiving = this.getNumFilesReceiving() - this.getTransientFiles().size();
        return this.getReceiveBandwidth(restrictions) / numNonTransFilesReceiving;
    }

    /**
     * Function to make room for new file to be downloaded. Returns deleted files
     * @param newFile
     * @return ArrayList<File>
     */
    public ArrayList<File> makeRoom(File newFile) {
        int newFileSize = newFile.getSize();
        int nonTransientBytes = 0;
        ArrayList<File> transFiles = this.getTransientFiles();
        ArrayList<File> files = this.getFiles();
        int numTransFiles = transFiles.size();

        for (File file: files) {
            if (file.getBytesTransmitted() == file.getSize() || this.inRangeOfSender(file.getReceivedFrom())) {
                nonTransientBytes += file.getSize();
            }
        }
        int capacity = this.getByteLimit(this.getBandwidthRestrictions()) - nonTransientBytes - newFileSize;
        if (capacity < 0) {
            return new ArrayList<File>();
        }
        int[] weights = new int[numTransFiles];
        int[] values = new int[numTransFiles];
        int counter = 0;
        for (File file: transFiles) {
            weights[counter] = file.getSize();
            values[counter] = file.getBytesTransmitted();
            counter++;
        }

        int[][] knapsack = new int[capacity + 1][numTransFiles + 1];
        int[][] backtracking = new int[capacity + 1][numTransFiles + 1];
        for (int i = 0; i <= capacity; i++) {
            for (int j = 0; j <= numTransFiles; j++) {
                if (i == 0 || j == 0) {
                    knapsack[i][j] = 0;
                    backtracking[i][j] = -1;
                } else if (i - weights[j - 1] < 0) {
                    knapsack[i][j] = knapsack[i][j - 1];
                    backtracking[i][j] = backtracking[i][j - 1];
                } else if (knapsack[i][j - 1] < knapsack[i - weights[j - 1]][j - 1] + values[j - 1]) {
                    knapsack[i][j] = knapsack[i - weights[j - 1]][j - 1] + values[j - 1];
                    backtracking[i][j] = j - 1;
                } else {
                    knapsack[i][j] = knapsack[i][j - 1];
                    backtracking[i][j] = backtracking[i][j - 1];
                }
            }
        }

        // Form an array of all the files to include
        counter = numTransFiles;
        ArrayList<File> filesToKeep = new ArrayList<File>();
        while (counter != 0) {
            if (backtracking[capacity][counter] == -1) {
                break;
            }
            filesToKeep.add(transFiles.get(backtracking[capacity][counter]));
            capacity = capacity - values[backtracking[capacity][counter]];
            counter--;
        }

        // Remove the file which is to not be included
        ArrayList<File> filesToRemove = new ArrayList<File>();
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (transFiles.contains(file) && !filesToKeep.contains(file)) {
                filesToRemove.add(file);
                this.updateNumFilesReceivingSending(file.getReceivedFrom());
                iterator.remove();
            }
        }

        return filesToRemove;
    }

    /**
     * Function to update the position of an ElephantSatellite
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
