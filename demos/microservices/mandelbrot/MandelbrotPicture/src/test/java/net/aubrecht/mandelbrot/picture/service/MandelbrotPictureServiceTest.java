package net.aubrecht.mandelbrot.picture.service;

import javax.naming.NamingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * Verify calculations of some points.
 *
 * @author Petr Aubrecht
 */
//@ExtendWith(WeldJunit5Extension.class)
public class MandelbrotPictureServiceTest {
    private static MandelbrotPictureService service;

    public MandelbrotPictureServiceTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        service = new MandelbrotPictureService();
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws NamingException {
    }

    @AfterEach
    public void tearDown() {
    }

    @ParameterizedTest
    @ArgumentsSource(MandelbrotTestArgumentProvider.class)
    public void testFewManderlbrotPoints(MandelbrotPointArgument arg) {
        System.out.println("Testing point: " + arg);
        Assertions.assertEquals(arg.getResult(), service.calcMandelPoint(arg.getX(), arg.getY(), arg.getIterations(), arg.getBailout()));
    }
}
