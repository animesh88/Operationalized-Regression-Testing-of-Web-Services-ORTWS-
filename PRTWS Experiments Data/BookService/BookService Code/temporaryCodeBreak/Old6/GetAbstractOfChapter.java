GetAbstractOfChapter[@WebParam(name = "bookNumber") int bookNumber, @WebParam(name = "chapterNumber") int chapterNumber]{
    String abst = "";
    switch(bookNumber) {
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