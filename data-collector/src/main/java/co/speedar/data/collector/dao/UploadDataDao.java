/**
 * 
 */
package co.speedar.data.collector.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.speedar.data.collector.domain.UploadData;

/**
 * @author ben
 *
 */
@Repository
public class UploadDataDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static final String sql = "insert into dataupload.tb_upload_data (reportTime, channel, mid, eid, ip) values (?, ?, ?, ?, ?)";

	public int batchInsert(final List<UploadData> dataList) {
		BatchPreparedStatementSetter pss = new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				UploadData data = dataList.get(i);
				ps.setTimestamp(1, new Timestamp(data.getReportTime().getTime()));
				ps.setString(2, data.getChannel());
				ps.setString(3, data.getMid());
				ps.setString(4, data.getEid());
				ps.setString(5, data.getIp());
			}

			@Override
			public int getBatchSize() {
				return dataList.size();
			}
		};
		return jdbcTemplate.batchUpdate(sql, pss).length;
	}
}
