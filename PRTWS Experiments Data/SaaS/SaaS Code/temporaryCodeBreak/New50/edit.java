edit[@WebParam(name = "filename") String filename, @WebParam(name = "text") String text]{
    BufferedWriter bufwriter = null;
    try {
        String str;
        bufwriter = new BufferedWriter(new FileWriter(filename));
        bufwriter.write(text);
        bufwriter.close();
    } catch (IOException ex) {
        Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        try {
            bufwriter.close();
        } catch (IOException ex) {
            Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    String str = "";
    String output = "";
    try {
        FileReader reader = null;
        try {
            reader = new FileReader(new File(filename));
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