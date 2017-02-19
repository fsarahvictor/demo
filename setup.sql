CREATE USER 'tg'@'localhost' IDENTIFIED BY 'tg';
CREATE USER 'tg'@'%' IDENTIFIED BY 'tg';

GRANT ALL PRIVILEGES ON sw.* TO tg@'%';
GRANT ALL PRIVILEGES ON *.* TO tg@'%' WITH GRANT OPTION;
GRANT SELECT ON mysql.proc to 'tg'@'localhost';
GRANT ALL PRIVILEGES ON sw.* TO tg@'localhost';
