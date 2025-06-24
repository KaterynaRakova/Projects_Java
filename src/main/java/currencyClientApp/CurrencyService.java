package currencyClientApp;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class CurrencyService {
    private final String apiKey;

    public CurrencyService() {
        this.apiKey = Utils.readApiKey("openexchangerates.api-key");}

    public  Optional<ExchangeRates> fetchRates(){
        String url ="https://openexchangerates.org/api/latest.json?app_id=" + apiKey;
        try {
            HttpClient client=HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request=HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()==200 && response.body() !=null && !response.body().isEmpty()){
                ExchangeRates rates=new Gson().fromJson(response.body(), ExchangeRates.class);
                return Optional.ofNullable(rates)
                        .filter(r -> r.rates != null);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error while fetching exchange rates", e);
        }
        return Optional.empty();
    }
    public Optional<Double> convert(String from,String to,double amount){
        Optional<ExchangeRates> ratesOpt =fetchRates();
        if(ratesOpt.isPresent()){
            ExchangeRates rates= ratesOpt.get();
            Map<String,Double> rateMap =rates.rates;
            double rateFrom =from.equalsIgnoreCase(rates.base) ? 1.0: rateMap.getOrDefault(from.toUpperCase(),-1.0);
            double rateTo=to.equalsIgnoreCase(rates.base) ? 1.0 :rateMap.getOrDefault(to.toUpperCase(), -1.0);
           if(rateFrom <= 0 || rateTo <=0) return Optional.empty();
           double usdAmount = amount/rateFrom;
           double converted = usdAmount * rateTo;
           return Optional.of(Math.round(converted * 100.0) / 100.0);
    }
        return  Optional.empty();
    }
    public Set<String> getAvailableCurrencies() {
        return fetchRates()
                .map(r -> r.rates.keySet())
                .orElse(Collections.emptySet());
    }

}
