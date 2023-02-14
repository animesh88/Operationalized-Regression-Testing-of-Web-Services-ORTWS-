Searching[@WebParam(name = "QueryString") String QueryString]{
    String outputString = null;
    try {
        CommonsHttpSolrServer server = null;
        try {
            server = new CommonsHttpSolrServer("http://localhost:8983/solr");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SolrQuery query = new SolrQuery();
        query.setQuery(QueryString);
        QueryResponse rsp = server.query(query);
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
    } catch (SolrServerException ex) {
    }
    return outputString;
}