CREATE TABLE `user` (
  `userId` bigint(20) NOT NULL,
  `userType` int(3) NOT NULL DEFAULT '0',
  `username` varchar(20) NOT NULL,
  `nickname` varchar(40) NOT NULL,
  `userIcon` varchar(255) DEFAULT NULL,
  `password` varchar(40) NOT NULL,
  `userStatus` int(3) NOT NULL DEFAULT '0',
  `createTime` bigint(15) NOT NULL,
  `privacy` int(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userId`),
  KEY `inx_user_un` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

CREATE TABLE `user_setting` (
  `userId` bigint(20) NOT NULL,
  `autoJoinGroup` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

