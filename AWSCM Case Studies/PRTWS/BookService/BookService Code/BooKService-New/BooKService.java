/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BooKService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author ani
 */
@WebService()
public class BooKService {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "GetVerseByBooKAndChapterAndVerseNumber")
    public String GetVerseByBooKAndChapterAndVerseNumber(@WebParam(name = "bookNumber")
    int bookNumber, @WebParam(name = "chapterNumber")
    int chapterNumber, @WebParam(name = "verseNumber")
    int verseNumber) {
        String verse = "";
        switch (bookNumber) {
            case 1:
                BGWS objBG = new BGWS();
                verse = objBG.bgWS(chapterNumber, verseNumber);
                break;
            case 2:
                BibleWS objBible = new BibleWS();
                verse = objBible.bibleWS(chapterNumber, verseNumber);
				System.out.println(" Verse " + verse);
                break;
            default:
                verse = "Invalid Book";
                break;
        }
        return verse;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "FindBookNumber")
    public String FindBookNumber() {
        return "For Book Selection: Bhag-Vad Gita as 1 and for Bible use 2";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "GetAllVerseByBookAndChapterNumber")
    public String GetAllVerseByBookAndChapterNumber(@WebParam(name = "bookNumber")
    int bookNumber, @WebParam(name = "chapterNumber")
    int chapterNumber) {
        String verse = "";
        switch (bookNumber) {
            case 1:
                BGWS objBG = new BGWS();
                verse = objBG.bgAllVerse(chapterNumber);
                break;
            case 2:
                BibleWS objBible = new BibleWS();
                verse = objBible.bibleAllVerse(chapterNumber);
                break;

            default:
                verse = "Invalid Book";
                break;
        }
        return verse;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "GetAbstractOfChapter")
    public String GetAbstractOfChapter(@WebParam(name = "bookNumber")
    int bookNumber, @WebParam(name = "chapterNumber")
    int chapterNumber) {
       String abst = "";
        switch (bookNumber) {
            case 1:
                BGWS objBG = new BGWS();
                abst = objBG.bgWSAbst(chapterNumber);
                break;
            case 2:
                BibleWS objBible = new BibleWS();
                abst = objBible.bibleWSAbst(chapterNumber);
                break;

            default:
                abst = "Invalid Book";
                break;
        }
        return abst;
    }


}
