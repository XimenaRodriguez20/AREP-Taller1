package org.example;

import java.net.*;
import java.io.*;
import java.util.HashMap;
/**
 * Esta clase es el Api fachada la cual contiene la conexion directa con el usuario para que este no vaya a
 * consultar de directamente al Api externa, esto lo logramos con ayuda del cache
 * @author ximena.rodriguez
 */
public class HttpServer {

    /**
     * Es el metodo que va a permitir ejecutar toda la aplicacion y a su ves contiene toda la logica del cache,
     * las consultas de nuestra api fachada a la api externa 
     * @param args
     * @throws IOException s una expecion que puede salir debido a una interrupcion
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;

        HashMap<String,String> cacheMovies = new HashMap<String,String>();

        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean firstLine = true;
            String uriStr ="";



            while ((inputLine = in.readLine()) != null) {
                if(firstLine){
                    uriStr = inputLine.split(" ")[1];
                    firstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            // Se tienen tres validaciones para la Api fachada
            // La primera nos manda a la pagina web sin haber realizado ninguna busqueda
            if(uriStr.startsWith("/Web")) {
                outputLine = httpClientHtml();
            //En esta parte validamos cuando se haga una busqueda, de acuerdo al nombre que nos provee el usuario
            }else if (uriStr.startsWith("/Movies")){
                //En caso de que ya se halla realizado una busqueda previa sobre esta pelicula no habra necesidad
                //de consultar la api externa  directamente retornaremos lo que se tiene en esta api fachada
                if(cacheMovies.containsKey(uriStr.split("=")[1])){
                    outputLine = cacheMovies.get(uriStr.split("=")[1]);
                //Sino se tiene una busqueda previa esta información como es nueva se hace la busqueda en la api externa
                //y se guarda en nuestra api fachada
                }else {
                    HttpConnection httpApiExternal = new HttpConnection();
                    outputLine = httpApiExternal.ResponseRequest(uriStr.split("=")[1]);
                    cacheMovies.put(uriStr.split("=")[1],outputLine);
                }
            // sino escribe el path correto no va ha mostrar la pagina donde se puede consultar las peliculas
            }else {
                outputLine = httpError();
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    /**
     * Pagina Web
     * @return Devuelve una pagina vacia, esto ocurre porque el usuario escribioe de manera incorrecta la url
     */

    public static String httpError() {
        return "HTTP/1.1 400 Not found\r\n" //encabezado necesario
                + "Content-Type:text/html\r\n"
                + "\r\n" //retorno de carro y salto de linea
                + "<!DOCTYPE html>"
                + "<html>\n"
                + "    <head>\n"
                + "        <title>Error Not found</title>\n"
                + "        <meta charset=\"UTF-8\">\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    </head>\n";

    }

    /**
     *  Pagina Web
     *
     * @return Nos devuelve la pagina web simple, es decir cuando no se ha realizado ninguna peticion de ninguna pelicula
     * ,pero tambien nos retorna la pagina web con todos los datos, una ves realizada una peticion con respecto a una pelicula
     */

    public static String httpClientHtml() {
        return "HTTP/1.1 200 OK\r\n" //encabezado necesario
                + "Content-Type:text/html\r\n"
                + "\r\n" //retorno de carro y salto de linea
                + "<!DOCTYPE html>"
                + "<html>\n"
                + "    <head>\n"
                + "        <title>Pelicula</title>\n"
                + "        <meta charset=\"UTF-8\">\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    </head>\n"
                + "    <body>\n"
                + "        <h1 style=\"text-align: center;\">What movie do you want to consult?</h1>\n"
                + "        <input  type=\"text\" id=\"name\" name=\"name\" value=\"Cars\"><br><br>\n"
                + "        <input type=\"button\" value=\"Search\" onclick=\"loadGetMsg()\"><br><br>\n"
                + "        <div id=\"getrespmsg\"></div>\n"
                + "\n"
                + "        <script>\n"
                + "            function loadGetMsg() {\n"
                + "                let nameVar = document.getElementById(\"name\").value;\n"
                + "                const xhttp = new XMLHttpRequest();\n"
                + "                xhttp.onload = function() {\n"
                + "                var jsonResponse = JSON.parse(this.responseText);\n"
                + "                var Content = \"Title: \" + jsonResponse.Title + \"<br>\";\n"
                + "                Content += \"Year: \" + jsonResponse.Year + \"<br>\";\n"
                + "                Content += \"Rated: \" + jsonResponse.Rated + \"<br>\";\n"
                + "                Content += \"Released: \" + jsonResponse.Released + \"<br>\";\n"
                + "                Content += \"Runtime: \" + jsonResponse.Runtime + \"<br>\";\n"
                + "                Content += \"Genre: \" + jsonResponse.Genre + \"<br>\";\n"
                + "                Content += \"Director: \" + jsonResponse.Director + \"<br>\";\n"
                + "                Content += \"Writer: \" + jsonResponse.Writer + \"<br>\";\n"
                + "                Content += \"Actors: \" + jsonResponse.Actors + \"<br>\";\n"
                + "                Content += \"Plot: \" + jsonResponse.Plot + \"<br>\";\n"
                + "                Content += \"Year: \" + jsonResponse.Language + \"<br>\";\n"
                + "                Content += \"Country: \" + jsonResponse.Country + \"<br>\";\n"
                + "                Content += \"Awards: \" + jsonResponse.Awards + \"<br>\";\n"
                + "                Content += \"Poster: <br> \" +`<img src=\"${jsonResponse.Poster}\" alt=\"Movie poster\" width=\"150\">`+ \"<br>\";\n"
                + "                Content += \"Metascore: \" + jsonResponse.Metascore + \"<br>\";\n"
                + "                Content += \"imdbRating: \" + jsonResponse.imdbRating + \"<br>\";\n"
                + "                Content += \"imdbVotes: \" + jsonResponse.imdbVotes + \"<br>\";\n"
                + "                Content += \"imdbID: \" + jsonResponse.imdbID + \"<br>\";\n"
                + "                Content += \"Type: \" + jsonResponse.Type + \"<br>\";\n"
                + "                Content += \"DVD: \" + jsonResponse.DVD + \"<br>\";\n"
                + "                Content += \"BoxOffice: \" + jsonResponse.BoxOffice + \"<br>\";\n"
                + "                Content += \"Production: \" + jsonResponse.Production + \"<br>\";\n"
                + "                Content += \"Website: \" + jsonResponse.Website + \"<br>\";\n"
                + "                Content += \"Response: \" + jsonResponse.Response + \"<br>\";\n"
                + "                Content;\n"
                + "                document.getElementById(\"getrespmsg\").innerHTML = Content;\n"
                + "                }\n"
                + "                xhttp.open(\"GET\", \"/Movies?t=\"+nameVar);\n"
                + "                xhttp.send();\n"
                + "            };\n"
                + "        </script>\n"
                + "\n"
                + "        "
                + "    </body>\n"
                + "</html>";


    }

}
