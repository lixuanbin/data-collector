/**
 * 
 */
package co.speedar.data.collector.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import co.speedar.data.collector.service.DataCollectService;

/**
 * @author ben
 *
 */
@WebServlet(urlPatterns = { "/query.do" }, loadOnStartup = 1)
public class QueryDataServlet extends HttpServlet {
	private static final long serialVersionUID = 5977484050293191582L;
	protected static final Logger log = Logger.getLogger(QueryDataServlet.class);
	@Autowired
	private DataCollectService service;

	/**
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (service.isValidRequest(request)) {
			PrintWriter writer = response.getWriter();
			writer.print(service.queryAll());
			writer.flush();
			writer.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
