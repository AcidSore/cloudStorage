package acidsore;

import io.netty.channel.Channel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class WindowController implements Initializable {
    @FXML
    Button send;
    @FXML
    Button download;
    @FXML
    TableView<MyFile> UserContent;
    @FXML
    TableView<MyFile> ServerContent;
    @FXML
    TableColumn<MyFile,String> FileNames;
    @FXML
    TableColumn<MyFile,String> StorageFileNames;

    @FXML
    private ObservableList<MyFile> ClientFiles = FXCollections.observableArrayList();
    @FXML
    private ObservableList<MyFile> StorageFiles = FXCollections.observableArrayList();

    private  Channel channel = NettyClient.getCurrentChannel(); // get the existing channel

    private  String clientStorage = "client_storage/";
    private File[] serverContent;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FileNames.setCellValueFactory(new PropertyValueFactory<MyFile,String>("name"));
        StorageFileNames.setCellValueFactory(new PropertyValueFactory<MyFile,String>("name"));
        ServerContent.setItems(StorageFiles);
        UserContent.setItems(ClientFiles);
        initData();

    }
    private void getServerAnswer(){
        serverContent = NettyClient.getInstance().getNettyClientHandler().list.getContent();
        for (int i=0;i<serverContent.length;i++) {
            if (serverContent[i].isFile()) {
                StorageFiles.add(new MyFile(serverContent[i].getName()));
            }
        }
    }

    private void initData(){
            NettyClient.getInstance().sendMsg("content ");
            File clientFolder = new File(clientStorage);
            File[] listOfClientsFile = clientFolder.listFiles();
            for (int i = 0; i < listOfClientsFile.length; i++) {
                if (listOfClientsFile[i].isFile()) {
                    ClientFiles.add(new MyFile(listOfClientsFile[i].getName()));
                }
            }
        NettyClient.getInstance().getNettyClientHandler().setDataAction(() -> getServerAnswer());
    }

    public void send(ActionEvent actionEvent) throws InterruptedException {
        String fileName = getData(UserContent);
        channel.attr(NettyClient.fn).set(fileName);
        String msg = "send"+" "+ fileName;
        NettyClient.getInstance().sendMsg(msg);
        NettyClient.getInstance().getNettyClientHandler().setReadyAction(()->refresh());
    }

    public void download(ActionEvent actionEvent) throws InterruptedException {
        String fileName = getData(ServerContent);
        channel.attr(NettyClient.fn).set(fileName);
        String msg = "dwnl"+" "+ fileName;
        NettyClient.getInstance().sendMsg(msg);
        NettyClient.getInstance().getNettyClientHandler().setFinishAction(()->refresh());
    }

    private String getData(TableView<MyFile> table){
        MyFile myFile = table.getSelectionModel().getSelectedItem();
        String data = myFile.getName();
        return data;
    }

    private void delete(String fileName){
        File file = new File(fileName);
        if(file.exists()) file.delete();
    }

    public void deleteU(ActionEvent actionEvent) {
        String fileName = clientStorage + getData(UserContent);
        delete(fileName);
        refresh();
    }

    public void deleteS(ActionEvent actionEvent) {
        String fileName =getData(ServerContent);
        String msg = "del"+" "+ fileName;
        NettyClient.getInstance().sendMsg(msg);
        NettyClient.getInstance().getNettyClientHandler().setDeleteAction(()->refresh());
    }

    private void refresh(){
        ServerContent.getItems().clear();
        UserContent.getItems().clear();
        initData();
    }

}
