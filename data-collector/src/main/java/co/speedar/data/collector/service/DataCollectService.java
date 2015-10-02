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
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import co.speedar.data.collector.dao.UploadDataDao;
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

	@Autowired
	private UploadDataDao dao;

	public DataCollectService() {
		mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * Check if request is granted.
	 * 
	 * @param request
	 * @return
	 */
	public boolean isValidRequest(HttpServletRequest request) {
		return true;
	}

	/**
	 * Submit data to cache. Might be blocked by {@link #drainCache()} method.
	 * 
	 * @param data
	 * @return
	 */
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

	/**
	 * Query all data in cache. Might be blocked by {@link #drainCache()}
	 * method.
	 * 
	 * @return
	 */
	public List<UploadData> queryAll() {
		List<UploadData> result = new ArrayList<UploadData>();
		try {
			readLock.lock();
			for (UploadData data : cache) {
				result.add(data);
			}
		} finally {
			readLock.unlock();
		}
		return result;
	}

	/**
	 * Persist given data into database.
	 * 
	 * @param dataList
	 * @return
	 */
	public int persistData(List<UploadData> dataList) {
		try {
			log.info("data collected: \n" + mapper.writeValueAsString(dataList));
		} catch (IOException e) {
			log.error(e, e);
		}
		return dao.batchInsert(dataList);
	}

	/**
	 * Drain cached data and return as a list. Might be blocked by
	 * {@link #addData(UploadData)} or {@link #queryAll()} method.
	 * 
	 * @return
	 */
	public List<UploadData> drainCache() {
		log.info("queue size: " + cache.size());
		List<UploadData> dataList = new ArrayList<UploadData>();
		try {
			writeLock.lock();
			while (!cache.isEmpty()) {
				dataList.add(cache.poll());
			}
		} finally {
			writeLock.unlock();
		}
		log.info("list size: " + dataList.size());
		return dataList;
	}

	/**
	 * Drain cached data and persist them periodically.
	 */
	@Scheduled(cron = "${data.collect.cron}")
	public void processData() {
		try {
			log.info("time to process data...");
			List<UploadData> dataList = drainCache();
			if (dataList != null && !dataList.isEmpty()) {
				persistData(dataList);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}
}
