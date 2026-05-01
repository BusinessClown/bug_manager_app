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
-- Table structure for table `bugs`
--

DROP TABLE IF EXISTS `bugs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bugs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `last_edited_by` bigint DEFAULT NULL,
  `title` varchar(150) NOT NULL,
  `description` text,
  `create_date` date NOT NULL,
  `due_date` date DEFAULT NULL,
  `status` enum('OPEN','IN_PROGRESS','COMPLETED') NOT NULL DEFAULT 'OPEN',
  `priority` enum('LOW','MEDIUM','HIGH') NOT NULL DEFAULT 'MEDIUM',
  `category` enum('FUNCTIONAL','GRAPHICAL','PERFORMANCE','TECHNICAL','SECURITY','COMPATIBILITY') NOT NULL DEFAULT 'FUNCTIONAL',
  `severity` enum('BLOCKER','SEVERE','MAJOR','MINOR','COSMETIC') NOT NULL DEFAULT 'MAJOR',
  `project_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_bugs_user` (`user_id`),
  KEY `fk_bugs_project` (`project_id`),
  KEY `fk_bugs_last_edited_by` (`last_edited_by`),
  CONSTRAINT `fk_bugs_last_edited_by` FOREIGN KEY (`last_edited_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_bugs_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_bugs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bugs`
--

LOCK TABLES `bugs` WRITE;
/*!40000 ALTER TABLE `bugs` DISABLE KEYS */;
INSERT INTO `bugs` VALUES (4,1,1,'run','run run run im the gerngerbread man you cant catch me cause nobody can','2026-03-27','2026-05-27','OPEN','MEDIUM','FUNCTIONAL','MAJOR',1),(6,1,NULL,'cart flys away','','2026-03-29','2026-11-15','COMPLETED','MEDIUM','FUNCTIONAL','MINOR',1),(7,1,NULL,'my name is steve','aaaaaaaaaaahhh','2026-03-29','2026-06-17','COMPLETED','HIGH','GRAPHICAL','SEVERE',4),(10,8,NULL,'mine hahahha','hide if u can','2026-03-29','2026-10-22','OPEN','HIGH','TECHNICAL','MAJOR',1),(11,9,16,'money dupe','by spaming x fast enough you can sell one item multi times','2026-03-30','2026-04-05','COMPLETED','HIGH','SECURITY','BLOCKER',1),(12,9,NULL,'pleaseeee','i refuse','2026-03-30','2026-03-31','OPEN','HIGH','COMPATIBILITY','COSMETIC',1),(13,1,NULL,'the mod doesnt exist','make it \n\nnotes \"no\"\n\nnew note \"why not\"','2026-04-15','2026-04-16','OPEN','HIGH','PERFORMANCE','BLOCKER',4),(14,NULL,NULL,'Booboo on front lawn','It stinks','2026-04-16','2026-05-01','OPEN','MEDIUM','FUNCTIONAL','MAJOR',1),(15,NULL,NULL,'holy crap','Why does it stink','2026-04-16','2026-06-01','OPEN','LOW','PERFORMANCE','SEVERE',1),(16,NULL,NULL,'Defacation','You sure it doesn\'t stink','2026-04-16','2026-04-20','OPEN','HIGH','SECURITY','COSMETIC',1),(17,11,NULL,'watch out for Gerald','Gerald sees all, and knows all.','2026-04-16','2026-04-17','OPEN','HIGH','SECURITY','COSMETIC',1),(18,13,18,'Wrong Turn','No. \n\nreply i think yes','2026-04-16','2026-05-21','COMPLETED','MEDIUM','SECURITY','SEVERE',1),(19,14,NULL,'Incompatibility','Sorry, our signs are just incompatible. I\'m a Gemini, you\'re an Aries. It just wasn\'t meant to be. AND YOU OWE ME MONEY FOR THOSE ROOFIES I DROPPED IN YOUR DRINK!!','2026-04-16','2026-05-25','OPEN','HIGH','COMPATIBILITY','BLOCKER',1),(20,15,NULL,'mew','mew','2026-04-16','2026-04-18','COMPLETED','MEDIUM','PERFORMANCE','MAJOR',1),(21,16,NULL,'Tokenizer error','Some tokens were unknown.','2026-04-17','2026-05-12','OPEN','HIGH','FUNCTIONAL','MAJOR',5),(22,18,18,'zdxgchjbklm;','','2026-04-27','2026-05-22','OPEN','HIGH','PERFORMANCE','BLOCKER',1);
/*!40000 ALTER TABLE `bugs` ENABLE KEYS */;
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
