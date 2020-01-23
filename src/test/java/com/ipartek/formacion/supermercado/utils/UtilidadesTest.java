package com.ipartek.formacion.supermercado.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class UtilidadesTest {

	@Test
	public void testObtenerId() throws Exception {

		assertEquals(-1, Utilidades.obtenerId(null));

		assertEquals(-1, Utilidades.obtenerId("/"));

		assertEquals(2, Utilidades.obtenerId("/2"));
		assertEquals(2, Utilidades.obtenerId("/2/"));
		assertEquals(99, Utilidades.obtenerId("/99"));

		// assertEquals(new Exception("url mal formada"),
		// Utilidades.obtenerId("/99/333/hola/"));
//		assertEquals(2, Utilidades.obtenerId("/99/333/hola/"));
//		fail("Deberia haber lanzado execpcion");

		try {
			assertEquals(2, Utilidades.obtenerId("/99/333/hola"));
			assertEquals(-1, Utilidades.obtenerId("/pepe"));
			assertEquals(-1, Utilidades.obtenerId("/pepe/"));
			fail("Deberia haber lanzado execpcion");
		} catch (Exception e) {
			assertTrue(true);
		}

	}

}
