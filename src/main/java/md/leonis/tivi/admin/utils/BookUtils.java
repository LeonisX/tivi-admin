package md.leonis.tivi.admin.utils;

import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import md.leonis.tivi.admin.model.*;
import md.leonis.tivi.admin.model.media.*;
import md.leonis.tivi.admin.model.media.links.*;
import md.leonis.tivi.admin.view.media.AuditController;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class BookUtils {

    public static List<Book> books;

    public enum Actions {ADD, EDIT, CLONE}

    public static BookUtils.Actions action;

    public static List<BookCategory> categories = new ArrayList<>();

    public static Video book;

    public static List<VideoView> siteBooks;

    public static ListVideousSettings listBooksSettings = new ListVideousSettings();

    public static int booksCount;


    public static void auditBooks() {
        JavaFxUtils.showPane("media/Audit.fxml");
    }

    public static void addVideo() {
        if (!book.getYid().isEmpty()) book.setUrl(book.getYid());
        String json = JsonUtils.gson.toJson(book);
        try {
            addVideo(json, book.getImage(), null, book.getPreviousImage());
            System.out.println("OK Add/Edit/Clone Video");
        } catch (Throwable e) {
            System.out.println("Error Add/Edit/Clone Video");
            System.out.println(e.getMessage());
            //TODO window with error
        }
    }

    public static List<BookCategory> readCategories() {
        List<BookCategory> bookCategories = new ArrayList<>();
        String requestURL = Config.apiPath + "media.php?to=cat";
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            bookCategories = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<BookCategory>>(){}.getType());
        } catch (IOException e) {
            System.out.println("Error in readVideoCategories");
        }
        return bookCategories;
    }

    public static void listBooks() {
        //$count,$page,$cat,$sort,$order;
        List<Video> videos = new ArrayList<>();
        String cat = "";
        if (listBooksSettings.catId != -1) cat = "&cat=" + listBooksSettings.catId;
        String requestURL = Config.apiPath + "media.php?to=list&count=" + listBooksSettings.count +"&page=" + listBooksSettings.page + cat + "&sort=" + listBooksSettings.sort + "&order=" + listBooksSettings.order;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            videos = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<Video>>(){}.getType());
        } catch (IOException e) {
            System.out.println("Error in listVideos");
        }
        siteBooks = videos.stream().map(VideoView::new).collect(Collectors.toList());
    }

    public static void countVideos() {
        String cat = "";
        if (listBooksSettings.catId != -1) cat = "&cat=" + listBooksSettings.catId;
        String requestURL = Config.apiPath + "media.php?to=count" + cat;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            booksCount = JsonUtils.gson.fromJson(jsonString, Count.class).getCount();
        } catch (IOException e) {
            System.out.println("Error in countVideos");
        }
    }

    public static void getVideo(int id) {
        String requestURL = Config.apiPath + "media.php?to=get&id=" + id;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            book = JsonUtils.gson.fromJson(jsonString, Video.class);
        } catch (IOException e) {
            System.out.println("Error in getVideo");
        }
    }

    public static void deleteVideo(int id) {
        String requestURL = Config.apiPath + "media.php?to=delete&id=" + id;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            System.out.println(jsonString);
        } catch (IOException e) {
            System.out.println("Error in deleteVideo");
        }
    }

    static boolean checkCpuExist(String cpu) {
        String requestURL = Config.apiPath + "media.php?to=getByCpu&cpu=" + cpu;
        Video vid = null;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            vid = JsonUtils.gson.fromJson(jsonString, Video.class);
        } catch (IOException e) {
            System.out.println("Error in getVideo");
        }
        return vid != null && vid.getCpu().equals(cpu);
    }

    public static String addVideo(String json, String imageName, InputStream inputStream, String deleteName) throws IOException {
        if (!imageName.isEmpty()) deleteName = "";
        String requestURL = Config.apiPath + "media.php?to=add";
        if (action == BookUtils.Actions.EDIT) {
            requestURL = Config.apiPath + "media.php?to=save";
        }
        MultipartUtility multipart;
        try {
            multipart = new MultipartUtility(requestURL, "UTF-8");
            multipart.addHeaderField("User-Agent", "TiVi's admin client");
            if (!deleteName.isEmpty()) {
                multipart.addFormField("delete", deleteName);
            }
            multipart.addJson("json", json);
            if (inputStream != null) {
                multipart.addInputStream("image", imageName, inputStream);
            }
        } catch (IOException ex) {
            return ex.getMessage();
        }
        return multipart.finish();
    }


    public static void readBooks(AuditController auditController) {
        ProgressForm pForm = new ProgressForm();

        // In real life this task would do something useful and return
        // some meaningful result:
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {

                books = selectBooks();
                updateProgress(1 ,1);


                List<Link> bookAuthors = selectLinks("books_authors_link", "author");
                updateProgress(2 ,2);
                List<Author> authors = selectAuthors();
                updateProgress(3 ,3);

                books.forEach(book -> {
                    List<Long> ids = bookAuthors.stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setAuthors(authors.stream().filter(author -> ids.contains(author.getId())).collect(toList()));
                });

                Integer[] c = {1, 2, 4, 6, 7, 10, 11, 12, 13, 14, 15};

                Map<Integer, List<Link>> links = Arrays.stream(c).collect(Collectors.toMap(i -> i, i -> selectLinks("books_custom_column_" + i + "_link", "value")));
                updateProgress(4 ,4);

                List<LanguageLink> languageLinks = selectLanguageLinks();
                List<PublisherLink> publisherLinks = selectPublisherLinks();
                List<RatingLink> ratingLinks = selectRatingsLinks();
                List<SerieLink> serieLinks = selectSeriesLinks();
                List<TagLink> tagLinks = selectTagsLinks();
                updateProgress(5 ,5);
                List<CustomColumn> isbns = selectCustomColumn(1);
                List<CustomColumn> bbks = selectCustomColumn(10);
                List<CustomColumn> formats = selectCustomColumn(11);
                List<CustomColumn> sources = selectCustomColumn(12);
                List<CustomColumn> officialTitles = selectCustomColumn(13);
                updateProgress(6 ,6);
                List<CustomColumn> types = selectCustomColumn(14);
                List<CustomColumn> companies = selectCustomColumn(15);
                List<CustomColumn> udks = selectCustomColumn(2);
                List<Link> editions = selectLinks("custom_column_3", "value");
                List<CustomColumn> postprocessings = selectCustomColumn(4);
                updateProgress(7 ,7);
                List<SignedInPrint> signedInPrint = selectSignedInPrint();
                List<CustomColumn> fileNames = selectCustomColumn(6);
                List<CustomColumn> scannedBys = selectCustomColumn(7);
                List<Link> pages = selectLinks("custom_column_8", "value");
                List<Own> owns = selectOwn();
                updateProgress(8 ,8);

                books.forEach(book -> {
                    List<Long> ids1 = links.get(1).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setIsbn(isbns.stream().filter(i -> ids1.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    List<Long> ids10 = links.get(10).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setBbk(bbks.stream().filter(i -> ids10.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    List<Long> ids11 = links.get(11).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setFormat(formats.stream().filter(i -> ids11.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    List<Long> ids12 = links.get(12).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setSource(sources.stream().filter(i -> ids12.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    List<Long> ids13 = links.get(13).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setOfficialTitle(officialTitles.stream().filter(i -> ids13.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    List<Long> ids14 = links.get(14).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setType(types.stream().filter(i -> ids14.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    List<Long> ids15 = links.get(15).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setCompany(companies.stream().filter(i -> ids15.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    List<Long> ids2 = links.get(2).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setUdk(udks.stream().filter(i -> ids2.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    book.setEdition(editions.stream().filter(e -> e.getBook().equals(book.getId())).findFirst().map(a -> a.getValue().intValue()).orElse(null));

                    List<Long> ids4 = links.get(4).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setPostprocessing(postprocessings.stream().filter(i -> ids4.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    book.setSignedInPrint(signedInPrint.stream().filter(a -> a.getBook().equals(book.getId())).map(SignedInPrint::getValue).findFirst().orElse(null));

                    List<Long> ids6 = links.get(6).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setFileName(fileNames.stream().filter(i -> ids6.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    List<Long> ids7 = links.get(7).stream().filter(a -> a.getBook().equals(book.getId())).map(Link::getValue).collect(toList());
                    book.setScannedBy(scannedBys.stream().filter(i -> ids7.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

                    book.setPages(pages.stream().filter(a -> a.getBook().equals(book.getId())).findFirst().map(a -> a.getValue().intValue()).orElse(null));
                    book.setOwn(owns.stream().filter(a -> a.getBook().equals(book.getId())).findFirst().map(Own::getValue).orElse(null));
                });

                List<CustomColumns> customColumns = selectCustomColumns();

                List<Data> dataList = selectDatas();

                books.forEach(book -> book.setDataList(dataList.stream().filter(a -> a.getBook().equals(book.getId())).collect(toList())));

                List<Identifier> identifierList = selectIdentifiers();

                books.forEach(book -> book.setIdentifiers(identifierList.stream().filter(a -> a.getBook().equals(book.getId())).collect(toList())));

                List<Language> languageList = selectLanguages();

                books.forEach(book -> {
                    List<Long> ids = languageLinks.stream().filter(a -> a.getBook().equals(book.getId())).map(LanguageLink::getLangCode).collect(toList());
                    book.setLanguages(languageList.stream().filter(l -> ids.contains(l.getId())).collect(toList()));
                });

                List<PublisherSeries> publisherList = selectPublishers("publishers");

                books.forEach(book -> {
                    List<Long> ids = publisherLinks.stream().filter(a -> a.getBook().equals(book.getId())).map(PublisherLink::getPublisher).collect(toList());
                    book.setPublisher(publisherList.stream().filter(author -> ids.contains(author.getId())).findFirst().orElse(null));
                });

                List<Rating> ratingList = selectRatings();

                books.forEach(book -> {
                    List<Long> ids = ratingLinks.stream().filter(a -> a.getBook().equals(book.getId())).map(RatingLink::getRatings).collect(toList());
                    book.setRating(ratingList.stream().filter(author -> ids.contains(author.getId())).findFirst().orElse(null));
                });

                List<PublisherSeries> seriesList = selectPublishers("series");

                books.forEach(book -> {
                    List<Long> ids = serieLinks.stream().filter(a -> a.getBook().equals(book.getId())).map(SerieLink::getSeries).collect(toList());
                    book.setSeries(seriesList.stream().filter(s -> ids.contains(s.getId())).findFirst().orElse(null));
                });

                List<Tag> tagList = selectTags();

                books.forEach(book -> {
                    List<Long> ids = tagLinks.stream().filter(a -> a.getBook().equals(book.getId())).map(TagLink::getTag).collect(toList());
                    book.setTags(tagList.stream().filter(t -> ids.contains(t.getId())).collect(toList()));
                });

                books.forEach(System.out::println);

                return null;
            }
        };

        // binds progress of progress bars to progress of task:
        pForm.activateProgressBar(task);

        // in real life this method would get the result of the task
        // and update the UI based on its value:
        task.setOnSucceeded(event -> {
            pForm.getDialogStage().close();
            auditController.updateStatus(true);
            //startButton.setDisable(false);
        });

        task.setOnFailed(event -> {
            pForm.label.setText(event.getSource().getException().getMessage());
            auditController.updateStatus(false);
        });

        //startButton.setDisable(true);
        pForm.getDialogStage().show();

        Thread thread = new Thread(task);
        thread.start();
    }

    public static class ProgressForm {
        private final Stage dialogStage;
        private final ProgressBar pb = new ProgressBar();
        private final ProgressIndicator pin = new ProgressIndicator();
        private final Label label = new Label();

        ProgressForm() {
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setWidth(250);

            pb.setProgress(-1F);
            pin.setProgress(-1F);

            label.setTextFill(Color.RED);

            final HBox hb = new HBox();
            hb.setSpacing(5);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().addAll(pb, pin);

            final VBox vBox = new VBox();
            vBox.setSpacing(5);
            vBox.setAlignment(Pos.CENTER);

            vBox.getChildren().addAll(hb, label);

            Scene scene = new Scene(vBox);
            dialogStage.setScene(scene);
        }

        void activateProgressBar(final Task<?> task)  {
            pb.progressProperty().bind(task.progressProperty());
            pin.progressProperty().bind(task.progressProperty());
            //label.textProperty().bind(task.messageProperty());
            dialogStage.show();
        }

        Stage getDialogStage() {
            return dialogStage;
        }
    }


    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:E://metadata.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static List<Book> selectBooks() {
        String sql = "SELECT * FROM books";

        List<Book> books = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("id"));
                book.setTitle(rs.getString("title"));
                book.setSort(rs.getString("sort"));
                book.setTimestamp(parseDate(rs.getString("timestamp")));
                book.setPublDate(parseDate(rs.getString("pubdate")));
                book.setSerieIndex(rs.getDouble("series_index"));
                book.setAuthorSort(rs.getString("author_sort"));
                book.setUnusedIsbn(rs.getString("isbn"));
                book.setUnusedLccn(rs.getString("lccn"));
                book.setPath(rs.getString("path"));
                book.setFlags(rs.getLong("flags"));
                book.setUuid(rs.getString("uuid"));
                book.setHasCover(rs.getBoolean("has_cover"));
                book.setLastModified(parseDate(rs.getString("last_modified")));

                //System.out.println(book);
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return books;
    }

    public static List<SignedInPrint> selectSignedInPrint() {
        String sql = "SELECT * FROM custom_column_5";

        List<SignedInPrint> list = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                SignedInPrint i = new SignedInPrint();
                i.setId(rs.getLong("id"));
                i.setBook(rs.getLong("book"));
                i.setValue(parseDate(rs.getString("value")));
                //System.out.println(book);
                list.add(i);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static List<Own> selectOwn() {
        String sql = "SELECT * FROM custom_column_9";

        List<Own> list = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                Own i = new Own();
                i.setId(rs.getLong("id"));
                i.setBook(rs.getLong("book"));
                i.setValue(rs.getBoolean("value"));
                list.add(i);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }


    public static List<Long> selectAuthorIds(Long bookId) {
        String sql = "SELECT * FROM books_authors_link WHERE book = " + bookId;

        List<Long> authorIds = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                authorIds.add(rs.getLong("author"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return authorIds;
    }

    public static List<Link> selectLinks(String entityName, String fieldName) {
        String sql = "SELECT * FROM " + entityName;

        List<Link> links = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                Link link = new Link();
                link.setId(rs.getLong("id"));
                link.setBook(rs.getLong("book"));
                link.setValue(rs.getLong(fieldName));
                links.add(link);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return links;
    }

    public static List<Author> selectAuthors() {
        String sql = "SELECT * FROM authors";

        List<Author> authors = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Author author = new Author();
                author.setId(rs.getLong("id"));
                author.setName(rs.getString("name"));
                author.setSort(rs.getString("sort"));
                author.setLink(rs.getString("link"));
                authors.add(author);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return authors;
    }


    public static List<Author> selectAuthorsByIds(List<Long> ids) {
        String idx = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        String sql = "SELECT * FROM authors WHERE id IN ( " + idx + " )";

        List<Author> authors = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Author author = new Author();
                author.setId(rs.getLong("id"));
                author.setName(rs.getString("name"));
                author.setSort(rs.getString("sort"));
                author.setLink(rs.getString("link"));
                authors.add(author);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return authors;
    }

    public static List<LanguageLink> selectLanguageLinks() {
        String sql = "SELECT * FROM books_languages_link";

        List<LanguageLink> links = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                LanguageLink link = new LanguageLink();
                link.setId(rs.getLong("id"));
                link.setBook(rs.getLong("book"));
                link.setItemOrder(rs.getLong("item_order"));
                link.setLangCode(rs.getLong("lang_code"));
                links.add(link);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return links;
    }

    public static List<PublisherLink> selectPublisherLinks() {
        String sql = "SELECT * FROM books_publishers_link";

        List<PublisherLink> links = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PublisherLink link = new PublisherLink();
                link.setId(rs.getLong("id"));
                link.setBook(rs.getLong("book"));
                link.setPublisher(rs.getLong("publisher"));
                links.add(link);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return links;
    }

    public static List<RatingLink> selectRatingsLinks() {
        String sql = "SELECT * FROM books_ratings_link";

        List<RatingLink> links = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                RatingLink link = new RatingLink();
                link.setId(rs.getLong("id"));
                link.setBook(rs.getLong("book"));
                link.setRatings(rs.getLong("rating"));
                links.add(link);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return links;
    }

    public static List<SerieLink> selectSeriesLinks() {
        String sql = "SELECT * FROM books_series_link";

        List<SerieLink> links = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SerieLink link = new SerieLink();
                link.setId(rs.getLong("id"));
                link.setBook(rs.getLong("book"));
                link.setSeries(rs.getLong("series"));
                links.add(link);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return links;
    }

    public static List<TagLink> selectTagsLinks() {
        String sql = "SELECT * FROM books_tags_link";

        List<TagLink> links = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TagLink link = new TagLink();
                link.setId(rs.getLong("id"));
                link.setBook(rs.getLong("book"));
                link.setTag(rs.getLong("tag"));
                links.add(link);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return links;
    }

    public static List<Comment> selectComments() {
        String sql = "SELECT * FROM books_comments";

        List<Comment> links = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Comment link = new Comment();
                link.setId(rs.getLong("id"));
                link.setBook(rs.getLong("book"));
                link.setText(rs.getString("text"));
                links.add(link);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return links;
    }

    public static List<CustomColumn> selectCustomColumn(int id) {
        String sql = "SELECT * FROM custom_column_" + id;

        List<CustomColumn> customColumns = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CustomColumn cc = new CustomColumn();
                cc.setId(rs.getLong("id"));
                cc.setValue(rs.getString("value"));
                customColumns.add(cc);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customColumns;
    }

    public static List<CustomColumns> selectCustomColumns() {
        String sql = "SELECT * FROM custom_columns";

        List<CustomColumns> columnsList = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CustomColumns columns = new CustomColumns();
                columns.setId(rs.getLong("id"));
                columns.setLabel(rs.getString("label"));
                columns.setName(rs.getString("name"));
                columns.setDataType(rs.getString("dataType"));
                columns.setMarkForDelete(rs.getBoolean("mark_for_delete"));
                columns.setEditable(rs.getBoolean("editable"));
                columns.setDisplay(rs.getString("display"));
                columns.setIsMultiple(rs.getBoolean("is_multiple"));
                columns.setNormalized(rs.getBoolean("normalized"));
                columnsList.add(columns);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return columnsList;
    }

    public static List<Data> selectDatas() {
        String sql = "SELECT * FROM data";

        List<Data> dataList = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Data columns = new Data();
                columns.setId(rs.getLong("id"));
                columns.setBook(rs.getLong("book"));
                columns.setFormat(rs.getString("format"));
                columns.setUncompressedSize(rs.getLong("uncompressed_size"));
                columns.setName(rs.getString("name"));
                dataList.add(columns);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dataList;
    }

    public static List<Identifier> selectIdentifiers() {
        String sql = "SELECT * FROM identifiers";

        List<Identifier> identifierList = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Identifier columns = new Identifier();
                columns.setId(rs.getLong("id"));
                columns.setBook(rs.getLong("book"));
                columns.setType(rs.getString("type"));
                columns.setVal(rs.getLong("val"));
                identifierList.add(columns);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return identifierList;
    }

    public static List<Language> selectLanguages() {
        String sql = "SELECT * FROM languages";

        List<Language> languageList = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Language language = new Language();
                language.setId(rs.getLong("id"));
                language.setLangCode(rs.getString("lang_code"));
                languageList.add(language);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return languageList;
    }

    public static List<PublisherSeries> selectPublishers(String db) {
        String sql = "SELECT * FROM " + db;

        List<PublisherSeries> publisherSeriesList = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PublisherSeries columns = new PublisherSeries();
                columns.setId(rs.getLong("id"));
                columns.setName(rs.getString("name"));
                columns.setSort(rs.getString("sort"));
                publisherSeriesList.add(columns);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return publisherSeriesList;
    }

    public static List<Tag> selectTags() {
        String sql = "SELECT * FROM tags";

        List<Tag> publisherSerieList = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Tag columns = new Tag();
                columns.setId(rs.getLong("id"));
                columns.setName(rs.getString("name"));
                publisherSerieList.add(columns);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return publisherSerieList;
    }

    public static List<Rating> selectRatings() {
        String sql = "SELECT * FROM ratings";

        List<Rating> ratings = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Rating rating = new Rating();
                rating.setId(rs.getLong("id"));
                rating.setRating(rs.getLong("rating"));
                ratings.add(rating);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ratings;
    }


    private static LocalDateTime parseDate(String date) {
        if (date.length() == 25) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
            return LocalDateTime.parse(date, formatter);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX");
            return LocalDateTime.parse(date, formatter);
        }
    }
}
