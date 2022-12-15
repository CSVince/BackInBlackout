package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import unsw.blackout.FileTransferException.VirtualFileNoBandwidthException;
import unsw.blackout.FileTransferException.VirtualFileNoStorageSpaceException;
import unsw.blackout.FileTransferException.VirtualFileNotFoundException;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class ExceptionTests {
    @Test
    public void testNotEnoughSendingBandwidth() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "A";
        controller.addFileToDevice("DeviceA", "File1", msg);
        controller.addFileToDevice("DeviceA", "File2", msg);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate();
        assertDoesNotThrow(() -> controller.sendFile("File2", "DeviceA", "Satellite1"));
        controller.simulate();
        controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        assertThrows(VirtualFileNoBandwidthException.class,
        () -> {
            controller.sendFile("File2", "Satellite1", "Satellite2");
            controller.sendFile("File1", "Satellite1", "Satellite2");
        });
    }

    @Test
    public void testNotEnoughSendingBandwidthAndFileNoteFound() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "Testing";
        controller.addFileToDevice("DeviceA", "File1", msg);
        controller.addFileToDevice("DeviceA", "File2", msg);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        assertThrows(VirtualFileNotFoundException.class,
        () -> {
            controller.sendFile("NoSuchFile", "DeviceA", "Satellite1");
        });
    }

    @Test
    public void testNotEnoughSendingBandwidthAndFileAlreadyExists() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "Testing";
        controller.addFileToDevice("DeviceA", "File1", msg);
        controller.addFileToDevice("DeviceA", "File2", msg);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        assertThrows(VirtualFileNoBandwidthException.class,
        () -> {
            controller.sendFile("File1", "DeviceA", "Satellite1");
        });
    }

    @Test
    public void testNotEnoughReceivingBandwidth() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "Testing";
        controller.addFileToDevice("DeviceA", "File1", msg);
        controller.addFileToDevice("DeviceB", "File2", msg);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        assertThrows(VirtualFileNoBandwidthException.class,
        () -> {
            controller.sendFile("File2", "DeviceB", "Satellite1");
        });
    }

    @Test
    public void testExceedFileLimit() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Relay1", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("Relay2", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(45));
        controller.createSatellite("Relay3", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));
        controller.createSatellite("Relay4", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(135));
        controller.createSatellite("Relay5", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        controller.createSatellite("Relay6", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(225));
        controller.createSatellite("Relay7", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(270));
        controller.createSatellite("Relay8", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "a";
        controller.addFileToDevice("DeviceA", "File1", msg);
        controller.addFileToDevice("DeviceA", "File2", msg);
        controller.addFileToDevice("DeviceA", "File3", msg);
        controller.addFileToDevice("DeviceA", "File4", msg);
        assertDoesNotThrow(() -> {
            controller.sendFile("File1", "DeviceA", "Satellite1");
            controller.simulate();
            controller.sendFile("File2", "DeviceA", "Satellite1");
            controller.simulate();
            controller.sendFile("File3", "DeviceA", "Satellite1");
        });
        controller.simulate();
        assertThrows(VirtualFileNoStorageSpaceException.class,
        () -> {
            controller.sendFile("File4", "DeviceA", "Satellite1");
        });
    }

    @Test
    public void testExceedFileLimitAndFileNotFound() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Relay1", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("Relay2", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(45));
        controller.createSatellite("Relay3", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));
        controller.createSatellite("Relay4", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(135));
        controller.createSatellite("Relay5", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        controller.createSatellite("Relay6", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(225));
        controller.createSatellite("Relay7", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(270));
        controller.createSatellite("Relay8", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "a";
        controller.addFileToDevice("DeviceA", "File1", msg);
        controller.addFileToDevice("DeviceA", "File2", msg);
        controller.addFileToDevice("DeviceA", "File3", msg);
        controller.addFileToDevice("DeviceA", "File4", msg);
        assertDoesNotThrow(() -> {
            controller.sendFile("File1", "DeviceA", "Satellite1");
            controller.simulate();
            controller.sendFile("File2", "DeviceA", "Satellite1");
            controller.simulate();
            controller.sendFile("File3", "DeviceA", "Satellite1");
        });
        controller.simulate();
        assertThrows(VirtualFileNotFoundException.class,
        () -> {
            controller.sendFile("TestingNotFound", "DeviceA", "Satellite1");
        });
    }

    @Test
    public void testExceedFileLimitAndFileAlreadyExists() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Relay1", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("Relay2", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(45));
        controller.createSatellite("Relay3", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));
        controller.createSatellite("Relay4", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(135));
        controller.createSatellite("Relay5", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        controller.createSatellite("Relay6", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(225));
        controller.createSatellite("Relay7", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(270));
        controller.createSatellite("Relay8", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "a";
        controller.addFileToDevice("DeviceA", "File1", msg);
        controller.addFileToDevice("DeviceA", "File2", msg);
        controller.addFileToDevice("DeviceA", "File3", msg);
        controller.addFileToDevice("DeviceA", "File4", msg);
        assertDoesNotThrow(() -> {
            controller.sendFile("File1", "DeviceA", "Satellite1");
            controller.simulate();
            controller.sendFile("File2", "DeviceA", "Satellite1");
            controller.simulate();
            controller.sendFile("File3", "DeviceA", "Satellite1");
        });
        controller.simulate();
        assertThrows(VirtualFileAlreadyExistsException.class,
        () -> {
            controller.sendFile("File3", "DeviceA", "Satellite1");
        });
    }

    @Test
    public void testExceedByteLimit() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "Ineedtomakethis80byteslongatleastIneedtomakethis80byteslongatleast"
        + "Ineedtomakethis80byteslongatleastIneedtomakethis80byteslongatleast"
        + "Ineedtomakethis80byteslongatleastIneedtomakethis80byteslongatleast"
        + "Ineedtomakethis80byteslongatleastIneedtomakethis80byteslongatleast"
        + "Ineedtomakethis80byteslongatleastIneedtomakethis80byteslongatleast";
        controller.addFileToDevice("DeviceA", "File1", msg);
        assertThrows(VirtualFileNoStorageSpaceException.class,
        () -> {
            controller.sendFile("File1", "DeviceA", "Satellite1");
        });
    }

    @Test
    public void testFileDoesntExist() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "a";
        controller.addFileToDevice("DeviceA", "File1", msg);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate();
        assertThrows(VirtualFileNotFoundException.class,
        () -> {
            controller.sendFile("Nothing", "DeviceA", "Satellite1");
        });
    }

    @Test
    public void testPartialFileSending() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(321));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "aaaa";
        controller.addFileToDevice("DeviceA", "File1", msg);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate();
        assertThrows(VirtualFileNotFoundException.class,
        () -> {
            controller.sendFile("File1", "Satellite1", "Satellite2");
        });
    }

    @Test
    public void testFileAlreadyExists() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        String msg = "a";
        controller.addFileToDevice("DeviceA", "File1", msg);
        controller.addFileToDevice("DeviceA", "File2", msg);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate();
        assertThrows(VirtualFileAlreadyExistsException.class,
        () -> {
            controller.sendFile("File1", "DeviceA", "Satellite1");
        });
    }
}
