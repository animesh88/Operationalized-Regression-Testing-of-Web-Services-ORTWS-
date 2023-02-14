GetAllVerseByBookAndChapterNumber[@WebParam(name = "bookNumber") int bookNumber, @WebParam(name = "chapterNumber") int chapterNumber]{
    String verse = "";
    switch(bookNumber) {
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