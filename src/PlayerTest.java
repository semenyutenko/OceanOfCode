import org.junit.Assert;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @org.junit.jupiter.api.Test
    void main() {
        String orderA = "TORPEDO TRIGGER SCANNER";
        String orderB = "MOVE TRIGGER SCANNER";
        String orderC = "TORPEDO SILENCE SCANNER";
        String orderD = "MOVE SILENCE SCANNER";
        Assert.assertFalse(orderA.contains("TORPEDO") ^ orderA.contains("TRIGGER"));
        Assert.assertTrue(orderB.contains("TORPEDO") ^ orderB.contains("TRIGGER"));
        Assert.assertTrue(orderC.contains("TORPEDO") ^ orderC.contains("TRIGGER"));
        Assert.assertFalse(orderD.contains("TORPEDO") ^ orderD.contains("TRIGGER"));
    }
}