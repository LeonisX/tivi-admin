package md.leonis.tivi.admin.model.template;

import md.leonis.tivi.admin.model.calibre.CalibreBook;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceItem {

    private String uri;
    private String title;
    private String names;
    private List<CalibreBook> books;

    public SourceItem(Map.Entry<String, List<CalibreBook>> entry) {
        List<String> names = new ArrayList<>();
        entry.getValue().forEach(book -> {
            names.addAll(book.getAuthors().stream().flatMap(a -> Stream.of(a.getName().split("\\| "))).distinct().collect(Collectors.toList()));
            names.add(book.getScannedBy());
            names.add(book.getPostprocessing());
        });
        this.names = names.stream().filter(Objects::nonNull).filter(n -> !n.equals("Неизвестный")).distinct().collect(Collectors.joining(", "));

        formatDomain(entry.getKey(), entry.getValue().get(0).getSource());

        books = entry.getValue().stream().sorted(Comparator.comparing(CalibreBook::getSort)).collect(Collectors.toList());
    }


    public static String getDomain(String uri) {
        if (uri == null || uri.isEmpty()) {
            return "Другие источники";
        }
        String domain = URI.create(uri).getHost();
        if (domain == null) { // русские буквы в URI
            return "Другие источники";
        }
        if (domain.contains("vk.com") || domain.contains("t.me")) {
            domain = uri;
            if (domain.contains("?")) {
                domain = domain.split("\\?")[0]; // https://vk.com/coeavg?w=wall-46576991_4815
            }
        }
        return domain;
    }

    private void formatDomain(String title, String uri) {
        if (title.contains("vk.com")) {
            this.uri = uri;
            this.title = this.names;
            this.names = null;
        } else {
            this.uri = uri;
            if (uri != null && !uri.isEmpty()) {
                this.uri = String.format("%s://%s", URI.create(uri).getScheme(), title);
            }
            this.title = title;
        }
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public List<CalibreBook> getBooks() {
        return books;
    }

    public void setBooks(List<CalibreBook> books) {
        this.books = books;
    }
}
