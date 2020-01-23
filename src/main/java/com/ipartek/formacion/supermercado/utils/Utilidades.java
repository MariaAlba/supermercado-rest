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

}
