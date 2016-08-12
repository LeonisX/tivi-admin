package md.leonis.tivi.admin.view.video;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.HTMLEditor;
import md.leonis.tivi.admin.model.Access;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.model.YesNo;
import md.leonis.tivi.admin.utils.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class AddVideo2Controller extends SubPane {
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
    private TextField age;
    @FXML
    private TextField mirror;
    @FXML
    private HTMLEditor text;
    @FXML
    private TextField author;
    @FXML
    private TextField authorSite;
    @FXML
    private TextField authorEmail;
    @FXML
    private TextField views;
    @FXML
    private ToggleGroup tgState;
    @FXML
    private ToggleButton onButton;
    @FXML
    private ToggleButton offButton;
    @FXML
    private ToggleGroup tgAccess;
    @FXML
    private ToggleButton allButton;
    @FXML
    private ToggleButton usersButton;
    /*@FXML
    private Button nextButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button reloadButton;
    @FXML
    private Button helpButton;*/
    @FXML
    private Button finishButton;
    @FXML
    private Label rate;
    @FXML
    private Label comments;

    private CatUtils cat;

    @FXML
    private void initialize() {
        onButton.setUserData(YesNo.yes);
        offButton.setUserData(YesNo.no);
        allButton.setUserData(Access.all);
        usersButton.setUserData(Access.user);
        CatUtils.setCellFactory(category);
    }

    @FXML
    private void help() {
        JavaFxUtils.showAlert("О добавлении/редактировании видео",
                "Что следует знать:",
                "Избегайте длинных названий.\n\n"
                + "ЧПУ замена нужна для `красивых` URL-адресов. Допустимые символы: буквы, цифры, знак подчёркивания.\n\n"
                + "Видео можно выкладывать `на перёд`, например, можно запланировать показ нескольких роликов на будущий месяц, они станут доступны для просмотра не ранее назначенного им дня.\n\n"
                + "Категория необходима для группировки видео материалов, например, по авторам или направленности.\n\n"
                + "Мета-описание и ключевые слова нужны для поисковиков.\n\n"
                + "Если в видео-ролике ведущий матерится, или есть какой-то непотребный материал, то лучше установить возрастной ценз, чтобы это не увидели малыши. Например, 18.\n\n"
                + "Поле ввода для `зеркала`, если не понадобится, то в будущем, вероятно мы уберём. Тут можно указывать ссылку на оригинал ролика.\n\n"
                + "Описание видео форматируется с помощью HTML разметки.\n\n"
                + "Почтовый адрес автора следует указывать только в том случае, если его РЕАЛЬНО ТРУДНО НАЙТИ. То есть, это поле почти никогда не потребуется.\n\n"
                + "Количество просмотров не следует менять. Поле редактируемо только для тех случаев, если замечена явная накрутка количества просмотров.\n\n"
                + "Средняя оценка это сумма оценок, поделённая на количество оценок.\n\n"
                + "Если выключить видео-ролик, то он будет доступен только в админке, но не для просмотра на сайте.\n\n"
                + "Справа можно удалить картинку, представляющую видео (если она есть), или заменить её на что-то другое.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void reload() {
        VideoUtils.parseVideoPage();
        fillFields();
    }

    @FXML
    private void cancel() {
        VideoUtils.showListVideous();
    }

    private void fillFields() {
        Video video = VideoUtils.video;
        title.setText(video.getTitle());
        cpu.setText(video.getCpu());
        age.setText(video.getAge());
        LocalDate insertDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(video.getDate()), ZoneId.systemDefault()).toLocalDate();
        if (data.getValue() == null || data.getValue().isBefore(insertDate)) data.setValue(insertDate);
        keywords.setText((video.getKeywords()));
        description.setText(video.getDescription());
        age.setText(video.getAge());
        mirror.setText(video.getMirror());
        author.setText(video.getAuthor());
        authorEmail.setText(video.getAuthorEmail());
        authorSite.setText(video.getAuthorSite());
        views.setText(Integer.toString(video.getViews()));
        rate.setText(video.getRate());
        comments.setText(Integer.toString(video.getComments()));

        if (video.getActive() == YesNo.yes) onButton.setSelected(true); else offButton.setSelected(true);
        if (video.getAccess() == Access.all) allButton.setSelected(true); else usersButton.setSelected(true);

        if (text != null) text.setHtmlText(VideoUtils.video.getText());

        cat = new CatUtils(VideoUtils.categories);
        category.setItems(FXCollections.observableList(cat.getCatList()));
        cat.setCategoryTextValue(category, VideoUtils.video.getCategoryId());

        finishButton.setDisable(video.getImage().isEmpty());
    }

    @Override
    public void init() {
        fillFields();
    }

    public void next() {
        if (checkAllValues()) {
            updateVideoObject();
            VideoUtils.showAddVideo3();
        }
    }

    public void finish() {
        if (checkAllValues()) {
            updateVideoObject();
            VideoUtils.addVideo();
            VideoUtils.showListVideous();
        }
    }

    private boolean checkAllValues() {
        CheckUtils checker = new CheckUtils();
        checker.checkLength(title.getText(), 54);
        checker.checkLength(cpu.getText(), 54);
        checker.checkCpu(cpu.getText());
        checker.checkCpuExist(cpu.getText());
        checker.checkAge(age.getText());
        checker.checkLength(description.getText(), 255);
        checker.checkLength(keywords.getText(), 255);
        checker.checkNumber(views.getText());

        // TODO tags;
        if (!checker.isOk()) JavaFxUtils.showAlert("Ошибка",
                "Следующие данные следует поправить:",
                checker.getErrors(),
                Alert.AlertType.ERROR);
        return checker.isOk();
    }

    private void updateVideoObject() {
        Video video = VideoUtils.video;
        video.setTitle(title.getText());
        video.setCpu(cpu.getText());
        LocalDate date = data.getValue();
        System.out.println("Selected date: " + date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
        video.setDate(date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
        int index = category.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            video.setCategoryId(cat.getCatIds().get(index));
        }
        video.setDescription(description.getText());
        video.setKeywords(keywords.getText());
        video.setAge(age.getText());
        video.setMirror(mirror.getText());
        if (text != null) {
            String txt = text.getHtmlText()
                    .replaceFirst("<html dir=\"ltr\"><head></head><body contenteditable=\"true\">", "")
                    .replaceFirst("</body></html>", "");
            video.setText(txt);
        }
        video.setAuthor(author.getText());
        video.setAuthorEmail(authorEmail.getText());
        video.setAuthorSite(authorSite.getText());
        video.setViews(Integer.parseInt(views.getText()));
        video.setActive((YesNo) tgState.getSelectedToggle().getUserData());
        video.setAccess((Access) tgAccess.getSelectedToggle().getUserData());
    }
}
