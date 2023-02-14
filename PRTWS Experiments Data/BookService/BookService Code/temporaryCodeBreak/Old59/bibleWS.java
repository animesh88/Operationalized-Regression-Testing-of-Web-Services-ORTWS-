bibleWS[@WebParam(name = "chapterNumber") int chapterNumber, @WebParam(name = "verseNumber") int verseNumber]{
    String verse = "";
    BibleVerse obj = new BibleVerse();
    switch(chapterNumber) {
        case 1:
            verse = obj.bibleChapter1(verseNumber);
            break;
        case 2:
            verse = obj.bibleChapter2(verseNumber);
            break;
        case 3:
            verse = obj.bibleChapter3(verseNumber);
            break;
        case 4:
            verse = obj.bibleChapter4(verseNumber);
            break;
        case 5:
            verse = obj.bibleChapter5(verseNumber);
            break;
        case 6:
            verse = obj.bibleChapter6(verseNumber);
            break;
        case 7:
            verse = obj.bibleChapter7(verseNumber);
            break;
        case 8:
            verse = obj.bibleChapter8(verseNumber);
            break;
        case 9:
            verse = obj.bibleChapter9(verseNumber);
            break;
        case 10:
            verse = obj.bibleChapter10(verseNumber);
            break;
        case 11:
            verse = obj.bibleChapter11(verseNumber);
            break;
        case 12:
            verse = obj.bibleChapter12(verseNumber);
            break;
        case 13:
            verse = obj.bibleChapter13(verseNumber);
            break;
        case 14:
            verse = obj.bibleChapter14(verseNumber);
            break;
        case 15:
            verse = obj.bibleChapter15(verseNumber);
            break;
        case 16:
            verse = obj.bibleChapter16(verseNumber);
            break;
        case 17:
            verse = obj.bibleChapter17(verseNumber);
            break;
        case 18:
            verse = obj.bibleChapter18(verseNumber);
            break;
        default:
            verse = "Invalid chapter number";
            break;
    }
    return verse;
}