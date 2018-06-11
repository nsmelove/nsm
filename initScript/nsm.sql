CREATE TABLE `User` (
  `userId` bigint(20) NOT NULL,
  `userType` int(3) NOT NULL DEFAULT '0',
  `username` varchar(20) NOT NULL,
  `nickname` varchar(40) NOT NULL,
  `userIcon` varchar(255) DEFAULT NULL,
  `password` varchar(40) NOT NULL,
  `userStatus` int(3) NOT NULL DEFAULT '0',
  `createTime` bigint(15) NOT NULL,
  PRIMARY KEY (`userId`),
  KEY `inx_user_un` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';
