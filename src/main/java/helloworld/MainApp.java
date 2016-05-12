package helloworld;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.fxml.*;
import md.leonis.tivi.admin.model.*;
import md.leonis.tivi.admin.view.MainStageController;
import md.leonis.tivi.admin.view.video.AddVideo2Controller;
import md.leonis.tivi.admin.view.video.AddVideoController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    public List<Category> categories = new ArrayList<Category>();

    public Video addVideo = new Video();

    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("TiVi Admin Panel");

        initRootLayout();
    }

    private void initRootLayout() {
        categories.add( new Category(1, 3, "dendy_new_reality", "Dendy - Новая Реальность", "Передача о 8-ми и 16-ти битках (1994-1995 года), транслировавшаяся на телеканале 2x2. Заказчиком выступала фирма Dendy, производством занималось агентство Sorec Video, а вёл передачу Сергей Супонев. Авторские права принадлежат их владельцам.",
                1, "images/video/dnr.png", Access.all, "public", Order.desc, YesNo.yes, 33)
        );
        categories.add( new Category(3, 0, "entertainment", "TV передачи", "",
                0, "images/video/ent.png", Access.all, "public", Order.asc, YesNo.yes, 166)
        );
        categories.add( new Category(3, 0, "discovery", "Образовательные каналы", "Всевозможные серьёзные передачи про игры",
                5, "images/systems/news.png", Access.all, "public", Order.asc, YesNo.yes, 213)
        );
        categories.add( new Category(9, 0, "gold_games", "Золотой фонд", "Видео прохождения самых лучших игр всех времён.",
                2, "images/video/gold_games.png", Access.all, "public", Order.asc, YesNo.yes, 26)
        );
        categories.add( new Category(5, 3, "new_reality", "Новая Реальность", "Передача о 8-ми и 16-ти битках (1995-1996 года), транслировавшаяся на телеканале ОРТ. Заказчиком выступала фирма Dendy, производила студия Класс, а вёл передачу Сергей Супонев. Авторские права принадлежат их владельцам.",
                2, "images/video/nr.png", Access.all, "public", Order.desc, YesNo.yes, 29)
        );
        categories.add( new Category(6, 11, "emugamer_tv", "EmuGamer TV", "Передача о ретро-играх. В основном это обзоры хитов прежних лет, сделанные на весьма приличном уровне.",
                2, "images/video/egt.png", Access.all, "public", Order.desc, YesNo.yes, 38)
        );
        categories.add( new Category(10, 9, "nes", "NES - Dendy", "Записи прохождений игр NES / Famicom / Dendy",
                0, "images/systems/nes.png", Access.all, "public", Order.desc, YesNo.yes, 9)
        );

        categories.sort((o1, o2) -> o1.getPosit().compareTo(o2.getPosit()));
        categories.sort((o1, o2) -> o1.getParentid().compareTo(o2.getParentid()));

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/md/leonis/tivi/admin/view/MainStage.fxml"));
            rootLayout = loader.load();

            // Give the controller access to the main app.
            MainStageController controller = loader.getController();
            controller.setMainApp(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout, 1024, 768);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddVideo() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/md/leonis/tivi/admin/view/video/AddVideo.fxml"));
            Parent addVideo = loader.load();
            AddVideoController controller = loader.getController();
            controller.setMainApp(this);
            rootLayout.setCenter(addVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showProcessVideo() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/md/leonis/tivi/admin/view/video/AddVideo2.fxml"));
            Parent addVideo2 = loader.load();
            //TODO
            AddVideo2Controller controller = loader.getController();
            controller.setMainApp(this);
            rootLayout.setCenter(addVideo2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}