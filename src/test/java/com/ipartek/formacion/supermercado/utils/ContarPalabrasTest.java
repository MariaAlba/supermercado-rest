package com.ipartek.formacion.supermercado.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContarPalabrasTest {

	@Test
	public void test1() {
		assertEquals(0, Utilidades.contarPalabras(null));
		assertEquals(0, Utilidades.contarPalabras(""));
		assertEquals(0, Utilidades.contarPalabras("           "));
		assertEquals(2, Utilidades.contarPalabras("hola caracola"));
		assertEquals(2, Utilidades.contarPalabras("hola                    caracola"));
	}

	@Test
	public void test2() {
		assertEquals(2, Utilidades.contarPalabras("hola,............?caracola"));
		assertEquals(2, Utilidades.contarPalabras("hola ,                   caracola"));
	}

}
