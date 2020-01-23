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

	private String pathInfo;
	private int statusCode;
	private String jsonResponseBody;

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

		// cuerpo respuesta
		jsonResponseBody = null;

		pathInfo = request.getPathInfo();

		LOG.debug("mirar pathInfo:" + pathInfo + " para saber si es listado o detalle");

		// lama al resto e metodos doGet doPost doDelete doPut
		super.service(request, response);

		LOG.debug("ahora entra en service despues de metodo que toke");

		response.setStatus(statusCode); // response status

		PrintWriter out = response.getWriter(); // out se encarga de escribir datos en el body

		out.print(jsonResponseBody.toString());

		out.flush(); // termina de escribir datos en body cierra el out
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		LOG.trace("peticion GET");

		if (pathInfo == null || "/".equals(pathInfo)) { // listar(request, response);

			LOG.trace("entra en listar");

			// Obtener productos de la BD
			ArrayList<Producto> lista = (ArrayList<Producto>) productoDao.getAll();

			// dejo la lista vacia si quiero que me ddevuelva 2014
			// ArrayList<Producto> lista = new ArrayList<Producto>();

			// hay que poner el status antes de escribir la response body
			statusCode = (lista.isEmpty()) ? HttpServletResponse.SC_NO_CONTENT : HttpServletResponse.SC_OK;
			;

			// response
			jsonResponseBody = new Gson().toJson(lista); // conversion de java a json
		} else {

			// detalle(request, response);
			LOG.trace("Entra en detalle");

			int id;
			Producto producto = null;

			try {
				id = Utilidades.obtenerId(pathInfo);

				if (id != -1) { // detalle
					producto = productoDao.getById(id);
				}

				if (producto == null) {
					statusCode = HttpServletResponse.SC_NOT_FOUND;
					jsonResponseBody = new Gson().toJson(new ResponseMensaje("no se encuentra producto"));
				} else {
					statusCode = HttpServletResponse.SC_OK; // response.setStatus(200);//esto es lo mismo
					jsonResponseBody = new Gson().toJson(producto); // conversion de java a json
				}
			} catch (Exception e) {
				LOG.error(e);
				statusCode = HttpServletResponse.SC_BAD_REQUEST;
				jsonResponseBody = new Gson().toJson(new ResponseMensaje(e.getMessage()));
			}
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		LOG.debug("POST crear recurso");

		// convertir json del request body a Objeto
		BufferedReader reader = request.getReader();
		Gson gson = new Gson();
		Producto producto = gson.fromJson(reader, Producto.class);
		LOG.debug(" Json convertido a Objeto: " + producto);

		try {
			productoDao.create(producto);
			statusCode = HttpServletResponse.SC_CREATED;
			jsonResponseBody = new Gson().toJson(producto);
		} catch (Exception e) {
			LOG.debug(e);
			statusCode = HttpServletResponse.SC_CONFLICT;
			jsonResponseBody = new Gson().toJson(new ResponseMensaje(e.getMessage()));
		}

	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		LOG.debug("PUT editar recurso");

		int id = 0;
		Producto productoOriginal = null;

		try {
			id = Utilidades.obtenerId(pathInfo);
			if (id != -1) {
				productoOriginal = productoDao.getById(id);
			}

			if (productoOriginal == null) {
				statusCode = HttpServletResponse.SC_NOT_FOUND;
				jsonResponseBody = new Gson().toJson(new ResponseMensaje("Recurso no encontrado"));
			} else {

				// convertir json del request body a Objeto
				BufferedReader reader = request.getReader();
				Gson gson = new Gson();
				Producto productoModificado = gson.fromJson(reader, Producto.class);

				LOG.debug(" Json convertido a Objeto: " + productoModificado);

				productoDao.update(id, productoModificado);
				statusCode = HttpServletResponse.SC_OK;
				jsonResponseBody = new Gson().toJson(productoModificado);

			}

		} catch (Exception e) {
			LOG.debug(e);
			statusCode = HttpServletResponse.SC_CONFLICT;
			jsonResponseBody = new Gson().toJson(new ResponseMensaje(e.getMessage()));
		}

	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		LOG.debug("DELETE recurso por id");

		Producto producto = null;
		int id = 0;

		try {
			id = Utilidades.obtenerId(pathInfo);

			if (id != -1) { // detalle
				producto = productoDao.getById(id);
			}

			if (producto == null) {
				statusCode = HttpServletResponse.SC_NOT_FOUND;
				jsonResponseBody = new Gson().toJson(new ResponseMensaje("No se encuentra el recurso"));
			} else {
				productoDao.delete(id);
				statusCode = HttpServletResponse.SC_OK;
				jsonResponseBody = new Gson().toJson(new ResponseMensaje(producto + " eliminado"));
			}
		} catch (Exception e) {
			LOG.error(e);
			statusCode = HttpServletResponse.SC_BAD_REQUEST;
			jsonResponseBody = new Gson().toJson(new ResponseMensaje(e.getMessage()));
		}

	}

}
