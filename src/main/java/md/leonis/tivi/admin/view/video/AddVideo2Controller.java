package md.leonis.tivi.admin.view.video;

import helloworld.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.HTMLEditor;
import md.leonis.tivi.admin.model.Category;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.utils.VideoUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class AddVideo2Controller {
    @FXML
    private TextField title;

    @FXML
    private TextField cpu;

    @FXML
    private DatePicker data;

    @FXML
    private ComboBox<String> category;

    @FXML
    private TextField description;

    @FXML
    private TextField keywords;

    @FXML
    private TextField year;

    @FXML
    private TextField originalUrl;

    @FXML
    private TextArea text;

    @FXML
    private TextArea fullText;

    @FXML
    private HTMLEditor textHtml;

    @FXML
    private TextField author;

    @FXML
    private TextField authorSite;

    @FXML
    private TextField authorEmail;

    @FXML
    private TextField loads;

    @FXML
    private TextField views;

    @FXML
    private ToggleButton state;

    @FXML
    private Button nextButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button reloadButton;

    @FXML
    private Button helpButton;

    // Reference to the main application.
    private MainApp mainApp;

    private List<String> catList = null;
    private List<Integer> catIds = null;

    @FXML
    private void processVideo() {
        mainApp.addVideo = new Video();
        VideoUtils.parsePage(mainApp.addVideo);
        mainApp.showProcessVideo();
    }


    private int getParentId(String catname) {
        for (Category category: mainApp.categories) if (category.getCatname().equals(catname)) return category.getParentid();
        return -1;
    }


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        Video video = mainApp.addVideo;
        title.setText(video.title);
        cpu.setText(video.cpu);
        year.setText(video.year);
        System.out.println(data.getValue());
        LocalDate insertDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(video.data), ZoneId.systemDefault()).toLocalDate();
        System.out.println(insertDate);
        if (data.getValue() == null || data.getValue().isBefore(insertDate)) data.setValue(insertDate);
        category.setValue(video.category);
        keywords.setText((video.getKeywords()));
        description.setText(video.getDescription());
        year.setText(video.getYear());
        originalUrl.setText(video.getOriginUrl());
        text.setText(video.getText());
        fullText.setText(video.getFullText());
        author.setText(video.getAuthor());
        authorEmail.setText(video.getAuthorEmail());
        authorSite.setText(video.getAuthorUrl());
        loads.setText(Integer.toString(video.getLoads()));
        views.setText(Integer.toString(video.getViews()));
        state.setSelected(video.getState());


        if (textHtml != null) textHtml.setHtmlText(mainApp.addVideo.text);

        catList = new ArrayList<>();
        catIds = new ArrayList<>();

        // not need mainApp.categories.sort((o1, o2) -> o1.getPosit().compareTo(o2.getPosit()));
        mainApp.categories.sort((o1, o2) -> o1.getParentid().compareTo(o2.getParentid()));

        for (Category category: mainApp.categories) {
            String name = "";
            for (Category cat: mainApp.categories) if (cat.getCatid().equals(category.getParentid())) name = cat.getCatname();
            Integer k = name.isEmpty() ? (catList.size() - 1) : catList.indexOf(name);
            catList.add(k + 1, category.getCatname());
            catIds.add(k + 1, category.getCatid());
        }

        ObservableList<String> oList = FXCollections.observableList(catList);

        category.setItems(oList);
        category.setValue("Выберите категорию");

        category.setCellFactory((ListView<String> param) ->
                new ListCell<String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            if (getParentId(item) == 0) {
                                setStyle("-fx-background-color: lavender;");
                            } else {
                                setStyle("-fx-padding: 5px 10px;");
                            }
                        } else {
                            setText(null);
                        }
                    }
                }

        );
    }

    public void next() {
        int index = category.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            System.out.println(catIds.get(index));
            System.out.println(catList.get(index));
        }
        LocalDate date = data.getValue();
        System.out.println("Selected date: " + date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
        // Check all values
    }

}