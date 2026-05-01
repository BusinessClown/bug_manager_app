-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bug_tracker
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '4f3a307a-2477-11f1-b987-a4bb6d530278:1-203';

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `fullname` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `is_admin` tinyint(1) NOT NULL DEFAULT '0',
  `job_title` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'johndoe','John Doe','johndoe@email.com','Ve6907Eu6j+kzdR6SP87/g==:7J36rADw9Zs5GVjYs16INa6fu3HD9Ip7Pv+DruIFZRM=',1,'Admin'),(4,'AdBaf','Adu Baffour','aadnxq@umkc.edu','FCM48IHthLB1V/LgGgS3uA==:8DhUcN8nniKFJIodUheJ+WnbDusNfvKyU84XdDTZ6qA=',0,''),(6,'AdBaf2','Adu Baffour','aabnxq@umkc.edu','36Z/ltAqKZtNG2Rf0n/hIg==:onxaQe/cysytBL9cGpGa4Gmif7dyUhCNzsLt7tDMsHQ=',0,''),(7,'Mackadoodle48','Mackenzi Mason','mackenzi1renee@gmail.com','WXxxOtUAXEeSg6xvweJrfA==:vRTkenPIdev9T8cIGsbl3iF9i8XlSV0vgCxX2HncCss=',0,''),(8,'zet','inz aowl','zet@mine.craft','w5/E/k2z+f9Qum/jzfEgSA==:ATTzGF4HWhK7zr2I9aqjJfI78P9ZY7cg68ZzUt93DsE=',0,'Miner'),(9,'catDog','dog cat','catdog@gmail.com','beDxLscFHrChRfJ7PPaAeQ==:QbqIn2LHio7KIwb45kUotLmnD/UMRIZFogXsAwdm+zM=',0,''),(11,'abby','Abby','abigailmeyer549@gmail.com','ePuqd2892eD6Ja0AHuzdfA==:GtXgPMWwBuUTKBJ8IpRJmSTpncRW6V0dePPvCF3Gdxo=',0,'av tech'),(13,'walshjm','Jamie Walsh','walsh.jamie.m@gmail.com','OHgXJg21ASp9ymdXOKocEQ==:mQgQVCpA3ryQCol+D2IBzEGJjr1Rle0v8FVfk5R7nHM=',0,''),(14,'Mitnick','Kevin','somebody@somewhere.com','eD3wkyFPnxP+gjxCR4gElw==:9xSDjDH+fOPCuAhdj1EtmAKPLPdOWO+03f9qCbMlDo4=',1,'test admin'),(15,'d','s','npmv2@umsystem.edu','f8MzaoCed15AiTTIvJvGvg==:12KfrPQfqtfT9YE1F9qFDKQ1BZzvbZcsksMht40+ImM=',0,''),(16,'han','Han','han@chan.com','XwYMjtFvxBc0vkXsmEG0jw==:f0wM6Ac2hLAisCcRtkNlcXCmYJri9qavCfDiu/JCFkY=',1,''),(17,'Foote','Ken','kfoote@gmail.com','Q+7qlzmOyYpRm+WM0g1kWA==:xjtHjuBGF63vUssSKuQ81E1jCqS6692WmUUypRt4qJw=',0,''),(18,'inz','inz gown','inzowlgown@nazarek.com','5MSGA66rHwG/vFsicCEHiA==:WAx8xO6zkq1M1dxVhSbU+3TvsamoGzgR0fCv4eZF4I0=',0,''),(19,'etryj','dfgjh','fdgh@fhgj.com','s6JUgOfohvTm3GFbRn+xNw==:3iyGkGKO+1rNx1NCSRVBnH5sQRi4HEaYdmu0uDxrFn4=',0,''),(20,'umm','erdtfgbhjn','ummm@uooga.uuga','3lELJyZtaaMZMsNFiHmMHg==:Zrc01us+IHXqW8v91E22Sa6PHjzgrWWamUQ1k+4MJPk=',0,'');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-01  9:43:42
