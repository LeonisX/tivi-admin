package md.leonis.tivi.admin.model.mysql;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableStatus {

    @SerializedName("Name")
    private String name;

    @SerializedName("Engine")
    private String engine;

    @SerializedName("Version")
    private String version;

    @SerializedName("Row_format")
    private String rowFormat;

    @SerializedName("Rows")
    private long rows;

    @SerializedName("Avg_row_length")
    private long avgRowLength;

    @SerializedName("Data_length")
    private long dataLength;

    @SerializedName("Max_data_length")
    private long maxDataLength;

    @SerializedName("Index_length")
    private long indexLength;

    @SerializedName("Data_free")
    private long dataFreee;

    @SerializedName("Auto_increment")
    private String autoIncrement;

    @SerializedName("Create_time")
    private String createTime;

    @SerializedName("Update_time")
    private String updateTime;

    @SerializedName("Check_time")
    private String checkTime;

    @SerializedName("Collation")
    private String collation;

    @SerializedName("Checksum")
    private String checksum;

    @SerializedName("Create_options")
    private String createOptions;

    @SerializedName("Comment")
    private String comment;

}
