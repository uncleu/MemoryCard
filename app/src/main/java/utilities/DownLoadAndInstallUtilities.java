package utilities;

import android.content.Context;
import android.util.Log;

import com.memorycard.android.memorycardapp.CardsGroup;
import com.memorycard.android.memorycardapp.MemoryCardDataBaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DownLoadAndInstallUtilities {

    public static class Unzip {

        public static void start(String zipPath){
            String desc = zipPath.substring(0, zipPath.lastIndexOf('/'))+"/";
            try{
                unZipFile(zipPath, desc);
            }catch(IOException e) {
                e.printStackTrace();
            }
        }

        public static void unZipFile(String zipPath,String desc) throws IOException{
            unZipFile(new File(zipPath), desc);
        }

        public static void unZipFile(File zipFile,String desc) throws IOException{
            File path = new File(desc);
            if(!path.exists()){
                Log.e("Unzip","Path not exist");
                new File(desc).mkdirs();
            }
            ZipFile zip = null;
            if(zipFile.exists())
                zip = new ZipFile(zipFile);
            Enumeration entries = zip.entries();

            while(entries.hasMoreElements()){
                ZipEntry entry = (ZipEntry)entries.nextElement();
                InputStream in = zip.getInputStream(entry);
                String entryName = entry.getName();
                String outPath = (desc+entryName).replaceAll("\\*", "/");
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                //!file.exists()
                if(!file.exists()){
                    Log.e("Unzip","File not exist");
                }
                //test outPath is directory
                if(new File(outPath).isDirectory()){
                    continue;
                }
                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while((len=in.read(buf1))>0){
                    out.write(buf1,0,len);
                }
                in.close();
                out.close();
            }
            zip.close();

            Log.d("Unzip","File Unziped");
        }

    }

    public static void install(Context context, String path){

        //initToolBar();
        XmlUtilities xmltool = new XmlUtilities();
        InputStream  in= null;
        try {
            //in = getAssets().open("food.xml");
            File file = new File(path);
            in = new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CardsGroup cg= xmltool.xmlReader(in);
        cg.getTotal();

        DataBaseManager dbmanager = DataBaseManager.getDbManager(context);
        dbmanager.createNewCardsGroupTab(cg);
    }


}
