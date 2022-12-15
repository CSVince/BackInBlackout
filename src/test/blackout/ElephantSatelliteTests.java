package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException.VirtualFileNoStorageSpaceException;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import java.util.Arrays;

@TestInstance(value = Lifecycle.PER_CLASS)
public class ElephantSatelliteTests {
    @Test
    public void testTransience() {
        BlackoutController controller = new BlackoutController();

        // Creates Elephant Satellite and Desktop Device
        controller.createSatellite("Satellite1", "ElephantSatellite", 91799, Angle.fromDegrees(52));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(90));
        // Create a file on the desktop device
        String msg = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest";
        controller.addFileToDevice("DeviceC", "newFile", msg);

        // Send the file to the elephant satellite and simulate twice so the elephant satellite is now out of range
        assertDoesNotThrow(() -> controller.sendFile("newFile", "DeviceC", "Satellite1"));
        controller.simulate(5);
        // Check the file is there (it should be transient now)
        assertEquals(new FileInfoResponse("newFile", "testtesttesttesttest", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("newFile"));
        assertListAreEqualIgnoringOrder(Arrays.asList(),
            controller.communicableEntitiesInRange("Satellite1"));

        // Check that the satellite is still not in range
        controller.simulate(120);
        assertListAreEqualIgnoringOrder(Arrays.asList(),
            controller.communicableEntitiesInRange("Satellite1"));
        assertEquals(new FileInfoResponse("newFile", "testtesttesttesttest", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("newFile"));

        // Check the file has not been changed and that the satellite is still out of range
        controller.simulate(55);
        assertListAreEqualIgnoringOrder(Arrays.asList(),
            controller.communicableEntitiesInRange("Satellite1"));
        assertEquals(new FileInfoResponse("newFile", "testtesttesttesttest", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("newFile"));

        // Check that the satellite is in range now and that it is receiving bytes
        controller.simulate();
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC"),
            controller.communicableEntitiesInRange("Satellite1"));
        assertEquals(new FileInfoResponse("newFile", "testtesttesttesttesttesttesttesttesttest", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("newFile"));

        // Check that the satellite has received more bytes
        controller.simulate();
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC"),
            controller.communicableEntitiesInRange("Satellite1"));
        assertEquals(new FileInfoResponse("newFile",
        "testtesttesttesttesttesttesttesttesttesttesttesttesttesttest", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("newFile"));

        controller.simulate();
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC"),
            controller.communicableEntitiesInRange("Satellite1"));
        assertEquals(new FileInfoResponse("newFile", msg, msg.length(), true),
            controller.getInfo("Satellite1").getFiles().get("newFile"));
    }

    @Test
    public void testRemoveFile() {
        BlackoutController controller = new BlackoutController();

        // Creates Elephant Satellite and Desktop Device
        controller.createSatellite("Satellite1", "ElephantSatellite", 91799, Angle.fromDegrees(52));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(90));

        // Create a file on the desktop device
        String msg = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest";
        controller.addFileToDevice("DeviceC", "newFile", msg);

        // Send the file to the elephant satellite and simulate twice so the elephant satellite is now out of range
        assertDoesNotThrow(() -> controller.sendFile("newFile", "DeviceC", "Satellite1"));
        controller.simulate(5);

        // Check the file is there (it should be transient now)
        assertEquals(new FileInfoResponse("newFile", "testtesttesttesttest", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("newFile"));

        // Create a new desktop device and add a new file to it
        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(48));
        controller.addFileToDevice("DeviceA", "newFile2", msg);

        // Send the new file to the elephantSatellite
        assertDoesNotThrow(() -> controller.sendFile("newFile2", "DeviceA", "Satellite1"));

        // Assert that the old file was removed and the new file is present but empty
        assertEquals(new FileInfoResponse("newFile2", "", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("newFile2"));
        assertEquals(null, controller.getInfo("Satellite1").getFiles().get("newFile"));
    }

    @Test
    public void testMultipleTransientFilesRemove() {
        BlackoutController controller = new BlackoutController();

        // Creates Elephant Satellite and Desktop Device
        controller.createSatellite("Elephant", "ElephantSatellite", 91799, Angle.fromDegrees(52));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(90));
        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(90));
        // Create a file on the desktop device
        String msg = "testtesttesttesttesttesttesttesttesttest";
        controller.addFileToDevice("DeviceC", "newFile", msg);
        controller.addFileToDevice("DeviceA", "newFile1", msg);

        // Send the file to the elephant satellite and simulate twice so the elephant satellite is now out of range
        assertDoesNotThrow(() -> controller.sendFile("newFile", "DeviceC", "Elephant"));
        controller.simulate();

        // Check the file is there (it should be transient now)
        assertEquals(new FileInfoResponse("newFile", "testtesttesttesttest", msg.length(), false),
            controller.getInfo("Elephant").getFiles().get("newFile"));
        assertDoesNotThrow(() -> controller.sendFile("newFile1", "DeviceA", "Elephant"));
        controller.simulate();

        // The newFile1 and newFile files should be stored as transient files in the Elephant Satellite
        // They will occupy 80 bytes between them.
        assertEquals(new FileInfoResponse("newFile1", "", msg.length(), false),
        controller.getInfo("Elephant").getFiles().get("newFile1"));
        assertEquals(new FileInfoResponse("newFile", "testtesttesttesttest", msg.length(), false),
        controller.getInfo("Elephant").getFiles().get("newFile"));

        // Send another 40 byte message to Elephant.
        controller.createDevice("DeviceB", "DesktopDevice", Angle.fromDegrees(50));
        controller.addFileToDevice("DeviceB", "newFile2", msg);
        assertDoesNotThrow(() -> controller.sendFile("newFile2", "DeviceB", "Elephant"));

        // It should get rid of the newFile1 as it has no bytes stored
        assertEquals(null, controller.getInfo("Elephant").getFiles().get("newFile1"));
        assertEquals(new FileInfoResponse("newFile", "testtesttesttesttest", msg.length(), false),
        controller.getInfo("Elephant").getFiles().get("newFile"));
        assertEquals(new FileInfoResponse("newFile2", "", msg.length(), false),
        controller.getInfo("Elephant").getFiles().get("newFile2"));

        controller.simulate();
        assertEquals(new FileInfoResponse("newFile2", "testtesttesttesttest", msg.length(), false),
        controller.getInfo("Elephant").getFiles().get("newFile2"));

        // Send another 40 byte message to elephant
        controller.addFileToDevice("DeviceB", "newFile3", msg);
        assertDoesNotThrow(() -> controller.sendFile("newFile3", "DeviceB", "Elephant"));
        // It should get rid of the newFile as it has no bytes stored
        assertEquals(null, controller.getInfo("Elephant").getFiles().get("newFile1"));
        assertEquals(null, controller.getInfo("Elephant").getFiles().get("newFile"));
        assertEquals(new FileInfoResponse("newFile3", "", msg.length(), false),
        controller.getInfo("Elephant").getFiles().get("newFile3"));

        // Since we have two non-transient files, both should increment by 10 bytes
        controller.simulate();
        assertEquals(new FileInfoResponse("newFile3", "testtestte", msg.length(), false),
        controller.getInfo("Elephant").getFiles().get("newFile3"));
        assertEquals(new FileInfoResponse("newFile2", "testtesttesttesttesttesttestte", msg.length(), false),
        controller.getInfo("Elephant").getFiles().get("newFile2"));
    }

    @Test
    public void testNormalSend() {
        BlackoutController controller = new BlackoutController();

        // Creates Elephant Satellite and Desktop Device
        controller.createSatellite("Elephant", "ElephantSatellite", 91799, Angle.fromDegrees(90));
        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(90));
        String msg = "testtesttesttesttesttesttesttesttesttest";
        controller.addFileToDevice("DeviceA", "newFile", msg);

        assertDoesNotThrow(() -> controller.sendFile("newFile", "DeviceA", "Elephant"));
        controller.simulate(4);
        assertEquals(new FileInfoResponse("newFile", msg, msg.length(), true),
            controller.getInfo("Elephant").getFiles().get("newFile"));
        controller.createSatellite("Standard", "StandardSatellite", 91799, Angle.fromDegrees(90));
        assertDoesNotThrow(() -> controller.sendFile("newFile", "Elephant", "Standard"));
        assertEquals(new FileInfoResponse("newFile", "", msg.length(), false),
        controller.getInfo("Standard").getFiles().get("newFile"));
        controller.simulate(1);
        assertEquals(new FileInfoResponse("newFile", "t", msg.length(), false),
        controller.getInfo("Standard").getFiles().get("newFile"));
    }

    @Test
    public void testElephantCannotMakeRoom() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Elephant", "ElephantSatellite", 91799, Angle.fromDegrees(90));
        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(90));
        String msg = "testtesttesttesttesttesttesttesttesttest";
        controller.addFileToDevice("DeviceA", "newFile", msg);
        controller.addFileToDevice("DeviceA", "newFile1", msg);
        controller.addFileToDevice("DeviceA", "newFile2", msg);
        controller.addFileToDevice("DeviceA", "newFile3", msg);
        assertThrows(VirtualFileNoStorageSpaceException.class, () -> {
            controller.sendFile("newFile", "DeviceA", "Elephant");
            controller.sendFile("newFile1", "DeviceA", "Elephant");
            controller.sendFile("newFile2", "DeviceA", "Elephant");
        });
    }
}
