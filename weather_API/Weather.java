package ram.com;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Weather {

    public static void main(String[] args) {
        // Creates a reference to CloseableHttpClient, which is thread safe
        CloseableHttpClient httpclient = HttpClients.createDefault();
        ResponseHandler<JSONObject> responseHandler = (ResponseHandler<JSONObject>) new MyJSONResponseHandler();

        //my data structure
        Map<City, ArrayList<Info>> locations = new HashMap<>();

        //add city values
        ArrayList<City> cities = new ArrayList<>();
        City marlboro = new City("Marlboro", "MA", "01572");
        cities.add(marlboro);
        City sanDiego = new City("San%20Diego", "CA", "22434");
        cities.add(sanDiego);
        City anchorage = new City("Anchorage", "AK","99501");
        cities.add(anchorage);
        City austin = new City("Austin", "TX","73301");
        cities.add(austin);
        City orlando = new City("Orlando", "FL","32789");
        cities.add(orlando);
        City seattle = new City("Seattle", "WA","98101");
        cities.add(seattle);
        City cleveland = new City("Cleveland", "OH","44101");
        cities.add(cleveland);
        City portland = new City("Portland", "ME","04019");
        cities.add(portland);
        City honolulu = new City("Honolulu", "HI","96795");
        cities.add(honolulu);

            try {
                for(City city: cities) {
                    String URI = ("http://api.openweathermap.org/data/2.5/forecast?q=" + city.getName() + ",us&mode=json&APPID=b33b2eee2ba8047016d3a9c1dcb5fef6");

                    HttpGet httpget = new HttpGet(URI);
                    JSONObject responseBody = (JSONObject) httpclient.execute(httpget, responseHandler);

                    //Add days and avgTemp to each city
                    city.setCity((String) ((JSONObject) responseBody.get("city")).get("name"));


                    ArrayList<Info> day = new ArrayList<>();
                    int[] days = {0, 7, 15, 23, 30};
                    double temp = 0;
                    boolean isRain =false;
                    for (int dayIterate : days) {

                        //gets new day
                        JSONArray listofTemp = ((JSONArray) responseBody.get("list"));
                        JSONObject getDay = (JSONObject) listofTemp.get(dayIterate);
                        String getDate = (String) getDay.get("dt_txt");
                        getDate = getDate.substring(0, 10);

                        //add temp for the whole day
                        for (int i = 1; i < 8; i++) {
                            getDay = (JSONObject) listofTemp.get(dayIterate + i);
                            JSONObject getTemp = (JSONObject) getDay.get("main");
                            double iterateTemp = (double) getTemp.get("temp");
                            temp = iterateTemp + temp;

                           JSONObject rain = (JSONObject) getDay.get("rain");
                          if(rain!=null){
                              isRain = true;
                          }

                        }

                        //average temp = temp/7
                        //convert to farenheight;
                        Double avgFarenTemp = (((9 / 5) * ((temp/7) - 273)) + 32);
                        temp =0;

                        Info dayInfo = new Info(getDate, avgFarenTemp, isRain);
                        day.add(dayInfo);
                    }
                    //add dat for City(5days) to MAP
                    locations.put(city, day);
                }
                for (Map.Entry <City, ArrayList<Info>> location : locations.entrySet()) {
                    System.out.println("_______________________");
                    String name = location.getKey().getName();
                    String state = location.getKey().getState();
                    String zip = location.getKey().getZip();

                    System.out.println(name + ", " + state + ", ("+ zip + ") " );
                    System.out.println();
                    System.out.println("Date       Avg Temp(F)");
                    System.out.println("-----------------------");
                        for(int i=0; i<location.getValue().size(); i++) {

                            String showDate = (location.getValue().get(i).getDate());
                            double showTemp= location.getValue().get(i).getTemp();
                            System.out.print(showDate );
                            if(location.getValue().get(i).getIfRain()){
                                System.out.print("* ");
                            }else{
                                System.out.print("  ");
                            }
                            System.out.printf("%.2f", showTemp);
                            System.out.print(" F");
                            System.out.println();

                        }
                }
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


    }



}


