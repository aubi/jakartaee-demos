package net.aubrecht.mandelbrot.picture.service;

import javax.naming.NamingException;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author aubi
 */
@ExtendWith(WeldJunit5Extension.class)
public class MandelbrotPictureServiceTest {

    public MandelbrotPictureServiceTest() {
    }

    @BeforeAll
    public static void setUpClass() {
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

    @Test
    public void testFewManderlbrotPoints() {
        MandelbrotPictureService service = new MandelbrotPictureService();
        Assertions.assertEquals(0d, service.calcMandelPoint(0, 0, 0, 0));
        Assertions.assertEquals(500d, service.calcMandelPoint(0, 0, 500, 4));
    }
}
