/**
 * 
 */
package co.speedar.data.collector.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import co.speedar.data.collector.domain.UploadData;
import co.speedar.data.collector.domain.UploadResult;

/**
 * @author ben
 *
 */
@Service
public class DataCollectService {
	public static final int capacity = 36000000;
	protected static final Logger log = Logger.getLogger(DataCollectService.class);
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private final Lock readLock = readWriteLock.readLock();
	private final Lock writeLock = readWriteLock.writeLock();
	private final Queue<UploadData> cache = new ArrayBlockingQueue<UploadData>(capacity);
	private final ObjectMapper mapper;

	public DataCollectService() {
		mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	public boolean isValidRequest(HttpServletRequest request) {
		return true;
	}

	public UploadResult addData(UploadData data) {
		UploadResult result = new UploadResult();
		try {
			readLock.lock();
			cache.add(data);
		} finally {
			readLock.unlock();
		}
		result.setCode(200);
		result.setMessage("success");
		return result;
	}

	public String queryAll() {
		String result = null;
		try {
			readLock.lock();
			result = mapper.writeValueAsString(cache);
		} catch (JsonGenerationException e) {
			log.error(e, e);
		} catch (JsonMappingException e) {
			log.error(e, e);
		} catch (IOException e) {
			log.error(e, e);
		} finally {
			readLock.unlock();
		}
		return result;
	}

	@Scheduled(fixedDelay = 3 * 60 * 1000)
	public void processData() {
		List<UploadData> dataList = new ArrayList<UploadData>();
		try {
			writeLock.lock();
			log.info("queue size: " + cache.size());
			while (!cache.isEmpty()) {
				dataList.add(cache.poll());
			}
			log.info("list size: " + dataList.size());
		} finally {
			writeLock.unlock();
		}
		persistData(dataList);
	}

	private int persistData(List<UploadData> dataList) {
		try {
			log.info("data collected: \n" + mapper.writeValueAsString(dataList));
		} catch (IOException e) {
			log.error(e, e);
		}
		return dataList.size();
	}
}
