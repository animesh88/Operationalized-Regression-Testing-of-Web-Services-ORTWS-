editFile[@WebParam(name = "fileName") String fileName, @WebParam(name = "lineToEdit") String lineToEdit, @WebParam(name = "replacementText") String replacementText]{
    try {
        Scanner fileToRead = null;
        StringBuffer stringBufferOfData = new StringBuffer();
        try {
            fileToRead = new Scanner(new File(fileName));
            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null; ) {
                stringBufferOfData.append(line).append("\r\n");
            }
            fileToRead.close();
        } catch (FileNotFoundException ex) {
        }
        int startIndex = stringBufferOfData.indexOf(lineToEdit);
        int endIndex = startIndex + lineToEdit.length();
        stringBufferOfData.replace(startIndex, endIndex, replacementText);
        try {
            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName));
            bufwriter.write(stringBufferOfData.toString());
            bufwriter.close();
        } catch (Exception e) {
        }
    } catch (Exception e) {
    }
    String str = "";
    String output = "";
    try {
        FileReader reader = null;
        try {
            reader = new FileReader(new File(fileName));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader bufferReader = new BufferedReader(reader);
        while ((str = bufferReader.readLine()) != null) {
            output = output.concat(str);
        }
        output = output.concat("");
    } catch (IOException ex) {
        Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
    }
    return output;
}