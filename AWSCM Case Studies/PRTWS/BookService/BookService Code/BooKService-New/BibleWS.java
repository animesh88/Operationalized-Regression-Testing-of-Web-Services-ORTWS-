/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BooKService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author ani
 */
@WebService()
public class BibleWS {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "bibleWS")
    public String bibleWS(@WebParam(name = "chapterNumber") int chapterNumber, @WebParam(name = "verseNumber") int verseNumber) {
        String verse = "";
        BibleVerse obj = new BibleVerse();
        switch (chapterNumber) {
            case 1:
                verse = "God created the heavens, the earth and everything that lives. He made humankind in his image, and gave them charge over the earth.";
                break;
            case 2:
                verse = "God formed a man and gave him the garden in Eden, except for the tree of knowledge. Adam was alone so God made a woman as his partner.";
                break;
            case 3:
                verse = "The serpent deceived the woman; she and Adam ate from the tree. The earth became cursed, and God sent Adam and Eve out of the garden.";
                break;
            case 4:
                verse = "Eve's sons made offerings to God. Only Abel's was acceptable, so Cain killed him. Abel's blood cried out and God sent Cain away.";
                break;
            case 5:
                verse = "Adam's line was: Seth, Enosh, Kenan, Mahalalel, Jared, Enoch, Methuselah, Lamech and Noah. Noah's sons were Shem, Ham and Japheth.";
                break;
            case 6:
                verse = "Humankind corrupted the earth with evil. God decided to destroy them. He told Noah to build an ark to be saved from the flood.";
                break;
            case 7:
                verse = "Noah and his family went into the ark with two of each creature. It rained for forty days and forty nights and the earth was covered.";
                break;
            case 8:
                verse = "Noah sent out a raven and two doves. When the earth was dry God called them all out of the ark. Noah built an altar.";
                break;
            case 9:
                verse = "God blessed Noah and set the rainbow as a sign that he would never flood the earth again. Noah got drunk and cursed Ham's son Canaan.";
                break;
            case 10:
                verse = "Japheth's line lived in the coastlands; Ham's included Nimrod and the Canaanites; Shem's lived in the East. These formed the nations.";
                break;
            case 11:
                verse = "They began building a great tower for themselves, but the Lord confused their language. Shem's line included Abram who married Sarai.";
                break;
            case 12:
                verse = "God told Abram, \"Go. I will make you a great nation. You will be a blessing.\" In Egypt Abram lied about Sarai and Pharaoh was cursed.";
                break;
            case 13:
                verse = "";
                break;
            case 14:
                verse = "";
                break;
            case 15:
                verse = "";
                break;
            case 16:
                verse = "";
                break;
            case 17:
                verse = "";
                break;
            case 18:
                verse = "";
                break;

            default:
                verse = "Invalid chapter number";
                break;
        }
        //System.out.println(verse);

        return verse;
    }

    @WebMethod(operationName = "bibleWSAbst")
    public String bibleWSAbst(@WebParam(name = "chapterNumber") int chapterNumber) {
        String verse = "";
        switch (chapterNumber) {
            case 1:
                verse = "";
                break;
            case 2:
                verse = "";
                break;
            case 3:
                verse = "";
                break;
            case 4:
                verse = "";
                break;
            case 5:
                verse = "";
                break;
            case 6:
                verse = "";
                break;
            case 7:
                verse = "";
                break;
            case 8:
                verse = "";
                break;
            case 9:
                verse = "";
                break;
            case 10:
                verse = "";
                break;
            case 11:
                verse = "";
                break;
            case 12:
                verse = "";
                break;
            case 13:
                verse = "";
                break;
            case 14:
                verse = "";
                break;
            case 15:
                verse = "";
                break;
            case 16:
                verse = "";
                break;
            case 17:
                verse = "";
                break;
            case 18:
                verse = "";
                break;

            default:
                verse = "Invalid chapter number";
                break;
        }
        return verse;
    }

    @WebMethod(operationName = "bibleAllVerse")
    public String bibleAllVerse(@WebParam(name = "chapterNumber") int chapterNumber) {
        String verse = "";
        BibleVerse obj = new BibleVerse();
        switch (chapterNumber) {
            case 1:
                for (int verseNumber = 1; obj.bibleChapter1(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter1(verseNumber));
                }
                break;
            case 2:
                for (int verseNumber = 1; obj.bibleChapter2(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter2(verseNumber));
                }
                break;
            case 3:
                for (int verseNumber = 1; obj.bibleChapter3(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter3(verseNumber));
                }
                break;
            case 4:
                for (int verseNumber = 1; obj.bibleChapter4(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter4(verseNumber));
                }
                break;
            case 5:
                for (int verseNumber = 1; obj.bibleChapter5(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter5(verseNumber));
                }
                break;
            case 6:
                for (int verseNumber = 1; obj.bibleChapter6(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter6(verseNumber));
                }
                break;
            case 7:
                for (int verseNumber = 1; obj.bibleChapter7(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter7(verseNumber));
                }
                break;
            case 8:
                for (int verseNumber = 1; obj.bibleChapter8(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter8(verseNumber));
                }
                break;
            case 9:
                for (int verseNumber = 1; obj.bibleChapter9(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter9(verseNumber));
                }
                break;
            case 10:
                for (int verseNumber = 1; obj.bibleChapter10(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter10(verseNumber));
                }
                break;
            case 11:
                for (int verseNumber = 1; obj.bibleChapter11(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter11(verseNumber));
                }
                break;
            case 12:
                for (int verseNumber = 1; obj.bibleChapter12(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter12(verseNumber));
                }
                break;
            case 13:
                for (int verseNumber = 1; obj.bibleChapter13(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter13(verseNumber));
                }
                break;
            case 14:
                for (int verseNumber = 1; obj.bibleChapter14(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter14(verseNumber));
                }
                break;
            case 15:
                for (int verseNumber = 1; obj.bibleChapter15(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter15(verseNumber));
                }
                break;
            case 16:
                for (int verseNumber = 1; obj.bibleChapter16(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter16(verseNumber));
                }
                break;
            case 17:
                for (int verseNumber = 1; obj.bibleChapter17(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter17(verseNumber));
                }
                break;
            case 18:
                for (int verseNumber = 1; obj.bibleChapter18(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                    verse = verse.concat(obj.bibleChapter18(verseNumber));
                }
                break;

            default:
                verse = "Invalid chapter number";
                break;
        }
        return verse;
    }

    
}
