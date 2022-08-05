package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.*;
import md.leonis.tivi.admin.model.danneo.Access;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.model.danneo.YesNo;
import md.leonis.tivi.admin.renderer.TextShortRenderer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.model.Type.EMULATOR;
import static md.leonis.tivi.admin.model.Type.GUIDE;
import static md.leonis.tivi.admin.utils.StringUtils.platformsTranslationMap;

public class CalibreBookVideoConverter {

    // on cloud: book, magazine, comics
    public static List<Type> onSiteList = Arrays.asList(/*DOC, */EMULATOR, GUIDE/*, MANUAL*/);

    static Video calibreToVideo(CalibreBook calibreBook, String category) {
        Video video = new Video();
        video.setTitle(calibreBook.getTitle());
        if (calibreBook.getTiviId() != null) {
            video.setId(Math.toIntExact(calibreBook.getTiviId()));
        }
        //TODO one time zone???
        if (calibreBook.getSignedInPrint() != null) {
            if (calibreBook.getSignedInPrint().isBefore(LocalDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.ofNanoOfDay(0)))) {
                video.setDate(4294967295L + 24 * 60 * 60 + calibreBook.getSignedInPrint().toEpochSecond(ZoneOffset.ofHours(-2)));
            } else {
                video.setDate(calibreBook.getSignedInPrint().toEpochSecond(ZoneOffset.ofHours(-2)));
            }
        }
        video.setCpu(calibreBook.getSiteCpu());
        video.setStartDate(0L);
        video.setEndDatedate(0L);

        List<Data> files = getDatasWithFileName(calibreBook);

        if (calibreBook.getDataList().isEmpty()) {
            video.setUrl("");
            video.setMirror("");
        } else {
            if (onSiteList.contains(calibreBook.getType())) {
                video.setUrl(SiteRenderer.generateDownloadLink(calibreBook.getType(), category, files.get(0).getFileName()));
                files.remove(0);
            } else {
                video.setUrl("");
                video.setMirror(BookUtils.cloudStorageLink);
                files.clear();
            }
        }
        if (calibreBook.getExternalLink() != null && !calibreBook.getExternalLink().isEmpty()) {
            video.setMirror(calibreBook.getExternalLink());
        } else {
            video.setMirror(BookUtils.cloudStorageLink); // exturl
        }
        video.setAge(""); // extsize
        video.setDescription(getDescription(calibreBook, category));
        video.setKeywords(getKeywords(calibreBook, category));
        video.setText(new TextShortRenderer(calibreBook).getTextShort());
        //TODO list other files
        video.setFullText(calibreBook.getTextMore());
        video.setUserText("");
        video.setMirrorsname("");
        video.setMirrorsurl("");
        video.setPlatforms("");
        video.setAuthor("");
        video.setAuthorSite("");
        video.setAuthorEmail("");
        video.setImage("");
        video.setOpenGraphImage(""); //image_thumb
        video.setImageAlt("");
        video.setActive(YesNo.yes);
        video.setAccess(Access.all);
        video.setListid(0);
        // TODO tags = "";
        return video;
    }

    static Video calibreMagazineToVideo(Map.Entry<CalibreBook, List<CalibreBook>> groupedMagazines, String category) {
        CalibreBook calibreBook = groupedMagazines.getValue().get(0);
        Video video = new Video();
        video.setTitle(calibreBook.getSeries().getName());
        if (calibreBook.getTiviId() != null) {
            video.setId(Math.toIntExact(calibreBook.getTiviId()));
        }
        if (calibreBook.getSignedInPrint() != null) {
            video.setDate(calibreBook.getSignedInPrint().toEpochSecond(ZoneOffset.ofHours(-2)));
        }
        video.setCpu(calibreBook.getSiteCpu());
        video.setStartDate(0L);
        video.setEndDatedate(0L);

        video.setUrl("");

        if (calibreBook.getExternalLink() != null && !calibreBook.getExternalLink().isEmpty()) {
            video.setMirror(calibreBook.getExternalLink());
        } else {
            video.setMirror(BookUtils.cloudStorageLink); // exturl
        }
        video.setAge(""); // extsize
        //TODO custom
        video.setDescription(getDescription(calibreBook, category));
        //TODO custom
        video.setKeywords(getKeywords(calibreBook, category));
        //TODO что-то обобщённое. продумать что выводить. нужен издатель, с какого года, платформы (все альт), описание
        if (!calibreBook.getOwn()) {
            calibreBook.setHasCover(0);
        }
        String cpu = calibreBook.getHasCover().equals(0) ? groupedMagazines.getValue().stream()
                .filter(b -> b.getOwn() != null && b.getOwn()).filter(b -> b.getHasCover() > 0)
                .sorted(Comparator.comparing(Book::getSort)).map(CalibreBook::getCpu).findFirst().orElseThrow(() -> new RuntimeException("cpu is null")) : calibreBook.getCpu();
        video.setText(new TextShortRenderer(calibreBook, cpu).getTextShort());
        /*if (calibreBook.getHasCover().equals(0)) {
            CalibreBook bookWithCover = groupedMagazines.getValue().stream().filter(b -> b.getOwn() != null && b.getOwn()).filter(b -> b.getHasCover() > 0).sorted(Comparator.comparing(Book::getSort)).findFirst().orElseThrow(() ->new RuntimeException("CalibreBook is null"));
            if (bookWithCover != null) {
                bookWithCover.setHasCover(0);
                video.setText(video.getText().replace(calibreBook.getCpu(), bookWithCover.getCpu()));
            }
        } else {
            groupedMagazines.getValue().get(0).setHasCover(0);
        }*/
        //TODO custom, generate - for all books
        //TODO list other (all) files
        video.setFullText(SiteRenderer.getMagazineFullText(groupedMagazines, category, cpu));
        video.setUserText("");
        video.setMirrorsname("");
        video.setMirrorsurl("");
        video.setPlatforms("");
        video.setAuthor("");
        video.setAuthorSite("");
        video.setAuthorEmail("");
        video.setImage("");
        video.setOpenGraphImage(""); //image_thumb
        video.setImageAlt("");
        video.setActive(YesNo.yes);
        video.setAccess(Access.all);
        video.setListid(0);
        // TODO tags = "";
        return video;
    }

    private static List<Data> getDatasWithFileName(CalibreBook calibreBook) {
        Set<String> fileNames = new HashSet<>();
        if (calibreBook.getDataList() == null) {
            return new ArrayList<>();
        }
        System.out.println(calibreBook.getDataList());
        String fileName = StringUtils.isBlank(calibreBook.getFileName()) ? calibreBook.getTitle() : calibreBook.getFileName();
        return calibreBook.getDataList().stream()
                .peek(data -> data.setFileName(FileUtils.findFreeFileName(fileNames, fileName, data.getFormat().toLowerCase(), 0))).collect(toList());
    }

    private static String getDescription(CalibreBook book, String category) {
        PlatformsTranslation translation = platformsTranslationMap.get(book.getType());
        /*System.out.println(book.getTitle());
        System.out.println(book.getOfficialTitle());
        System.out.println(book.getType());
        System.out.println(translation);*/
        return String.format(translation.getDescription(), ((book.getOfficialTitle() == null) ? book.getTitle() : book.getOfficialTitle()),
                BookUtils.getCategoryName(category));
    }

    private static String getKeywords(CalibreBook book, String category) {
        PlatformsTranslation translation = platformsTranslationMap.get(book.getType());
        List<String> chunks = new ArrayList<>(Arrays.asList(book.getTitle().toLowerCase().replaceAll("[^\\w\\sА-Яа-я]", "").split(" ")));
        chunks.add(book.getType().getValue());
        chunks.add(translation.getName());
        if (book.getPublisher() != null) {
            chunks.addAll(Arrays.asList(book.getPublisher().getName().toLowerCase().replaceAll("[^\\w\\sА-Яа-я]", "").split(" ")));
        }
        if (book.getIsbn() != null) {
            chunks.add(book.getIsbn());
        }
        chunks.addAll(Arrays.asList(book.getAuthors().stream().map(Author::getName).filter(n -> !n.equalsIgnoreCase("неизвестный")).collect(joining(" ")).toLowerCase().replaceAll("[^\\w\\sА-Яа-я]", "").split(" ")));
        chunks.add(category);
        chunks.addAll(new ArrayList<>(Arrays.asList(BookUtils.getCategories().stream().filter(c -> c.getCatcpu().equals(category)).findFirst().orElseThrow(() -> new RuntimeException("BookCategory is null")).getCatname().toLowerCase().replaceAll("[^\\w\\sА-Яа-я]", "").split(" "))));
        chunks.add(category);
        // TODO дополнить
        chunks.add(translation.getName());
        chunks.addAll(Arrays.asList(translation.getKeywords().split(", ")));
        if (book.getAltTags() != null) {
            chunks.addAll(book.getAltTags().stream().map(CustomColumn::getValue).collect(toList()));
        }
        return chunks.stream().filter(s -> !s.isEmpty()).distinct().map(String::toLowerCase).collect(joining(", "));
    }
}
