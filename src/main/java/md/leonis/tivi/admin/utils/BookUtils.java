package md.leonis.tivi.admin.utils;

import com.google.gson.JsonObject;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import md.leonis.tivi.admin.model.ComparisionResult;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.Book;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.calibre.PublisherSeries;
import md.leonis.tivi.admin.model.calibre.Tag;
import md.leonis.tivi.admin.model.danneo.BookCategory;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.renderer.*;
import md.leonis.tivi.admin.view.media.CalibreInterface;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static md.leonis.tivi.admin.model.Type.*;
import static md.leonis.tivi.admin.utils.CalibreBookVideoConverter.calibreMagazineToVideo;
import static md.leonis.tivi.admin.utils.CalibreBookVideoConverter.calibreToVideo;
import static md.leonis.tivi.admin.utils.StringUtils.*;

public class BookUtils {

    private static List<BookCategory> categories = new ArrayList<>();

    public static List<BookCategory> getCategories() {
        if (categories.isEmpty()) {
            loadCategories();
        }
        return categories;
    }

    public static void loadCategories() {
        categories = SiteDbUtils.readCategories();
    }

    public static List<Video> siteBooks = new ArrayList<>();

    public static String cloudStorageLink;

    public static void auditBooks() {
        JavaFxUtils.showPane("media/CalibreAudit.fxml");
    }

    public static void reportBooks() {
        JavaFxUtils.showPane("media/CalibreReports.fxml");
    }

    public static void compareCalibreDbs() {
        JavaFxUtils.showPane("media/CalibreCompare.fxml");
    }

    public static void compareWithSite() {
        JavaFxUtils.showPane("media/SiteCompare.fxml");
    }

    public static List<Pair<BookCategory, BookCategory>> compareCategories() {
        List<Pair<BookCategory, BookCategory>> result = getCategories().stream().map(c -> new Pair<>(c, new BookCategory(c))).collect(toList());
        result.forEach(p -> p.getValue().setTotal(0));
        result.forEach(p -> {
            List<Video> children = siteBooks.stream().filter(b -> b.getCategoryId().equals(p.getValue().getCatid())).collect(toList());
            if (!children.isEmpty()) {
                p.getValue().setTotal(p.getValue().getTotal() + children.size());
                SiteDbUtils.updateParentTotals(p.getValue(), result);
            }
        });
        //TODO remove GD filter
        result.stream().filter(p -> p.getValue().getCatcpu().equals("magazines")).forEach(c -> c.getValue().setTotal(c.getValue().getTotal() +
                result.stream().filter(p -> p.getValue().getCatcpu().equals("gd")).findFirst().get().getKey().getTotal()));
        return result.stream().filter(p -> !p.getKey().getTotal().equals(p.getValue().getTotal())).filter(p -> !p.getValue().getCatcpu().equals("gd")).collect(toList());
    }


    //TODO same functional as listBooks() ???
    public static void getSiteBooks() {
        siteBooks = SiteDbUtils.getSiteBooks();
    }

    public static List<ComparisionResult<Video>> compare(List<CalibreBook> calibreBooks, String category) { //category == cpu
        List<ComparisionResult<Video>> comparisionResults = new ArrayList<>();
        if (category == null) {
            for (int i = 0; i < getCategories().size(); i++) {
                comparisionResults.add(doCompare(calibreBooks, getCategories().get(i).getCatcpu()));
            }
        } else {
            comparisionResults.add(doCompare(calibreBooks, category));
        }
        return comparisionResults;
    }

    public static ComparisionResult<Video> doCompare(List<CalibreBook> calibreBooks, String category) {

        System.out.println("============================= doCompare: " + category);
        if (getParentRoot(getCategories(), category).getCatcpu().equals("magazines") && !category.equals("gd")) {
            return compareMagazines(calibreBooks, category);
        } else {
            return compareBooks(calibreBooks, category);
        }
    }

    public static ComparisionResult<Video> compareBooks(List<CalibreBook> calibreBooks, String category) {

        //List<CalibreBook> filteredCalibreBooks = calibreBooks.stream().filter(b -> !b.getType().equals("magazines"))
        List<CalibreBook> filteredCalibreBooks = calibreBooks.stream().filter(b -> b.getType() != null && b.getType().equals(BOOK))
                /*.filter(b -> b.getOwn() != null && b.getOwn()).sorted(Comparator.comparing(Book::getTitle))*/.collect(toList());

        List<String> multi = Arrays.asList("consoles", "computers"); //computers реально не задействован - только для журналов.
        if (multi.contains(category)) {
            // При multi не нужно искать упоминания в журналах
            filteredCalibreBooks = filteredCalibreBooks.stream()
                    .filter(b -> /*b.getTags().size() > 1 ||*/ (b.getTags().size() == 1 && multi.contains(b.getTags().get(0).getName())))
                    .filter(b -> {
                        List<String> p = b.getTags().stream().map(Tag::getName)
                                .map(t -> getParentRoot(getCategories(), t)).map(BookCategory::getCatcpu).distinct().collect(toList());
                        return p.size() == 1 && p.contains(category);
                    }).collect(toList());
        } else {
            filteredCalibreBooks = filteredCalibreBooks.stream().filter(b -> b.belongsToCategory(category)).collect(toList());
        }

        // сгруппировать книги с сериями
        Map<Boolean, List<CalibreBook>> dividedBooks = filteredCalibreBooks.stream().collect(partitioningBy(b -> b.getGroup() != null));

        Map<String, List<CalibreBook>> groupedBooks = dividedBooks.get(true).stream()
                .filter(b -> b.getType().equals(BOOK))
                .filter(b -> b.belongsToCategory(category) || b.mentionedInCategory(category))
                .collect(groupingBy(CalibreBook::getGroup));

        List<Video> books = groupedBooks.values().stream()
                .filter(b -> b.stream().anyMatch(v -> v.getOwn() != null & v.getOwn()))
                .map(b -> {
                    Video book = calibreMagazineToVideo(b.stream().sorted(Comparator.comparing(CalibreBook::getSort)).collect(toList()), category, "книга");
                    book.setCategoryId(BookUtils.getCategoryId(category));
                    return book;
                }).collect(toList());

        // без серий
        List<Video> filteredCalibreBooksV = dividedBooks.get(false).stream().filter(b -> b.getOwn() != null && b.getOwn())
                .sorted(Comparator.comparing(CalibreBook::getSort)).map(b -> calibreToVideo(b, category)).collect(toList());
        filteredCalibreBooksV.addAll(books);

        // добавить специализированные журналы
        //потом можно добавить и комиксы, если будет такая необходимость
        Map<String, List<CalibreBook>> groupedMags = MagazinesCitationRenderer.getGroupedBooks(calibreBooks, MAGAZINE, category);
        Map<String, List<CalibreBook>> filteredMags = MagazinesCitationRenderer.getMags(groupedMags, category, true);

        List<Video> mags = filteredMags.values().stream().map(b -> {
            Video mag = calibreMagazineToVideo(b.stream().sorted(Comparator.comparing(CalibreBook::getSort)).collect(toList()), category, "выпуск");
            mag.setGroup(true);
            return mag;
        }).collect(toList());
        filteredCalibreBooksV.addAll(mags);

        Set<String> cpus = filteredMags.values().stream().map(m -> m.get(0).getSiteCpu()).collect(toSet());
        cpus.addAll(books.stream().map(Video::getCpu).collect(toList()));

        List<Video> filteredSiteBooks = siteBooks.stream().filter(b -> b.getCategoryId().equals(getCategoryId(category)) || cpus.contains(b.getCpu())).collect(toList());

        //Если в Calibre null, 0 - значит добавленные
        Collection<Video> addedBooks = filteredCalibreBooksV.stream().filter(b -> b.getId() == null || b.getId() < 1)
                .peek(b -> b.setCategoryId(getCategoryId(category))).sorted(Comparator.comparing(Video::getTitle)).collect(toList());

        //calibre -> site
        List<Video> oldBooks = filteredCalibreBooksV.stream().filter(b -> b.getId() != null && b.getId() > 0).collect(toList());

        //oldbooks - генерить
        // - мануалами (солюшенами) и другими страницами
        for (Type type : Arrays.asList(DOC, EMULATOR, GUIDE, MANUAL)) { //doc, emu, guide, manual
            //new ManualGuideRenderer(filteredCalibreBooks, filteredSiteBooks, category, addedBooks, oldBooks, type).generateManualsPage();
            new ManualGuideRenderer(calibreBooks, filteredSiteBooks, category, addedBooks, oldBooks, type).generateManualsPage();
        }
        // - других книгах,
        //new CitationsRenderer(filteredCalibreBooks, filteredSiteBooks, category, addedBooks, oldBooks).generateCitationsPage();
        new BooksCitationsRenderer(calibreBooks, filteredSiteBooks, category, addedBooks, oldBooks).generateCitationsPage();
        // - так же страница с поиском книг
        //SiteRenderer.generateSearchPage(filteredCalibreBooks, filteredSiteBooks, category, addedBooks, oldBooks);
        new SearchPageRenderer(calibreBooks, filteredSiteBooks, category, addedBooks, oldBooks).generateSearchPage();
        // - упоминания в журналах
        new MagazinesCitationRenderer(calibreBooks, filteredSiteBooks, category, addedBooks, oldBooks).generateCitationPage();
        // - упоминания в комиксах
        new ComicsCitationRenderer(calibreBooks, filteredSiteBooks, category, addedBooks, oldBooks).generateCitationPage();

        //Если в Calibre нет нужного ID значит удалённые
        Map<Integer, Video> newIds = oldBooks.stream().collect(Collectors.toMap(Video::getId, Function.identity()));
        Map<Integer, Video> oldIds = filteredSiteBooks.stream().collect(Collectors.toMap(Video::getId, Function.identity()));

        Collection<Video> deletedBooks = new ArrayList<>(CalibreUtils.mapDifference(oldIds, newIds));

        //Разницу считаем только у тех, что имеют теги
        List<Video> allBooks = new ArrayList<>(filteredSiteBooks);
        allBooks.addAll(oldBooks);
        /*allBooks.addAll(allCalibreBooks.stream()*//*.filter(b -> b.getType().equals(BOOK))*//*
                .filter(b -> b.getOwn() != null && b.getOwn()).filter(b -> b.getTiviId() != null && b.getTiviId() > 0)
                .map(b -> calibreToVideo(b, category)).collect(toList()));*/
        List<Pair<Video, Video>> changed = allBooks.stream().collect(groupingBy(Video::getId))
                .values().stream().filter(videos -> videos.size() == 2)
                .map(videos -> {
                    Video siteBook = videos.get(0);
                    Video calibreBook = videos.get(1);
                    //calibreBook.setCategoryId(siteBook.getCategoryId());
                    calibreBook.setCategoryId(getCategoryId(category));
                    calibreBook.setImage_align(siteBook.getImage_align());
                    calibreBook.setViews(siteBook.getViews());
                    calibreBook.setLoads(siteBook.getLoads());
                    calibreBook.setLastdown(siteBook.getLastdown());
                    calibreBook.setRated_count(siteBook.getRated_count());
                    calibreBook.setTotal_rating(siteBook.getTotal_rating());
                    calibreBook.setComments(siteBook.getComments());
                    if (Math.abs(siteBook.getDate() - calibreBook.getDate()) <= 24 * 60 * 60) {
                        siteBook.setDate(calibreBook.getDate());
                    }
                    return new Pair<>(siteBook, calibreBook);
                })
                .filter(e -> !e.getKey().equals(e.getValue()))
                .collect(toList());

        Map<Video, List<Pair<String, Pair<String, String>>>> changedBooks = changed.stream().collect(Collectors.toMap(Pair::getKey, pair -> {
            List<Pair<String, Pair<String, String>>> res = new ArrayList<>();
            JsonObject oldJsonObject = JsonUtils.gson.toJsonTree(pair.getKey()).getAsJsonObject();
            JsonObject newJsonObject = JsonUtils.gson.toJsonTree(pair.getValue()).getAsJsonObject();
            oldJsonObject.entrySet().forEach(e -> {
                String ov = e.getValue().isJsonNull() ? "" : SiteRenderer.cleanHtml(e.getValue().getAsString());
                String nv = newJsonObject.get(e.getKey()).isJsonNull() ? "" : SiteRenderer.cleanHtml(newJsonObject.get(e.getKey()).getAsString());
                if (!ov.equals(nv)) {
                    /*try {
                        Files.write(Paths.get("1.txt"), ov.getBytes());
                        Files.write(Paths.get("2.txt"), nv.getBytes());
                    } catch (Exception ee) {

                    }*/
                    if (e.getKey().equals("public")) {
                        String oldDate = timestampWithOffset(e.getValue().getAsLong(), 0);
                        String newDate = timestampWithOffset(newJsonObject.get(e.getKey()).getAsLong(), 1);
                        if (!oldDate.equals(newDate)) {
                            Pair<String, String> value = new Pair<>(oldDate, newDate);
                            res.add(new Pair<>(e.getKey(), value));
                        }
                    } else {
                        Pair<String, String> value = new Pair<>(e.getValue().getAsString(), newJsonObject.get(e.getKey()).isJsonNull() ? null : newJsonObject.get(e.getKey()).getAsString());
                        System.out.println("====");
                        System.out.println(value.getKey());
                        System.out.println(value.getValue());
                        res.add(new Pair<>(e.getKey(), value));
                    }
                }
            });
            return res;
        })).entrySet().stream().filter(e -> !e.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ComparisionResult<>(category, addedBooks, deletedBooks, changedBooks);
    }

    public static ComparisionResult<Video> compareMagazines(List<CalibreBook> calibreBooks, String category) {
        List<CalibreBook> calibreMagazines = calibreBooks.stream().filter(b -> b.getType().getValue().equals(category.equals("magazines") ? MAGAZINE.getValue() : category) && !category.equals("gd"))
                .sorted(Comparator.comparing(Book::getSort)).collect(toList());

        // убирать все специфические журналы
        Map<String, List<CalibreBook>> filteredMags = calibreMagazines.stream()
                .peek(b1 -> {
                    if (b1.getSeries() == null) {
                        b1.setSeries(new PublisherSeries(0L, b1.getTitle(), ""));
                    }
                })
                .collect(groupingBy(calibreBook1 -> calibreBook1.getSeries().getName()))
                .entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue().get(0).getSeries().getName(), Map.Entry::getValue));

        if (!category.equals("comics")) {
            filteredMags = MagazinesCitationRenderer.getMags(filteredMags, category, false);
        }

        List<Video> siteMagazines = siteBooks.stream().filter(b -> b.getCategoryId().equals(getCategoryId(category))).collect(toList());
        List<Video> calibreMagazinesV = filteredMags.values().stream().map(b -> calibreMagazineToVideo(b, category, "выпуск")).collect(toList());

        //Если в Calibre null, 0 - значит добавленные
        Collection<Video> addedBooks = calibreMagazinesV.stream().filter(b -> b.getId() == null || b.getId() < 1)
                .peek(b -> b.setCategoryId(getCategoryId(category))).collect(toList());

        //calibre -> site
        List<Video> oldBooks = calibreMagazinesV.stream().filter(b -> b.getId() != null && b.getId() > 0).collect(toList());

        // страница с поиском журналов
        new MagazinesSearchPageRenderer(calibreMagazines, siteMagazines, category, addedBooks, oldBooks).generateMagazinesSearchPage();

        // страница журналов, специализированных под конкретную платформу
        if (!category.equals("comics")) {
            new MagazinesSpecialPageRenderer(calibreMagazines, siteMagazines, category, addedBooks, oldBooks).generateMagazinesSpecialPage();
        }

        //Если в Calibre нет нужного ID значит удалённые
        Map<Integer, Video> newIds = oldBooks.stream().collect(Collectors.toMap(Video::getId, Function.identity()));
        Map<Integer, Video> oldIds = siteMagazines.stream().collect(Collectors.toMap(Video::getId, Function.identity()));

        Collection<Video> deletedBooks = CalibreUtils.mapDifference(oldIds, newIds);

        //Разницу считаем только у тех, что имеют теги
        List<Video> allBooks = new ArrayList<>(siteMagazines);
        allBooks.addAll(oldBooks);
        List<Pair<Video, Video>> changed = allBooks.stream().collect(groupingBy(Video::getId))
                .values().stream().filter(videos -> videos.size() == 2)
                .map(videos -> {
                    Video siteBook = videos.get(0);
                    Video calibreBook = videos.get(1);
                    calibreBook.setCategoryId(siteBook.getCategoryId());
                    calibreBook.setImage_align(siteBook.getImage_align());
                    calibreBook.setViews(siteBook.getViews());
                    calibreBook.setLoads(siteBook.getLoads());
                    calibreBook.setLastdown(siteBook.getLastdown());
                    calibreBook.setRated_count(siteBook.getRated_count());
                    calibreBook.setTotal_rating(siteBook.getTotal_rating());
                    calibreBook.setComments(siteBook.getComments());
                    /*calibreBook.setStartDate(siteBook.getStartDate());
                    calibreBook.setEndDatedate(siteBook.getEndDatedate());
                    calibreBook.setDate(siteBook.getDate());*/
                    if (Math.abs(siteBook.getDate() - calibreBook.getDate()) <= 24 * 60 * 60) {
                        siteBook.setDate(calibreBook.getDate());
                    }
                    return new Pair<>(siteBook, calibreBook);
                })
                .filter(e -> !e.getKey().equals(e.getValue()))
                .collect(toList());

        Map<Video, List<Pair<String, Pair<String, String>>>> changedBooks = changed.stream().collect(toMap(Pair::getKey, pair -> {
            List<Pair<String, Pair<String, String>>> res = new ArrayList<>();
            JsonObject oldJsonObject = JsonUtils.gson.toJsonTree(pair.getKey()).getAsJsonObject();
            JsonObject newJsonObject = JsonUtils.gson.toJsonTree(pair.getValue()).getAsJsonObject();
            oldJsonObject.entrySet().forEach(e -> {
                if (!SiteRenderer.cleanHtml(e.getValue().toString()).equals(SiteRenderer.cleanHtml(newJsonObject.get(e.getKey()).toString()))) {
                    /*try {
                        Files.write(Paths.get("1.txt"), clean(e.getValue().toString()).getBytes());
                        Files.write(Paths.get("2.txt"), clean(newJsonObject.get(e.getKey()).toString()).getBytes());
                    } catch (Exception ee) {

                    }*/
                    if (e.getKey().equals("public")) {
                        String oldDate = timestampWithOffset(e.getValue().getAsLong(), 0);
                        String newDate = timestampWithOffset(newJsonObject.get(e.getKey()).getAsLong(), 1);
                        if (!oldDate.equals(newDate)) {
                            res.add(new Pair<>(e.getKey(), new Pair<>(oldDate, newDate)));
                        }
                    } else {
                        Pair<String, String> value = new Pair<>(e.getValue().getAsString(), newJsonObject.get(e.getKey()).getAsString());
                        System.out.println("====");
                        System.out.println(value.getKey());
                        System.out.println(value.getValue());
                        res.add(new Pair<>(e.getKey(), value));
                    }
                }
            });
            return res;
        })).entrySet().stream().filter(e -> !e.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ComparisionResult<>(category, addedBooks, deletedBooks, changedBooks);
    }

    private static String timestampWithOffset(long timestamp, int offset) {
        LocalDate localDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.ofHours(offset)).toLocalDate();
        return Long.toString(localDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
    }

/*
    private static boolean getChecked(Data data) {
        if (CalibreUtils.bookRecordMap.get(data.getCrc32()) != null) {
            return CalibreUtils.bookRecordMap.get(data.getCrc32()).getChecked();
        }
        return CalibreUtils.bookRecords.stream().filter(b -> b.getName().equals(data.getFileName())).
    }
*/

    public static String getCategoryName(String cpu) {
        return getCategoryByCpu(cpu).getCatname();
    }

    public static int getCategoryId(String cpu) {
        return getCategoryByCpu(cpu).getCatid();
    }

    public static BookCategory getCategoryByCpu(String cpu) {
        return getCategories().stream().filter(c -> c.getCatcpu().equals(cpu)).findFirst().orElseThrow(() -> new RuntimeException("BookCategory is null: "+ cpu));
    }

    public static BookCategory getCategoryById(Integer id) {
        return getCategories().stream().filter(c -> c.getCatid().equals(id)).findFirst().orElseThrow(() -> new RuntimeException("BookCategory is null: "+ id));
    }

    public static String getCategoryByTags(CalibreBook book) {
        if (book.getTags().size() == 1) {
            return book.getTags().get(0).getName();
        } else {
            return "consoles"; //TODO detect - consoles or computers
        }
    }

    public static List<String> syncDataWithSite(List<ComparisionResult<Video>> comparisionResults, boolean write) {

        List<String> lines = new ArrayList<>();

        comparisionResults.forEach(comparisionResult -> {
            List<String> insertQueries = comparisionResult.getAddedBooks().stream()
                    .map(b -> "-- " + b.getTitle() + "\n" + SiteDbUtils.objectToSqlInsertQuery(b, Video.class, "danny_media")).collect(toList());
            List<String> deleteQueries = comparisionResult.getDeletedBooks().stream()
                    .map(b -> "-- " + b.getTitle() + "\n" + "DELETE FROM `danny_media` WHERE downid=" + b.getId() + ";").collect(toList());
            List<String> updateQueries = comparisionResult.getChangedBooks().entrySet().stream().filter(b -> !b.getValue().isEmpty())
                    .map(b -> "-- " + b.getKey().getTitle() + "\n" + SiteDbUtils.comparisionResultToSqlUpdateQuery(b, "danny_media")).collect(toList());

            if (write) {
                List<String> results = deleteQueries.stream().map(SiteDbUtils::queryRequest).collect(toList());
                results.forEach(System.out::println);
                results = insertQueries.stream().map(SiteDbUtils::queryRequest).collect(toList());
                results.forEach(System.out::println);
                results = updateQueries.stream().map(SiteDbUtils::queryRequest).collect(toList());
                results.forEach(System.out::println);
            }

            if ((insertQueries.size() + deleteQueries.size() + updateQueries.size() > 0)) {
                String categoryName = getCategoryName(comparisionResult.getCategory());
                lines.add("-- " + StringUtils.repeat('=', categoryName.length()));
                lines.add("-- " + categoryName);
                lines.add("-- " + StringUtils.repeat('=', categoryName.length()));
                lines.add("");
            }

            lines.addAll(insertQueries);
            lines.addAll(deleteQueries);
            lines.addAll(updateQueries);
        });

        return lines;
    }

    public static void loadTiviIds(List<CalibreBook> calibreBooks, List<ComparisionResult<Video>> comparisionResults, String calibreDbDirectory) {
        CalibreUtils calibreUtils = new CalibreUtils(calibreDbDirectory);
        comparisionResults.forEach(comparisionResult -> {
            String category = comparisionResult.getCategory();
            //TODO "IN" QUERY ??
            comparisionResult.getAddedBooks().forEach(b -> {
                if (!category.equals("magazines") && !b.isGroup()) {
                    //!!!
                    if (!(b.getCpu().startsWith(category + "_"))
                            && !(b.getCpu().endsWith("_manuals"))
                            && !(b.getCpu().endsWith("_comics") && !BookUtils.getCategoryById(b.getCategoryId()).getCatcpu().equals("comics"))
                            && !(b.getCpu().endsWith("_docs")
                            && !(b.getCpu().endsWith("_guides")
                            && !(b.getCpu().endsWith("_emulators"))))) {
                        List<Video> videoList = SiteDbUtils.listBooks(b.getCpu(), b.getCategoryId());
                        if (videoList.isEmpty()) {
                            System.out.println(String.format("Empty. CPU: %s; CatId: %s", b.getCpu(), b.getCategoryId()));
                        } else {
                            Integer tiviId = videoList.get(0).getId();
                            System.out.println(tiviId);
                            System.out.println(b.getCpu());
                            Long bookId = calibreBooks.stream().filter(cb -> cb.getSiteCpu() != null && cb.getSiteCpu().equals(b.getCpu()))
                                    .min(Comparator.comparing(CalibreBook::getSort)).map(Book::getId).orElseThrow(() -> new RuntimeException("BookId is null"));

                            calibreUtils.upsertTiviId(bookId, tiviId);
                        }
                    }
                } else { // magazines
                    if (!b.getCpu().equals(MagazinesSearchPageRenderer.CPU) & !b.getCpu().equals(MagazinesSpecialPageRenderer.CPU)) {
                        List<Video> videoList = SiteDbUtils.listBooks(b.getCpu(), b.getCategoryId());
                        if (videoList.isEmpty()) {
                            System.out.println(String.format("Empty. CPU: %s; CatId: %s" + b.getCpu(), b.getCategoryId()));
                        } else {
                            Integer tiviId = videoList.get(0).getId();
                            System.out.println(tiviId);
                            System.out.println(b.getCpu());

                            Long bookId = calibreBooks.stream()
                                    .filter(cb -> cb.getSeries() != null && generateCpu(cb.getSeries().getName()).equals(b.getCpu()))
                                    .min(Comparator.comparing(Book::getSort)).map(Book::getId).orElseThrow(() -> new RuntimeException("BookId is null"));

                            calibreUtils.upsertTiviId(bookId, tiviId);
                        }
                    }
                }
            });
        });
    }

    //TODO delete???
    public static void loadTiviIds(List<CalibreBook> calibreBooks, String calibreDbDirectory) {
        CalibreUtils calibreUtils = new CalibreUtils(calibreDbDirectory);
        Map<String, Video> map = siteBooks.stream().collect(Collectors.toMap(Video::getCpu, Function.identity()));

        calibreBooks.stream().filter(b -> (b.getType().equals(BOOK) || b.getType().equals(COMICS))
                && b.getTiviId() == null && b.getOwn()
        ).forEach(b -> {
            Video video = map.get(b.getSiteCpu());
            if (video == null) {
                System.out.println(String.format("Empty. CPU: %s; Title: %s", b.getSiteCpu(), b.getTitle()));
            } else {
                System.out.println(String.format("Assigned TiviId %s to %s", video.getId(), b.getSiteCpu()));
                calibreUtils.upsertTiviId(b.getId(), video.getId());
            }
        });
        calibreBooks.stream().filter(b -> b.getType().equals(MAGAZINE) && b.getSeries() != null)
                .collect(Collectors.groupingBy(b -> b.getSeries().getName())).values().forEach(v -> {
            CalibreBook b = v.stream().sorted(Comparator.comparing(Book::getSort)).collect(Collectors.toList()).get(0);
            if (b.getTiviId() == null) {
                Video video = map.get(b.getSiteCpu());
                if (video == null) {
                    System.out.println(String.format("Empty magazine. CPU: %s; Title: %s", b.getSiteCpu(), b.getTitle()));
                } else {
                    System.out.println(String.format("Assigned TiviId %s to %s", video.getId(), b.getSiteCpu()));
                    calibreUtils.upsertTiviId(b.getId(), video.getId());
                }
            }
        });
    }

    public static void readBooks(CalibreInterface controller, List<CalibreBook> calibreBooks) {
        readBooks(controller, calibreBooks, null);
    }

    public static void readBooks(CalibreInterface controller, List<CalibreBook> calibreBooks, String dbFileName) {
        ProgressForm pForm = new ProgressForm();

        // In real life this task would do something useful and return some meaningful result:
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    calibreBooks.clear();
                    if (dbFileName == null) {
                        calibreBooks.addAll(new CalibreUtils().readBooks());
                    } else {
                        calibreBooks.addAll(new CalibreUtils(dbFileName).readBooks());
                    }
                    updateProgress(1, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        // binds progress of progress bars to progress of task:
        pForm.activateProgressBar(task);

        // in real life this method would get the result of the task
        // and update the UI based on its value:
        task.setOnSucceeded(event -> {
            pForm.getDialogStage().close();
            controller.updateStatus(true);
            //startButton.setDisable(false);
        });

        task.setOnFailed(event -> {
            final Text text = new Text(event.getSource().getException().getMessage());
            pForm.label.setText(text.getText());

            pForm.dialogStage.setWidth(text.getLayoutBounds().getWidth() + 10);
            controller.updateStatus(false);
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
            //label.setMinWidth(500);

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

        void activateProgressBar(final Task<?> task) {
            pb.progressProperty().bind(task.progressProperty());
            pin.progressProperty().bind(task.progressProperty());
            //label.textProperty().bind(task.messageProperty());
            dialogStage.show();
        }

        Stage getDialogStage() {
            return dialogStage;
        }
    }

    private static BookCategory getParentRoot(List<BookCategory> categories, String cpu) {
        BookCategory cat = getCategoryByCpu(cpu);
        if (cat.getParentid().equals(0)) {
            return cat;
        } else {
            return getParentRoot(categories, cat.getParentid());
        }
    }

    private static BookCategory getParentRoot(List<BookCategory> categories, Integer id) {
        BookCategory cat = categories.stream().filter(c -> c.getCatid().equals(id)).findFirst()
                .orElseThrow(() -> new RuntimeException("BookCategory is null"));
        if (cat.getParentid().equals(0)) {
            return cat;
        } else {
            return getParentRoot(categories, cat.getParentid());
        }
    }
}
