package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Esta clase es la que tiene conexion directa con el Api Externa omdbapi, donde se encuentra toda la informacion
 * de las peliculas de cine
 */

public class HttpConnection {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://www.omdbapi.com/?apikey=5d891b41&t=";

    /**
     *
     * @param movie es el nombre de la pelicula que el usuario, esta interesado en conocer su informacion
     * @return Devuelve una cadena de texto que contiene toda la informacion de la pelicula que se esta buscando
     * @throws IOException es una expecion que puede salir debido a una interrupcion
     */
    public String  ResponseRequest(String movie) throws IOException {

        URL obj = new URL(GET_URL + movie );
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        StringBuffer response = new StringBuffer("HTTP/1.1 200 \r\n" //encabezado necesario
                                                + "Content-Type:text/html\r\n"
                                                + "\r\n" );//retorno de carro y salto de linea)

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println("GET DONE");
        return response.toString();
    }

}