import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

public class Parce {
    public static double courseBuy(String currency) {
        double currentCourse = currencyCourse(currency);
        double courseBuylValue = currentCourse / 100 + currentCourse;
        return courseBuylValue;
    }
    // TODO courseBuy

    public static double courseSell(String currency) {
        double currentCourse = currencyCourse(currency);
        double courseSellValue = currentCourse - (currentCourse / 100);
        return courseSellValue;
    }
    @SneakyThrows
    private static double currencyCourse(String currency) {
        URL url = new URL("https://api.binance.com/api/v3/ticker/price");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine; // "ABC"
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONArray jsonArray = new JSONArray(response.toString());
        // JSONObject -> {...}
        // JSONArray -> [...]

        // for (int i = 0; i < ...; i++)
        // for (int val : list)
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentCurrency = jsonArray.getJSONObject(i);
//            {
//                    "symbol": "ETHBTC",
//                    "price": "0.07861700"
//            }
            String symbol = currentCurrency.getString("symbol");
            String price = currentCurrency.getString("price");
            if (symbol.equals(currency)) {
                return Double.parseDouble(price);
            }
        }
        throw new Exception("Currency not exist!!!");
    }

    public static void main(String[] args) {
        // static
        // Parce.courseSell();


        // non-static
        // Parce.courseSell(); - НЕЛЬЗЯ

        // Parce parce = new Parce(); - МОЖНО
        // parce.courseSell()


//        System.out.println(courseSell("USDTRUB"));
//        System.out.println(courseSell("BTCRUB"));
//        System.out.println(courseSell("ETHRUB"));
//
//        System.out.println(courseBuy("USDTRUB"));
//        System.out.println(courseBuy("BTCRUB"));
//        System.out.println(courseBuy("ETHRUB"));
    }
}
