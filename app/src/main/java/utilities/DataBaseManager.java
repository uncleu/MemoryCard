package utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.memorycard.android.memorycardapp.Card;
import com.memorycard.android.memorycardapp.CardsGroup;
import com.memorycard.android.memorycardapp.MemoryCardDataBaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yina on 2016/12/18.
 */

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

    private static final String LIST_TAB = "cardsgroup_list";

    private static String CREATE_CARDSGROUP_TAB = "create ?(" +
            "id integer primary key," +
            "groupId integer," +
            "difficulty integer," +
            "txt_question text," +
            "txt_answer text," +
            "blob_question blob," +
            "blob_answer blob)";

    private static String CREATE_GROUPLIST_TAB = "create cardsgroup_list (" +
            "id integer primary key," +
            "name text," +
            "tab_name text," +
            "create_date long," +
            "lastmodif_date long)";


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
        String sql = "select max(id) as id from cardsgroup_list";
        try {
            readableDB = databaseHelper.getReadableDatabase();
           // Cursor cursor = readableDB.query("cardsgroup_list", new String[]{"MAX(id) as id"}, null, null, null, null, null);
            Cursor cursor = readableDB.rawQuery(sql, null);

            Integer id = 0;
            if(cursor != null){
                if(cursor.moveToFirst()){
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
        String sql = "select max(id) from ?";
        try {
            readableDB = databaseHelper.getReadableDatabase();
            Cursor cursor = readableDB.query(tab_name, new String[]{"MAX(id) as id"}, null, null, null, null, null);

            if(cursor != null){
                if(cursor.moveToFirst()){
                    String sId = cursor.getString(cursor.getColumnIndex("id"));
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

    public void ceateNewCardsGroupDataBaseTable(String tab_name) {
        String sql = "create table if not exists "+
                tab_name+
                "(" +
                "id integer primary key," +
                "groupId integer," +
                "tab_name text," +
                "difficulty integer," +
                "day integer," +
                "txt_question text," +
                "txt_answer text," +
                "blob_question text," +
                "blob_answer text)";
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
        if (cardsgroup.getTotal() > 0) {
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
            contentValues.put("id", newId);
            contentValues.put("name", cardsgroup.getName());
            contentValues.put("tab_name", tab_name);
            contentValues.put("create_date", TimeUtilities.getCurrentTimeInMillies());
            Long id = writableDB.insert("cardsgroup_list", null, contentValues);
            Log.d(TAG, "adding card group into group list successful");
        } catch (Exception e) {
            Log.e(TAG, "error during adding card group into group list");
            e.printStackTrace();
        }
        return tab_name;
    }


    private void addCard(Card card, String tab_name) {

        String sql = "insert into "+
                tab_name+
                "(" +
                "id," +
                "groupId," +
                "tab_name,"+
                "difficulty," +
                "day," +
                "txt_question," +
                "txt_answer," +
                "blob_question," +
                "blob_answer) values(?,?,?,?,?,?,?,?)";
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
        String sql = "update "+
                tab_name+
                "(" +
                "id integer primary key," +
                "groupId integer," +
                "tab_name text,"+
                "difficulty integer," +
                "day integer" +
                "txt_question text," +
                "txt_answer text," +
                "blob_question blob," +
                "blob_answer blob) values(?,?,?,?,?,?,?,?)";

        writableDB = databaseHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("groupId", card.getmGroupId());
        cv.put("tab_name",card.getMtabName());
        cv.put("difficulty", card.getmDifficultyScore());
        cv.put("day", card.getmDay());
        cv.put("txt_question", card.getMtxtQuestion());
        cv.put("txt_answer", card.getMtxtAnswer());
        cv.put("blob_question", card.getMblobQuestion());
        cv.put("blob_answer", card.getMblobAnswer());
        try {
            writableDB.update(tab_name, cv, "id=" + card.getmDay(), null);
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

    public void updateCard(Card card){
        String tab_name = card.getMtabName();
        updateExistedCard(card,tab_name);
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
        String sql = "select id from tab_name";
        CardsGroup cardsGroup = new CardsGroup();
        try {
            readableDB = databaseHelper.getReadableDatabase();
            Cursor cursor = readableDB.query(LIST_TAB,
                    new String[]{"name", "tab_name", "description", "lastmodif_date"},
                    "id=" + id,
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
            Log.e(TAG, "error found during getCardsGroupInfoById  groupid = " + id + " in database");
            e.printStackTrace();
        }
        return cardsGroup;
    }

    public List<Card> loadCardsByTabName(String tabName) {

        List<Card> cardslist = new ArrayList<>();
        try {
            readableDB = databaseHelper.getReadableDatabase();

            Cursor cursor = readableDB.rawQuery("select * from "+tabName,null);

            while(cursor.moveToNext()){

                Card card = new Card();

                card.setmId(cursor.getInt(0));//id
                card.setmGroupId(cursor.getInt(1));//groupId
                card.setMtabName(tabName);
                card.setmDifficultyScore(cursor.getInt(3));
                card.setmDay(cursor.getInt(4));
                card.setMtxtQuestion(cursor.getString(5));
                card.setMtxtAnswer(cursor.getString(6));
                card.setMblobQuestion(cursor.getString(7));
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

    void deleteCardsGroupFromDataBase(CardsGroup cardsGroup)
    {
        String tab_name = cardsGroup.getTab_name();
        int groupId = cardsGroup.getmId();
        String sql = "delete from "+
                tab_name+
                " where id = ?";
        writableDB = databaseHelper.getWritableDatabase();
        try {
            writableDB.execSQL(sql,new String[]{Integer.toString(groupId)});
            Log.d(TAG, "Delete cardsgroup " +
                    cardsGroup.getName() +
                    "from grouplist successful");
        } catch (Exception e) {
            Log.e(TAG, "error during delete CardsGroup " + cardsGroup.getName() );
            e.printStackTrace();
        }

        sql = "drop table "+tab_name;
        try {
            writableDB = databaseHelper.getWritableDatabase();
            writableDB.execSQL(sql,new String[]{tab_name});
            Log.d(TAG, "Delete cardsgroup table from database" + cardsGroup.getName() + " successful");
        } catch (Exception e) {
            Log.e(TAG, "error during delete CardsGroup " + cardsGroup.getName() );
            e.printStackTrace();
        }


    }

    void CreateCardsGroupDBListe(){
        String sql ="create table if not exists cardsgroup_list (" +
                "id integer primary key," +
                "name text," +
                "tab_name text," +
                "description text," +
                "create_date long," +
                "lastmodif_date long)";

        writableDB = databaseHelper.getWritableDatabase();
        try {
            writableDB.execSQL(sql);
            Log.d(TAG, "Create cardsgroup list table into database successful");
        } catch (Exception e) {
            Log.e(TAG, "error during Create cardsgroup list table into database" );
            e.printStackTrace();
        }
    }

    public void dropTable(String tab_name){
        String sql = "drop table if exists "+tab_name;
        writableDB = databaseHelper.getWritableDatabase();
        try {
            writableDB.execSQL(sql);
            Log.d(TAG, "drop cardsgroup list table into database successful");
        } catch (Exception e) {
            Log.e(TAG, "error during drop cardsgroup list table into database" );
            e.printStackTrace();
        }

    }



    public List<CardsGroup> loadCardsGroupList(){
        List<CardsGroup> list = new ArrayList<>();

        try {
            readableDB = databaseHelper.getReadableDatabase();

            Cursor cursor = readableDB.rawQuery("select * from "+LIST_TAB, null);

            while(cursor.moveToNext()){

                CardsGroup cardsGroup = new CardsGroup();

                cardsGroup.setmId(cursor.getInt(0));
                cardsGroup.setName(cursor.getString(1));
                cardsGroup.setTab_name(cursor.getString(2));
                cardsGroup.setDiscription(cursor.getString(3));
                cardsGroup.setlLastModifTimeInMillis(cursor.getLong(5));

                list.add(cardsGroup);
                Log.d(TAG, "found CardsGroup " + cardsGroup.getName() + " in database successful");
            }

        } catch (Exception e) {
            Log.e(TAG, "error found during getCardsGroupList in database ");
            e.printStackTrace();
        }

        return  list;

    }




}
