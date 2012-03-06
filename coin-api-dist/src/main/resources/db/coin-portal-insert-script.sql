--
-- Database: `coin_portal`
--

--
-- Dumping data for table `gadgetdefinition`
--

INSERT INTO `gadgetdefinition` (`id`,`added`,`approved`,`author`,`author_email`,`description`,`install_count`,`screenshot`,`supports_groups`,`supportssso`,`thumbnail`,`title`,`url`)
VALUES
	(1, '2010-09-16 15:06:05', 'F', 'Google', 'noreply@google.com', 'Daily horoscopes and Astrosync courtesy', 3952, '/coin/media/gadget_example.png', 'F', 'F', NULL, 'Horoscope', 'http://www.google.com/ig/modules/horoscope.xml'),
	(2, '2010-10-03 13:13:00', 'F', 'Francois', NULL, 'Open Video Platform\n', 3791, '/coin/media/gadget_example.png', 'F', 'F', NULL, 'Open Video Player', 'http://frkosp.wind.surfnet.nl/fkooman/ovp/opensocial-dev.xml'),
	(3, '2010-10-07 11:57:00', 'F', 'SURFmedia', 'noreply@surfmedia.nl', 'SURFmedia list gadget. See you favourite list, recently uploaded and public videos in a list.', 3739, '/coin/media/surfmedia.png', 'F', 'F', NULL, 'SURFmedia list', 'http://www.integration.surfmedia.nl/gadget/xml/lists'),
	(4, '2010-10-08 09:42:00', 'F', 'SURFmedia', 'noreply@surfmedia.nl', 'SURFmedia gadget. Plays SURFmedia videos from http://www.integration.surfmedia.nl', 3793, '/coin/media/surfmedia.png', 'F', 'F', NULL, 'SURFmedia player', 'http://www.integration.surfmedia.nl/gadget/xml/player'),
	(5, '2010-10-02 11:42:00', 'F', 'JPRmedia', '', 'De officiele buienradar gadget. Met het weer, weerbericht, weersvoorspelling, weersvooruitzichten, minimum en maximum temperatuur, zon en neerslag en natuurlijk de buienradar.', 3779, 'http://www.gmodules.com/gadgets/proxy?refresh=86400&url=http://gadgets.videgro.net/images/gadgetBuienradarThumbnail.png&container=ig&gadget=http://gadgets.videgro.net/igoogle/buienradar.xml', 'F', 'F', NULL, 'Buienradar', 'http://igooglegadget.buienradar.nl/buienradar.xml'),
	(6, '2010-10-02 08:42:00', 'F', 'Christiaan', '', 'View your groups\n', 3786, '/coin/media/gadget_example.png', 'T', 'F', NULL, 'Osapi groups', 'http://www.2move-it.nl/xml/osapigroups.xml'),
	(7, '2010-10-04 20:00:00', 'F', 'SURFnet', '', 'Notifications\ngadget', 3790, '/coin/media/surfnet.png', 'T', 'T', '', 'Notifications to team', 'https://gui.dev.coin.surf.net/coin/gadgets/notifications.xml'),
	(8, '2010-10-22 15:53:51', 'F', 'Christiaan', 's', 'Test OAuth by giving the gadget access to your Google contact list', 3660, '/coin/media/gadget_example.png', 'F', 'T', '', 'Google contacts (OAuth)', 'http://gadgets.steinwelberg.nl/christiaanoauth.xml'),
	(9, '2010-09-17 20:00:00', 'F', 'My experiments', '', 'My experiment', 3740, '/coin/media/myexperiment.png', 'F', 'F', '', 'My experiment', 'http://hosting.gmodules.com/ig/gadgets/file/112390767058175733214/myEx.xml'),
	(10, '2010-10-05 13:13:00', 'F', 'Franscois', NULL, 'See list of conferences planned for your team.\n', 3731, '/coin/media/bigbluebutton.png', 'T', 'T', NULL, 'Big Blue Button', 'http://frkosp.wind.surfnet.nl/fkooman/ovp/bigbluebutton.xml'),
	(11, '2010-10-05 13:13:00', 'F', 'Webex', NULL, 'See the conferences of webex\n', 3693, '/coin/media/gadget_example.png', 'F', 'F', NULL, 'Webex', 'http://surfnet.webex.com/mw0306lb/mywebex/googleWidget.do?siteurl=surfnet'),
	(12, '2010-10-28 11:09:00', 'F', 'SURFnet', 'noreply@surfnet.nl', 'See and manage your teams in the SURFteams gadget', 3721, '/coin/media/surfnet.png', 'T', 'T', NULL, 'SURFteams', 'https://gui.dev.coin.surf.net/teams/media/teams.xml'),
	(13, '2010-09-17 20:00:00', 'F', 'Alfresco', '', 'Alfresco', 3665, '/coin/media/alfresco.gif', 'F', 'F', '', 'Alfresco folder browser', 'http://alfresco-coin.igi.nl:8080/alfresco/d/d/workspace/SpacesStore/f552304e-69a9-450a-9f09-0d83280dc051/Alfresco_Browser.xml'),
	(14, '2010-09-17 20:00:00', 'F', 'Alfresco', '', 'Alfresco', 3643, '/coin/media/alfresco.gif', 'F', 'F', '', 'Alfresco task list', 'http://alfresco-coin.igi.nl:8080/alfresco/d/d/workspace/SpacesStore/7aa5343c-2877-40cd-a5b4-3d8b28fbf3ab/Alfresco_Tasklist.xml'),
	(15, '2010-10-28 11:09:00', 'F', 'SURFnet', 'noreply@surfnet.nl', 'Test gadget Sotero', 3746, '/coin/media/surfnet.png', 'T', 'T', NULL, 'Zotero gadget', 'https://zotero.coin.surfnetlabs.nl/test/zotero-widget.xml');
