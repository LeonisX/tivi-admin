package md.leonis.tivi.admin.test

import md.leonis.tivi.admin.test.JdbcUtils._

object Schema {
  def main(args: Array[String]) {
    executeUpdate("DROP DATABASE IF EXISTS tests;")
    executeUpdate("CREATE DATABASE tests;")
    executeUpdate("USE tests;")

    executeUpdate("DROP TABLE IF EXISTS video;")
    executeUpdate("CREATE TABLE video (" +
      "downid int(11) unsigned NOT NULL auto_increment," +
      "catid int(11) unsigned NOT NULL default '0'," +
      "public int(11) unsigned NOT NULL default '0'," +
      "stpublic int(11) unsigned NOT NULL default '0'," +
      "unpublic int(11) unsigned NOT NULL default '0'," +
      "cpu varchar(255) NOT NULL default ''," +
      "locurl text NOT NULL," +
      "exturl text NOT NULL," +
      "extsize varchar(255) NOT NULL default ''," +
      "descript text NOT NULL," +
      "keywords text NOT NULL," +
      "title varchar(255) NOT NULL default ''," +
      "textshort text NOT NULL," +
      "textmore longtext NOT NULL," +
      "textnotice text NOT NULL," +
      "mirrorsname text NOT NULL," +
      "mirrorsurl text NOT NULL," +
      "relisdown varchar(255) NOT NULL default ''," +
      "authdown varchar(255) NOT NULL default ''," +
      "sitedown varchar(255) NOT NULL default ''," +
      "maildown varchar(255) NOT NULL default ''," +
      "image varchar(255) NOT NULL default ''," +
      "image_thumb varchar(255) NOT NULL default ''," +
      "image_align enum('left','right') NOT NULL default 'left'," +
      "image_alt varchar(255) NOT NULL default ''," +
      "hits int(11) unsigned NOT NULL default '0'," +
      "trans int(11) unsigned NOT NULL default '0'," +
      "lastdown int(11) unsigned NOT NULL default '0'," +
      "rating int(11) unsigned NOT NULL default '0'," +
      "totalrating int(11) unsigned NOT NULL default '0'," +
      "act enum('yes','no') NOT NULL default 'yes'," +
      "acc enum('all','user') NOT NULL default 'all'," +
      "listid int(11) unsigned NOT NULL default '0'," +
      "comments int(11) unsigned NOT NULL default '0'," +
      "tags varchar(255) NOT NULL default ''," +
      "PRIMARY KEY  (downid)," +
      "KEY catid (catid)," +
      "KEY public (public)," +
      "KEY stpublic (stpublic)," +
      "KEY unpublic (unpublic)" +
      ") ENGINE=MyISAM;")

    executeUpdate("DROP TABLE IF EXISTS video_index;")
    executeUpdate("CREATE TABLE video_index (" +
      "downid int(11) unsigned NOT NULL auto_increment," +
      "catid int(11) unsigned NOT NULL default '0'," +
      "public int(11) unsigned NOT NULL default '0'," +
      "stpublic int(11) unsigned NOT NULL default '0'," +
      "unpublic int(11) unsigned NOT NULL default '0'," +
      "cpu varchar(255) NOT NULL default ''," +
      "locurl text NOT NULL," +
      "exturl text NOT NULL," +
      "extsize varchar(255) NOT NULL default ''," +
      "descript text NOT NULL," +
      "keywords text NOT NULL," +
      "title varchar(255) NOT NULL default ''," +
      "textshort text NOT NULL," +
      "textmore longtext NOT NULL," +
      "textnotice text NOT NULL," +
      "mirrorsname text NOT NULL," +
      "mirrorsurl text NOT NULL," +
      "relisdown varchar(255) NOT NULL default ''," +
      "authdown varchar(255) NOT NULL default ''," +
      "sitedown varchar(255) NOT NULL default ''," +
      "maildown varchar(255) NOT NULL default ''," +
      "image varchar(255) NOT NULL default ''," +
      "image_thumb varchar(255) NOT NULL default ''," +
      "image_align enum('left','right') NOT NULL default 'left'," +
      "image_alt varchar(255) NOT NULL default ''," +
      "hits int(11) unsigned NOT NULL default '0'," +
      "trans int(11) unsigned NOT NULL default '0'," +
      "lastdown int(11) unsigned NOT NULL default '0'," +
      "rating int(11) unsigned NOT NULL default '0'," +
      "totalrating int(11) unsigned NOT NULL default '0'," +
      "act enum('yes','no') NOT NULL default 'yes'," +
      "acc enum('all','user') NOT NULL default 'all'," +
      "listid int(11) unsigned NOT NULL default '0'," +
      "comments int(11) unsigned NOT NULL default '0'," +
      "tags varchar(255) NOT NULL default ''," +
      "PRIMARY KEY  (downid)," +
      "KEY catid (catid)," +
      "KEY public (public)," +
      "KEY stpublic (stpublic)," +
      "KEY unpublic (unpublic)," +
      "KEY tags (tags)" +
      ") ENGINE=MyISAM;")


    executeUpdate("DROP TABLE IF EXISTS video_tag;")
    executeUpdate("CREATE TABLE video_tag (" +
      "tagid int(11) unsigned NOT NULL auto_increment," +
      "tagcpu varchar(255) NOT NULL default ''," +
      "tagword varchar(255) NOT NULL default ''," +
      "tagrating smallint(3) unsigned NOT NULL default '0'," +
      "PRIMARY KEY  (tagid)," +
      "KEY tagrating (tagrating)," +
      "KEY tagcpu (tagcpu)" +
      ") ENGINE=MyISAM;")
  }
}