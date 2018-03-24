package md.leonis.tivi.admin.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookRecord {

    private Boolean checked;
    private String bookName;
    private Long id;
    private String name;
    private String tail;
    private Long size;
    private String crc32;

    public String toCsv() {
        return String.join(
                "\t",
                escape(checked),
                escape(bookName),
                id == null ? "" : Long.toString(id),
                escape(name),
                escape(tail),
                Long.toString(size),
                escape(crc32)
        );
    }

    public static BookRecord fromCsv(String csv) {
        String[] chunks = csv.split("\t");
        return BookRecord.newBuilder()
                .withChecked(unEscapeBoolean(chunks[0]))
                .withBookName(chunks[1])
                .withId(chunks[2].isEmpty() ? null : Long.valueOf(chunks[2]))
                .withName(chunks[3])
                .withTail(chunks[4])
                .withSize(Long.valueOf(chunks[5]))
                .withCrc32(chunks[6])
                .build();
        /*.withChecked(unEscapeBoolean(chunks[0]))
                .withBookName(unEscape(chunks[1]))
                .withId(Long.valueOf(chunks[2]))
                .withName(unEscape(chunks[3]))
                .withTail(unEscape(chunks[4]))
                .withSize(Long.valueOf(chunks[5]))
                .withCrc32(unEscape(chunks[6]))
                .build();*/
    }


    public static Builder newBuilder() {
        return new BookRecord().new Builder();
    }

    public class Builder {

        private Builder() {
            // private constructor
        }

        public Builder withChecked(Boolean checked) {
            BookRecord.this.checked = checked;
            return this;
        }

        public Builder withName(String name) {
            BookRecord.this.name = name;
            return this;
        }

        public Builder withCrc32(String crc32) {
            BookRecord.this.crc32 = crc32;
            return this;
        }

        public Builder withCrc32(long crc32) {
            //this.crc32 = Long.toHexString(crc32);
            BookRecord.this.crc32 = String.format("%08X", crc32);
            return this;
        }

        public Builder withSize(Long size) {
            BookRecord.this.size = size;
            return this;
        }

        public Builder withBookName(String bookName) {
            BookRecord.this.bookName = bookName;
            return this;
        }

        public Builder withTail(String tail) {
            BookRecord.this.tail = tail;
            return this;
        }


        public Builder withId(Long id) {
            BookRecord.this.id = id;
            return this;
        }

        public BookRecord build() {
            return BookRecord.this;
        }

    }


    /*private static String escape(Boolean bool) {
        if (bool == null) {
            return "\"\"";
        }
        if (bool) {
            return "\"+\"";
        } else {
            return "\"-\"";
        }
    }*/

    private static String escape(Boolean bool) {
        if (bool == null) {
            return "";
        }
        if (bool) {
            return "+";
        } else {
            return "-";
        }
    }

    private static String escape(String text) {
        if (text == null) {
            //return "\"\"";
            return "";
        } else {
            return text;
            //return "\"" + text.replace("\"", "\\\"") + "\"";
        }
    }

    public static String unEscape(String text) {
        if (text == null) {
            return "";
        } else {
            return text.replaceAll("^\"|\"$", "").replace("\\\"", "\"");
        }
    }

    /*public static Boolean unEscapeBoolean(String text) {
        switch (text) {
            case "\"+\"":
                return true;
            case "\"-\"":
                return false;
            default:
                return null;
        }
    }*/

    public static Boolean unEscapeBoolean(String text) {
        switch (text) {
            case "+":
                return true;
            case "-":
                return false;
            default:
                return null;
        }
    }
}
