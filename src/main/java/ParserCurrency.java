import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ParserCurrency {
    /**
     * Возвращает курс покупки по заданной валюте
     *
     * @param currency название валюты
     * @return курс покупки
     */
    public static double courseBuy(String currency) {
        double currentCourse = currencyCourse(currency);
        double courseBuylValue = currentCourse / 100 + currentCourse;
        return courseBuylValue;
    }

    /**
     * Возвращает курс продажи с комиссией 1 процент по заданной валюте
     *
     * @param currency название валюты
     * @return курс продажи
     */
    public static double courseSell(String currency) {
        double currentCourse = currencyCourse(currency);
        double courseSellValue = currentCourse - (currentCourse / 100);
        return courseSellValue;
    }


    /**
     * Парсит курс валют с Бинанс и возвращает
     * Заносит в буфер строку, которую получает парсер и превращает в JSON
     * Цикл поиска поданой валюты и если валюта не найдена, выкидывается исключение
     *
     * @param currency название валюты
     * @return Возвращает стоимость валют
     */
    @SneakyThrows
    private static double currencyCourse(String currency) {
        URL url = new URL("https://api.binance.com/api/v3/ticker/price");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONArray jsonArray = new JSONArray(response.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentCurrency = jsonArray.getJSONObject(i);

            String symbol = currentCurrency.getString("symbol");
            String price = currentCurrency.getString("price");
            if (symbol.equals(currency)) {
                return Double.parseDouble(price);
            }
        }
        throw new Exception("Currency not exist!!!");
    }
}
