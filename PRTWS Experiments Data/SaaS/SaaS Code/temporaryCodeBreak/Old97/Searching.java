Searching[@WebParam(name = "QueryString") String QueryString]{
    String outputString = null;
    CommonsHttpSolrServer server = null;
    try {
        server = new CommonsHttpSolrServer("http://localhost:8983/solr");
    } catch (MalformedURLException ex) {
        Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
    }
    SolrQuery query = new SolrQuery();
    query.setQuery(QueryString);
    QueryResponse rsp = null;
    try {
        rsp = server.query(query);
    } catch (SolrServerException ex) {
        Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
    }
    rsp.getResults();
    SolrDocumentList listOfdoc = rsp.getResults();
    String Output = ("\n\n***** The total results returned is = " + listOfdoc.size() + " *****\n");
    String output = "\n***********************************************************\n";
    for (Iterator<SolrDocument> it = listOfdoc.iterator(); it.hasNext(); ) {
        SolrDocument solrDocument = it.next();
        String id = (String) solrDocument.getFieldValue("id");
        String name = (String) solrDocument.getFieldValue("name");
        output = output.concat("\n ID: " + id.concat("           Path: " + name + " \n\n"));
    }
    outputString = Output.concat(output);
    return outputString;
}