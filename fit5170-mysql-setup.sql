-- Host: localhost
-- Generation Time: Apr 15, 2014 at 10:42 PM
-- Server version: 5.5.9
-- PHP Version: 5.3.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `fit5170`
--

-- --------------------------------------------------------

--
-- Table structure for table `Booking`
--

CREATE TABLE `Booking` (
  `bookingID` int(11) NOT NULL AUTO_INCREMENT,
  `roomTypeID` int(11) NOT NULL,
  `clientID` int(11) NOT NULL,
  `checkInDate` datetime NOT NULL,
  `checkOutDate` datetime NOT NULL,
  `roomsBooked` int(11) NOT NULL,
  `hotelID` int(11) NOT NULL,
  PRIMARY KEY (`bookingID`),
  KEY `roomTypeID` (`roomTypeID`),
  KEY `clientID` (`clientID`),
  KEY `hotelID` (`hotelID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=11 ;

--
-- Dumping data for table `Booking`
--

INSERT INTO `Booking` VALUES(4, 1, 2, '2014-04-22 05:29:23', '2014-04-26 05:29:29', 4, 1);
INSERT INTO `Booking` VALUES(5, 1, 4, '2014-04-16 00:00:00', '2014-04-19 00:00:00', 3, 1);
INSERT INTO `Booking` VALUES(6, 1, 5, '2014-07-16 00:00:00', '2014-07-18 00:00:00', 3, 1);
INSERT INTO `Booking` VALUES(7, 1, 6, '2014-07-05 00:00:00', '2014-07-08 00:00:00', 3, 1);
INSERT INTO `Booking` VALUES(8, 1, 7, '2014-04-17 00:00:00', '2014-04-29 00:00:00', 3, 1);
INSERT INTO `Booking` VALUES(9, 1, 8, '2014-05-11 00:00:00', '2014-05-12 00:00:00', 4, 1);
INSERT INTO `Booking` VALUES(10, 1, 9, '2014-05-19 00:00:00', '2014-05-25 00:00:00', 10, 1);

-- --------------------------------------------------------

--
-- Table structure for table `City`
--

CREATE TABLE `City` (
  `cityID` int(11) NOT NULL AUTO_INCREMENT,
  `cityName` varchar(30) NOT NULL,
  PRIMARY KEY (`cityID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `City`
--

INSERT INTO `City` VALUES(1, 'Melbourne');
INSERT INTO `City` VALUES(2, 'Sydney');
INSERT INTO `City` VALUES(3, 'Perth');
INSERT INTO `City` VALUES(4, 'Adelaide');
INSERT INTO `City` VALUES(5, 'Brisbane');
INSERT INTO `City` VALUES(6, 'Hobart');
INSERT INTO `City` VALUES(7, 'Canberra');

-- --------------------------------------------------------

--
-- Table structure for table `Client`
--

CREATE TABLE `Client` (
  `clientID` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(30) NOT NULL,
  `lastName` varchar(30) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` int(10) NOT NULL,
  `creditCardNo` varchar(16) NOT NULL,
  PRIMARY KEY (`clientID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Dumping data for table `Client`
--

INSERT INTO `Client` VALUES(2, 'Bhavik', 'Maneck', 'bmaneck@gmail.com', 431132908, '1234567812345678');
INSERT INTO `Client` VALUES(3, 'Bhavik', 'Maneck', 'bman3@studnet.monash', 908123419, '1234567898121234');
INSERT INTO `Client` VALUES(4, 'Joe', 'Smith', 'joe@smith.com', 1234124, '12341241241243');
INSERT INTO `Client` VALUES(5, 'Kate', 'SMith', 'kate@google.com', 190243843, '123412341234');
INSERT INTO `Client` VALUES(6, 'Bhavik', 'Maneck', 'bmaneck@gmail.com', 98701234, '1234123412341234');
INSERT INTO `Client` VALUES(7, 'Will', 'Wheaton', 'will@wheaton.com', 98785653, '1234123412341234');
INSERT INTO `Client` VALUES(8, 'Peter', 'Pan', 'peterpan@gmail.com', 98789878, '1234123412341234');
INSERT INTO `Client` VALUES(9, 'Pete', 'Mcoy', 'pete@mcoy.com', 12341234, '1234123412341234');

-- --------------------------------------------------------

--
-- Table structure for table `Hotel`
--

CREATE TABLE `Hotel` (
  `hotelID` int(11) NOT NULL AUTO_INCREMENT,
  `cityID` int(11) NOT NULL,
  `hotelChainID` int(11) NOT NULL,
  PRIMARY KEY (`hotelID`),
  KEY `cityID` (`cityID`),
  KEY `hotelChainID` (`hotelChainID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=16 ;

--
-- Dumping data for table `Hotel`
--

INSERT INTO `Hotel` VALUES(1, 1, 1);
INSERT INTO `Hotel` VALUES(2, 1, 2);
INSERT INTO `Hotel` VALUES(3, 1, 3);
INSERT INTO `Hotel` VALUES(4, 2, 1);
INSERT INTO `Hotel` VALUES(5, 2, 2);
INSERT INTO `Hotel` VALUES(6, 2, 3);
INSERT INTO `Hotel` VALUES(7, 2, 4);
INSERT INTO `Hotel` VALUES(8, 2, 5);
INSERT INTO `Hotel` VALUES(9, 3, 1);
INSERT INTO `Hotel` VALUES(10, 3, 2);
INSERT INTO `Hotel` VALUES(11, 3, 3);
INSERT INTO `Hotel` VALUES(12, 4, 1);
INSERT INTO `Hotel` VALUES(13, 4, 3);
INSERT INTO `Hotel` VALUES(14, 5, 1);
INSERT INTO `Hotel` VALUES(15, 5, 3);

-- --------------------------------------------------------

--
-- Table structure for table `HotelChain`
--

CREATE TABLE `HotelChain` (
  `hotelChainID` int(11) NOT NULL AUTO_INCREMENT,
  `hotelChainName` varchar(30) NOT NULL,
  PRIMARY KEY (`hotelChainID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `HotelChain`
--

INSERT INTO `HotelChain` VALUES(1, 'Hilton');
INSERT INTO `HotelChain` VALUES(2, 'Windsor');
INSERT INTO `HotelChain` VALUES(3, 'Novotel');
INSERT INTO `HotelChain` VALUES(4, 'Four Seasons');
INSERT INTO `HotelChain` VALUES(5, 'Shangri-La');

-- --------------------------------------------------------

--
-- Table structure for table `HotelRooms`
--

CREATE TABLE `HotelRooms` (
  `hotelID` int(11) NOT NULL,
  `roomTypeID` int(11) NOT NULL,
  `roomRate` float NOT NULL,
  `numberOfRooms` int(11) NOT NULL,
  PRIMARY KEY (`hotelID`,`roomTypeID`),
  KEY `hotelID` (`hotelID`),
  KEY `roomTypeID` (`roomTypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `HotelRooms`
--

INSERT INTO `HotelRooms` VALUES(1, 1, 150, 10);
INSERT INTO `HotelRooms` VALUES(1, 2, 250, 10);
INSERT INTO `HotelRooms` VALUES(1, 3, 450, 10);
INSERT INTO `HotelRooms` VALUES(2, 1, 150, 10);
INSERT INTO `HotelRooms` VALUES(2, 2, 250, 5);
INSERT INTO `HotelRooms` VALUES(3, 1, 120, 40);
INSERT INTO `HotelRooms` VALUES(3, 3, 400, 5);
INSERT INTO `HotelRooms` VALUES(4, 2, 400, 10);
INSERT INTO `HotelRooms` VALUES(5, 1, 159, 50);
INSERT INTO `HotelRooms` VALUES(6, 2, 300, 20);
INSERT INTO `HotelRooms` VALUES(6, 3, 550, 30);
INSERT INTO `HotelRooms` VALUES(7, 1, 120, 45);
INSERT INTO `HotelRooms` VALUES(8, 3, 500, 30);
INSERT INTO `HotelRooms` VALUES(9, 1, 130, 34);
INSERT INTO `HotelRooms` VALUES(9, 3, 500, 2);
INSERT INTO `HotelRooms` VALUES(10, 1, 150, 10);
INSERT INTO `HotelRooms` VALUES(11, 2, 300, 30);
INSERT INTO `HotelRooms` VALUES(12, 3, 500, 50);
INSERT INTO `HotelRooms` VALUES(13, 1, 123, 12);
INSERT INTO `HotelRooms` VALUES(14, 1, 100, 2);
INSERT INTO `HotelRooms` VALUES(15, 2, 250, 20);

-- --------------------------------------------------------

--
-- Table structure for table `Rooms`
--

CREATE TABLE `Rooms` (
  `roomTypeID` int(11) NOT NULL AUTO_INCREMENT,
  `roomTypeName` varchar(30) NOT NULL,
  PRIMARY KEY (`roomTypeID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `Rooms`
--

INSERT INTO `Rooms` VALUES(1, 'Standard');
INSERT INTO `Rooms` VALUES(2, 'Deluxe');
INSERT INTO `Rooms` VALUES(3, 'Executive');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Booking`
--
ALTER TABLE `Booking`
  ADD CONSTRAINT `Booking_ibfk_1` FOREIGN KEY (`roomTypeID`) REFERENCES `HotelRooms` (`roomTypeID`),
  ADD CONSTRAINT `Booking_ibfk_2` FOREIGN KEY (`clientID`) REFERENCES `Client` (`clientID`),
  ADD CONSTRAINT `Booking_ibfk_3` FOREIGN KEY (`hotelID`) REFERENCES `Hotel` (`hotelID`);

--
-- Constraints for table `Hotel`
--
ALTER TABLE `Hotel`
  ADD CONSTRAINT `Hotel_ibfk_1` FOREIGN KEY (`cityID`) REFERENCES `City` (`cityID`),
  ADD CONSTRAINT `Hotel_ibfk_2` FOREIGN KEY (`hotelChainID`) REFERENCES `HotelChain` (`hotelChainID`);

--
-- Constraints for table `HotelRooms`
--
ALTER TABLE `HotelRooms`
  ADD CONSTRAINT `HotelRooms_ibfk_1` FOREIGN KEY (`hotelID`) REFERENCES `Hotel` (`hotelID`),
  ADD CONSTRAINT `HotelRooms_ibfk_2` FOREIGN KEY (`roomTypeID`) REFERENCES `Rooms` (`roomTypeID`);