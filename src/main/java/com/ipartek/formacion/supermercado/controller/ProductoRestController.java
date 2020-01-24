package com.ipartek.formacion.supermercado.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.ipartek.formacion.supermercado.modelo.dao.ProductoDAO;
import com.ipartek.formacion.supermercado.modelo.pojo.Producto;
import com.ipartek.formacion.supermercado.pojo.ResponseMensaje;
import com.ipartek.formacion.supermercado.utils.Utilidades;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

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
	private Object reponseBody;

	private int idProducto;

	private ValidatorFactory factory;
	private Validator validator;

	private Boolean listarOrdenado;
	private String orden;
	private String criterio;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		productoDao = ProductoDAO.getInstance();
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		productoDao = null;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		listarOrdenado = false;
		LOG.debug(request.getMethod() + " " + request.getRequestURL());

		orden = request.getParameter("_order");
		criterio = request.getParameter("columna");
		if (orden != null && criterio != null) {
			listarOrdenado = true;
		}

//		Map<String, String[]> parametros = request.getParameterMap();
//		for (String s : parametros.keySet()) {
//			request.getParameter(s);
//		}
//		parametros.values();
//		parametros.keySet();

		// prepara la response
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		reponseBody = null;
		pathInfo = request.getPathInfo();

		try {

			idProducto = Utilidades.obtenerId(pathInfo);

			// llama a doGEt, doPost, doPut, doDelete
			super.service(request, response);

		} catch (Exception e) {

			statusCode = HttpServletResponse.SC_BAD_REQUEST;
			reponseBody = new ResponseMensaje(e.getMessage());

		} finally {

			response.setStatus(statusCode);

			if (reponseBody != null) {
				// response body
				PrintWriter out = response.getWriter(); // out se encarga de poder escribir datos en el body
				String jsonResponseBody = new Gson().toJson(reponseBody); // conversion de Java a Json
				out.print(jsonResponseBody.toString());
				out.flush();
			}
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (idProducto != -1) {
			detalle(idProducto);
		} else {

			if (listarOrdenado == true)
				listarporOrden(orden, criterio);
			else
				listar();
		}

	}

	private void listarporOrden(String forma, String columna) {
		ArrayList<Producto> productos = (ArrayList<Producto>) productoDao.getAllOrderBy(forma, columna);
		reponseBody = productos;
		if (productos.isEmpty()) {
			statusCode = HttpServletResponse.SC_NO_CONTENT;
		} else {
			statusCode = HttpServletResponse.SC_OK;
		}
	}

	private void detalle(int id) {

		reponseBody = productoDao.getById(id);
		if (reponseBody != null) {
			statusCode = HttpServletResponse.SC_OK;
		} else {
			reponseBody = null;
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
	}

	private void listar() {

		ArrayList<Producto> productos = (ArrayList<Producto>) productoDao.getAll();
		reponseBody = productos;
		if (productos.isEmpty()) {
			statusCode = HttpServletResponse.SC_NO_CONTENT;
		} else {
			statusCode = HttpServletResponse.SC_OK;
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			Producto pEliminado = productoDao.delete(idProducto);
			statusCode = HttpServletResponse.SC_OK;
			reponseBody = pEliminado;

		} catch (Exception e) {

			statusCode = HttpServletResponse.SC_NOT_FOUND;
			reponseBody = new ResponseMensaje(e.getMessage());
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Producto pOut = null;

		try {

			// convertir json del request body a Objeto
			BufferedReader reader = request.getReader();
			Gson gson = new Gson();
			Producto producto = gson.fromJson(reader, Producto.class);
			LOG.debug(" Json convertido a Objeto: " + producto);

			// validar objeto
			Set<ConstraintViolation<Producto>> validacionesErrores = validator.validate(producto);
			if (validacionesErrores.isEmpty()) {

				if (producto.getId() == 0) {
					pOut = productoDao.create(producto);
					statusCode = HttpServletResponse.SC_CREATED;
				} else {
					pOut = productoDao.update(idProducto, producto);
					statusCode = HttpServletResponse.SC_OK;
				}
				reponseBody = pOut;

			} else {

				statusCode = HttpServletResponse.SC_BAD_REQUEST;
				ResponseMensaje responseMensaje = new ResponseMensaje("valores no correctos");
				ArrayList<String> errores = new ArrayList<String>();
				for (ConstraintViolation<Producto> error : validacionesErrores) {
					errores.add(error.getPropertyPath() + " " + error.getMessage());
				}
				responseMensaje.setErrores(errores);
				reponseBody = responseMensaje;

			}

		} catch (MySQLIntegrityConstraintViolationException e) {

			statusCode = HttpServletResponse.SC_CONFLICT;
			reponseBody = new ResponseMensaje("nombre de producto repetido");

		} catch (Exception e) {

			statusCode = HttpServletResponse.SC_BAD_REQUEST;
			reponseBody = new ResponseMensaje(e.getMessage());
		}

	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.debug("PUT modificar recurso");
		doPost(request, response);
	}

	// protected void doPut(HttpServletRequest request, HttpServletResponse
	// response)
//			throws ServletException, IOException {
//
//		LOG.debug("PUT editar recurso");
//
//		int id = 0;
//		Producto productoOriginal = null;
//
//		try {
//			id = Utilidades.obtenerId(pathInfo);
//			if (id != -1) {
//				productoOriginal = productoDao.getById(id);
//			}
//
//			if (productoOriginal == null) {
//				statusCode = HttpServletResponse.SC_NOT_FOUND;
//				jsonResponseBody = new Gson().toJson(new ResponseMensaje("Recurso no encontrado"));
//			} else {
//
//				// convertir json del request body a Objeto
//				BufferedReader reader = request.getReader();
//				Gson gson = new Gson();
//				Producto productoModificado = gson.fromJson(reader, Producto.class);
//
//				LOG.debug(" Json convertido a Objeto: " + productoModificado);
//
//				productoDao.update(id, productoModificado);
//				statusCode = HttpServletResponse.SC_OK;
//				jsonResponseBody = new Gson().toJson(productoModificado);
//
//			}
//
//		} catch (Exception e) {
//			LOG.debug(e);
//			statusCode = HttpServletResponse.SC_CONFLICT;
//			jsonResponseBody = new Gson().toJson(new ResponseMensaje(e.getMessage()));
//		}
//
//	}

}
