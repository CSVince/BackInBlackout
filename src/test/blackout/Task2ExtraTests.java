package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2ExtraTests {
    @Test
    public void testSupportedDevices() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Handheld", "HandheldDevice", Angle.fromDegrees(0));
        controller.createDevice("Laptop", "LaptopDevice", Angle.fromDegrees(0));
        controller.createDevice("Desktop", "DesktopDevice", Angle.fromDegrees(0));
        controller.createSatellite("Relay1", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("Relay2", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(30));
        controller.createSatellite("Relay3", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(60));
        controller.createSatellite("Relay4", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));
        controller.createSatellite("Relay5", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(120));
        controller.createSatellite("Relay6", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(150));
        controller.createSatellite("Relay7", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        controller.createSatellite("Relay8", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(210));
        controller.createSatellite("Relay9", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(240));
        controller.createSatellite("Relay10", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(270));
        controller.createSatellite("Relay11", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(300));
        controller.createSatellite("Relay12", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(330));
        controller.createSatellite("Standard1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        controller.createSatellite("Elephant1", "ElephantSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        controller.createSatellite("Elephant2", "ElephantSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(190));
        controller.createSatellite("Teleporting1", "TeleportingSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(179));
        assertListAreEqualIgnoringOrder(Arrays.asList(
            "Relay1", "Relay2", "Relay3", "Relay4",
            "Relay5", "Relay6", "Relay7", "Relay8", "Relay9", "Relay10", "Relay11",
            "Relay12", "Standard1", "Teleporting1"),
            controller.communicableEntitiesInRange("Handheld"));
        assertListAreEqualIgnoringOrder(Arrays.asList(
            "Relay1", "Relay2", "Relay3", "Relay4",
            "Relay5", "Relay6", "Relay7", "Relay8", "Relay9", "Relay10", "Relay11",
            "Relay12", "Standard1", "Teleporting1", "Elephant1", "Elephant2"),
            controller.communicableEntitiesInRange("Laptop"));
        assertListAreEqualIgnoringOrder(Arrays.asList(
            "Relay1", "Relay2", "Relay3", "Relay4",
            "Relay5", "Relay6", "Relay7", "Relay8", "Relay9", "Relay10", "Relay11",
            "Relay12", "Elephant1", "Teleporting1", "Elephant2"),
            controller.communicableEntitiesInRange("Desktop"));
        assertListAreEqualIgnoringOrder(Arrays.asList(
            "Laptop", "Desktop", "Handheld", "Relay2", "Relay3", "Relay4",
            "Relay5", "Relay6", "Relay7", "Relay8", "Relay9", "Relay10", "Relay11",
            "Relay12", "Standard1", "Teleporting1", "Elephant1", "Elephant2"),
            controller.communicableEntitiesInRange("Relay1"));
        assertListAreEqualIgnoringOrder(Arrays.asList(
            "Laptop", "Handheld", "Relay1", "Relay2", "Relay3", "Relay4",
            "Relay5", "Relay6", "Relay7", "Relay8", "Relay9", "Relay10", "Relay11",
            "Relay12", "Teleporting1", "Elephant1", "Elephant2"),
            controller.communicableEntitiesInRange("Standard1"));
        assertListAreEqualIgnoringOrder(Arrays.asList(
            "Laptop", "Handheld", "Desktop", "Relay1", "Relay2", "Relay3", "Relay4",
            "Relay5", "Relay6", "Relay7", "Relay8", "Relay9", "Relay10", "Relay11",
            "Relay12", "Standard1"),
            controller.communicableEntitiesInRange("Teleporting1"));
        assertListAreEqualIgnoringOrder(Arrays.asList(
            "Laptop", "Desktop", "Relay1", "Relay2", "Relay3", "Relay4",
            "Relay5", "Relay6", "Relay7", "Relay8", "Relay9", "Relay10", "Relay11",
            "Relay12", "Standard1", "Elephant2"),
            controller.communicableEntitiesInRange("Elephant1"));
    }

    @Test
    public void testCheckMessageHalfwayStandard() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device", "HandheldDevice", Angle.fromDegrees(0));
        controller.createSatellite("Standard", "StandardSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(337));
        String msg = "Hello";
        controller.addFileToDevice("Device", "File", msg);
        assertDoesNotThrow(() -> controller.sendFile("File", "Device", "Standard"));
        controller.simulate(3);
        assertEquals(new FileInfoResponse("File", "Hel", msg.length(), false),
        controller.getInfo("Standard").getFiles().get("File"));
    }

    @Test
    public void testOutOfRange() {
        // Create a device and a teleporting satellite
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device", "HandheldDevice", Angle.fromDegrees(0));
        // Create a satellite 3 ticks in range of the device
        controller.createSatellite("Standard", "StandardSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(337));
        String msg = "Hello";
        controller.addFileToDevice("Device", "File", msg);
        assertDoesNotThrow(() -> controller.sendFile("File", "Device", "Standard"));
        // At this point, the satellite is just in range of the device
        controller.simulate(3);
        assertEquals(new FileInfoResponse("File", "Hel", msg.length(), false),
        controller.getInfo("Standard").getFiles().get("File"));
        // The satellite is now out of range of the device
        controller.simulate();
        assertEquals(null, controller.getInfo("Standard").getFiles().get("File"));
    }

    @Test
    public void testCheckMessageHalfwayTeleporting() {
        // Create a device and a teleporting satellite
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device", "HandheldDevice", Angle.fromDegrees(0));
        // Create a satellite 3 ticks in range of the device
        controller.createSatellite("Teleporting", "TeleportingSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(337));
        String msg = "HelloHelloHelloHello";
        controller.addFileToDevice("Device", "File", msg);
        assertDoesNotThrow(() -> controller.sendFile("File", "Device", "Teleporting"));
        // At this point, the satellite is just in range of the device
        controller.simulate();
        assertEquals(new FileInfoResponse("File", "HelloHello", msg.length(), false),
        controller.getInfo("Teleporting").getFiles().get("File"));
    }

    @Test
    public void testCheckMessageHalfwayTeleportingSharingBandwidth() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device", "LaptopDevice", Angle.fromDegrees(0));
        controller.createSatellite("Teleporting", "TeleportingSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("Relay0", "RelaySatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(330));
        controller.createSatellite("Relay1", "RelaySatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(300));
        controller.createSatellite("Relay3", "RelaySatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(270));
        String msg = "HelloHello";
        controller.addFileToDevice("Device", "File", msg);
        controller.addFileToDevice("Device", "File1", msg);
        assertDoesNotThrow(() -> controller.sendFile("File", "Device", "Teleporting"));
        assertDoesNotThrow(() -> controller.sendFile("File1", "Device", "Teleporting"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File", "Hello", msg.length(), false),
        controller.getInfo("Teleporting").getFiles().get("File"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File", msg, msg.length(), true),
        controller.getInfo("Teleporting").getFiles().get("File"));
    }

    @Test
    public void testCheckMessageHalfwayTeleportingAsSender() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device", "LaptopDevice", Angle.fromDegrees(0));
        controller.createSatellite("Teleporting", "TeleportingSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("Relay0", "RelaySatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(330));
        controller.createSatellite("Relay1", "RelaySatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(300));
        controller.createSatellite("Relay3", "RelaySatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(270));
        String msg = "HelloHello";
        controller.addFileToDevice("Device", "File", msg);
        controller.addFileToDevice("Device", "File1", msg);
        assertDoesNotThrow(() -> controller.sendFile("File", "Device", "Teleporting"));
        assertDoesNotThrow(() -> controller.sendFile("File1", "Device", "Teleporting"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File", "Hello", msg.length(), false),
        controller.getInfo("Teleporting").getFiles().get("File"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File", msg, msg.length(), true),
        controller.getInfo("Teleporting").getFiles().get("File"));
        // Add another file
        controller.addFileToDevice("Device", "File2", msg);
        assertDoesNotThrow(() -> controller.sendFile("File2", "Device", "Teleporting"));
        controller.simulate();

        // Create a new TeleportingSatellite
        controller.createSatellite("Teleporting1", "TeleportingSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        // Send one file
        assertDoesNotThrow(() -> controller.sendFile("File", "Teleporting", "Teleporting1"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File", msg, msg.length(), true),
        controller.getInfo("Teleporting1").getFiles().get("File"));

        // Send two files to share bandwidth
        assertDoesNotThrow(() -> controller.sendFile("File1", "Teleporting", "Teleporting1"));
        assertDoesNotThrow(() -> controller.sendFile("File2", "Teleporting", "Teleporting1"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File1", "Hello", msg.length(), false),
        controller.getInfo("Teleporting1").getFiles().get("File1"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File1", msg, msg.length(), true),
        controller.getInfo("Teleporting1").getFiles().get("File1"));
        assertEquals(new FileInfoResponse("File2", msg, msg.length(), true),
        controller.getInfo("Teleporting1").getFiles().get("File2"));
    }

    @Test
    public void testSatelliteSendingToDevice() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device", "LaptopDevice", Angle.fromDegrees(0));
        controller.createSatellite("Standard", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.addFileToDevice("Device", "File", "Hi");
        assertDoesNotThrow(() -> controller.sendFile("File", "Device", "Standard"));
        controller.simulate(2);
        controller.createDevice("Device1", "HandheldDevice", Angle.fromDegrees(0));
        assertDoesNotThrow(() -> controller.sendFile("File", "Standard", "Device1"));
    }
}
