package org.texttechnologylab.parliament_browser_3_4.webservice;

import freemarker.template.Configuration;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Factory_MongoDB_Impl;
import org.texttechnologylab.parliament_browser_3_4.database.MongoDBHandler;
import spark.Spark;
import spark.servlet.SparkApplication;

import java.io.File;
import java.io.IOException;

/**
 * Spark Webserver class
 * @author Simon Schuett
 */
public class Webserver implements SparkApplication {

    /** Parliament corpus for data access */
    private final Factory_MongoDB_Impl parliament;

    /** MongoDBConnection Handler for data access */
    private final MongoDBHandler mongoDB;

    /** FreeMarker Configuration */
    private final Configuration config = new Configuration(Configuration.VERSION_2_3_26);

    /**
     * Constructor for webserver
     * @param parliament
     * @author Simon Schuett
     */
    public Webserver(Factory_MongoDB_Impl parliament, MongoDBHandler mongoDB) {
        super();
        this.parliament = parliament;
        this.mongoDB = mongoDB;

        Spark.staticFileLocation("templates/public");
        Spark.externalStaticFileLocation("templates/public");
        Spark.staticFiles.location("templates/public");
        Spark.staticFiles.externalLocation("templates/public");

        try {
            config.setDirectoryForTemplateLoading(new File("templates/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        RestRoutes routes = new RestRoutes(config, parliament, mongoDB);
        routes.createSiteRoutes();
        routes.createDataRoutes();
        routes.createLoginRoutes();
        routes.createNetworkGraphRoutes();
    }

}
