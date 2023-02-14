bgAllVerse[@WebParam(name = "chapterNumber") int chapterNumber]{
    String verse = "";
    BGVerse obj = new BGVerse();
    switch(chapterNumber) {
        case 1:
            for (int verseNumber = 1; obj.bgChapter1(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter1(verseNumber));
            }
            break;
        case 2:
            for (int verseNumber = 1; obj.bgChapter2(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter2(verseNumber));
            }
            break;
        case 3:
            for (int verseNumber = 1; obj.bgChapter3(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter3(verseNumber));
            }
            break;
        case 4:
            for (int verseNumber = 1; obj.bgChapter4(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter4(verseNumber));
            }
            break;
        case 5:
            for (int verseNumber = 1; obj.bgChapter5(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter5(verseNumber));
            }
            break;
        case 6:
            for (int verseNumber = 1; obj.bgChapter6(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter6(verseNumber));
            }
            break;
        case 7:
            for (int verseNumber = 1; obj.bgChapter7(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter7(verseNumber));
            }
            break;
        case 8:
            for (int verseNumber = 1; obj.bgChapter8(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter8(verseNumber));
            }
            break;
        case 9:
            for (int verseNumber = 1; obj.bgChapter9(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter9(verseNumber));
            }
            break;
        case 10:
            for (int verseNumber = 1; obj.bgChapter10(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter10(verseNumber));
            }
            break;
        case 11:
            for (int verseNumber = 1; obj.bgChapter11(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter11(verseNumber));
            }
            break;
        case 12:
            for (int verseNumber = 1; obj.bgChapter12(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter12(verseNumber));
            }
            break;
        case 13:
            for (int verseNumber = 1; obj.bgChapter13(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter13(verseNumber));
            }
            break;
        case 14:
            for (int verseNumber = 1; obj.bgChapter14(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter14(verseNumber));
            }
            break;
        case 15:
            for (int verseNumber = 1; obj.bgChapter15(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter15(verseNumber));
            }
            break;
        case 16:
            for (int verseNumber = 1; obj.bgChapter16(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter16(verseNumber));
            }
            break;
        case 17:
            for (int verseNumber = 1; obj.bgChapter17(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter17(verseNumber));
            }
            break;
        case 18:
            for (int verseNumber = 1; obj.bgChapter18(verseNumber) != "Invalid verseNumber"; verseNumber++) {
                verse = verse.concat(obj.bgChapter18(verseNumber));
            }
            break;
        default:
            verse = "Invalid chapter number";
            break;
    }
    return verse;
}