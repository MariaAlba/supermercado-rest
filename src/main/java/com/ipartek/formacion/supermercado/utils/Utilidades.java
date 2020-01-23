package com.ipartek.formacion.supermercado.utils;

public class Utilidades {

	/**
	 * Obtenenos el id del pathInfo o uri
	 * 
	 * @param pathInfo parte de la uri donde debemos buscar un numero
	 * @return numero id
	 * @throws Exception si el pathInfo esta mal formado
	 * 
	 *                   ejemplos:
	 *                   <ul>
	 *                   <li>/ pathInfo valido</li>
	 *                   <li>/2 pathInfo valido</li>
	 *                   <li>/2/ pathInfo valido</li>
	 *                   <li>/2/2 pathInfo esta mal formado</li>
	 *                   <li>/2/2/otracosa/34 pathInfo mal formado</li>
	 *                   </ul>
	 * 
	 */
	public static int obtenerId(String pathInfo) throws Exception {
		// throw new Exception("Sin implementar, primero Test!!!");
		int id = -1;

		if (pathInfo == null || "/".equals(pathInfo)) {
			id = -1;
		} else {

			String[] pathInfoParts = pathInfo.split("/");

			if (pathInfoParts.length > 2) {
				throw new Exception("url mal formada");
			}

			if (!pathInfoParts[1].matches("\\d+")) {
				throw new Exception("url mal formada");
			} else {
				id = Integer.parseInt(pathInfoParts[1]);
			}
		}

		return id;
	}

	/**
	 * Obtenemos el numero de palabras que tiene la frase
	 * 
	 * @param frase de la que hay que contar las palabras
	 * @return numero de palabras ó 0 si es null o cadena vacia o cadena con spacios
	 *         en blanco
	 */
	public static int contarPalabras(String frase) {

		int result = 0;

		if (frase != null) {

			if (frase.matches("\\s+") || frase.matches("")) {
				result = 0;
			} else {
				frase = frase.replaceAll("\\W+", " ");
				String[] partes = frase.split("\\s+");
				result = partes.length;
			}

		}

		return result;

	}
}
