package application.controller;

import application.model.Album;
import application.model.Photo;
import application.model.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;

/**
 * 管理相册内照片的控制器
 */
public class AlbumController {
    @FXML
    private ListView<Photo> photoListView;
    
    @FXML
    private Label photoDetailsLabel;
    
    private Album album;
    
    public void setAlbum(Album album){
        this.album = album;
        refreshPhotoList();
    }
    
    @FXML
    private void initialize(){
        photoListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV != null) {
                photoDetailsLabel.setText(newV.getDetails());
            }
        });
    }
    
    private void refreshPhotoList(){
        ObservableList<Photo> photos = FXCollections.observableArrayList(album.getPhotos());
        photoListView.setItems(photos);
    }
    
    @FXML
    public void handleAddPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择照片");
        // 限制文件格式
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("图像文件", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File file = fileChooser.showOpenDialog(photoListView.getScene().getWindow());
        if(file != null) {
            Photo photo = new Photo(file.getAbsolutePath());
            // 弹出对话框输入照片标题
            TextInputDialog captionDialog = new TextInputDialog();
            captionDialog.setTitle("添加照片");
            captionDialog.setHeaderText("请输入照片标题：");
            captionDialog.showAndWait().ifPresent(caption -> photo.setCaption(caption));
            album.getPhotos().add(photo);
            // 保存所属用户数据（album.owner 在创建相册时已设置）
            DataStore.saveUser(album.getOwner());
            refreshPhotoList();
        }
    }
    
    @FXML
    public void handleDeletePhoto(ActionEvent event) {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if(selected == null){
            new Alert(Alert.AlertType.ERROR, "未选择照片！").showAndWait();
            return;
        }
        album.getPhotos().remove(selected);
        DataStore.saveUser(album.getOwner());
        refreshPhotoList();
    }
    
    @FXML
    public void handleBack(ActionEvent event) {
        // 关闭当前窗口
        photoListView.getScene().getWindow().hide();
    }
}

