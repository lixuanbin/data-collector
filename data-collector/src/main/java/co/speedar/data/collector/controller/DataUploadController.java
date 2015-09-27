/**
 * 
 */
package co.speedar.data.collector.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import co.speedar.data.collector.domain.UploadData;
import co.speedar.data.collector.domain.UploadResult;
import co.speedar.data.collector.service.DataCollectService;

/**
 * @author ben
 *
 */
@Controller
public class DataUploadController {
	@Autowired
	private DataCollectService service;

	@RequestMapping(value = "upload.do", method = RequestMethod.GET)
	public @ResponseBody UploadResult upload(@RequestParam String channel, @RequestParam String eid,
			@RequestParam String mid, HttpServletRequest request) {
		UploadData data = new UploadData();
		data.setChannel(channel);
		data.setEid(eid);
		data.setIp(request.getRemoteAddr());
		data.setMid(mid);
		data.setReportTime(new Date());
		UploadResult result = service.addData(data);
		return result;
	}
}
