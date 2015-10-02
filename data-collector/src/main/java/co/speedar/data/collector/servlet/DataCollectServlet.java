/**
 * 
 */
package co.speedar.data.collector.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import co.speedar.data.collector.domain.UploadData;
import co.speedar.data.collector.domain.UploadResult;
import co.speedar.data.collector.service.DataCollectService;

/**
 * @author ben
 *
 */
@WebServlet(urlPatterns = { "/upload" }, loadOnStartup = 1)
public class DataCollectServlet extends HttpServlet {
	private static final long serialVersionUID = 5977484050293191582L;
	protected static final Logger log = Logger.getLogger(DataCollectServlet.class);
	@Autowired
	private DataCollectService service;

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	private UploadData buildDataFromRequest(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		String mid = request.getParameter("mid");
		String eid = request.getParameter("eid");
		UploadData data = new UploadData();
		data.setEid(eid);
		data.setIp(ip);
		data.setMid(mid);
		data.setReportTime(new Date());
		return data;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (service.isValidRequest(request)) {
			UploadData data = buildDataFromRequest(request);
			UploadResult result = service.addData(data);
			String json = mapper.writeValueAsString(result);
			if (StringUtils.isNotBlank(request.getParameter("callback"))) {
				// deal with jsonp
				StringBuffer sb = new StringBuffer();
				sb.append(request.getParameter("callback"));
				sb.append("(");
				sb.append(json);
				sb.append(");");
				json = sb.toString();
				response.setContentType("text/javascript;charset=UTF-8");
			}
			PrintWriter writer = response.getWriter();
			writer.print(json);
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
