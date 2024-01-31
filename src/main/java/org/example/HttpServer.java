package org.example;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ximena.rodriguez
 */
import java.net.*;
import java.io.*;
import java.util.HashMap;

public class HttpServer {


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


            if(uriStr.startsWith("/Web")) {
                outputLine = httpClientHtml();
            }else if (uriStr.startsWith("/Movies")){

                if(cacheMovies.containsKey(uriStr.split("=")[1])){
                    outputLine = cacheMovies.get(uriStr.split("=")[1]);
                    System.out.println("NOOOOOOOOOOOOOO esta guardando:");
                }else {
                    HttpConnection httpApiExternal = new HttpConnection();
                    outputLine = httpApiExternal.Request(uriStr.split("=")[1]);
                    cacheMovies.put(uriStr.split("=")[1],outputLine);
                    System.out.println("si esta guardando:");
                }

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
                + "        <h1>Form with GET</h1>\n"
                + "        <form action=\"/hello\">\n"
                + "            <label for=\"name\">Nombre de la pelicula:</label><br>\n"
                + "            <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\n"
                + "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n"
                + "        </form> \n"
                + "        <div id=\"getrespmsg\"></div>\n"
                + "\n"
                + "        <script>\n"
                + "            function loadGetMsg() {\n"
                + "                let nameVar = document.getElementById(\"name\").value;\n"
                + "                const xhttp = new XMLHttpRequest();\n"
                + "                xhttp.onload = function() {\n"
                + "                    document.getElementById(\"getrespmsg\").innerHTML =\n"
                + "                    this.responseText;\n"
                + "                }\n"
                + "                xhttp.open(\"GET\", \"/Movies?t=\"+nameVar);\n"
                + "                xhttp.send();\n"
                + "            }\n"
                + "        </script>\n"
                + "\n"
                + "        "
                + "    </body>\n"
                + "</html>";


    }

}
