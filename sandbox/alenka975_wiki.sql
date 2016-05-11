SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


CREATE TABLE danny_admin (
  admid int(11) unsigned NOT NULL auto_increment,
  adlog varchar(25) NOT NULL default '',
  adpwd varchar(32) default NULL,
  admail varchar(50) NOT NULL default '',
  adlast int(11) NOT NULL default '0',
  permiss text NOT NULL,
  PRIMARY KEY  (admid),
  KEY adlog (adlog)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_admin_sess (
  `hash` varchar(32) NOT NULL default '',
  admid int(11) NOT NULL default '0',
  ipadd varchar(16) NOT NULL default '',
  starttime int(11) unsigned NOT NULL default '0',
  lastactivity int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (`hash`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_article (
  artid int(11) unsigned NOT NULL auto_increment,
  catid int(11) unsigned NOT NULL default '0',
  public int(11) unsigned NOT NULL default '0',
  stpublic int(11) unsigned NOT NULL default '0',
  unpublic int(11) unsigned NOT NULL default '0',
  `cpu` varchar(255) NOT NULL default '',
  title varchar(255) NOT NULL default '',
  textshort text NOT NULL,
  textmore longtext NOT NULL,
  textnotice text NOT NULL,
  keywords text NOT NULL,
  descript text NOT NULL,
  downtitle varchar(255) NOT NULL default '',
  downlink varchar(255) NOT NULL default '',
  image varchar(255) NOT NULL default '',
  image_thumb varchar(255) NOT NULL default '',
  image_align enum('left','right') NOT NULL default 'left',
  image_alt varchar(255) NOT NULL default '',
  hits int(11) unsigned NOT NULL default '0',
  act enum('yes','no') NOT NULL default 'yes',
  acc enum('all','user') NOT NULL default 'all',
  listid int(11) unsigned NOT NULL default '0',
  letid int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (artid),
  KEY catid (catid),
  KEY act (act),
  KEY `cpu` (`cpu`),
  KEY public (public)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_article_search (
  seaid int(11) unsigned NOT NULL auto_increment,
  seaword varchar(255) NOT NULL default '',
  seaip varchar(255) NOT NULL default '',
  seatime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (seaid),
  KEY seaip (seaip),
  KEY seatime (seatime)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_banners (
  banid int(11) unsigned NOT NULL auto_increment,
  bantype enum('code','click') default NULL,
  banurl varchar(255) NOT NULL default '',
  bancode text,
  bantitle varchar(255) NOT NULL default '',
  banimg varchar(255) NOT NULL default '',
  banlimit int(11) unsigned NOT NULL default '0',
  banview int(11) unsigned NOT NULL default '0',
  banclick int(11) unsigned NOT NULL default '0',
  banmods text NOT NULL,
  banzonid int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (banid),
  KEY banlimit (banlimit),
  KEY banview (banview)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_banners_zone (
  banzonid int(11) unsigned NOT NULL auto_increment,
  banzoncode varchar(255) NOT NULL default '',
  PRIMARY KEY  (banzonid)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_block (
  blockid int(11) unsigned NOT NULL auto_increment,
  positid int(11) unsigned NOT NULL default '0',
  block_side varchar(255) NOT NULL default '',
  block_file varchar(100) NOT NULL default '',
  block_name varchar(80) NOT NULL default '',
  block_cont text NOT NULL,
  block_active enum('yes','no') NOT NULL default 'yes',
  block_posit int(11) unsigned NOT NULL default '0',
  block_temp varchar(255) NOT NULL default '',
  block_mods text NOT NULL,
  block_label text NOT NULL,
  block_access enum('all','user') NOT NULL default 'all',
  PRIMARY KEY  (blockid),
  KEY block_active (block_active),
  KEY block_posit (block_posit)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_block_posit (
  positid int(11) unsigned NOT NULL auto_increment,
  positcode varchar(255) NOT NULL default '',
  PRIMARY KEY  (positid)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_captcha (
  captchid int(11) NOT NULL auto_increment,
  captchip varchar(255) NOT NULL default '',
  captchcode varchar(10) NOT NULL default '',
  captchtime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (captchid)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_chat (
  id int(5) NOT NULL auto_increment,
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user` varchar(25) character set cp1251 NOT NULL,
  message varchar(255) character set cp1251 NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_down (
  downid int(11) unsigned NOT NULL auto_increment,
  catid int(11) unsigned NOT NULL default '0',
  public int(11) unsigned NOT NULL default '0',
  stpublic int(11) unsigned NOT NULL default '0',
  unpublic int(11) unsigned NOT NULL default '0',
  `cpu` varchar(255) NOT NULL default '',
  locurl text NOT NULL,
  exturl text NOT NULL,
  extsize varchar(255) NOT NULL default '',
  descript text NOT NULL,
  keywords text NOT NULL,
  title varchar(255) NOT NULL default '',
  textshort text NOT NULL,
  textmore longtext NOT NULL,
  textnotice text NOT NULL,
  mirrorsname text NOT NULL,
  mirrorsurl text NOT NULL,
  relisdown varchar(255) NOT NULL default '',
  authdown varchar(255) NOT NULL default '',
  sitedown varchar(255) NOT NULL default '',
  maildown varchar(255) NOT NULL default '',
  image varchar(255) NOT NULL default '',
  image_thumb varchar(255) NOT NULL default '',
  image_align enum('left','right') NOT NULL default 'left',
  image_alt varchar(255) NOT NULL default '',
  hits int(11) unsigned NOT NULL default '0',
  trans int(11) unsigned NOT NULL default '0',
  lastdown int(11) unsigned NOT NULL default '0',
  rating int(11) unsigned NOT NULL default '0',
  totalrating int(11) unsigned NOT NULL default '0',
  act enum('yes','no') NOT NULL default 'yes',
  acc enum('all','user') NOT NULL default 'all',
  listid int(11) unsigned NOT NULL default '0',
  comments int(11) unsigned NOT NULL default '0',
  tags varchar(255) NOT NULL default '',
  PRIMARY KEY  (downid),
  KEY catid (catid),
  KEY public (public),
  KEY stpublic (stpublic),
  KEY unpublic (unpublic)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_down_broken (
  brokid int(11) unsigned NOT NULL auto_increment,
  downid int(11) unsigned NOT NULL default '0',
  brokip varchar(255) NOT NULL default '',
  broktime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (brokid),
  KEY downid (downid),
  KEY brokip (brokip),
  KEY broktime (broktime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_down_cat (
  catid int(11) unsigned NOT NULL auto_increment,
  parentid int(11) unsigned NOT NULL default '0',
  catcpu varchar(255) NOT NULL default '',
  catname varchar(255) NOT NULL default '',
  catdesc text NOT NULL,
  posit int(11) unsigned NOT NULL default '0',
  icon varchar(255) NOT NULL default '',
  access enum('all','user') NOT NULL default 'all',
  sort varchar(11) NOT NULL default 'newsid',
  ord enum('asc','desc') NOT NULL default 'asc',
  rss enum('yes','no') NOT NULL default 'yes',
  total int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (catid),
  KEY cid (catid),
  KEY title (catname)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_down_comment (
  comid int(11) unsigned NOT NULL auto_increment,
  downid int(11) unsigned NOT NULL default '0',
  userid int(11) unsigned NOT NULL default '0',
  ctime int(11) unsigned NOT NULL default '0',
  cname varchar(50) NOT NULL default '',
  ctitle varchar(255) NOT NULL default '',
  ctext text NOT NULL,
  cip varchar(20) NOT NULL default '',
  PRIMARY KEY  (comid),
  KEY downid (downid),
  KEY ctime (ctime),
  KEY cip (cip)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_down_rating (
  ratingid int(11) unsigned NOT NULL auto_increment,
  downid int(11) unsigned NOT NULL default '0',
  ratingip varchar(255) NOT NULL default '',
  ratingtime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (ratingid),
  KEY downid (downid),
  KEY ratingip (ratingip),
  KEY ratingtime (ratingtime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_down_search (
  seaid int(11) unsigned NOT NULL auto_increment,
  seaword varchar(255) NOT NULL default '',
  seaip varchar(255) NOT NULL default '',
  seatime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (seaid),
  KEY seaip (seaip),
  KEY seatime (seatime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_down_sess (
  sessid varchar(32) NOT NULL default '',
  downid int(11) unsigned NOT NULL default '0',
  sessip varchar(255) NOT NULL default '',
  sesstime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (sessid)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_down_tag (
  tagid int(11) unsigned NOT NULL auto_increment,
  tagcpu varchar(255) NOT NULL default '',
  tagword varchar(255) NOT NULL default '',
  tagrating smallint(3) unsigned NOT NULL default '0',
  PRIMARY KEY  (tagid),
  KEY tagrating (tagrating),
  KEY tagcpu (tagcpu)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_field (
  fieldid int(11) unsigned NOT NULL auto_increment,
  fieldtype varchar(10) NOT NULL default '',
  fieldname varchar(10) NOT NULL default '',
  fieldlist text NOT NULL,
  `name` varchar(255) NOT NULL default '',
  requires enum('yes','no') NOT NULL default 'yes',
  method varchar(10) NOT NULL default '',
  minlen int(1) unsigned NOT NULL default '3',
  maxlen int(3) unsigned NOT NULL default '255',
  act enum('no','yes') NOT NULL default 'no',
  posit int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (fieldid),
  KEY act (act),
  KEY posit (posit)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_gallery (
  id int(11) unsigned NOT NULL auto_increment,
  catid int(11) unsigned NOT NULL default '0',
  public int(11) unsigned NOT NULL default '0',
  `cpu` varchar(255) NOT NULL default '',
  title varchar(255) NOT NULL default '',
  `text` text NOT NULL,
  keywords text NOT NULL,
  descript text NOT NULL,
  image varchar(255) NOT NULL default '',
  image_thumb varchar(255) NOT NULL default '',
  image_alt varchar(255) NOT NULL default '',
  video varchar(255) NOT NULL default '',
  hits int(11) unsigned NOT NULL default '0',
  act enum('yes','no') NOT NULL default 'yes',
  rating int(11) NOT NULL default '0',
  totalrating int(11) NOT NULL default '0',
  comments int(11) NOT NULL default '0',
  tags varchar(255) NOT NULL,
  PRIMARY KEY  (id),
  KEY `cpu` (`cpu`),
  KEY public (public),
  KEY catid (catid),
  KEY act (act)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_gallery_cat (
  catid int(11) unsigned NOT NULL auto_increment,
  parentid int(11) unsigned NOT NULL default '0',
  catcpu varchar(255) NOT NULL default '',
  catname varchar(255) NOT NULL default '',
  catdesc text NOT NULL,
  posit int(11) unsigned NOT NULL default '0',
  icon varchar(255) NOT NULL default '',
  access enum('all','user') NOT NULL default 'all',
  sort varchar(11) NOT NULL default 'newsid',
  ord enum('asc','desc') NOT NULL default 'asc',
  rss enum('yes','no') NOT NULL default 'yes',
  total int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (catid),
  KEY cid (catid),
  KEY title (catname)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_gallery_comment (
  comid int(11) unsigned NOT NULL auto_increment,
  id int(11) unsigned NOT NULL default '0',
  userid int(11) unsigned NOT NULL default '0',
  ctime int(11) unsigned NOT NULL default '0',
  cname varchar(50) NOT NULL default '',
  ctitle varchar(255) NOT NULL default '',
  ctext text NOT NULL,
  cip varchar(20) NOT NULL default '',
  PRIMARY KEY  (comid),
  KEY id (id),
  KEY ctime (ctime),
  KEY cip (cip)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_gallery_rating (
  ratingid int(11) unsigned NOT NULL auto_increment,
  id int(11) unsigned NOT NULL default '0',
  ratingip varchar(255) NOT NULL default '',
  ratingtime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (ratingid),
  KEY photosid (id),
  KEY ratingip (ratingip),
  KEY ratingtime (ratingtime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_gallery_search (
  seaid int(11) unsigned NOT NULL auto_increment,
  seaword varchar(255) NOT NULL default '',
  seaip varchar(255) NOT NULL default '',
  seatime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (seaid),
  KEY seaip (seaip),
  KEY seatime (seatime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_gallery_tag (
  tagid int(11) unsigned NOT NULL auto_increment,
  tagcpu varchar(255) NOT NULL default '',
  tagword varchar(255) NOT NULL default '',
  tagrating smallint(3) unsigned NOT NULL default '0',
  PRIMARY KEY  (tagid),
  KEY tagrating (tagrating),
  KEY tagcpu (tagcpu)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_game_comment (
  comid int(11) unsigned NOT NULL auto_increment,
  newsid varchar(255) NOT NULL default '',
  userid int(11) unsigned NOT NULL default '0',
  ctime int(11) unsigned NOT NULL default '0',
  cname varchar(50) NOT NULL default '',
  ctitle varchar(255) NOT NULL default '',
  ctext text NOT NULL,
  cip varchar(20) NOT NULL default '',
  PRIMARY KEY  (comid),
  KEY newsid (newsid),
  KEY ctime (ctime),
  KEY cip (cip)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_info (
  infoid int(11) unsigned NOT NULL auto_increment,
  infotitle varchar(255) NOT NULL default '',
  infotext mediumtext,
  infotpl varchar(255) NOT NULL default '',
  PRIMARY KEY  (infoid),
  KEY infotpl (infotpl)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_language (
  langid int(11) unsigned NOT NULL auto_increment,
  langpackid int(2) unsigned NOT NULL default '1',
  langsetid int(5) unsigned NOT NULL default '0',
  langvars varchar(35) NOT NULL default '',
  langvals text NOT NULL,
  langvalsold text NOT NULL,
  langcache int(1) unsigned NOT NULL default '0',
  PRIMARY KEY  (langid),
  KEY langpackid (langpackid),
  KEY langcache (langcache)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_language_pack (
  langpackid int(2) NOT NULL auto_increment,
  langpack varchar(100) NOT NULL default '',
  langcode varchar(4) NOT NULL default '',
  langcharset varchar(25) NOT NULL default '',
  langdateset int(11) unsigned NOT NULL default '0',
  langauthor text NOT NULL,
  PRIMARY KEY  (langpackid),
  KEY langcharset (langcharset)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_language_setting (
  langsetid int(11) unsigned NOT NULL auto_increment,
  langpackid int(11) unsigned NOT NULL default '0',
  langsetname varchar(80) NOT NULL default '',
  langsetmd5 varchar(32) NOT NULL default '',
  PRIMARY KEY  (langsetid),
  KEY langpackid (langpackid)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_link (
  linkid int(11) unsigned NOT NULL auto_increment,
  catid int(11) unsigned NOT NULL default '0',
  public int(11) unsigned NOT NULL default '0',
  `cpu` varchar(255) NOT NULL default '',
  url varchar(255) NOT NULL default '',
  title varchar(255) NOT NULL default '',
  textshort text NOT NULL,
  textmore longtext NOT NULL,
  keywords text NOT NULL,
  descript text NOT NULL,
  image varchar(255) NOT NULL default '',
  image_align enum('left','right') NOT NULL default 'left',
  image_alt varchar(255) NOT NULL default '',
  hits int(11) unsigned NOT NULL default '0',
  trans int(11) unsigned NOT NULL default '0',
  act enum('yes','no') NOT NULL default 'yes',
  acc enum('all','user') NOT NULL default 'all',
  PRIMARY KEY  (linkid),
  KEY catid (catid),
  KEY act (act),
  KEY `cpu` (`cpu`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_media (
  downid int(11) unsigned NOT NULL auto_increment,
  catid int(11) unsigned NOT NULL default '0',
  public int(11) unsigned NOT NULL default '0',
  stpublic int(11) unsigned NOT NULL default '0',
  unpublic int(11) unsigned NOT NULL default '0',
  `cpu` varchar(255) NOT NULL default '',
  locurl text NOT NULL,
  exturl text NOT NULL,
  extsize varchar(255) NOT NULL default '',
  descript text NOT NULL,
  keywords text NOT NULL,
  title varchar(255) NOT NULL default '',
  textshort text NOT NULL,
  textmore longtext NOT NULL,
  textnotice text NOT NULL,
  mirrorsname text NOT NULL,
  mirrorsurl text NOT NULL,
  relisdown varchar(255) NOT NULL default '',
  authdown varchar(255) NOT NULL default '',
  sitedown varchar(255) NOT NULL default '',
  maildown varchar(255) NOT NULL default '',
  image varchar(255) NOT NULL default '',
  image_thumb varchar(255) NOT NULL default '',
  image_align enum('left','right') NOT NULL default 'left',
  image_alt varchar(255) NOT NULL default '',
  hits int(11) unsigned NOT NULL default '0',
  trans int(11) unsigned NOT NULL default '0',
  lastdown int(11) unsigned NOT NULL default '0',
  rating int(11) unsigned NOT NULL default '0',
  totalrating int(11) unsigned NOT NULL default '0',
  act enum('yes','no') NOT NULL default 'yes',
  acc enum('all','user') NOT NULL default 'all',
  listid int(11) unsigned NOT NULL default '0',
  comments int(11) unsigned NOT NULL default '0',
  tags varchar(255) NOT NULL default '',
  PRIMARY KEY  (downid),
  KEY catid (catid),
  KEY public (public),
  KEY stpublic (stpublic),
  KEY unpublic (unpublic)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_media_broken (
  brokid int(11) unsigned NOT NULL auto_increment,
  downid int(11) unsigned NOT NULL default '0',
  brokip varchar(255) NOT NULL default '',
  broktime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (brokid),
  KEY downid (downid),
  KEY brokip (brokip),
  KEY broktime (broktime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_media_cat (
  catid int(11) unsigned NOT NULL auto_increment,
  parentid int(11) unsigned NOT NULL default '0',
  catcpu varchar(255) NOT NULL default '',
  catname varchar(255) NOT NULL default '',
  catdesc text NOT NULL,
  posit int(11) unsigned NOT NULL default '0',
  icon varchar(255) NOT NULL default '',
  access enum('all','user') NOT NULL default 'all',
  sort varchar(11) NOT NULL default 'newsid',
  ord enum('asc','desc') NOT NULL default 'asc',
  rss enum('yes','no') NOT NULL default 'yes',
  total int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (catid),
  KEY cid (catid),
  KEY title (catname)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_media_comment (
  comid int(11) unsigned NOT NULL auto_increment,
  downid int(11) unsigned NOT NULL default '0',
  userid int(11) unsigned NOT NULL default '0',
  ctime int(11) unsigned NOT NULL default '0',
  cname varchar(50) NOT NULL default '',
  ctitle varchar(255) NOT NULL default '',
  ctext text NOT NULL,
  cip varchar(20) NOT NULL default '',
  PRIMARY KEY  (comid),
  KEY downid (downid),
  KEY ctime (ctime),
  KEY cip (cip)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_media_list (
  listid int(11) unsigned NOT NULL auto_increment,
  listname varchar(255) default NULL,
  listdesc text NOT NULL,
  listcol int(11) unsigned NOT NULL default '0',
  access enum('all','user') NOT NULL default 'all',
  PRIMARY KEY  (listid),
  KEY access (access)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_media_rating (
  ratingid int(11) unsigned NOT NULL auto_increment,
  downid int(11) unsigned NOT NULL default '0',
  ratingip varchar(255) NOT NULL default '',
  ratingtime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (ratingid),
  KEY downid (downid),
  KEY ratingip (ratingip),
  KEY ratingtime (ratingtime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_media_search (
  seaid int(11) unsigned NOT NULL auto_increment,
  seaword varchar(255) NOT NULL default '',
  seaip varchar(255) NOT NULL default '',
  seatime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (seaid),
  KEY seaip (seaip),
  KEY seatime (seatime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_media_sess (
  sessid varchar(32) NOT NULL default '',
  downid int(11) unsigned NOT NULL default '0',
  sessip varchar(255) NOT NULL default '',
  sesstime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (sessid)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_media_tag (
  tagid int(11) unsigned NOT NULL auto_increment,
  tagcpu varchar(255) NOT NULL default '',
  tagword varchar(255) NOT NULL default '',
  tagrating smallint(3) unsigned NOT NULL default '0',
  PRIMARY KEY  (tagid),
  KEY tagrating (tagrating),
  KEY tagcpu (tagcpu)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_mods (
  modid int(11) unsigned NOT NULL auto_increment,
  mod_fold varchar(255) NOT NULL default 'Default',
  mod_temp varchar(255) NOT NULL default '',
  mod_name varchar(255) NOT NULL default '',
  mod_map text NOT NULL,
  mod_posit int(11) unsigned NOT NULL default '0',
  mod_label text NOT NULL,
  active enum('yes','no') NOT NULL default 'no',
  PRIMARY KEY  (modid),
  KEY mod_fold (mod_fold)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_news (
  newsid int(11) unsigned NOT NULL auto_increment,
  catid int(11) unsigned NOT NULL default '0',
  public int(11) unsigned NOT NULL default '0',
  stpublic int(11) unsigned NOT NULL default '0',
  unpublic int(11) unsigned NOT NULL default '0',
  `cpu` varchar(255) NOT NULL default '',
  title varchar(255) NOT NULL default '',
  textshort text NOT NULL,
  textmore longtext NOT NULL,
  textnotice text NOT NULL,
  keywords text NOT NULL,
  descript text NOT NULL,
  image varchar(255) NOT NULL default '',
  image_thumb varchar(255) NOT NULL default '',
  image_align enum('left','right') NOT NULL default 'left',
  image_alt varchar(255) NOT NULL default '',
  video varchar(255) NOT NULL default '',
  hits int(11) unsigned NOT NULL default '0',
  act enum('yes','no') NOT NULL default 'yes',
  acc enum('all','user') NOT NULL default 'all',
  listid int(11) NOT NULL default '0',
  comments int(11) NOT NULL default '0',
  tags varchar(255) NOT NULL default '',
  PRIMARY KEY  (newsid),
  KEY sid (newsid),
  KEY catid (catid),
  KEY counter (hits)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_news_cat (
  catid int(11) unsigned NOT NULL auto_increment,
  parentid int(11) unsigned NOT NULL default '0',
  catcpu varchar(255) NOT NULL default '',
  catname varchar(255) NOT NULL default '',
  catdesc text NOT NULL,
  posit int(11) unsigned NOT NULL default '0',
  icon varchar(255) NOT NULL default '',
  access enum('all','user') NOT NULL default 'all',
  sort varchar(11) NOT NULL default 'newsid',
  ord enum('asc','desc') NOT NULL default 'asc',
  rss enum('yes','no') NOT NULL default 'yes',
  total int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (catid),
  KEY cid (catid),
  KEY title (catname)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_news_comment (
  comid int(11) unsigned NOT NULL auto_increment,
  newsid int(11) unsigned NOT NULL default '0',
  userid int(11) unsigned NOT NULL default '0',
  ctime int(11) unsigned NOT NULL default '0',
  cname varchar(50) NOT NULL default '',
  ctitle varchar(255) NOT NULL default '',
  ctext text NOT NULL,
  cip varchar(20) NOT NULL default '',
  PRIMARY KEY  (comid),
  KEY newsid (newsid),
  KEY ctime (ctime),
  KEY cip (cip)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_news_search (
  seaid int(11) unsigned NOT NULL auto_increment,
  seaword varchar(255) NOT NULL default '',
  seaip varchar(255) NOT NULL default '',
  seatime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (seaid),
  KEY seaip (seaip),
  KEY seatime (seatime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_news_tag (
  tagid int(11) unsigned NOT NULL auto_increment,
  tagcpu varchar(255) NOT NULL default '',
  tagword varchar(255) NOT NULL default '',
  tagrating smallint(3) unsigned NOT NULL default '0',
  PRIMARY KEY  (tagid),
  KEY tagrating (tagrating),
  KEY tagcpu (tagcpu)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_news_user (
  newsid int(11) unsigned NOT NULL auto_increment,
  catid int(11) unsigned NOT NULL default '0',
  userid int(11) unsigned NOT NULL default '0',
  public int(11) unsigned NOT NULL default '0',
  title varchar(255) NOT NULL default '',
  textnews text NOT NULL,
  image varchar(255) NOT NULL default '',
  image_thumb varchar(255) NOT NULL default '',
  PRIMARY KEY  (newsid)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_polling (
  pollid int(11) NOT NULL auto_increment,
  poll_act enum('yes','no') NOT NULL default 'no',
  poll_start int(11) unsigned NOT NULL default '0',
  poll_finish int(11) unsigned NOT NULL default '0',
  poll_only enum('all','user') NOT NULL default 'all',
  poll_title varchar(255) NOT NULL default '',
  poll_decs text NOT NULL,
  poll_ajax enum('no','yes') NOT NULL default 'no',
  PRIMARY KEY  (pollid),
  KEY poll_act (poll_act),
  KEY poll_start (poll_start),
  KEY poll_finish (poll_finish)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_polling_comment (
  comid int(11) unsigned NOT NULL auto_increment,
  pollid int(11) unsigned NOT NULL default '0',
  userid int(11) NOT NULL default '0',
  ctime int(11) unsigned NOT NULL default '0',
  cname varchar(50) NOT NULL default '',
  ctitle varchar(255) NOT NULL default '',
  ctext text NOT NULL,
  cip varchar(20) NOT NULL default '',
  PRIMARY KEY  (comid),
  KEY pollid (pollid),
  KEY ctime (ctime),
  KEY cip (cip)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_polling_vals (
  valsid int(11) unsigned NOT NULL auto_increment,
  pollid int(11) unsigned NOT NULL default '0',
  vals_title text NOT NULL,
  vals_voices int(11) unsigned NOT NULL default '0',
  vals_color varchar(6) NOT NULL default '',
  posit smallint(2) unsigned NOT NULL default '0',
  PRIMARY KEY  (valsid),
  KEY pollid (pollid)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_polling_vote (
  voteid int(11) unsigned NOT NULL auto_increment,
  pollid int(11) unsigned NOT NULL default '0',
  userid int(11) unsigned NOT NULL default '0',
  votedate int(11) unsigned NOT NULL default '0',
  voteip varchar(255) NOT NULL default '',
  PRIMARY KEY  (voteid)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_settings (
  setid int(11) unsigned NOT NULL auto_increment,
  setopt varchar(255) NOT NULL default '',
  setname varchar(255) NOT NULL default '',
  setval mediumtext NOT NULL,
  setmark int(1) unsigned NOT NULL default '0',
  setlang varchar(255) NOT NULL default '',
  setcode mediumtext NOT NULL,
  setvalid mediumtext NOT NULL,
  PRIMARY KEY  (setid),
  FULLTEXT KEY setname (setname)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_smilie (
  smid int(11) unsigned NOT NULL auto_increment,
  smcode varchar(10) NOT NULL default '',
  smalt varchar(100) NOT NULL default '',
  smimg varchar(100) NOT NULL default '',
  posit mediumint(5) NOT NULL default '0',
  PRIMARY KEY  (smid)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_statis_ref (
  refid int(11) unsigned NOT NULL auto_increment,
  refdate int(11) unsigned NOT NULL default '0',
  refurl text NOT NULL,
  hits int(11) unsigned default '0',
  PRIMARY KEY  (refid),
  KEY refdate (refdate)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_subscribe_archive (
  archivid int(11) unsigned NOT NULL auto_increment,
  title varchar(255) NOT NULL default '',
  mail varchar(255) NOT NULL default '',
  `text` text NOT NULL,
  formats int(1) unsigned NOT NULL default '0',
  ignores enum('no','yes') NOT NULL default 'no',
  `status` enum('un','finish') NOT NULL default 'un',
  send int(11) unsigned NOT NULL default '0',
  total int(11) unsigned NOT NULL default '0',
  step int(5) unsigned NOT NULL default '0',
  PRIMARY KEY  (archivid)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_subscribe_users (
  subuserid int(11) unsigned NOT NULL auto_increment,
  subname varchar(255) NOT NULL default '',
  submail varchar(255) NOT NULL default '',
  subformat int(1) unsigned NOT NULL default '0',
  subcode varchar(11) NOT NULL default '',
  subactive int(1) unsigned NOT NULL default '0',
  regtime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (subuserid),
  KEY subcode (subcode)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_users (
  userid int(11) unsigned NOT NULL auto_increment,
  uname varchar(50) NOT NULL default '',
  upass varchar(32) NOT NULL default '',
  umail varchar(50) NOT NULL default '',
  regdate int(11) unsigned NOT NULL default '0',
  lastvisit int(11) unsigned NOT NULL default '0',
  icq varchar(15) NOT NULL default '',
  msn varchar(50) NOT NULL default '',
  www varchar(250) NOT NULL default '',
  newpass varchar(32) NOT NULL default '',
  activate varchar(11) NOT NULL default '0',
  active int(1) unsigned NOT NULL default '0',
  blocked int(1) unsigned NOT NULL default '0',
  PRIMARY KEY  (userid),
  KEY username (uname),
  KEY active (active),
  KEY blocked (blocked)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_users_field (
  userid int(11) unsigned NOT NULL auto_increment,
  userfield text NOT NULL,
  PRIMARY KEY  (userid)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_video (
  downid int(11) unsigned NOT NULL auto_increment,
  catid int(11) unsigned NOT NULL default '0',
  public int(11) unsigned NOT NULL default '0',
  stpublic int(11) unsigned NOT NULL default '0',
  unpublic int(11) unsigned NOT NULL default '0',
  `cpu` varchar(255) NOT NULL default '',
  locurl text NOT NULL,
  exturl text NOT NULL,
  extsize varchar(255) NOT NULL default '',
  descript text NOT NULL,
  keywords text NOT NULL,
  title varchar(255) NOT NULL default '',
  textshort text NOT NULL,
  textmore longtext NOT NULL,
  textnotice text NOT NULL,
  mirrorsname text NOT NULL,
  mirrorsurl text NOT NULL,
  relisdown varchar(255) NOT NULL default '',
  authdown varchar(255) NOT NULL default '',
  sitedown varchar(255) NOT NULL default '',
  maildown varchar(255) NOT NULL default '',
  image varchar(255) NOT NULL default '',
  image_thumb varchar(255) NOT NULL default '',
  image_align enum('left','right') NOT NULL default 'left',
  image_alt varchar(255) NOT NULL default '',
  hits int(11) unsigned NOT NULL default '0',
  trans int(11) unsigned NOT NULL default '0',
  lastdown int(11) unsigned NOT NULL default '0',
  rating int(11) unsigned NOT NULL default '0',
  totalrating int(11) unsigned NOT NULL default '0',
  act enum('yes','no') NOT NULL default 'yes',
  acc enum('all','user') NOT NULL default 'all',
  listid int(11) unsigned NOT NULL default '0',
  comments int(11) unsigned NOT NULL default '0',
  tags varchar(255) NOT NULL default '',
  PRIMARY KEY  (downid),
  KEY catid (catid),
  KEY public (public),
  KEY stpublic (stpublic),
  KEY unpublic (unpublic)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_video_broken (
  brokid int(11) unsigned NOT NULL auto_increment,
  downid int(11) unsigned NOT NULL default '0',
  brokip varchar(255) NOT NULL default '',
  broktime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (brokid),
  KEY downid (downid),
  KEY brokip (brokip),
  KEY broktime (broktime)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_video_cat (
  catid int(11) unsigned NOT NULL auto_increment,
  parentid int(11) unsigned NOT NULL default '0',
  catcpu varchar(255) NOT NULL default '',
  catname varchar(255) NOT NULL default '',
  catdesc text NOT NULL,
  posit int(11) unsigned NOT NULL default '0',
  icon varchar(255) NOT NULL default '',
  access enum('all','user') NOT NULL default 'all',
  sort varchar(11) NOT NULL default 'newsid',
  ord enum('asc','desc') NOT NULL default 'asc',
  rss enum('yes','no') NOT NULL default 'yes',
  total int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (catid),
  KEY cid (catid),
  KEY title (catname)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_video_comment (
  comid int(11) unsigned NOT NULL auto_increment,
  downid int(11) unsigned NOT NULL default '0',
  userid int(11) unsigned NOT NULL default '0',
  ctime int(11) unsigned NOT NULL default '0',
  cname varchar(50) NOT NULL default '',
  ctitle varchar(255) NOT NULL default '',
  ctext text NOT NULL,
  cip varchar(20) NOT NULL default '',
  PRIMARY KEY  (comid),
  KEY downid (downid),
  KEY ctime (ctime),
  KEY cip (cip)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_video_rating (
  ratingid int(11) unsigned NOT NULL auto_increment,
  downid int(11) unsigned NOT NULL default '0',
  ratingip varchar(255) NOT NULL default '',
  ratingtime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (ratingid),
  KEY downid (downid),
  KEY ratingip (ratingip),
  KEY ratingtime (ratingtime)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE danny_video_search (
  seaid int(11) unsigned NOT NULL auto_increment,
  seaword varchar(255) NOT NULL default '',
  seaip varchar(255) NOT NULL default '',
  seatime int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (seaid),
  KEY seaip (seaip),
  KEY seatime (seatime)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE danny_video_tag (
  tagid int(11) unsigned NOT NULL auto_increment,
  tagcpu varchar(255) NOT NULL default '',
  tagword varchar(255) NOT NULL default '',
  tagrating smallint(3) unsigned NOT NULL default '0',
  PRIMARY KEY  (tagid),
  KEY tagrating (tagrating),
  KEY tagcpu (tagcpu)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
