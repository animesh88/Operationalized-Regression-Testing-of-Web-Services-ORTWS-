bibleAllVerse[@WebParam(name = "chapterNumber") int chapterNumber]{
    String verse = "";
    BibleVerse obj = new BibleVerse();
    switch(chapterNumber) {
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