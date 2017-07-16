package ru.SnowVolf.certgirl;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by Snow Volf on 15.07.2017, 19:59
 */

public class FilePathUtils {
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        if (!contentUri.toString().startsWith("file://"))
            return contentUri.getPath();

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri,
                filePathColumn, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        cursor.close();

        return cursor.getString(column_index);
    }
}
