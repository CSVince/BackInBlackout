package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class RelaySatelliteTests {
    @Test
    public void testAboveThreshhold() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Relay", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(350));
        controller.simulate(5);
        Angle firstPosition = controller.getInfo("Relay").getPosition();
        assertTrue(firstPosition.compareTo(Angle.fromDegrees(350)) == 1);
    }

    @Test
    public void testBelowThreshhold() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Relay", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        controller.simulate(5);
        Angle firstPosition = controller.getInfo("Relay").getPosition();
        assertTrue(firstPosition.compareTo(Angle.fromDegrees(340)) == -1);
    }

    @Test
    public void testOnThreshhold() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Relay", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(345));
        controller.simulate(5);
        Angle firstPosition = controller.getInfo("Relay").getPosition();
        assertTrue(firstPosition.compareTo(Angle.fromDegrees(345)) == 1);
    }
}
