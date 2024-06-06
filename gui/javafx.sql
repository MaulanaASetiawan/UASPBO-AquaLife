-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 29, 2024 at 06:20 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `javafx`
--

-- --------------------------------------------------------

--
-- Table structure for table `akun`
--

CREATE TABLE `akun` (
  `id` int(11) NOT NULL,
  `username` text NOT NULL,
  `password` text NOT NULL,
  `role` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `akun`
--

INSERT INTO `akun` (`id`, `username`, `password`, `role`) VALUES
(1, 'admin', 'admin', 'admin'),
(2, 'agus', 'agus123', 'user');

-- --------------------------------------------------------

--
-- Table structure for table `hewan`
--

CREATE TABLE `hewan` (
  `id` int(11) NOT NULL,
  `nama` text NOT NULL,
  `jenis` text NOT NULL,
  `habitat` text NOT NULL,
  `populasi` text NOT NULL,
  `tanggal` date NOT NULL,
  `gambar` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `hewan`
--

INSERT INTO `hewan` (`id`, `nama`, `jenis`, `habitat`, `populasi`, `tanggal`, `gambar`) VALUES
(1, 'Steller\'s Sea Cow', 'Mamalia Laut', 'Laut Bering dan Laut Okhotsk', 'Punah', '2009-05-06', 'file:/C:/Users/Toby/Desktop/ImgPBO/SeaCow.jpg'),
(2, 'Caribbean Monk Seal', 'Mamalia Laut', 'Karibia dan Teluk Meksiko', 'Punah', '2007-01-10', 'file:/C:/Users/Toby/Desktop/ImgPBO/Caribbean%20Monk.jpg'),
(3, 'Vaquita', 'Mamalia Laut', 'Teluk California, Meksiko', 'Terancam Punah', '2011-06-02', 'file:/C:/Users/Toby/Desktop/ImgPBO/Vaquita.jpg'),
(4, 'Hawksbill Turtle', 'Reptil Laut', 'Terumbu karang', 'Terancam Punah', '2020-03-15', 'file:/C:/Users/Toby/Desktop/ImgPBO/Hawksbill%20Turtle.jpg'),
(5, 'Sawfish', 'Ikan', 'Pantai tropis dan subtropis', 'Terancam Punah', '2019-09-11', 'file:/C:/Users/Toby/Desktop/ImgPBO/SawFish.jpg'),
(6, 'Great White Shark', 'Ikan', 'Lautan global', 'Stabil', '2023-08-14', 'file:/C:/Users/Toby/Desktop/ImgPBO/Great%20White%20Shark.jpg'),
(7, 'Green Sea Turtle', 'Reptil Laut', 'Perairan tropis dan subtropis', 'Stabil', '2024-02-12', 'file:/C:/Users/Toby/Desktop/ImgPBO/Green%20Sea%20Turtle.jpg'),
(8, 'Blue Tang', 'Ikan', 'Terumbu karang', 'Stabil', '2022-06-09', 'file:/C:/Users/Toby/Desktop/ImgPBO/Blue%20Tang.jpg'),
(9, 'Hiu Goblin', 'Ikan', 'Laut Dalam', 'Punah', '2008-10-15', 'file:/C:/Users/Toby/Desktop/ImgPBO/hiuGoblin.jpg'),
(10, 'Dugong', 'Mamalia Laut', 'Laut Tropis', 'Terancam Punah', '2022-04-16', 'file:/C:/Users/Toby/Desktop/ImgPBO/Dugong.jpg');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `akun`
--
ALTER TABLE `akun`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `hewan`
--
ALTER TABLE `hewan`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `akun`
--
ALTER TABLE `akun`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `hewan`
--
ALTER TABLE `hewan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
