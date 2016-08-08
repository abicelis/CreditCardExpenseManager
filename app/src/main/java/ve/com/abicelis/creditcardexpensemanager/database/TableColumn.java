package ve.com.abicelis.creditcardexpensemanager.database;

/**
 * Created by Alex on 8/8/2016.
 */
public class TableColumn {

    private DataType datatype;
    private String name;

    public TableColumn(DataType datatype, String name) {
        this.datatype = datatype;
        this.name = name;
    }

    public DataType getDataType() {
        return datatype;
    }

    public String getName() {
        return name;
    }

}
