/**
 * 
 */
package co.speedar.data.collector.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ben
 *
 */
public class UploadData implements Serializable {
	private static final long serialVersionUID = -4399094056867364565L;
	private String ip;
	private String channel;
	private String mid;
	private String eid;
	private Date reportTime;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public Date getReportTime() {
		return reportTime;
	}

	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
}
