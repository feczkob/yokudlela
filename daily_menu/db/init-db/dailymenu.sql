CREATE DATABASE IF NOT EXISTS `dailymenu` CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE USER 'dailymenu_user'@'%' IDENTIFIED BY 'dailymenu1234';
SET GLOBAL default_storage_engine = 'InnoDB';
GRANT ALL PRIVILEGES ON `dailymenu`.* TO `dailymenu_user`@`%`;
