-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        5.5.62 - MySQL Community Server (GPL)
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 테이블 holdem.item 구조 내보내기
CREATE TABLE IF NOT EXISTS `item` (
  `midx` int(11) NOT NULL,
  `type` varchar(50) NOT NULL,
  `amount` int(11) NOT NULL,
  PRIMARY KEY (`midx`,`type`),
  KEY `FK_item_static_item_table` (`type`),
  CONSTRAINT `FK_item_static_item_table` FOREIGN KEY (`type`) REFERENCES `static_item_table` (`type`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK__member` FOREIGN KEY (`midx`) REFERENCES `member` (`midx`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 holdem.item:~116 rows (대략적) 내보내기
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT IGNORE INTO `item` (`midx`, `type`, `amount`) VALUES
	(1, 'avata1', 0),
	(1, 'avata2', 0),
	(1, 'avata3', 0),
	(1, 'avata4', 0),
	(1, 'balance', 2100),
	(1, 'cash', 1430),
	(1, 'point', 10),
	(1, 'safe_balance', 0),
	(1, 'safe_point', 5),
	(2, 'avata1', 0),
	(2, 'avata2', 0),
	(2, 'avata3', 0),
	(2, 'avata4', 0),
	(2, 'balance', 0),
	(2, 'cash', 0),
	(2, 'point', 0),
	(2, 'safe_balance', 0),
	(2, 'safe_point', 0),
	(3, 'avata1', 0),
	(3, 'avata2', 0),
	(3, 'avata3', 0),
	(3, 'avata4', 0),
	(3, 'balance', 0),
	(3, 'cash', 0),
	(3, 'point', 0),
	(3, 'safe_balance', 0),
	(3, 'safe_point', 0),
	(4, 'avata1', 0),
	(4, 'avata2', 0),
	(4, 'avata3', 0),
	(4, 'avata4', 0),
	(4, 'balance', 0),
	(4, 'cash', 0),
	(4, 'point', 0),
	(4, 'safe_balance', 0),
	(4, 'safe_point', 0),
	(5, 'avata1', 0),
	(5, 'avata2', 0),
	(5, 'avata3', 0),
	(5, 'avata4', 0),
	(5, 'balance', 0),
	(5, 'cash', 0),
	(5, 'point', 0),
	(5, 'safe_balance', 0),
	(5, 'safe_point', 0),
	(6, 'avata1', 0),
	(6, 'avata2', 0),
	(6, 'avata3', 0),
	(6, 'avata4', 0),
	(6, 'balance', 0),
	(6, 'cash', 0),
	(6, 'point', 0),
	(6, 'safe_balance', 0),
	(6, 'safe_point', 0),
	(7, 'avata1', 0),
	(7, 'avata2', 0),
	(7, 'avata3', 0),
	(7, 'avata4', 0),
	(7, 'balance', 0),
	(7, 'cash', 0),
	(7, 'point', 0),
	(7, 'safe_balance', 0),
	(7, 'safe_point', 0),
	(8, 'avata1', 0),
	(8, 'avata2', 0),
	(8, 'avata3', 0),
	(8, 'avata4', 0),
	(8, 'balance', 0),
	(8, 'cash', 0),
	(8, 'point', 0),
	(8, 'safe_balance', 0),
	(9, 'avata1', 0),
	(9, 'avata2', 0),
	(9, 'avata3', 0),
	(9, 'avata4', 0),
	(9, 'balance', 0),
	(9, 'cash', 0),
	(9, 'point', 0),
	(9, 'safe_balance', 0),
	(9, 'safe_point', 0),
	(10, 'avata1', 0),
	(10, 'avata2', 0),
	(10, 'avata3', 0),
	(10, 'avata4', 0),
	(10, 'balance', 0),
	(10, 'cash', 0),
	(10, 'point', 0),
	(10, 'safe_balance', 0),
	(10, 'safe_point', 0),
	(11, 'avata1', 0),
	(11, 'avata2', 0),
	(11, 'avata3', 0),
	(11, 'avata4', 0),
	(11, 'balance', 0),
	(11, 'cash', 0),
	(11, 'point', 0),
	(11, 'safe_balance', 0),
	(11, 'safe_point', 0),
	(12, 'avata1', 0),
	(12, 'avata2', 0),
	(12, 'avata3', 0),
	(12, 'avata4', 0),
	(12, 'balance', 0),
	(12, 'cash', 0),
	(12, 'point', 0),
	(12, 'safe_balance', 0),
	(12, 'safe_point', 0),
	(13, 'avata1', 0),
	(13, 'avata2', 0),
	(13, 'avata3', 0),
	(13, 'avata4', 0),
	(13, 'balance', 0),
	(13, 'cash', 0),
	(13, 'point', 0),
	(13, 'safe_balance', 0),
	(13, 'safe_point', 0);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;

-- 테이블 holdem.static_item_table 구조 내보내기
CREATE TABLE IF NOT EXISTS `static_item_table` (
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='재화 타입 정의용 테이블';

-- 테이블 데이터 holdem.static_item_table:~10 rows (대략적) 내보내기
/*!40000 ALTER TABLE `static_item_table` DISABLE KEYS */;
INSERT IGNORE INTO `static_item_table` (`type`) VALUES
	('avata1'),
	('avata2'),
	('avata3'),
	('avata4'),
	('balance'),
	('budget'),
	('cash'),
	('point'),
	('safe_balance'),
	('safe_point');
/*!40000 ALTER TABLE `static_item_table` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
