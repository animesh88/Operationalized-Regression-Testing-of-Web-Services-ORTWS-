GetVerseByBooKAndChapterAndVerseNumber[@WebParam(name = "bookNumber") int bookNumber, @WebParam(name = "chapterNumber") int chapterNumber, @WebParam(name = "verseNumber") int verseNumber]{
    String verse = "";
    switch(bookNumber) {
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