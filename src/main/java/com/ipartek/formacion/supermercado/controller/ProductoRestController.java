package com.ipartek.formacion.supermercado.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.ipartek.formacion.supermercado.modelo.dao.ProductoDAO;
import com.ipartek.formacion.supermercado.modelo.pojo.Producto;

/**
 * Servlet implementation class ProductoRestController
 */
@WebServlet({ "/producto/*", "/producto" })
public class ProductoRestController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Logger.getLogger(ProductoRestController.class);

	private ProductoDAO productoDao;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// Inicializamos dao para poder hacer peticiones a bbdd
		productoDao = ProductoDAO.getInstance();
	}

	@Override
	public void destroy() {
		// Destruimos el dao
		productoDao = null;

	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// lama al resto e metodos doGet doPost doDelete doPut
		super.service(request, response);

		// Preparar la response: la cabecera

		// Content type
		response.setContentType("application/json");

		// Charset
		response.setCharacterEncoding("utf-8");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		LOG.trace("peticion GET");

		String pathInfo = request.getPathInfo();

		LOG.debug("mirar pathInfo:" + pathInfo + " para saber si es listado o detalle");

		if (pathInfo == null || "/".equals(pathInfo)) {
			listar(request, response);
		} else {
			detalle(request, response);
		}

	}

	private void listar(HttpServletRequest request, HttpServletResponse response) throws IOException {

		LOG.trace("entra en listar");

		// Obtener productos de la BD
		ArrayList<Producto> lista = (ArrayList<Producto>) productoDao.getAll();

		// dejo la lista vacia si quiero que me ddevuelva 2014
		// ArrayList<Producto> lista = new ArrayList<Producto>();

		// Preparar la response: Content type y charset y responsebody

		// Content type
		response.setContentType("application/json");

		// Charset
		response.setCharacterEncoding("utf-8");

		if (lista.isEmpty()) {
			response.setStatus(204);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			// response.setStatus(200);//esto es lo mismo que lo de abajo
		}

		// response body

		PrintWriter out = response.getWriter(); // out se encarga de escribir datos en el body

		String jsonResponseBody = new Gson().toJson(lista); // conversion de java a json

		out.print(jsonResponseBody.toString());

		out.flush(); // termina de escribir datos en body cierra el out

	}

	private void detalle(HttpServletRequest request, HttpServletResponse response) throws IOException {

		LOG.trace("Entra en detalle");

		// Obtener producto de la BD por id
		ArrayList<Producto> lista = (ArrayList<Producto>) productoDao.getAll();

		// dejo la lista vacia si quiero que me ddevuelva 2014
		// ArrayList<Producto> lista = new ArrayList<Producto>();

		// Preparar la response: Content type y charset y responsebody

		// Content type
		response.setContentType("application/json");

		// Charset
		response.setCharacterEncoding("utf-8");

		if (lista.isEmpty()) {
			response.setStatus(204);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			// response.setStatus(200);//esto es lo mismo que lo de abajo
		}

		// se pone primero el status y luego se pinta la respuesta (si se pinta por
		// defecto devuelve 200)
		// response body

		PrintWriter out = response.getWriter(); // out se encarga de escribir datos en el body

		String jsonResponseBody = new Gson().toJson(lista); // conversion de java a json

		out.print(jsonResponseBody.toString());

		out.flush(); // termina de escribir datos en body cierra el out

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
