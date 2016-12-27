package utilities;

import android.util.Log;

import com.memorycard.android.memorycardapp.Card;
import com.memorycard.android.memorycardapp.CardsGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class XmlUtilities {

    private static final String TAG = "XmlUtilities";


    public CardsGroup xmlReader(InputStream inputStream) {

        String cardsGroupName = null;
        CardsGroup newCardsGroup = new CardsGroup();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(inputStream,"utf-8");
            int eventType = parser.getEventType();


            Card newCard = null;
            String text = "";
            int total=0;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("cards")) {
                            // create a new instance of employee
                            newCardsGroup.setName(parser.getAttributeValue(0));
                            newCardsGroup.setDiscription(parser.getAttributeValue(0));

                        } else if (tagname.equalsIgnoreCase("card")) {
                            newCard = new Card();
                            newCard.setmId(Integer.parseInt(parser.getAttributeValue(0))); //getId
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("card")) {
                            // add employee object to list
                            newCardsGroup.addCard(newCard);
                        } else if (tagname.equalsIgnoreCase("txtquestion")) {
                            newCard.setMtxtQuestion(text);
                        } else if (tagname.equalsIgnoreCase("txtanswer")) {
                            newCard.setMtxtAnswer(text);
                        } else if (tagname.equalsIgnoreCase("blobquestion")) {
                            newCard.setMblobQuestion(text);
                        } else if (tagname.equalsIgnoreCase("blobanswer")) {
                            newCard.setMblobAnswer(text);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            int test = 1;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error during Parsing xml file");
            e.printStackTrace();
        }catch (FileNotFoundException e){
            Log.e(TAG, "xml File not Found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e(TAG, "Error during F xml file");
            e.printStackTrace();
        }

        return newCardsGroup;
    }

}
