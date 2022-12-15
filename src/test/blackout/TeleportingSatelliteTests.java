package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class TeleportingSatelliteTests {
    @Test
    public void testTeleportingSatelliteTeleportsWhileReceivingFromDevice() throws FileTransferException {
        // Create a device and a teleporting satellite
        BlackoutController controller = new BlackoutController();
        controller.createDevice("hi", "HandheldDevice", Angle.fromDegrees(180));
        controller.createSatellite("Satellite1", "TeleportingSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(179.5));
        // Add file to device and send it to the teleporting satellite
        controller.addFileToDevice("hi", "file1", "att");
        controller.sendFile("file1", "hi", "Satellite1");
        // Assert that the file is ready to send
        assertEquals(new FileInfoResponse("file1", "", "att".length(), false),
            controller.getInfo("Satellite1").getFiles().get("file1"));
        controller.simulate(5);
        // Assert that the file has been removed from the satellite
        assertEquals(null, controller.getInfo("Satellite1").getFiles().get("file1"));
        // Assert that the file has had its t's removed from the device
        assertEquals(new FileInfoResponse("file1", "a", "a".length(), true),
            controller.getInfo("hi").getFiles().get("file1"));
    }

    @Test
    public void testTeleportingSatelliteTeleportsWhileReceivingFromSatellite() throws FileTransferException {
        // Create a device and a teleporting satellite
        BlackoutController controller = new BlackoutController();
        controller.createDevice("hi", "HandheldDevice", Angle.fromDegrees(180));
        controller.createSatellite("Satellite1", "StandardSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        // Add file to device and send it to the teleporting satellite
        controller.addFileToDevice("hi", "file1", "att");
        controller.sendFile("file1", "hi", "Satellite1");
        controller.simulate(3);
        // Assert that the file has finished sending
        assertEquals(new FileInfoResponse("file1", "att", "att".length(), true),
            controller.getInfo("hi").getFiles().get("file1"));
        // Create a new teleporting satellite
        controller.createSatellite("Satellite2", "TeleportingSatellite",
            10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(179));
        // Send file from standard satellite to teleporting satellite
        controller.sendFile("file1", "Satellite1", "Satellite2");
        // Assert that the file is ready to send
        assertEquals(new FileInfoResponse("file1", "", "att".length(), false),
            controller.getInfo("Satellite2").getFiles().get("file1"));
        assertDoesNotThrow(() -> {
            controller.simulate(10);
        });
        // By this point the teleporting satellite should have teleported.
        // Assert that the file was downloaded with missing ts
        assertEquals(new FileInfoResponse("file1", "a", "a".length(), true),
            controller.getInfo("Satellite2").getFiles().get("file1"));
    }

    @Test
    public void testTeleportingSatelliteTeleportsWhileSending() throws FileTransferException {
        // Create a device and a teleporting satellite
        BlackoutController controller = new BlackoutController();
        controller.createDevice("hi", "HandheldDevice", Angle.fromDegrees(180));
        controller.createSatellite("Satellite1", "TeleportingSatellite",
            1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        // Add file to device and send it to the teleporting satellite
        controller.addFileToDevice("hi", "file1", "testingtestingtesting");
        controller.sendFile("file1", "hi", "Satellite1");
        controller.simulate(3);
        // Assert that the file has finished sending
        assertEquals(new FileInfoResponse("file1", "testingtestingtesting", "testingtestingtesting".length(), true),
            controller.getInfo("hi").getFiles().get("file1"));
        // Create a new standard satellite
        controller.createSatellite("Satellite2", "StandardSatellite",
            1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        // Send file from teleporting satellite to standard satellite
        controller.sendFile("file1", "Satellite1", "Satellite2");
        assertDoesNotThrow(() -> {
            controller.simulate(3);
        });
        assertEquals(new FileInfoResponse("file1", "tes", "testingtestingtesting".length(), false),
            controller.getInfo("Satellite2").getFiles().get("file1"));
        assertDoesNotThrow(() -> {
            controller.simulate(10);
        });
        // By this point the teleporting satellite should have teleported.
        // Assert that the file was downloaded with missing ts
        assertEquals(new FileInfoResponse("file1", "tesingesingesing", "tesingesingesing".length(), true),
            controller.getInfo("Satellite2").getFiles().get("file1"));
    }

    @Test
    public void testTeleportingGoesOutOfRangeNormally() {
        // Create a device and a teleporting satellite
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device", "HandheldDevice", Angle.fromDegrees(0));
        controller.createSatellite("Teleporting", "TeleportingSatellite",
            1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        String msg = "This is quite a long message and I need it to be for the test.";
        controller.addFileToDevice("Device", "File", msg);
        assertDoesNotThrow(() -> controller.sendFile("File", "Device", "Teleporting"));
        controller.simulate(7);
        assertEquals(new FileInfoResponse("File", msg, msg.length(), true),
        controller.getInfo("Teleporting").getFiles().get("File"));
        // Create a satellite just in range of the Teleporting Satellite
        controller.createSatellite("Standard", "StandardSatellite",
            1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(350));
        assertDoesNotThrow(() -> controller.sendFile("File", "Teleporting", "Standard"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File", "T", msg.length(), false),
        controller.getInfo("Standard").getFiles().get("File"));

        // The satellite should now be out of range and the file on the StandardSatellite should be gone.
        controller.simulate();
        assertEquals(null, controller.getInfo("Standard").getFiles().get("File"));
        assertEquals(new FileInfoResponse("File", msg, msg.length(), true),
        controller.getInfo("Teleporting").getFiles().get("File"));
    }

    @Test
    public void testTeleportingTwice() {
        // Test for expected teleportation movement behaviour
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(0));

        controller.simulate();
        Angle clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
        controller.simulate();
        Angle clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == 1);

        // It should take 250 simulations to reach theta = 180.
        // Simulate until Satellite1 reaches theta=180
        controller.simulate(250);

        // Verify that Satellite1 is now at theta=0
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);
        // It should now move anti-clockwise
        controller.simulate();
        Angle clockwiseOnThirdMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(clockwiseOnThirdMovement.compareTo(Angle.fromDegrees(360)) == -1);
        controller.simulate(251);
        // Verify it is back at theta=0
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);
    }
}
