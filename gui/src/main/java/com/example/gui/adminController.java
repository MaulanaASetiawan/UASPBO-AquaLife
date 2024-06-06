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
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class adminController implements Initializable {
    @FXML
    private Button btnCancel;

    @FXML
    private Button btnHapus;

    @FXML
    private Button btnSimpan;

    @FXML
    private Button btnUbah;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnUpload;

    @FXML
    private ComboBox<String> cbJenis;

    @FXML
    private DatePicker dtTanggal;

    @FXML
    private RadioButton rbTersedia;

    @FXML
    private RadioButton rbLimited;

    @FXML
    private RadioButton rbHabis;

    @FXML
    private TextField txtHarga;

    @FXML
    private TextField txtNama;

    @FXML
    private TextField txtStok;

    @FXML
    private TableView<Hewan> tableView;

    @FXML
    private TableColumn<Hewan, Integer> colId;

    @FXML
    private TableColumn<Hewan, String> colNama;

    @FXML
    private TableColumn<Hewan, String> colJenis;

    @FXML
    private TableColumn<Hewan, Integer> colHarga;

    @FXML
    private TableColumn<Hewan, Integer> colStok;

    @FXML
    private TableColumn<Hewan, String> colStatus;

    @FXML
    private TableColumn<Hewan, String> colTanggal;

    @FXML
    private ImageView img;

    private static Connection conn = dbconn.connect();
    private static PreparedStatement pstmt;
    private static ResultSet rs;

    private ObservableList<Hewan> hewanList;
    private String imagePath;

    private void conn() {
        if (conn == null) {
            System.out.println("Koneksi Gagal");
            System.exit(1);
        }
    }

    String selectStatus() {
        if (rbTersedia.isSelected()) {
            return "Tersedia";
        } else if (rbLimited.isSelected()) {
            return "Limited";
        } else if (rbHabis.isSelected()) {
            return "Habis";
        }
        return null;
    }

    private boolean isValidInput() {
        if (
                txtNama.getText().isEmpty() ||
                txtHarga.getText().isEmpty() ||
                txtStok.getText().isEmpty() ||
                cbJenis.getValue() == null ||
                dtTanggal.getValue() == null ||
                (!rbTersedia.isSelected() && !rbLimited.isSelected() && !rbHabis.isSelected()) ||
                imagePath == null || imagePath.isEmpty()
        ) {
            showAlert("Harap lengkapi semua field!", Alert.AlertType.WARNING);

            return false;
        }
        return true;
    }

    private boolean isNamaUnique(String nama) throws SQLException {
        String query = "SELECT COUNT(*) FROM hewan WHERE nama = ?";
        pstmt = conn.prepareStatement(query);
        pstmt.setString(1, nama);
        ResultSet resultSet = pstmt.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count == 0;
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imagePath = selectedFile.toURI().toString();
            img.setImage(new Image(imagePath));
        }
    }

    private void add() throws SQLException {
        conn();
        if (!isValidInput()) return;

        String nama = txtNama.getText();
        int harga = Integer.parseInt(txtHarga.getText());
        int stok = Integer.parseInt(txtStok.getText());
        String status = selectStatus();
        String jenis = cbJenis.getValue();
        String tanggal = dtTanggal.getValue().toString();

        if (!isNamaUnique(nama)) {
            showAlert("Nama sudah ada!", Alert.AlertType.ERROR);
            return;
        }

        if(harga < 0 || stok < 0){
            showAlert("Value tidak boleh negatif", Alert.AlertType.ERROR);
            return;
        }

        if(harga > 10000000 || stok > 1000){
            showAlert("Value terlalu tinggi", Alert.AlertType.ERROR);
            return;
        }

        String query = "INSERT INTO hewan (nama, jenis, harga, stok, status, tanggal, gambar) VALUES (?, ?, ?, ?, ?, ?, ?)";
        pstmt = conn.prepareStatement(query);
        pstmt.setString(1, nama);
        pstmt.setString(2, jenis);
        pstmt.setInt(3, harga);
        pstmt.setInt(4, stok);
        pstmt.setString(5, status);
        pstmt.setString(6, tanggal);
        pstmt.setString(7, imagePath);
        pstmt.executeUpdate();
        clear();
        showAlert("Data berhasil disimpan", Alert.AlertType.INFORMATION);
        loadData();
    }

    private void update() throws SQLException {
        conn();
        if (!isValidInput()) return;
        String nama = txtNama.getText();
        int harga = Integer.parseInt(txtHarga.getText());
        int stok = Integer.parseInt(txtStok.getText());
        String status = selectStatus();
        String jenis = cbJenis.getValue();
        String tanggal = dtTanggal.getValue().toString();

        Hewan selectedHewan = tableView.getSelectionModel().getSelectedItem();
        if (selectedHewan == null) {
            showAlert("Pilih data yang akan diubah", Alert.AlertType.WARNING);
            return;
        }
        if (!isNamaUnique(nama) && !nama.equals(selectedHewan.getNama())) {
            showAlert("Nama sudah ada!", Alert.AlertType.ERROR);
            return;
        }

        if(harga < 0 || stok < 0){
            showAlert("Value tidak boleh negatif", Alert.AlertType.ERROR);
            return;
        }

        String query = "UPDATE hewan SET nama=?, jenis=?, harga=?, stok=?, status=?, tanggal=?, gambar=? WHERE id=?";
        pstmt = conn.prepareStatement(query);
        pstmt.setString(1, nama);
        pstmt.setString(2, jenis);
        pstmt.setInt(3, harga);
        pstmt.setInt(4, stok);
        pstmt.setString(5, status);
        pstmt.setString(6, tanggal);
        pstmt.setString(7, imagePath);
        pstmt.setInt(8, selectedHewan.getId());
        pstmt.executeUpdate();
        clear();
        showAlert("Data berhasil diubah", Alert.AlertType.INFORMATION);
        loadData();
    }

    private void delete() throws SQLException {
        conn();
        Hewan selectedHewan = tableView.getSelectionModel().getSelectedItem();
        if (selectedHewan == null) {
            showAlert("Pilih data yang akan dihapus", Alert.AlertType.WARNING);
            return;
        }

        String query = "DELETE FROM hewan WHERE id=?";
        pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, selectedHewan.getId());
        pstmt.executeUpdate();
        clear();
        showAlert("Data berhasil dihapus", Alert.AlertType.INFORMATION);
        loadData();
    }

    private void loadData() {
        conn();
        hewanList = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM hewan";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Hewan hewan = new Hewan(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("jenis"),
                        rs.getInt("harga"),
                        rs.getInt("stok"),
                        rs.getString("status"),
                        rs.getString("tanggal"),
                        rs.getString("gambar")
                );
                hewanList.add(hewan);
            }
            tableView.setItems(hewanList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void clear() {
        txtNama.clear();
        txtHarga.clear();
        txtStok.clear();
        cbJenis.setValue(null);
        dtTanggal.setValue(null);
        rbTersedia.setSelected(false);
        rbLimited.setSelected(false);
        rbHabis.setSelected(false);
        img.setImage(null);
        imagePath = null;
        tableView.getSelectionModel().clearSelection();
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icon.png"))));
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> jenisList = FXCollections.observableArrayList(
                "Ikan", "Udang", "Kepiting", "Lobster", "Cumi-cumi", "Gurita",
                "Kerang", "Tiram", "Abalone", "Terumbu Karang", "Anemon Laut",
                "Moluska", "Krustasea"
        );
        cbJenis.setItems(jenisList);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colJenis.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        loadData();

        ToggleGroup statusGroup = new ToggleGroup();
        rbTersedia.setToggleGroup(statusGroup);
        rbLimited.setToggleGroup(statusGroup);
        rbHabis.setToggleGroup(statusGroup);

        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                txtNama.setText(newValue.getNama());
                txtHarga.setText(String.valueOf(newValue.getHarga()));
                txtStok.setText(String.valueOf(newValue.getStok()));
                cbJenis.setValue(newValue.getJenis());
                dtTanggal.setValue(java.time.LocalDate.parse(newValue.getTanggal()));
                rbTersedia.setSelected(false);
                rbLimited.setSelected(false);
                rbHabis.setSelected(false);

                switch (newValue.getStatus()) {
                    case "Tersedia":
                        rbTersedia.setSelected(true);
                        break;
                    case "Limited":
                        rbLimited.setSelected(true);
                        break;
                    case "Habis":
                        rbHabis.setSelected(true);
                        break;
                }

                imagePath = newValue.getImage();
                if (imagePath != null && !imagePath.isEmpty()) {
                    img.setImage(new Image(imagePath));
                } else {
                    img.setImage(null);
                }
            }
        });

        txtHarga.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));

        txtStok.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));

        btnSimpan.setOnAction(actionEvent -> {
            try {
                add();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        btnUbah.setOnAction(actionEvent -> {
            try {
                update();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        btnHapus.setOnAction(actionEvent -> {
            try {
                delete();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        btnCancel.setOnAction(actionEvent -> {
            clear();
        });

        btnLogout.setOnAction(ActionEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("log.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    scene.getStylesheets().add(HelloApplication.class.getResource("/stylesheet/log.css").toExternalForm());
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

        btnUpload.setOnAction(actionEvent -> handleUploadImage());
    }
}