package com.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

public class cartController implements Initializable {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnCheckout;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnBack;

    @FXML
    private TableColumn<cart, Integer> colId;

    @FXML
    private TableColumn<cart, String> colNama;

    @FXML
    private TableColumn<cart, String> colJenis;

    @FXML
    private TableColumn<cart, Integer> colHarga;

    @FXML
    private TableColumn<cart, Integer> colJumlah;

    @FXML
    private TableColumn<cart, Integer> colTotalHarga;

    @FXML
    private TableView<cart> tableView;

    private ObservableList<cart> cartList;
    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        try {
            cartList = getCartList();
            tableView.setItems(cartList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCartList(ObservableList<cart> cartList) {
        this.cartList = cartList;
        tableView.setItems(cartList);
    }

    public ObservableList<cart> getCartList() throws SQLException {
        ObservableList<cart> cartList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM cart WHERE id_user = ?";

        try (Connection conn = dbconn.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cartList.add(
                            new cart(
                                    rs.getInt("id_cart"),
                                    rs.getString("nama_biota"),
                                    rs.getString("jenis_biota"),
                                    rs.getInt("harga_biota"),
                                    rs.getInt("quantity"),
                                    rs.getInt("total_harga")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cartList;
    }

    private void handleCheckout() {
        ObservableList<cart> selectedItems = tableView.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Tidak ada item yang dipilih untuk di Chekcout.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        int totalAmount = selectedItems.stream().mapToInt(cart::getTotalHarga).sum();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Total Pembayaran: " + totalAmount + "\nLanjutkan Checkout?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                processTransaction(selectedItems);
            }
        });
    }

    private void processTransaction(ObservableList<cart> selectedItems) {
        try (Connection conn = dbconn.connect()) {
            conn.setAutoCommit(false);

            String insertSQL = "INSERT INTO transaksi (id_user, nama, jenis, harga, jumlah, total_harga, tanggal) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String deleteSQL = "DELETE FROM cart WHERE id_cart = ?";
            String updateStockSQL = "UPDATE hewan SET stok = stok - ? WHERE nama = ?";
            String checkStockSQL = "SELECT stok FROM hewan WHERE nama = ?";
            String updateStatusSQL = "UPDATE hewan SET status = 'Habis' WHERE nama = ? AND stok = 0";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
                 PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSQL);
                 PreparedStatement checkStockStmt = conn.prepareStatement(checkStockSQL);
                 PreparedStatement updateStatusStmt = conn.prepareStatement(updateStatusSQL)) {

                LocalDate date = getDateFromUser();
                boolean sufficientStock = true;

                for (cart Cart : selectedItems) {
                    checkStockStmt.setString(1, Cart.getNama());
                    try (ResultSet rs = checkStockStmt.executeQuery()) {
                        if (rs.next()) {
                            int currentStock = rs.getInt("stok");
                            if (Cart.getJumlah() > currentStock) {
                                sufficientStock = false;
                                break;
                            }
                        }
                    }
                }

                if (!sufficientStock) {
                    conn.rollback();
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Item ini mungkin sudah habis!.", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }

                for (cart Cart : selectedItems) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setString(2, Cart.getNama());
                    insertStmt.setString(3, Cart.getJenis());
                    insertStmt.setInt(4, Cart.getHarga());
                    insertStmt.setInt(5, Cart.getJumlah());
                    insertStmt.setInt(6, Cart.getTotalHarga());
                    insertStmt.setString(7, date.toString());
                    insertStmt.addBatch();

                    deleteStmt.setInt(1, Cart.getId());
                    deleteStmt.addBatch();

                    updateStockStmt.setInt(1, Cart.getJumlah());
                    updateStockStmt.setString(2, Cart.getNama());
                    updateStockStmt.addBatch();
                }

                insertStmt.executeBatch();
                deleteStmt.executeBatch();
                updateStockStmt.executeBatch();

                for (cart Cart : selectedItems) {
                    updateStatusStmt.setString(1, Cart.getNama());
                    updateStatusStmt.addBatch();
                }
                updateStatusStmt.executeBatch();

                conn.commit();

                cartList.removeAll(selectedItems);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Checkout Berhasil!, Silahkan Bayar ke nomor berikut +62-XXX-XXXX-XXXX", ButtonType.OK);
                alert.showAndWait();
            } catch (SQLException e) {
                conn.rollback();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error ketika checkout: " + e.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database connection error: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void handleDelete() {
        ObservableList<cart> selectedItems = tableView.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Tidak ada item yang dipilih untuk dihapus.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Apakah Anda yakin ingin menghapus item ini?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                deleteItems(selectedItems);
            }
        });
    }

    private void deleteItems(ObservableList<cart> selectedItems) {
        String deleteSQL = "DELETE FROM cart WHERE id_cart = ?";

        try (Connection conn = dbconn.connect();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL)) {

            for (cart Cart : selectedItems) {
                deleteStmt.setInt(1, Cart.getId());
                deleteStmt.addBatch();
            }

            deleteStmt.executeBatch();
            cartList.removeAll(selectedItems);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item berhasil dihapus!", ButtonType.OK);
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error ketika menghapus item: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private LocalDate getDateFromUser() {
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        return datePicker.getValue();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colJenis.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colTotalHarga.setCellValueFactory(new PropertyValueFactory<>("totalHarga"));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        btnCancel.setOnAction(e -> tableView.getSelectionModel().clearSelection());
        btnCheckout.setOnAction(e -> handleCheckout());
        btnDelete.setOnAction(e -> handleDelete());
        btnBack.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("user.fxml"));
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("/stylesheet/admin.css")).toExternalForm());

                userController.getUserId();

                Image img = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/assets/icon.png")));
                Stage stage = (Stage) btnBack.getScene().getWindow();
                stage.getIcons().add(img);
                stage.setResizable(false);
                stage.setTitle("AquaLife");
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
