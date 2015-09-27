create schema if not exists dataupload;
create TABLE if not exists dataupload.tb_upload_data (
  `id` bigint not null AUTO_INCREMENT,
  `reportTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `channel` varchar(50) NOT NULL DEFAULT '',
  `mid` varchar(100) DEFAULT NULL,
  `eid` varchar(30) DEFAULT NULL,
  `ip` varchar(30) DEFAULT NULL,
  `updateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `select_index_1` (`reportTime` ASC, `channel` ASC, `eid` ASC)  COMMENT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 