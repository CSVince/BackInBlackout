package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class BlackoutController {

    private ArrayList<Satellite> satellites = new ArrayList<Satellite>();
    private ArrayList<Device> devices = new ArrayList<Device>();

    public void createDevice(String deviceId, String type, Angle position) {
        if (type.equals("HandheldDevice")) {
            HandheldDevice newDevice = new HandheldDevice(deviceId, type, position);
            devices.add(newDevice);
        }
        if (type.equals("LaptopDevice")) {
            LaptopDevice newDevice = new LaptopDevice(deviceId, type, position);
            devices.add(newDevice);
        }
        if (type.equals("DesktopDevice")) {
            DesktopDevice newDevice = new DesktopDevice(deviceId, type, position);
            devices.add(newDevice);
        }
    }

    public void removeDevice(String deviceId) {
        devices.remove(findDevice(deviceId));
    }

    public Device findDevice(String deviceId) {
        for (Device device: devices) {
            if (device.getId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        if (type.equals("StandardSatellite")) {
            StandardSatellite newSatellite = new StandardSatellite(satelliteId, type, height, position);
            satellites.add(newSatellite);
        }
        if (type.equals("TeleportingSatellite")) {
            TeleportingSatellite newSatellite = new TeleportingSatellite(satelliteId, type, height, position);
            satellites.add(newSatellite);
        }
        if (type.equals("RelaySatellite")) {
            RelaySatellite newSatellite = new RelaySatellite(satelliteId, type, height, position);
            satellites.add(newSatellite);
        }
        if (type.equals("ElephantSatellite")) {
            ElephantSatellite newSatellite = new ElephantSatellite(satelliteId, type, height, position);
            satellites.add(newSatellite);
        }
    }

    public void removeSatellite(String satelliteId) {
        satellites.remove(findSatellite(satelliteId));
    }

    public Satellite findSatellite(String satelliteId) {
        for (Satellite satellite: satellites) {
            if (satellite.getId().equals(satelliteId)) {
                return satellite;
            }
        }
        return null;
    }

    public List<String> listDeviceIds() {
        List<String> deviceIds = new ArrayList<String>();
        for (Device device: devices) {
            deviceIds.add(device.getId());
        }
        return deviceIds;
    }

    public List<String> listSatelliteIds() {
        List<String> satelliteIds = new ArrayList<String>();
        for (Satellite satellite: satellites) {
            satelliteIds.add(satellite.getId());
        }
        return satelliteIds;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Device deviceToBeAdded = findDevice(deviceId);
        ArrayList<File> deviceFiles = deviceToBeAdded.getFiles();
        deviceFiles.add(new File(filename, content, content.length(), content, deviceToBeAdded, deviceToBeAdded));
        deviceToBeAdded.setFiles(deviceFiles);
    }

    public EntityInfoResponse getInfo(String id) {
        Map<String, FileInfoResponse> fileMap = new HashMap<String, FileInfoResponse>();
        Entity entityToFindInfo = findEntity(id);
        ArrayList<File> files = entityToFindInfo.getFiles();
        for (File file: files) {
            fileMap.put(file.getFilename(), new FileInfoResponse(file.getFilename(), file.getCurrentContents(),
                 file.getSize(), (file.getBytesTransmitted() == file.getSize())));
        }
        return new EntityInfoResponse(id, entityToFindInfo.getPosition(), entityToFindInfo.getHeight(),
            entityToFindInfo.getType(), fileMap);
    }

    public void simulate() {
        // Iterate over all satellites and update position
        for (Satellite satellite: satellites) {
            satellite.updatePosition();
        }
        // Update files
        for (Device device: devices) {
            device.updateFiles();
        }
        for (Satellite satellite: satellites) {
            satellite.updateFiles();
        }

    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public void relayRecursion(List<String> communicableIds, Satellite relay,
        Entity original) {

        for (Satellite satellite: satellites) {
            if (!satellite.getDevicesSupported().contains(original.getType())) {
                continue;
            }
            if (satellite.inRangeOfSender(relay) && !communicableIds.contains(satellite.getId())) {
                communicableIds.add(satellite.getId());
                if (satellite.getType().equals("RelaySatellite")) {
                    relayRecursion(communicableIds, satellite, original);
                }
            }
        }
        for (Device device: devices) {
            if (device.inRangeOfSender(relay)) {
                if (original instanceof Device || !original.getDevicesSupported().contains(device.getType())) {
                    continue;
                }
                communicableIds.add(device.getId());
            }
        }
        return;
    }

    public List<String> communicableEntitiesInRange(String id) {
        Entity entity = findEntity(id);
        ArrayList<String> communicableIds = new ArrayList<>();
        for (Satellite satellite: satellites) {
            if (satellite.inRangeOfSender(entity) && satellite.getDevicesSupported().contains(entity.getType())) {
                communicableIds.add(satellite.getId());
                if (satellite.getType().equals("RelaySatellite")) {
                    relayRecursion(communicableIds, satellite, entity);
                }
            }
        }
        for (Device device: devices) {
            if (device.inRangeOfSender(entity) && device.getDevicesSupported().contains(entity.getType())) {
                communicableIds.add(device.getId());
            }
        }
        Set<String> set = new HashSet<>(communicableIds);
        communicableIds.clear();
        communicableIds.addAll(set);
        communicableIds.remove(id);
        return communicableIds;
    }

    public Entity findEntity(String id) {
        Device device = findDevice(id);
        if (device == null) {
            return findSatellite(id);
        }
        return device;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        Entity sender = findEntity(fromId);
        Entity receiver = findEntity(toId);
        File fileOnSender = sender.findFile(fileName);
        File fileOnReceiver = receiver.findFile(fileName);

        if (fileOnSender == null || fileOnSender.getBytesTransmitted() != fileOnSender.getSize()) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName
            + "doesn't exist/hasn't finished downloading to send");
        }
        if (sender instanceof RelaySatellite || receiver instanceof RelaySatellite) {
            throw new FileTransferException.VirtualFileNoBandwidthException(fromId
                + "does not have enough bandwidth to send the file");
        }
        if (sender instanceof TransferSatellite) {
            TransferSatellite senderSatellite = (TransferSatellite) sender;
            if (senderSatellite.getNumFilesSending() + 1
            > senderSatellite.getSendBandwidth(senderSatellite.getBandwidthRestrictions())) {
                throw new FileTransferException.VirtualFileNoBandwidthException(fromId
                + "does not have enough bandwidth to send the file");
            }
        }

        if (receiver instanceof TransferSatellite) {
            TransferSatellite receiverSatellite = (TransferSatellite) receiver;
            if (receiverSatellite.getNumFilesReceiving() + 1
                > receiverSatellite.getReceiveBandwidth(receiverSatellite.getBandwidthRestrictions())) {
                throw new FileTransferException.VirtualFileNoBandwidthException(toId
                + "does not have enough bandwidth to receive the file");
            }
        }
        if (fileOnReceiver != null) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(fileName
            + "already exists on the target entity.");
        }
        if (receiver instanceof TransferSatellite) {
            TransferSatellite receiverSatellite = (TransferSatellite) receiver;
            if (receiverSatellite.getFiles().size() + 1
                > receiverSatellite.getFileLimit(receiverSatellite.getBandwidthRestrictions())) {
                throw new FileTransferException.VirtualFileNoStorageSpaceException("Max files reached for satellite"
                + toId);
            }
            int currentBytes = 0;
            for (File file: receiverSatellite.getFiles()) {
                currentBytes += file.getSize();
            }
            if (currentBytes + fileOnSender.getSize()
            > receiverSatellite.getByteLimit(receiverSatellite.getBandwidthRestrictions())) {
                if (receiver instanceof ElephantSatellite) {
                    ElephantSatellite elephantReceiver = (ElephantSatellite) receiver;
                    ArrayList<File> filesRemoved = elephantReceiver.makeRoom(fileOnSender);
                    if (filesRemoved.size() == 0) {
                        throw new FileTransferException.VirtualFileNoStorageSpaceException(
                            "Max storage reached for satellite" + toId);
                    }
                } else {
                    throw new FileTransferException.VirtualFileNoStorageSpaceException(
                        "Max storage reached for satellite" + toId);
                }
            }
        }

        File newFileToBeSent = new File(fileOnSender.getFilename(), "", 0,
            fileOnSender.getFinalContents(), sender, receiver);
        ArrayList<File> receiverFiles = receiver.getFiles();
        receiverFiles.add(newFileToBeSent);
        receiver.setFiles(receiverFiles);
        receiver.setNumFilesReceiving(receiver.getNumFilesReceiving() + 1);
        sender.setNumFilesSending(sender.getNumFilesSending() + 1);
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }

}
