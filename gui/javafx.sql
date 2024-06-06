-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 06, 2024 at 03:43 AM
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
-- Database: `uaspbo`
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
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `id_cart` int(11) NOT NULL,
  `id_user` int(255) NOT NULL,
  `nama_biota` text NOT NULL,
  `jenis_biota` text NOT NULL,
  `harga_biota` int(255) NOT NULL,
  `quantity` int(255) NOT NULL,
  `total_harga` int(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `hewan`
--

CREATE TABLE `hewan` (
  `id` int(11) NOT NULL,
  `nama` text NOT NULL,
  `jenis` text NOT NULL,
  `harga` int(255) NOT NULL,
  `stok` int(255) NOT NULL,
  `status` text NOT NULL,
  `tanggal` date NOT NULL,
  `gambar` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `hewan`
--

INSERT INTO `hewan` (`id`, `nama`, `jenis`, `harga`, `stok`, `status`, `tanggal`, `gambar`) VALUES
(1, 'Ikan Dory', 'Ikan', 20000, 30, 'Tersedia', '2024-06-05', 'file:/C:/Users/Toby/Desktop/ImgPBO/Blue%20Tang.jpg'),
(2, 'ClownFish', 'Ikan', 20000, 0, 'Habis', '2024-06-07', 'file:/C:/Users/Toby/Desktop/ImgPBO/IkanBadut.png');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id_transaksi` int(11) NOT NULL,
  `id_user` int(255) NOT NULL,
  `nama` text NOT NULL,
  `jenis` text NOT NULL,
  `harga` int(255) NOT NULL,
  `jumlah` int(255) NOT NULL,
  `total_harga` int(255) NOT NULL,
  `tanggal` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id_transaksi`, `id_user`, `nama`, `jenis`, `harga`, `jumlah`, `total_harga`, `tanggal`) VALUES
(1, 2, 'ClownFish', 'Ikan', 20000, 30, 600000, '2024-06-05');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `akun`
--
ALTER TABLE `akun`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`id_cart`);

--
-- Indexes for table `hewan`
--
ALTER TABLE `hewan`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id_transaksi`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `akun`
--
ALTER TABLE `akun`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `cart`
--
ALTER TABLE `cart`
  MODIFY `id_cart` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `hewan`
--
ALTER TABLE `hewan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id_transaksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
