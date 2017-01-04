package utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.memorycard.android.memorycardapp.Card;
import com.memorycard.android.memorycardapp.CardsGroup;
import com.memorycard.android.memorycardapp.MemoryCardContentProvider;
import com.memorycard.android.memorycardapp.MemoryCardDataBaseHelper;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class DataBaseManager {

    private static String TAG = "DataBaseManager";
    private static DataBaseManager dbManager;

    public static MemoryCardDataBaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    private static MemoryCardDataBaseHelper databaseHelper;
    private static SQLiteDatabase readableDB;
    private static SQLiteDatabase writableDB;

    private Context mContext;

    public static final String LIST_TAB = "cardsgroup_list";

    private static String CREATE_CARDSGROUP_TAB = "create ?(" +
            "_id integer primary key," +
            "group_id integer," +
            "difficulty integer," +
            "txt_question text," +
            "txt_answer text," +
            "blob_question blob," +
            "blob_answer blob)";

    private static String CREATE_GROUPLIST_TAB = "create cardsgroup_list (" +
            "_id integer primary key," +
            "name text," +
            "tab_name text," +
            "create_date long," +
            "lastmodif_date long)";


    //card db columns
    public static final String COL_CARD_ID = "_id";
    public static final String COL_CARD_GROUP_ID = "group_id";
    public static final String COL_CARD_TAB_NAME = "tab_name";
    public static final String COL_CARD_DIFFICULTY = "difficulty";
    public static final String COL_CARD_DAY = "day";
    public static final String COL_CARD_TXT_QUESTION = "txt_question";
    public static final String COL_CARD_TXT_ANSWER = "txt_answer";
    public static final String COL_CARD_IMG_QUESTION = "img_question";
    public static final String COL_CARD_IMG_ANSWER = "img_answer";


    //cardgroup list columns
    public static final String COL_LIST_ID = "_id";
    public static final String COL_LIST_NAME = "name";
    public static final String COL_LIST_TAB_NAME = "tab_name";
    public static final String COL_LIST_TAB_DESCRIPTION= "description";
    public static final String COL_LIST_CREATE_DATE = "create_date";
    public static final String COL_LIST_LAST_MODIF_DATE = "lastmodif_date";
    public static final String COL_LIST_ACCURACY = "accuracy";


    public static final String[] listProjection =
            {
                    DataBaseManager.COL_LIST_ID,
                    DataBaseManager.COL_LIST_NAME,
                    DataBaseManager.COL_LIST_TAB_NAME,
                    DataBaseManager.COL_LIST_TAB_DESCRIPTION,
                    DataBaseManager.COL_LIST_CREATE_DATE,
                    DataBaseManager.COL_LIST_LAST_MODIF_DATE,
                    DataBaseManager.COL_LIST_ACCURACY
            };


    public static final String[] cardProjection =
            {
                    DataBaseManager.COL_CARD_ID,
                    DataBaseManager.COL_CARD_GROUP_ID,
                    DataBaseManager.COL_CARD_TAB_NAME,
                    DataBaseManager.COL_CARD_DIFFICULTY,
                    DataBaseManager.COL_CARD_DAY,
                    DataBaseManager.COL_CARD_TXT_QUESTION,
                    DataBaseManager.COL_CARD_TXT_ANSWER,
                    DataBaseManager.COL_CARD_IMG_QUESTION,
                    DataBaseManager.COL_CARD_IMG_ANSWER
            };


    public static DataBaseManager getDbManager(Context context) {
        if (dbManager == null) {
            dbManager = new DataBaseManager(context);
        }

        return dbManager;
    }

    private DataBaseManager(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new MemoryCardDataBaseHelper(context);
            CreateCardsGroupDBListe();
        }
    }


    private int getMaxGroupListId() {
        String sql = "select max(_id) as _id from cardsgroup_list";
        try {
            readableDB = databaseHelper.getReadableDatabase();
            // Cursor cursor = readableDB.query("cardsgroup_list", new String[]{"MAX(id) as id"}, null, null, null, null, null);
            Cursor cursor = readableDB.rawQuery(sql, null);

            Integer id = 0;
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
            }

            return id;
        } catch (Exception e) {
            Log.e(TAG, "error found max id of grouplist in database");
            e.printStackTrace();
        }
        return 0;
    }

    private int getMaxCardId(String tab_name) {
        String sql = "select max(_id) from ?";
        try {
            readableDB = databaseHelper.getReadableDatabase();
            Cursor cursor = readableDB.query(tab_name, new String[]{"MAX(_id) as _id"}, null, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String sId = cursor.getString(cursor.getColumnIndex("_id"));
                    Log.d(TAG, "found max CardId of " + tab_name + " in database successful");
                    return Integer.parseInt(sId);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "error found max CardId of " + tab_name + " in database");
            e.printStackTrace();
        }
        return 0;
    }

    public static void ceateNewCardsGroupDataBaseTable(String tab_name) {
        String sql = "create table if not exists " +
                tab_name +
                "(" +
                "_id integer primary key," +
                "group_id integer," +
                "tab_name text," +
                "difficulty integer," +
                "day integer," +
                "txt_question text," +
                "txt_answer text," +
                "img_question blob," +
                "img_answer text)";
        try {
            writableDB = databaseHelper.getWritableDatabase();
            writableDB.execSQL(sql);
        } catch (Exception e) {
            Log.e(TAG, "error during creating new card group table");
            e.printStackTrace();
        }
    }


    public void createNewCardsGroupTab(CardsGroup cardsgroup) {

        String tab_name = addCardsGroupIntoGroupList(cardsgroup);

        //create tabe
        if (tab_name != null) {
            ceateNewCardsGroupDataBaseTable(tab_name);
        }

        //fill table
        if (cardsgroup != null && cardsgroup.getTotal() > 0) {
            List<Card> cardslist = cardsgroup.getmCardsList();

            for (Card card : cardslist) {
                addCard(card, tab_name);
            }
        }
    }

    private String addCardsGroupIntoGroupList(CardsGroup cardsgroup) {
        String tab_name = null;
        try {
            //tables
            int newId = getMaxGroupListId() + 1;
            tab_name = "t" + newId;
            writableDB = databaseHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", newId);
            contentValues.put("name", cardsgroup.getName());
            contentValues.put("tab_name", tab_name);
            contentValues.put("create_date", TimeUtilities.getCurrentTimeInMillies());
            contentValues.put("lastmodif_date", TimeUtilities.getCurrentTimeInMillies());
            Long id = writableDB.insert("cardsgroup_list", null, contentValues);
            Log.d(TAG, "adding card group into group list successful");
        } catch (Exception e) {
            Log.e(TAG, "error during adding card group into group list");
            e.printStackTrace();
        }
        return tab_name;
    }


    private void addCard(Card card, String tab_name) {

        String sql = "insert into " +
                tab_name +
                "(" +
                "_id," +
                "group_id," +
                "tab_name," +
                "difficulty," +
                "day," +
                "txt_question," +
                "txt_answer," +
                "img_question," +
                "img_answer) values(?,?,?,?,?,?,?,?,?)";
        try {
            writableDB = databaseHelper.getWritableDatabase();
            writableDB.execSQL(sql, new Object[]{
                    card.getmId(),
                    card.getmGroupId(),
                    tab_name,
                    card.getmDifficultyScore(),
                    card.getmDay(),
                    card.getMtxtQuestion(),
                    card.getMtxtAnswer(),
                    card.getMblobQuestion(),
                    card.getMblobAnswer()});
            Log.d(TAG, "adding card " +
                    card.getmId() +
                    " into table " +
                    tab_name +
                    " successful");
        } catch (Exception e) {
            Log.e(TAG, "error during adding new Card, Card Id = " + card.getmId());
            e.printStackTrace();
        }
    }

    void updateExistedCard(Card card, String tab_name) {
        String sql = "update " +
                tab_name +
                "(" +
                "_id integer primary key," +
                "group_id integer," +
                "tab_name text," +
                "difficulty integer," +
                "day integer" +
                "txt_question text," +
                "txt_answer text," +
                "img_question blob," +
                "img_answer blob) values(?,?,?,?,?,?,?,?,?)";

        writableDB = databaseHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("group_id", card.getmGroupId());
        cv.put("tab_name", card.getMtabName());
        cv.put("difficulty", card.getmDifficultyScore());
        cv.put("day", card.getmDay());
        cv.put("txt_question", card.getMtxtQuestion());
        cv.put("txt_answer", card.getMtxtAnswer());
        cv.put("img_question", card.getMblobQuestion());
        cv.put("img_answer", card.getMblobAnswer());
        try {
            writableDB.update(tab_name, cv, "_id=" + card.getmDay(), null);
            Log.d(TAG, "Updating card " +
                    card.getmId() +
                    " into table " +
                    tab_name +
                    " successful");
        } catch (Exception e) {
            Log.e(TAG, "error during update existed Card, Card Id = " + card.getmId());
            e.printStackTrace();
        }
    }

    public void updateCard(Card card) {
        String tab_name = card.getMtabName();
        updateExistedCard(card, tab_name);
    }

    private void updateCardsGroup(Card card, String tab_name) {
        int cardId = card.getmId();

        if (cardId == 0) {
            cardId = getMaxCardId(tab_name);
            card.setmId(cardId);
            addCard(card, tab_name);
        } else {
            updateExistedCard(card, tab_name);
        }

    }

    private CardsGroup getCardsGroupInfoById(int id) {
        String sql = "select _id from tab_name";
        CardsGroup cardsGroup = new CardsGroup();
        try {
            readableDB = databaseHelper.getReadableDatabase();
            Cursor cursor = readableDB.query(LIST_TAB,
                    new String[]{"name", "tab_name", "description", "lastmodif_date"},
                    "_id=" + id,
                    null, null, null, null);

            String sName = cursor.getString(cursor.getColumnIndex("name"));
            String sTab_name = cursor.getString(cursor.getColumnIndex("tab_name"));
            String lastmodif_date = cursor.getString(cursor.getColumnIndex("lastmodif_date"));
            String description = cursor.getString(cursor.getColumnIndex("description"));

            cardsGroup.setName(sName);
            cardsGroup.setTab_name(sTab_name);
            cardsGroup.setDiscription(description);
            cardsGroup.setlLastModifTimeInMillis(Long.parseLong(lastmodif_date));

            Log.d(TAG, "found name " + sName + " in database successful");
            Log.d(TAG, "found tab_name " + sTab_name + " in database successful");
            Log.d(TAG, "found lastmodif_date " + lastmodif_date + " in database successful");
            Log.d(TAG, "found description " + description + " in database successful");

        } catch (Exception e) {
            Log.e(TAG, "error found during getCardsGroupInfoById  group_id = " + id + " in database");
            e.printStackTrace();
        }
        return cardsGroup;
    }

    public List<Card> loadCardsByTabName(String tabName) {

        List<Card> cardslist = new ArrayList<>();
        try {
            readableDB = databaseHelper.getReadableDatabase();

            Cursor cursor = readableDB.rawQuery("select * from " + tabName, null);

            while (cursor.moveToNext()) {

                Card card = new Card();

                card.setmId(cursor.getInt(0));//id
                card.setmGroupId(cursor.getInt(1));//groupId
                card.setMtabName(tabName);
                card.setmDifficultyScore(cursor.getInt(3));
                card.setmDay(cursor.getInt(4));//
                if(cursor.getInt(4)==0){
                    card.setmDay(1);
                } else
                    card.setmDay(cursor.getInt(4));
                card.setMtxtQuestion(cursor.getString(5));
                card.setMtxtAnswer(cursor.getString(6));
                byte[] bytes = cursor.getBlob(7);
                card.setMblobQuestion(bytes);
                card.setMblobAnswer(cursor.getString(8));

                cardslist.add(card);
                Log.d(TAG, "found CardsGroup " + tabName + " in database successful");
            }

        } catch (Exception e) {
            Log.e(TAG, "error found during getCardsGroupList in database ");
            e.printStackTrace();
        }

        return cardslist;
    }

    void deleteCardsGroupFromDataBase(CardsGroup cardsGroup) {
        String tab_name = cardsGroup.getTab_name();
        int groupId = cardsGroup.getmId();
        String sql = "delete from " +
                tab_name +
                " where _id = ?";
        writableDB = databaseHelper.getWritableDatabase();
        try {
            writableDB.execSQL(sql, new String[]{Integer.toString(groupId)});
            Log.d(TAG, "Delete cardsgroup " +
                    cardsGroup.getName() +
                    "from grouplist successful");
        } catch (Exception e) {
            Log.e(TAG, "error during delete CardsGroup " + cardsGroup.getName());
            e.printStackTrace();
        }

        sql = "drop table " + tab_name;
        try {
            writableDB = databaseHelper.getWritableDatabase();
            writableDB.execSQL(sql, new String[]{tab_name});
            Log.d(TAG, "Delete cardsgroup table from database" + cardsGroup.getName() + " successful");
        } catch (Exception e) {
            Log.e(TAG, "error during delete CardsGroup " + cardsGroup.getName());
            e.printStackTrace();
        }


    }

    public void deleteCardsGroupFromDataBase(ContentResolver cr,String tab_name){

        String where = COL_LIST_TAB_NAME + "=?";
        String[] args = new String[] { tab_name };
        Uri uri = Uri.withAppendedPath(MemoryCardContentProvider.BASE_URI,LIST_TAB);
        MemoryCardContentProvider.checkURI(uri,LIST_TAB);
        MemoryCardContentProvider.tabName = LIST_TAB;
        cr.delete(uri, where, args );

        dropTable(tab_name);

    }




    void CreateCardsGroupDBListe() {
        String sql = "create table if not exists cardsgroup_list (" +
                "_id integer primary key," +
                "name text," +
                "tab_name text," +
                "description text," +
                "create_date long," +
                "lastmodif_date long," +
                "accuracy integer)";

        writableDB = databaseHelper.getWritableDatabase();
        try {
            writableDB.execSQL(sql);
            Log.d(TAG, "Create cardsgroup list table into database successful");
        } catch (Exception e) {
            Log.e(TAG, "error during Create cardsgroup list table into database");
            e.printStackTrace();
        }
    }

    public void dropTable(String tab_name) {
        String sql = "drop table if exists " + tab_name;
        writableDB = databaseHelper.getWritableDatabase();
        try {
            writableDB.execSQL(sql);
            Log.d(TAG, "drop cardsgroup list table into database successful");
        } catch (Exception e) {
            Log.e(TAG, "error during drop cardsgroup list table into database");
            e.printStackTrace();
        }

    }


    public ArrayList<CardsGroup> loadCardsGroupList() {
        ArrayList<CardsGroup> list = new ArrayList<>();

        try {
            readableDB = databaseHelper.getReadableDatabase();

            Cursor cursor = readableDB.rawQuery("select * from " + LIST_TAB, null);

            while (cursor.moveToNext()) {

                CardsGroup cardsGroup = new CardsGroup();

                cardsGroup.setmId(cursor.getInt(0));
                cardsGroup.setName(cursor.getString(1));
                cardsGroup.setTab_name(cursor.getString(2));
                cardsGroup.setDiscription(cursor.getString(3));
                cardsGroup.setlLastModifTimeInMillis(cursor.getLong(5));
                cardsGroup.setAccuracy(cursor.getInt(6));

                list.add(cardsGroup);
                Log.d(TAG, "found CardsGroup " + cardsGroup.getName() + " in database successful");
            }

        } catch (Exception e) {
            Log.e(TAG, "error found during getCardsGroupList in database ");
            e.printStackTrace();
        }

        return list;

    }

    public void updateCardsGroup(CardsGroup cardsGroup){
            writableDB = databaseHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("lastmodif_date", cardsGroup.getlLastModifTimeInMillis());
            try {
                writableDB.update(LIST_TAB, cv, "_id=" + cardsGroup.getmId(), null);
                Log.d(TAG, "Updating cardgroup " +
                        cardsGroup.getmId() +
                        " into table " +
                        LIST_TAB +
                        " successful");
            } catch (Exception e) {
                Log.e(TAG, "error during update existed cardgroup, Id = " + cardsGroup.getmId());
                e.printStackTrace();
            }

    }


    public static boolean isTableExists(String tableName) {

        try {
            readableDB = databaseHelper.getReadableDatabase();

            Cursor cursor = readableDB.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});

            if (!cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;

        } catch (Exception e) {
            Log.e(TAG, "error found during getCardsGroupList in database ");
            e.printStackTrace();
            return false;
        }
    }




    public static List<CardsGroup> extractCGfromCursor(Cursor cursor) {
        List<CardsGroup> list = new ArrayList<>();


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                CardsGroup cardsGroup = new CardsGroup();

                cardsGroup.setmId(cursor.getInt(0));
                cardsGroup.setName(cursor.getString(1));
                cardsGroup.setTab_name(cursor.getString(2));
                cardsGroup.setDiscription(cursor.getString(3));
                cardsGroup.setlLastModifTimeInMillis(cursor.getLong(5));
                cardsGroup.setAccuracy(cursor.getInt(6));

                list.add(cardsGroup);
                Log.d(TAG, "found CardsGroup " + cardsGroup.getName());
            }


        }
        return list;

    }


    public static List<Card> extractCardfromCursor(Cursor cursor) {

        List<Card> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {

                Card card = new Card();

                card.setmId(cursor.getInt(0));//id
                card.setmGroupId(cursor.getInt(1));//groupId
                card.setMtabName(cursor.getString(2));
                card.setmDifficultyScore(cursor.getInt(3));
                card.setmDay(cursor.getInt(4));
                card.setMtxtQuestion(cursor.getString(5));
                card.setMtxtAnswer(cursor.getString(6));
                card.setMblobQuestion(cursor.getBlob(7));
                card.setMblobAnswer(cursor.getString(8));

                list.add(card);
                Log.d(TAG, "found Card");
            }

        }
        return list;
    }

    public static ContentValues generateContentValues(Card card){

        ContentValues cv = new ContentValues();
        cv.put("group_id", card.getmGroupId());
        cv.put("tab_name", card.getMtabName());
        cv.put("difficulty", card.getmDifficultyScore());
        cv.put("day", card.getmDay());
        cv.put("txt_question", card.getMtxtQuestion());
        cv.put("txt_answer", card.getMtxtAnswer());
        cv.put("img_question", card.getMblobQuestion());
        cv.put("img_answer", card.getMblobAnswer());

        return cv;
    }

}