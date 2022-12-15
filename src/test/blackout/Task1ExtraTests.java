package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task1ExtraTests {
    @Test
    public void testExample() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(115));
        controller.createDevice("FirstLaptop", "LaptopDevice", Angle.fromDegrees(115));
        controller.addFileToDevice("FirstLaptop", "FirstFile", "Hello My name is Vincent");
        assertDoesNotThrow(() -> {
            controller.sendFile("FirstFile", "FirstLaptop", "Satellite1");
        });
    }

    @Test
    public void testNormalSatelliteMovement() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(115));
        controller.simulate(1000);
    }
}
