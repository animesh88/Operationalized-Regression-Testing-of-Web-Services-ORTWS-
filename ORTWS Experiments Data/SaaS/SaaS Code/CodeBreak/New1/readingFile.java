readingFile[@WebParam(name = "File") String File]{
    String str = "";
    String output = "";
    try {
        FileReader reader = null;
        try {
            reader = new FileReader(new File(File));
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