/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.pirk.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pirk.test.utils.Inputs;
import org.apache.solr.SolrJettyTestBase;
import static org.apache.solr.SolrTestCaseJ4.assertQ;
import static org.apache.solr.SolrTestCaseJ4.deleteCore;
import static org.apache.solr.SolrTestCaseJ4.initCore;
import static org.apache.solr.SolrTestCaseJ4.req;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.json.simple.JSONObject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author rrockenbaugh
 */
public class PirkSolrTest extends SolrJettyTestBase {

    private static final Logger log = LoggerFactory.getLogger(PirkSolrTest.class);

    private static final String COLLECTION1 = "collection1";

    private static final String CONF_DIR = "solr/collection1/conf/";

    @BeforeClass
    public static void beforeClass() throws Exception {
        initCore("solr/collection1/conf/solrconfig.xml", "solr/collection1/conf/schema.xml");

        /* make sure some misguided soul doesn't inadvertently give us 
       a uniqueKey field and defeat the point of the tests
         */
//        assertNull("UniqueKey Field isn't null",
//                h.getCore().getLatestSchema().getUniqueKeyField());

        lrf.args.put(CommonParams.VERSION, "2.2");

//        assertNull("Simple assertion that adding a document works", h.validateUpdate(
//                adoc("id", "4055",
//                        "subject", "Hoss",
//                        "project", "Solr")));
//        assertNull(h.validateUpdate(adoc("id", "4056",
//                "subject", "Yonik",
//                "project", "Solr")));
//        assertNull(h.validateUpdate(commit()));
//        assertNull(h.validateUpdate(optimize()));

    }

    @Override
    public void tearDown() throws Exception {
        deleteCore();
        super.tearDown();
    }
    
    private void indexData(List<JSONObject> dataElements) throws SolrServerException, IOException {
        
        for (JSONObject jsonObject : dataElements) {
            SolrInputDocument doc = new SolrInputDocument();
            for (Object key : jsonObject.keySet()) {
                doc.addField((String) key, jsonObject.get(key));
            }
            try {
                getSolrClient().add(doc);
                System.out.println("doc=" + doc.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
     
        }
        getSolrClient().commit(true, true);
        
        
    }
  
    private List<JSONObject> queryDataElements() {
        ArrayList<JSONObject> dataElements = new ArrayList<JSONObject>();
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.add("q", "*:*");
        try {
            QueryResponse response = getSolrClient().query(params);
            for (SolrDocument doc : response.getResults()) {
                JSONObject obj = new JSONObject(doc.getFieldValueMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        
        
        return dataElements;
    }
 
    
    @Test
    public void testLoad() throws SolrServerException, IOException {
        indexData(Inputs.createJSONDataElements());
        assertQ("couldn't find records",
                req(Inputs.SRCIP + ":1.1.1.1" ),
                "//result[@numFound=1]",
                "//str[@name='qname'][.='something.else']"
                );
        QueryResponse response = getSolrClient().query(req(Inputs.SRCIP + ":1.1.1.1" ).getParams());
        System.out.println(response.toString());
    }
    
    @Test
    public void testSimpleQueries() {

//        assertQ("couldn't find subject hoss",
//                req("subject:Hoss"),
//                 "//result[@numFound=1]",
//                 "//str[@name='id'][.='4055']"
 //       );

//        assertQ("couldn't find subject Yonik",
//                req("subject:Yonik"),
//                 "//result[@numFound=1]",
 //                "//str[@name='id'][.='4056']"
//        );
    }
}
