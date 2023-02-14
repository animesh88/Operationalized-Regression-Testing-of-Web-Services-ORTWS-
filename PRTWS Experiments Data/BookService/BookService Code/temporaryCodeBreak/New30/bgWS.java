bgWS[@WebParam(name = "chapterNumber") int chapterNumber, @WebParam(name = "verseNumber") int verseNumber]{
    String verse = "";
    BGVerse obj = new BGVerse();
    switch(chapterNumber) {
        case 1:
            verse = obj.bgChapter1(verseNumber);
            break;
        case 2:
            verse = obj.bgChapter2(verseNumber);
            break;
        case 3:
            verse = obj.bgChapter3(verseNumber);
            break;
        case 4:
            verse = obj.bgChapter4(verseNumber);
            break;
        case 5:
            verse = obj.bgChapter5(verseNumber);
            break;
        case 6:
            verse = obj.bgChapter6(verseNumber);
            break;
        case 7:
            verse = obj.bgChapter7(verseNumber);
            break;
        case 8:
            verse = obj.bgChapter8(verseNumber);
            break;
        case 9:
            verse = obj.bgChapter9(verseNumber);
            break;
        case 10:
            verse = obj.bgChapter10(verseNumber);
            break;
        case 11:
            verse = obj.bgChapter11(verseNumber);
            break;
        case 12:
            verse = obj.bgChapter12(verseNumber);
            break;
        case 13:
            verse = obj.bgChapter13(verseNumber);
            break;
        case 14:
            verse = obj.bgChapter14(verseNumber);
            break;
        case 15:
            verse = obj.bgChapter15(verseNumber);
            break;
        case 16:
            verse = obj.bgChapter16(verseNumber);
            break;
        case 17:
            verse = obj.bgChapter17(verseNumber);
            break;
        case 18:
            verse = obj.bgChapter18(verseNumber);
            break;
        default:
            verse = "Invalid chapter number";
            break;
    }
    return verse;
}