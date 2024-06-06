package com.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Optional;

public class userController {
    @FXML
    private TableColumn<Hewan, String> colNama;

    @FXML
    private TableView<Hewan> tableView;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnCart;

    @FXML
    private Button btnAddtoCart;

    @FXML
    private Label lblNama;

    @FXML
    private Label lblJenis;

    @FXML
    private Label lblHarga;

    @FXML
    private Label lblStok;

    @FXML
    private Label lblStatus;

    @FXML
    private ImageView imgBiota;

    private static Connection conn = dbconn.connect();
    private static PreparedStatement pstmt;
    private static ResultSet rs;

    private ObservableList<Hewan> hewanList;
    private static int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static int getUserId() {
        return userId;
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icon.png"))));
        alert.show();
    }

    private void updatePanel(Hewan hewan) {
        lblNama.setText("Nama : " + hewan.getNama());
        lblJenis.setText("Jenis : " + hewan.getJenis());
        lblHarga.setText("Harga : " + hewan.getHarga());
        lblStok.setText("Stok : " + hewan.getStok());
        lblStatus.setText("Status : " + hewan.getStatus());
        imgBiota.setImage(new Image(hewan.getImage()));
    }

    private void addToCart(Hewan hewan, int quantity, int userId) {
        try {
            String stockQuery = "SELECT stok FROM hewan WHERE nama = ?";
            pstmt = conn.prepareStatement(stockQuery);
            pstmt.setString(1, hewan.getNama());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int availableStock = rs.getInt("stok");
                if (quantity > availableStock) {
                    showAlert("Jumlah yang diminta melebihi stok yang tersedia!", Alert.AlertType.WARNING);
                    return;
                }
            }

            String query = "SELECT * FROM cart WHERE id_user = ? AND nama_biota = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, hewan.getNama());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int existingQuantity = rs.getInt("quantity");
                int newQuantity = existingQuantity + quantity;
                int total_harga = hewan.getHarga() * newQuantity;

                if (newQuantity > rs.getInt("quantity")) {
                    showAlert("Jumlah yang diminta melebihi stok yang tersedia!", Alert.AlertType.WARNING);
                    return;
                }

                updateCart(hewan, newQuantity, total_harga, userId);
            } else {
                int total_harga = hewan.getHarga() * quantity;
                String queryInsert = "INSERT INTO cart (id_user, nama_biota, jenis_biota, harga_biota, quantity, total_harga) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(queryInsert);
                pstmt.setInt(1, userId);
                pstmt.setString(2, hewan.getNama());
                pstmt.setString(3, hewan.getJenis());
                pstmt.setInt(4, hewan.getHarga());
                pstmt.setInt(5, quantity);
                pstmt.setInt(6, total_harga);
                pstmt.executeUpdate();
            }
            showAlert("Barang berhasil ditambahkan ke keranjang!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Gagal menambahkan item ke keranjang!", Alert.AlertType.ERROR);
        }
    }

    private void updateCart(Hewan hewan, int quantity, int total_harga, int userId) {
        try {
            String query = "UPDATE cart SET quantity = ?, total_harga = ? WHERE id_user = ? AND nama_biota = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, total_harga);
            pstmt.setInt(3, userId);
            pstmt.setString(4, hewan.getNama());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Gagal memperbarui item di keranjang!", Alert.AlertType.ERROR);
        }
    }

    private void showQuantityDialog(Hewan hewan) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Tambahkan ke Keranjang");
        dialog.setHeaderText("Masukkan Jumlah Barang");
        dialog.setContentText("Jumlah:");

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icon.png"))));

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty > 0) {
                    String stockQuery = "SELECT stok FROM hewan WHERE nama = ?";
                    pstmt = conn.prepareStatement(stockQuery);
                    pstmt.setString(1, hewan.getNama());
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int availableStock = rs.getInt("stok");
                        if (qty > availableStock) {
                            showAlert("Jumlah yang diminta melebihi stok yang tersedia!", Alert.AlertType.WARNING);
                            return;
                        }
                    }
                    addToCart(hewan, qty, userId);
                } else {
                    showAlert("Jumlah harus lebih besar dari 0!", Alert.AlertType.WARNING);
                }
            } catch (NumberFormatException e) {
                showAlert("Jumlah yang dimasukkan tidak valid!", Alert.AlertType.ERROR);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Gagal memeriksa stok!", Alert.AlertType.ERROR);
            }
        });
    }

    public void initialize() {
        try {
            hewanList = FXCollections.observableArrayList();
            String query = "SELECT * FROM hewan";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                hewanList.add(
                        new Hewan(
                                rs.getInt(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getInt(4),
                                rs.getInt(5),
                                rs.getString(6),
                                rs.getString(7),
                                rs.getString(8)
                        ));
            }

            colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
            tableView.setItems(hewanList);
            tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updatePanel(newValue);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnAddtoCart.setOnAction(actionEvent -> {
            Hewan selectedHewan = tableView.getSelectionModel().getSelectedItem();
            if (selectedHewan != null) {
                showQuantityDialog(selectedHewan);
            } else {
                showAlert("Silakan pilih item dari tabel!", Alert.AlertType.WARNING);
            }
        });

        btnCart.setOnAction(actionEvent -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("cart.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("/stylesheet/admin.css")).toExternalForm());

                cartController cartController = fxmlLoader.getController();
                cartController.setUserId(userId);
                cartController.setCartList(cartController.getCartList());

                Image img = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/assets/icon.png")));
                Stage stage = (Stage) btnCart.getScene().getWindow();
                stage.getIcons().add(img);
                stage.setResizable(false);
                stage.setTitle("AquaLife");
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnLogout.setOnAction(ActionEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Apakah Anda yakin ingin keluar?");

            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icon.png"))));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("log.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("/stylesheet/log.css")).toExternalForm());

                    Image img = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/assets/icon.png")));
                    Stage stage = (Stage) btnLogout.getScene().getWindow();
                    stage.getIcons().add(img);
                    stage.setResizable(false);
                    stage.setTitle("AquaLife");
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
