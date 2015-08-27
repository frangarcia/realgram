import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.http.HttpServerRequest

def	log			= container.getLogger()

/*====================
 * Ping handler
 ====================*/
def pingHandler = { req ->
    String status="ALIVE"
    req.response.end("${status}")
    req.response.close()
    return;
}

/*====================
 *404
 ====================*/
def fourOFourHandler = { req->
	println "fourOFourHandler"
    req.response.putHeader("Access-Control-Allow-Origin", "*" )
    req.response.putHeader("Access-Control-Allow-Credentials", "true" )
    req.response.putHeader("P3P", "CP=\"We dont use P3P policies. Search stack overflow to learn more\"" )
    req.response.setStatusCode(404).setStatusMessage("Four O Four" ).end()
}

def websiteHandler = { req ->
	String path=req.uri
    def file = path == "/" ? "/index.html" : path
    req.response.sendFile ".$file"
}

RouteMatcher rm = new RouteMatcher()
rm.getWithRegEx("/console/(.)*", websiteHandler)
rm.get("/ping", pingHandler)
rm.noMatch fourOFourHandler

RouteMatcher rmSecure = new RouteMatcher()
rmSecure.noMatch fourOFourHandler

def server = vertx.createHttpServer()



// Serve the static resources
server.requestHandler(rm.asClosure())
vertx.createSockJSServer(server).bridge(prefix: '/eventbus', [[:]], [[:]])

server.listen(8080)

