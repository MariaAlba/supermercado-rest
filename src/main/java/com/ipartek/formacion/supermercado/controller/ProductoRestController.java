package com.ipartek.formacion.supermercado.controller;

import java.io.BufferedReader;
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
import com.ipartek.formacion.supermercado.pojo.ResponseMensaje;
import com.ipartek.formacion.supermercado.utils.Utilidades;

/**
 * Servlet implementation class ProductoRestController
 */
@WebServlet({ "/producto/*", "/producto" })
public class ProductoRestController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Logger.getLogger(ProductoRestController.class);

	private ProductoDAO productoDao;

	// TODO respuesta de error cuando ocurre error

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

		// RESPONSE: cabecera (la ponemos aquí porque se va a repetir en todos los
		// métodos)

		// Content type
		response.setContentType("application/json");

		// Charset
		response.setCharacterEncoding("utf-8");

		// lama al resto e metodos doGet doPost doDelete doPut
		super.service(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		LOG.trace("peticion GET");

		String pathInfo = request.getPathInfo();

		LOG.debug("pathInfo:" + pathInfo + " para saber si es listado o detalle");

		if (pathInfo == null || "/".equals(pathInfo)) {
			listar(request, response);
		} else {
			detalle(request, response, pathInfo);
		}

	}

	private void listar(HttpServletRequest request, HttpServletResponse response) throws IOException {

		LOG.trace("entra en listar");

		// Obtener productos de la BD
		ArrayList<Producto> lista = (ArrayList<Producto>) productoDao.getAll();

		// dejo la lista vacia si quiero que me ddevuelva 2014
		// ArrayList<Producto> lista = new ArrayList<Producto>();

		// hay que poner el status antes de escribir la response body (si no no lo pilla
		// bien)
		if (lista.isEmpty()) {
			response.setStatus(204);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
		}

		// response body

		PrintWriter out = response.getWriter(); // out se encarga de escribir datos en el body

		String jsonResponseBody = new Gson().toJson(lista); // conversion de java a json

		out.print(jsonResponseBody.toString());

		out.flush(); // termina de escribir datos en body cierra el out

	}

	private void detalle(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {

		LOG.trace("Entra en detalle");

		int id;
		Producto producto = null;
		String jsonResponseBody = "";
		PrintWriter out = response.getWriter(); // out se encarga de escribir datos en el body

		try {
			id = Utilidades.obtenerId(pathInfo);

			if (id != -1) { // detalle
				producto = productoDao.getById(id);
			}

			if (producto == null) {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				jsonResponseBody = new Gson().toJson(new ResponseMensaje("no se encuentra producto"));
			} else {
				response.setStatus(HttpServletResponse.SC_OK);// response.setStatus(200);//esto es lo mismo
				jsonResponseBody = new Gson().toJson(producto); // conversion de java a json
			}
		} catch (Exception e) {
			LOG.error(e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			jsonResponseBody = new Gson().toJson(new ResponseMensaje("Peticion incorrecta"));
		} finally { // response body

			out.print(jsonResponseBody.toString());
			out.flush(); // termina de escribir datos en body cierra el out
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		LOG.debug("POST crear recurso");
		String jsonResponseBody = null;

		// convertir json del request body a Objeto
		BufferedReader reader = request.getReader();
		Gson gson = new Gson();
		Producto producto = gson.fromJson(reader, Producto.class);
		LOG.debug(" Json convertido a Objeto: " + producto);

		try {
			productoDao.create(producto);
		} catch (Exception e) {
			LOG.debug(e);
			jsonResponseBody = new Gson().toJson(new ResponseMensaje("A pikar kodigo"));
		} finally {

			PrintWriter out = response.getWriter();
			out.print(jsonResponseBody.toString());
			out.flush();
		}

		response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);

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
